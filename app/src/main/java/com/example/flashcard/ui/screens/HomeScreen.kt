package com.example.flashcard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.flashcard.ui.FlashcardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: FlashcardViewModel,
    onNavigateToReview: () -> Unit,
    onNavigateToAdd: () -> Unit
) {
    val allCards by viewModel.allFlashcards.collectAsState()
    val cardsToReview by viewModel.cardsToReview.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("My Flashcards") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAdd) {
                Icon(Icons.Default.Add, contentDescription = "Add Flashcard")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Due for review:", style = MaterialTheme.typography.titleMedium)
                        Text("${cardsToReview.size} cards", style = MaterialTheme.typography.headlineMedium)
                    }
                    Button(
                        onClick = onNavigateToReview,
                        enabled = cardsToReview.isNotEmpty()
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Start Review")
                    }
                }
            }

            Text(
                "All Cards (${allCards.size})",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.titleSmall
            )

            LazyColumn {
                items(allCards) { card ->
                    ListItem(
                        headlineContent = { Text(card.front) },
                        supportingContent = { Text(card.back) }
                    )
                }
            }
        }
    }
}
