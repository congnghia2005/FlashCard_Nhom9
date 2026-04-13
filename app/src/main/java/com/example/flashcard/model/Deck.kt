package com.example.flashcard.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "decks")
data class Deck(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val uuid: String = UUID.randomUUID().toString(), // Định danh duy nhất toàn cầu
    val name: String = "",
    val ownerId: String = "" // Email của người tạo
)
