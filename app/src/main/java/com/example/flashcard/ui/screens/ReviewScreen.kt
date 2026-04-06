package com.example.flashcard.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcard.ui.FlashcardViewModel
import com.example.flashcard.ui.components.FlipCard // Import component Mai vừa tạo

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
                title = { Text("Reviewing Cards", fontWeight = FontWeight.Bold) },
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
                    Spacer(Modifier.height(24.dp))
                    Button(onClick = onNavigateBack, shape = MaterialTheme.shapes.medium) {
                        Text("Go Back Home")
                    }
                }
            }
        } else {
            val currentCard = cardsToReview[currentIndex]

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Thanh tiến trình nhỏ gọn
                Text(
                    text = "CARD ${currentIndex + 1} OF ${cardsToReview.size}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )

                LinearProgressIndicator(
                    progress = (currentIndex + 1).toFloat() / cardsToReview.size,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(Modifier.height(16.dp))

                //FLIPCARD 3D
                FlipCard(
                    frontContent = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = currentCard.front,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.headlineLarge,
                                modifier = Modifier.padding(16.dp)
                            )
                            IconButton(onClick = { viewModel.speak(currentCard.front) }) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Speak Front", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    },
                    backContent = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = currentCard.back,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(16.dp)
                            )
                            IconButton(onClick = { viewModel.speak(currentCard.back) }) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Speak Back", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    },
                    modifier = Modifier.weight(1f) // Chiếm không gian chính giữa màn hình
                )

                Spacer(Modifier.height(32.dp))


                AnimatedVisibility(
                    visible = !showBack, // Nút này chỉ hiện khi chưa lật thẻ
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Button(
                        onClick = { showBack = true }, // nhấn nút này showBack thành true
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Text("TAP CARD TO SEE ANSWER", letterSpacing = 1.sp)
                    }
                }

                AnimatedVisibility(
                    visible = showBack,
                    enter = fadeIn() + expandVertically()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("How difficult was this?", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Các nút đánh giá độ khó SM-2
                            for (quality in 0..5) {
                                Button(
                                    onClick = {
                                        viewModel.updateAfterReview(currentCard, quality)
                                        showBack = false
                                        currentIndex++
                                    },
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (quality >= 4) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                                    )
                                ) {
                                    Text(quality.toString(), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}