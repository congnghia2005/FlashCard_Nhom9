package com.example.flashcard.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.flashcard.ui.FlashcardViewModel
import kotlin.random.Random

enum class ReviewType {
    TYPE_ANSWER, FLIP_CARD
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    viewModel: FlashcardViewModel,
    onNavigateBack: () -> Unit
) {
    // SỬ DỤNG remember ĐỂ GIỮ DANH SÁCH THẺ TRONG SUỐT PHIÊN HỌC
    val cardsFromViewModel by viewModel.cardsToReview.collectAsState()
    val sessionCards = remember(cardsFromViewModel) { cardsFromViewModel }
    
    var currentIndex by remember { mutableIntStateOf(0) }
    var showBack by remember { mutableStateOf(false) }
    var userInput by remember { mutableStateOf("") }
    
    var currentReviewType by remember { mutableStateOf(ReviewType.FLIP_CARD) }

    LaunchedEffect(currentIndex) {
        showBack = false
        userInput = ""
        currentReviewType = if (Random.nextBoolean()) ReviewType.TYPE_ANSWER else ReviewType.FLIP_CARD
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Review Mode", 
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .padding(8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        if (sessionCards.isEmpty() || currentIndex >= sessionCards.size) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.WorkspacePremium,
                            null,
                            modifier = Modifier.size(50.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                    Text(
                        "Mission Accomplished!",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "All cards have been reviewed for today.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(40.dp))
                    Button(
                        onClick = onNavigateBack,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Text("Back to Dashboard", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            val currentCard = sessionCards[currentIndex]
            val progress = (currentIndex + 1).toFloat() / sessionCards.size

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Progress and Indicator
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "Card ${currentIndex + 1}/${sessionCards.size}",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = if (currentReviewType == ReviewType.TYPE_ANSWER) "Mode: Type Answer" else "Mode: Flip Card",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
                    )
                }
                
                Spacer(Modifier.height(32.dp))

                // Premium Card Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .shadow(24.dp, RoundedCornerShape(40.dp), ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                        .background(
                            brush = if (showBack) {
                                Brush.verticalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.secondary,
                                        MaterialTheme.colorScheme.tertiary
                                    )
                                )
                            } else {
                                Brush.verticalGradient(
                                    listOf(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.surface)
                                )
                            },
                            shape = RoundedCornerShape(40.dp)
                        )
                        .clickable { if (!showBack) showBack = true }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Image if available
                        if (currentCard.imageUri != null) {
                            AsyncImage(
                                model = currentCard.imageUri,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .shadow(8.dp, RoundedCornerShape(24.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.height(24.dp))
                        }

                        // Content
                        if (showBack) {
                            if (currentReviewType == ReviewType.TYPE_ANSWER) {
                                val comparisonResult = compareAnswers(userInput, currentCard.back)
                                Text(
                                    text = comparisonResult,
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.displaySmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                if (userInput.isNotBlank() && userInput.lowercase() != currentCard.back.lowercase()) {
                                    Spacer(Modifier.height(12.dp))
                                    Text(
                                        text = "Your: $userInput",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                            } else {
                                Text(
                                    text = currentCard.back,
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.displaySmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        } else {
                            Text(
                                text = currentCard.front,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.displaySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Spacer(Modifier.height(32.dp))

                        FilledTonalIconButton(
                            onClick = { viewModel.speak(if (showBack) currentCard.back else currentCard.front) },
                            modifier = Modifier.size(64.dp),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = if (showBack) Color.White.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                contentColor = if (showBack) Color.White else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.Default.VolumeUp, null, modifier = Modifier.size(28.dp))
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))

                // Action Area
                AnimatedContent(targetState = showBack, label = "") { backShown ->
                    if (!backShown) {
                        if (currentReviewType == ReviewType.TYPE_ANSWER) {
                            Column {
                                OutlinedTextField(
                                    value = userInput,
                                    onValueChange = { userInput = it },
                                    placeholder = { Text("Write your answer...") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = MaterialTheme.colorScheme.primary,
                                        unfocusedTextColor = MaterialTheme.colorScheme.primary,
                                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                        unfocusedBorderColor = Color.Transparent,
                                        focusedBorderColor = MaterialTheme.colorScheme.primary
                                    ),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                                )
                                Spacer(Modifier.height(16.dp))
                                Button(
                                    onClick = { showBack = true },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(64.dp),
                                    shape = RoundedCornerShape(20.dp),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                                ) {
                                    Text("Check Answer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            Button(
                                onClick = { showBack = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(64.dp),
                                shape = RoundedCornerShape(20.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                            ) {
                                Text("Flip Card", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            DifficultyBtn("Hard", 1, Color(0xFFEF4444), Modifier.weight(1f)) {
                                viewModel.updateAfterReview(currentCard, 1)
                                currentIndex++
                            }
                            DifficultyBtn("Good", 3, Color(0xFFF59E0B), Modifier.weight(1f)) {
                                viewModel.updateAfterReview(currentCard, 3)
                                currentIndex++
                            }
                            DifficultyBtn("Easy", 5, Color(0xFF10B981), Modifier.weight(1f)) {
                                viewModel.updateAfterReview(currentCard, 5)
                                currentIndex++
                            }
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun DifficultyBtn(label: String, quality: Int, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(60.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Text(label, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

fun compareAnswers(userInput: String, correctAnswer: String): AnnotatedString {
    return buildAnnotatedString {
        val userChars = userInput.trim().lowercase()
        val correctChars = correctAnswer.trim()
        val correctCharsLower = correctChars.lowercase()
        for (i in correctChars.indices) {
            val char = correctChars[i]
            if (i < userChars.length) {
                if (userChars[i] == correctCharsLower[i]) append(char)
                else withStyle(style = SpanStyle(color = Color.White, background = Color.Red.copy(alpha = 0.5f))) { append(char) }
            } else {
                withStyle(style = SpanStyle(color = Color.White.copy(alpha = 0.6f))) { append(char) }
            }
        }
    }
}
