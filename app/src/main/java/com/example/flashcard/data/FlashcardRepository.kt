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
    suspend fun insertDeck(deck: Deck): Long = deckDao.insertDeck(deck)
    suspend fun deleteDeck(deck: Deck) = deckDao.deleteDeck(deck)

    // Flashcard operations
    fun getFlashcardsByUser(userId: String): Flow<List<Flashcard>> = flashcardDao.getFlashcardsByUser(userId)
    fun getFlashcardsByDeck(deckId: Long): Flow<List<Flashcard>> = flashcardDao.getFlashcardsByDeck(deckId)
    suspend fun getFlashcardsByDeckSync(deckId: Long): List<Flashcard> = flashcardDao.getFlashcardsByDeckSync(deckId)

    fun getFlashcardsToReviewByUser(userId: String): Flow<List<Flashcard>> {
        return flashcardDao.getFlashcardsToReviewByUser(userId, System.currentTimeMillis())
    }

    suspend fun insert(flashcard: Flashcard) {
        flashcardDao.insertFlashcard(flashcard)
    }

    suspend fun insertFlashcards(flashcards: List<Flashcard>) {
        flashcardDao.insertFlashcards(flashcards)
    }

    suspend fun updateFlashcardAfterReview(flashcard: Flashcard, quality: Int) {
        // Logic SM-2
        val sm2Result = SM2Algorithm.calculate(
            quality = quality,
            previousInterval = flashcard.interval,
            previousRepetition = flashcard.repetition,
            previousEF = flashcard.easinessFactor
        )

        val calendar = Calendar.getInstance()
        if (quality == 1) {
            calendar.add(Calendar.HOUR_OF_DAY, 8)
        } else {
            calendar.add(Calendar.DAY_OF_YEAR, sm2Result.interval)
        }

        val updatedFlashcard = flashcard.copy(
            interval = sm2Result.interval,
            repetition = sm2Result.repetition,
            easinessFactor = sm2Result.easinessFactor,
            nextReviewDate = calendar.timeInMillis,
            lastUpdated = System.currentTimeMillis()
        )
        flashcardDao.updateFlashcard(updatedFlashcard)
    }
    
    suspend fun update(flashcard: Flashcard) = flashcardDao.updateFlashcard(flashcard)
    suspend fun delete(flashcard: Flashcard) = flashcardDao.deleteFlashcard(flashcard)
}
