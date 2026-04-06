package com.example.flashcard.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flashcards")
data class Flashcard(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val front: String,
    val back: String,
    val ownerId: String, // Username of the creator
    val deckId: Long = 0,
    val imageUri: String? = null,
    
    // SM-2 Algorithm fields
    val interval: Int = 0, // Days until next review
    val repetition: Int = 0, // Number of times reviewed
    val easinessFactor: Float = 2.5f,
    val nextReviewDate: Long = System.currentTimeMillis()
)

data class SM2Result(
    val interval: Int,
    val repetition: Int,
    val easinessFactor: Float
)

object SM2Algorithm {
    fun calculate(quality: Int, previousInterval: Int, previousRepetition: Int, previousEF: Float): SM2Result {
        var interval: Int
        var repetition: Int
        var easinessFactor: Float

        if (quality >= 3) {
            if (previousRepetition == 0) {
                interval = 1
                repetition = 1
            } else if (previousRepetition == 1) {
                interval = 6
                repetition = 2
            } else {
                interval = (previousInterval * previousEF).toInt()
                repetition = previousRepetition + 1
            }
            easinessFactor = previousEF + (0.1f - (5 - quality) * (0.08f + (5 - quality) * 0.02f))
        } else {
            repetition = 0
            interval = 1
            easinessFactor = previousEF
        }

        if (easinessFactor < 1.3f) easinessFactor = 1.3f

        return SM2Result(interval, repetition, easinessFactor)
    }
}
