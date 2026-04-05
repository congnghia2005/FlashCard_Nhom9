package com.example.flashcard.data

import com.example.flashcard.model.Flashcard
import com.example.flashcard.model.SM2Algorithm
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class FlashcardRepository(private val flashcardDao: FlashcardDao) {
    val allFlashcards: Flow<List<Flashcard>> = flashcardDao.getAllFlashcards()

    fun getFlashcardsToReview(): Flow<List<Flashcard>> {
        return flashcardDao.getFlashcardsToReview(System.currentTimeMillis())
    }

    suspend fun addFlashcard(front: String, back: String) {
        val flashcard = Flashcard(front = front, back = back)
        flashcardDao.insertFlashcard(flashcard)
    }

    suspend fun updateFlashcardAfterReview(flashcard: Flashcard, quality: Int) {
        val sm2Result = SM2Algorithm.calculate(
            quality = quality,
            previousInterval = flashcard.interval,
            previousRepetition = flashcard.repetition,
            previousEF = flashcard.easinessFactor
        )

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, sm2Result.interval)

        val updatedFlashcard = flashcard.copy(
            interval = sm2Result.interval,
            repetition = sm2Result.repetition,
            easinessFactor = sm2Result.easinessFactor,
            nextReviewDate = calendar.timeInMillis
        )
        flashcardDao.updateFlashcard(updatedFlashcard)
    }
}
