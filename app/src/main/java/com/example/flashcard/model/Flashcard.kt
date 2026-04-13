package com.example.flashcard.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "flashcards")
data class Flashcard(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val uuid: String = UUID.randomUUID().toString(),
    val front: String = "",
    val back: String = "",
    val ownerId: String = "",
    val deckId: Long = 0,
    val deckUuid: String = "", // THÊM DÒNG NÀY ĐỂ ĐỒNG BỘ CHÍNH XÁC
    val imageUri: String? = null,
    val interval: Int = 0,
    val repetition: Int = 0,
    val easinessFactor: Float = 2.5f,
    val nextReviewDate: Long = System.currentTimeMillis(),
    val lastUpdated: Long = System.currentTimeMillis()
)

data class SM2Result(val interval: Int, val repetition: Int, val easinessFactor: Float)

object SM2Algorithm {
    fun calculate(quality: Int, previousInterval: Int, previousRepetition: Int, previousEF: Float): SM2Result {
        var interval: Int; var repetition: Int; var easinessFactor: Float
        if (quality >= 3) {
            if (previousRepetition == 0) { interval = 1; repetition = 1 }
            else if (previousRepetition == 1) { interval = 6; repetition = 2 }
            else { interval = (previousInterval * previousEF).toInt(); repetition = previousRepetition + 1 }
            easinessFactor = previousEF + (0.1f - (5 - quality) * (0.08f + (5 - quality) * 0.02f))
        } else { repetition = 0; interval = 1; easinessFactor = previousEF }
        if (easinessFactor < 1.3f) easinessFactor = 1.3f
        return SM2Result(interval, repetition, easinessFactor)
    }
}
