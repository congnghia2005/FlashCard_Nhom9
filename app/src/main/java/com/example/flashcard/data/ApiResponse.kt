package com.example.flashcard.data

data class ApiResponse(
    val results: List<FlashcardDto>
)

data class FlashcardDto(
    val question: String,
    val correct_answer: String
)