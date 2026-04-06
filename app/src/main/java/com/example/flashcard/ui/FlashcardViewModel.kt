package com.example.flashcard.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcard.data.AppDatabase
import com.example.flashcard.data.FlashcardRepository
import com.example.flashcard.data.UserDao
import com.example.flashcard.model.Deck
import com.example.flashcard.model.Flashcard
import com.example.flashcard.model.User
import com.example.flashcard.util.TtsManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FlashcardViewModel(application: Application) : AndroidViewModel(application) {
    private val database: AppDatabase = AppDatabase.getDatabase(application)
    private val repository: FlashcardRepository = FlashcardRepository(database.flashcardDao(), database.deckDao())
    private val userDao: UserDao = database.userDao()
    private val ttsManager: TtsManager = TtsManager(application)
    
    // SharedPreferences to persist login
    private val prefs = application.getSharedPreferences("flashcard_prefs", Context.MODE_PRIVATE)

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    @OptIn(ExperimentalCoroutinesApi::class)
    val userDecks: StateFlow<List<Deck>> = _currentUser
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList())
            else repository.getDecksByUser(user.username)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val allFlashcards: StateFlow<List<Flashcard>> = _currentUser
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList())
            else repository.getFlashcardsByUser(user.username)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val cardsToReview: StateFlow<List<Flashcard>> = _currentUser
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList())
            else repository.getFlashcardsToReviewByUser(user.username)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Tự động kiểm tra xem có ai đã đăng nhập chưa khi khởi tạo
        checkLastLogin()
    }

    private fun checkLastLogin() {
        val lastUser = prefs.getString("last_username", null)
        if (lastUser != null) {
            viewModelScope.launch {
                val user = userDao.getUserByUsername(lastUser)
                if (user != null) {
                    _currentUser.value = user
                }
            }
        }
    }

    fun login(username: String, password: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val user = userDao.getUserByUsername(username)
                if (user != null && user.passwordHash == password) {
                    _currentUser.value = user
                    // Lưu lại username để lần sau tự động đăng nhập
                    prefs.edit().putString("last_username", username).apply()
                    onResult(true, "Success")
                } else onResult(false, "Invalid credentials")
            } catch (e: Exception) { onResult(false, e.message ?: "Error") }
        }
    }

    fun register(username: String, password: String, displayName: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val existing = userDao.getUserByUsername(username)
                if (existing != null) onResult(false, "Exists")
                else {
                    userDao.registerUser(User(username, password, displayName))
                    onResult(true, "Success")
                }
            } catch (e: Exception) { onResult(false, e.message ?: "Error") }
        }
    }

    fun logout() {
        _currentUser.value = null
        // Xóa lưu vết đăng nhập
        prefs.edit().remove("last_username").apply()
    }

    fun createDeck(name: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.insertDeck(Deck(name = name, ownerId = user.username))
        }
    }

    fun deleteDeck(deck: Deck) {
        viewModelScope.launch { repository.deleteDeck(deck) }
    }

    fun addFlashcard(front: String, back: String, deckId: Long, imageUri: String?) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.insert(Flashcard(front = front, back = back, ownerId = user.username, deckId = deckId, imageUri = imageUri))
        }
    }

    fun updateFlashcard(flashcard: Flashcard) { viewModelScope.launch { repository.update(flashcard) } }
    fun deleteFlashcard(flashcard: Flashcard) { viewModelScope.launch { repository.delete(flashcard) } }
    fun updateAfterReview(flashcard: Flashcard, quality: Int) { viewModelScope.launch { repository.updateFlashcardAfterReview(flashcard, quality) } }
    fun speak(text: String) { ttsManager.speak(text) }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }
}
