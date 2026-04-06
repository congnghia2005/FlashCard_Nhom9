package com.example.flashcard.data

import retrofit2.http.GET

interface FlashcardApi {

    @GET("api.php?amount=10")
    suspend fun getFlashcards(): ApiResponse
}