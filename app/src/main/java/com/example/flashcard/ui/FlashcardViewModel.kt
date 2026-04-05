package com.example.flashcard.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcard.data.FlashcardRepository
import com.example.flashcard.model.Flashcard
import com.example.flashcard.util.TtsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FlashcardViewModel @Inject constructor(
    application: Application,
    private val repository: FlashcardRepository
) : AndroidViewModel(application) {

    val allFlashcards: StateFlow<List<Flashcard>> = repository.allFlashcards.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val cardsToReview: StateFlow<List<Flashcard>> = repository.getFlashcardsToReview().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val ttsManager: TtsManager = TtsManager(application)

    fun addFlashcard(front: String, back: String) {
        viewModelScope.launch {
            repository.addFlashcard(front, back)
        }
    }

    fun updateAfterReview(flashcard: Flashcard, quality: Int) {
        viewModelScope.launch {
            repository.updateFlashcardAfterReview(flashcard, quality)
        }
    }

    fun speak(text: String) {
        ttsManager.speak(text)
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }
}
