package com.example.flashcard.model

import androidx.room.Entity
import androidx.room.PrimaryKey
<<<<<<< HEAD
import java.util.UUID
=======
>>>>>>> 27d4e2849a9709f1e2be39e4ce2aed2922d414bf

@Entity(tableName = "decks")
data class Deck(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
<<<<<<< HEAD
    val uuid: String = UUID.randomUUID().toString(), // Định danh duy nhất toàn cầu
    val name: String = "",
    val ownerId: String = "" // Email của người tạo
=======
    val name: String,
    val ownerId: String // Username of the creator
>>>>>>> 27d4e2849a9709f1e2be39e4ce2aed2922d414bf
)
