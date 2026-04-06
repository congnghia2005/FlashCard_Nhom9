package com.example.flashcard.data

import com.example.flashcard.model.Deck
import com.example.flashcard.model.Flashcard
import com.example.flashcard.model.SM2Algorithm
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class FlashcardRepository(
    private val flashcardDao: FlashcardDao,
    private val deckDao: DeckDao
) {

    // Deck operations
    fun getDecksByUser(userId: String): Flow<List<Deck>> = deckDao.getDecksByUser(userId)
    
    suspend fun insertDeck(deck: Deck) = deckDao.insertDeck(deck)
    
    suspend fun deleteDeck(deck: Deck) = deckDao.deleteDeck(deck)

    // Flashcard operations
    fun getFlashcardsByUser(userId: String): Flow<List<Flashcard>> = 
        flashcardDao.getFlashcardsByUser(userId)

    fun getFlashcardsByDeck(deckId: Long): Flow<List<Flashcard>> =
        flashcardDao.getFlashcardsByDeck(deckId)

    fun getFlashcardsToReviewByUser(userId: String): Flow<List<Flashcard>> {
        return flashcardDao.getFlashcardsToReviewByUser(userId, System.currentTimeMillis())
    }

    suspend fun insert(flashcard: Flashcard) {
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
        
        // KIỂM TRA ĐỘ KHÓ: Nếu chọn Hard (quality = 1), đặt thời gian nhắc lại là 8 tiếng
        if (quality == 1) {
            calendar.add(Calendar.HOUR_OF_DAY, 8)
        } else {
            // Các trường hợp khác (Good/Easy) vẫn giữ nguyên logic SM-2 theo ngày
            calendar.add(Calendar.DAY_OF_YEAR, sm2Result.interval)
        }

        val updatedFlashcard = flashcard.copy(
            interval = sm2Result.interval,
            repetition = sm2Result.repetition,
            easinessFactor = sm2Result.easinessFactor,
            nextReviewDate = calendar.timeInMillis
        )
        flashcardDao.updateFlashcard(updatedFlashcard)
    }
    
    suspend fun update(flashcard: Flashcard) {
        flashcardDao.updateFlashcard(flashcard)
    }

    suspend fun delete(flashcard: Flashcard) {
        flashcardDao.deleteFlashcard(flashcard)
    }
}
