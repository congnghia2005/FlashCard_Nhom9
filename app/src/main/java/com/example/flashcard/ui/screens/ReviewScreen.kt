package com.example.flashcard.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcard.ui.FlashcardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    viewModel: FlashcardViewModel,
    onNavigateBack: () -> Unit
) {
    val cardsToReview by viewModel.cardsToReview.collectAsState()
    var currentIndex by remember { mutableIntStateOf(0) }
    var showBack by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reviewing Cards") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (cardsToReview.isEmpty() || currentIndex >= cardsToReview.size) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("All cards reviewed!", style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onNavigateBack) {
                        Text("Go Back")
                    }
                }
            }
        } else {
            val currentCard = cardsToReview[currentIndex]

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Card ${currentIndex + 1} of ${cardsToReview.size}",
                    style = MaterialTheme.typography.labelLarge
                )
                
                Spacer(Modifier.height(32.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clickable { showBack = !showBack },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (showBack) currentCard.back else currentCard.front,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp),
                                modifier = Modifier.padding(16.dp)
                            )
                            
                            IconButton(onClick = { viewModel.speak(if (showBack) currentCard.back else currentCard.front) }) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Speak")
                            }
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))

                if (!showBack) {
                    Button(
                        onClick = { showBack = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Show Answer")
                    }
                } else {
                    Text("How difficult was this?", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (quality in 0..5) {
                            Button(
                                onClick = {
                                    viewModel.updateAfterReview(currentCard, quality)
                                    showBack = false
                                    currentIndex++
                                },
                                contentPadding = PaddingValues(4.dp),
                                modifier = Modifier.weight(1f).padding(2.dp)
                            ) {
                                Text(quality.toString())
                            }
                        }
                    }
                }
            }
        }
    }
}
