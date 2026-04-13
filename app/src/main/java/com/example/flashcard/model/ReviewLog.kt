package com.example.flashcard.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "review_logs")
data class ReviewLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cardId: Long,
    val userId: String,
    val reviewDate: Long = System.currentTimeMillis()
)
