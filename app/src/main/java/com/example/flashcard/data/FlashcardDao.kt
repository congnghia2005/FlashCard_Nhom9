package com.example.flashcard.data

import androidx.room.*
import com.example.flashcard.model.Flashcard
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardDao {
    @Query("SELECT * FROM flashcards WHERE ownerId = :userId")
    fun getFlashcardsByUser(userId: String): Flow<List<Flashcard>>

    @Query("SELECT * FROM flashcards WHERE deckId = :deckId")
    fun getFlashcardsByDeck(deckId: Long): Flow<List<Flashcard>>

<<<<<<< HEAD
    @Query("SELECT * FROM flashcards WHERE deckId = :deckId")
    suspend fun getFlashcardsByDeckSync(deckId: Long): List<Flashcard>

=======
>>>>>>> 27d4e2849a9709f1e2be39e4ce2aed2922d414bf
    @Query("SELECT * FROM flashcards WHERE ownerId = :userId AND nextReviewDate <= :currentTime")
    fun getFlashcardsToReviewByUser(userId: String, currentTime: Long): Flow<List<Flashcard>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(flashcard: Flashcard)

<<<<<<< HEAD
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcards(flashcards: List<Flashcard>)

=======
>>>>>>> 27d4e2849a9709f1e2be39e4ce2aed2922d414bf
    @Update
    suspend fun updateFlashcard(flashcard: Flashcard)

    @Delete
    suspend fun deleteFlashcard(flashcard: Flashcard)
}
