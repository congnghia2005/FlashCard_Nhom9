package com.example.flashcard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.flashcard.ui.FlashcardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFlashcardScreen(
    viewModel: FlashcardViewModel,
    onNavigateBack: () -> Unit
) {
    var front by remember { mutableStateOf("") }
    var back by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Flashcard") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = front,
                onValueChange = { front = it },
                label = { Text("Front (Question)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = back,
                onValueChange = { back = it },
                label = { Text("Back (Answer)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    if (front.isNotBlank() && back.isNotBlank()) {
                        viewModel.addFlashcard(front, back)
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = front.isNotBlank() && back.isNotBlank()
            ) {
                Text("Save Flashcard")
            }
        }
    }
}
