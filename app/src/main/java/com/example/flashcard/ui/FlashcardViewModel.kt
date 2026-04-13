package com.example.flashcard.ui

import android.app.Application
import android.content.Context
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcard.data.AppDatabase
import com.example.flashcard.data.FlashcardRepository
import com.example.flashcard.data.UserDao
import com.example.flashcard.model.Deck
import com.example.flashcard.model.Flashcard
import com.example.flashcard.model.User
import com.example.flashcard.util.TtsManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class SuggestedDeck(
    val name: String,
    val description: String,
    val icon: String,
    val shareCode: String
)

class FlashcardViewModel(application: Application) : AndroidViewModel(application) {
    private val database: AppDatabase = AppDatabase.getDatabase(application)
    private val repository: FlashcardRepository = FlashcardRepository(database.flashcardDao(), database.deckDao())
    private val userDao: UserDao = database.userDao()
    private val ttsManager: TtsManager = TtsManager(application)
    private val gson = Gson()
    
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val prefs = application.getSharedPreferences("flashcard_prefs", Context.MODE_PRIVATE)

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing

    val suggestedDecks = listOf(
        SuggestedDeck("Từ Vựng TOEIC", "Từ vựng công sở & hợp đồng", "💼", "eyJuYW1lIjoiVOG7qyBW4buxbmcgVE9FSUMiLCJjYXJkcyI6W3siZiI6IlByb3Bvc2FsIiwiYiI6IsSQ4buBIHh14bqldCJ9LHsiZiI6Ik5lZ290aWF0ZSIsImIiOiIsIMSQw6BtIHBow6FuIn1dfQ=="),
        SuggestedDeck("Lập Trình Python", "Cấu trúc dữ liệu cơ bản", "🐍", "eyJuYW1lIjoiTOG6rXAgVHLDrG5oIFB5dGhvbiIsImNhcmRzIjpbeyJmIjoiTGlzdCIsImIiOiJEYW5oIHPDoWNoIn0seyJmIjoiRGljdGlvbmFyeSIsImIiOiJU4burIMSRaeG7g24ifV19"),
        SuggestedDeck("Tiếng Hàn Giao Tiếp", "Câu chào hỏi cơ bản", "🇰🇷", "eyJuYW1lIjoiVGnhur9uZyBIw6BuIEdpYW8gVaeG6vHAiLCJjYXJkcyI6W3siZiI6IkFubnllb25nIiwiYiI6IlhpbiBjaMOgbyJ9LHsiZiI6IkthbXNhaGFtbmlkYSIsImIiOiJD4bqjbSDGoW4ifV19")
    )

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
        auth.currentUser?.let { firebaseUser ->
            _currentUser.value = User(firebaseUser.email ?: "", "", firebaseUser.displayName ?: "Người học")
        }
    }

    fun login(email: String, password: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                val firebaseUser = auth.currentUser
                val user = User(email, "", firebaseUser?.displayName ?: "Người học")
                _currentUser.value = user
                userDao.registerUser(user) 
                syncData()
                onResult(true, "Thành công")
            } catch (e: Exception) {
                onResult(false, "Lỗi: ${e.localizedMessage}")
            }
        }
    }

    fun register(email: String, password: String, displayName: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(displayName).build()
                result.user?.updateProfile(profileUpdates)?.await()
                val user = User(email, "", displayName)
                _currentUser.value = user
                userDao.registerUser(user)
                onResult(true, "Đăng ký thành công")
            } catch (e: Exception) {
                onResult(false, "Lỗi: ${e.localizedMessage}")
            }
        }
    }

    fun syncData() {
        val userEmail = auth.currentUser?.email ?: return
        viewModelScope.launch {
            _isSyncing.value = true
            try {
                // 1. TẢI DỮ LIỆU TỪ FIREBASE VỀ MÁY
                val deckSnapshot = db.collection("users").document(userEmail).collection("decks").get().await()
                val remoteDecks = deckSnapshot.toObjects(Deck::class.java)
                remoteDecks.forEach { repository.insertDeck(it) }

                val cardSnapshot = db.collection("users").document(userEmail).collection("cards").get().await()
                val remoteCards = cardSnapshot.toObjects(Flashcard::class.java)
                repository.insertFlashcards(remoteCards)

                // 2. ĐẨY DỮ LIỆU TỪ MÁY LÊN FIREBASE (HỢP NHẤT)
                val localDecks = userDecks.value
                val localCards = allFlashcards.value

                if (localDecks.isNotEmpty() || localCards.isNotEmpty()) {
                    val batch = db.batch()
                    localDecks.forEach { deck ->
                        batch.set(db.collection("users").document(userEmail).collection("decks").document(deck.uuid), deck)
                    }
                    localCards.forEach { card ->
                        batch.set(db.collection("users").document(userEmail).collection("cards").document(card.uuid), card)
                    }
                    batch.commit().await()
                }
                
                Toast.makeText(getApplication(), "Đồng bộ thành công!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("SYNC_ERROR", "Error: ${e.message}")
            } finally {
                _isSyncing.value = false
            }
        }
    }

    fun logout() {
        auth.signOut()
        _currentUser.value = null
    }

    fun deleteFlashcard(flashcard: Flashcard) {
        val userEmail = auth.currentUser?.email
        viewModelScope.launch { 
            if (userEmail != null) {
                db.collection("users").document(userEmail).collection("cards").document(flashcard.uuid).delete()
            }
            repository.delete(flashcard)
        } 
    }

    fun deleteDeck(deck: Deck) {
        val userEmail = auth.currentUser?.email
        viewModelScope.launch { 
            if (userEmail != null) {
                db.collection("users").document(userEmail).collection("decks").document(deck.uuid).delete()
                val cardsInDeck = allFlashcards.value.filter { it.deckId == deck.id || it.deckUuid == deck.uuid }
                cardsInDeck.forEach { card ->
                    db.collection("users").document(userEmail).collection("cards").document(card.uuid).delete()
                }
            }
            repository.deleteDeck(deck)
        }
    }

    fun createDeck(name: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch { 
            repository.insertDeck(Deck(name = name, ownerId = user.username))
            syncData() 
        }
    }

    fun addFlashcard(front: String, back: String, deckId: Long, imageUri: String?) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            // Tìm UUID của Deck tương ứng để gán vào thẻ
            val parentDeck = userDecks.value.find { it.id == deckId }
            val card = Flashcard(
                front = front, 
                back = back, 
                ownerId = user.username, 
                deckId = deckId, 
                deckUuid = parentDeck?.uuid ?: "",
                imageUri = imageUri
            )
            repository.insert(card)
            syncData()
        }
    }

    fun generateShareCode(deck: Deck, onResult: (String) -> Unit) {
        viewModelScope.launch {
            val cards = repository.getFlashcardsByDeckSync(deck.id)
            val shareData = mapOf("name" to deck.name, "cards" to cards.map { mapOf("f" to it.front, "b" to it.back) })
            val json = gson.toJson(shareData)
            val code = Base64.encodeToString(json.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
            onResult(code)
        }
    }

    fun importDeck(shareCode: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            _isSyncing.value = true
            try {
                val json = String(Base64.decode(shareCode, Base64.DEFAULT), Charsets.UTF_8)
                val type = object : TypeToken<Map<String, Any>>() {}.type
                val data: Map<String, Any> = gson.fromJson(json, type)
                val deckName = data["name"].toString()

                if (userDecks.value.any { it.name.trim().equals(deckName.trim(), ignoreCase = true) }) {
                    Toast.makeText(getApplication(), "Bạn đã có bộ thẻ này rồi!", Toast.LENGTH_SHORT).show()
                    _isSyncing.value = false
                    return@launch
                }

                val cardsRaw = data["cards"] as? List<*>
                val newDeck = Deck(name = deckName, ownerId = user.username)
                val newDeckId = repository.insertDeck(newDeck)

                val newCards = cardsRaw?.map {
                    val cardMap = it as? Map<*, *>
                    Flashcard(
                        front = cardMap?.get("f")?.toString() ?: "", 
                        back = cardMap?.get("b")?.toString() ?: "", 
                        ownerId = user.username, 
                        deckId = newDeckId,
                        deckUuid = newDeck.uuid
                    )
                } ?: emptyList()
                
                repository.insertFlashcards(newCards)
                syncData()
                Toast.makeText(getApplication(), "Đã nhập: $deckName", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(getApplication(), "Mã không hợp lệ!", Toast.LENGTH_SHORT).show()
            } finally {
                _isSyncing.value = false
            }
        }
    }

    fun updateFlashcard(flashcard: Flashcard) { viewModelScope.launch { repository.update(flashcard); syncData() } }
    fun updateAfterReview(flashcard: Flashcard, quality: Int) { viewModelScope.launch { repository.updateFlashcardAfterReview(flashcard, quality); syncData() } }
    fun speak(text: String) { ttsManager.speak(text) }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }
}
