package com.example.flashcard.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val username: String,
    val passwordHash: String, // Trong thực tế nên hash mật khẩu, ở đây tôi để đơn giản để bạn dễ hình dung
    val displayName: String
)
