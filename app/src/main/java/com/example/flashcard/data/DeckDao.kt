package com.example.flashcard.data

import androidx.room.*
import com.example.flashcard.model.Deck
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {
    @Query("SELECT * FROM decks WHERE ownerId = :userId")
    fun getDecksByUser(userId: String): Flow<List<Deck>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeck(deck: Deck)

    @Delete
    suspend fun deleteDeck(deck: Deck)
}
