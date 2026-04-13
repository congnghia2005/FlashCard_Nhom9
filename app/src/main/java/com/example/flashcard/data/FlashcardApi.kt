package com.example.flashcard.data

<<<<<<< HEAD
import com.example.flashcard.model.Flashcard
import com.example.flashcard.model.User
import retrofit2.http.*

interface FlashcardApi {
    // Thẻ bài
    @GET("flashcards")
    suspend fun getFlashcards(@Query("ownerId") userId: String): List<Flashcard>

    @POST("flashcards")
    suspend fun addFlashcard(@Body flashcard: Flashcard): Flashcard

    // Đồng bộ danh sách thẻ
    @POST("flashcards/sync")
    suspend fun syncFlashcards(@Body flashcards: List<Flashcard>): ApiResponse

    // Người dùng (Để đăng nhập được trên nhiều máy)
    @GET("users")
    suspend fun getUsers(): List<User>

    @POST("users")
    suspend fun registerUser(@Body user: User): User

    // Chia sẻ bộ thẻ
    @GET("decks/shared/{shareCode}")
    suspend fun getSharedDeck(@Path("shareCode") shareCode: String): SharedDeckResponse
}

data class SharedDeckResponse(val deckName: String, val flashcards: List<Flashcard>)
data class ApiResponse(val success: Boolean, val message: String)
=======
import retrofit2.http.GET

interface FlashcardApi {

    @GET("api.php?amount=10")
    suspend fun getFlashcards(): ApiResponse
}
>>>>>>> 27d4e2849a9709f1e2be39e4ce2aed2922d414bf
