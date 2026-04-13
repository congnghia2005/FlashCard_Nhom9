package com.example.flashcard.data

import androidx.room.*
import com.example.flashcard.model.Deck
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {
    @Query("SELECT * FROM decks WHERE ownerId = :userId")
    fun getDecksByUser(userId: String): Flow<List<Deck>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
<<<<<<< HEAD
    suspend fun insertDeck(deck: Deck): Long
=======
    suspend fun insertDeck(deck: Deck)
>>>>>>> 27d4e2849a9709f1e2be39e4ce2aed2922d414bf

    @Delete
    suspend fun deleteDeck(deck: Deck)
}
