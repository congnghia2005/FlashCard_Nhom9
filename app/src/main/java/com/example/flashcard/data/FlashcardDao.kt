package com.example.flashcard.data

import androidx.room.*
import com.example.flashcard.model.Flashcard
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardDao {
    @Query("SELECT * FROM flashcards")
    fun getAllFlashcards(): Flow<List<Flashcard>>

<<<<<<< HEAD
=======
    // Lấy các thẻ có ngày review nhỏ hơn hoặc bằng thời gian truyền vào
>>>>>>> origin/develop
    @Query("SELECT * FROM flashcards WHERE nextReviewDate <= :currentTime")
    fun getFlashcardsToReview(currentTime: Long): Flow<List<Flashcard>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(flashcard: Flashcard)

    @Update
    suspend fun updateFlashcard(flashcard: Flashcard)

    @Delete
    suspend fun deleteFlashcard(flashcard: Flashcard)
}
