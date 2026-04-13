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
import androidx.compose.ui.draw.rotate
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
import com.example.flashcard.model.Flashcard
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
    // CHỤP ẢNH DỮ LIỆU (SNAPSHOT): Chỉ lấy danh sách thẻ 1 lần duy nhất khi vào màn hình
    val rawCards by viewModel.cardsToReview.collectAsState()
    val sessionCards = remember { rawCards.shuffled() } // Xáo trộn và giữ cố định
    
    var currentIndex by remember { mutableIntStateOf(0) }
    var showBack by remember { mutableStateOf(false) }
    var userInput by remember { mutableStateOf("") }
    var currentReviewType by remember { mutableStateOf(ReviewType.FLIP_CARD) }

    // Reset trạng thái cho mỗi thẻ mới
    LaunchedEffect(currentIndex) {
        showBack = false
        userInput = ""
        currentReviewType = if (Random.nextBoolean()) ReviewType.TYPE_ANSWER else ReviewType.FLIP_CARD
    }

    Scaffold(
        containerColor = Color(0xFF0A0E1A),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Ôn tập", fontWeight = FontWeight.ExtraBold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.background(Color(0xFF161B2C), CircleShape)) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        if (sessionCards.isEmpty() || currentIndex >= sessionCards.size) {
            ReviewCompleteView(onNavigateBack)
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
                StudyProgressHeader(currentIndex, sessionCards.size, progress, currentReviewType)
                
                Spacer(Modifier.height(32.dp))

                // GỌI HÀM VỚI COLUMN SCOPE
                FlashcardPremiumView(
                    card = currentCard,
                    showBack = showBack,
                    reviewType = currentReviewType,
                    userInput = userInput,
                    onCardClick = { if (!showBack) showBack = true },
                    onSpeak = { viewModel.speak(it) }
                )

                Spacer(Modifier.height(32.dp))

                ActionArea(
                    showBack = showBack,
                    reviewType = currentReviewType,
                    userInput = userInput,
                    onUserInputChange = { userInput = it },
                    onShowBack = { showBack = true },
                    onRate = { quality ->
                        viewModel.updateAfterReview(currentCard, quality)
                        currentIndex++
                    }
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun StudyProgressHeader(index: Int, total: Int, progress: Float, type: ReviewType) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text("Thẻ ${index + 1}/$total", fontWeight = FontWeight.Bold, color = Color(0xFF7C3AED))
                Text(if (type == ReviewType.TYPE_ANSWER) "Chế độ: Viết" else "Chế độ: Lật thẻ", fontSize = 12.sp, color = Color.Gray)
            }
            Text("${(progress * 100).toInt()}%", fontWeight = FontWeight.Bold, color = Color.White)
        }
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
            color = Color(0xFF7C3AED),
            trackColor = Color(0xFF161B2C)
        )
    }
}

// THÊM ColumnScope. ĐỂ SỬ DỤNG ĐƯỢC weight(1f)
@Composable
fun ColumnScope.FlashcardPremiumView(
    card: Flashcard,
    showBack: Boolean,
    reviewType: ReviewType,
    userInput: String,
    onCardClick: () -> Unit,
    onSpeak: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f) // Bây giờ weight đã hợp lệ vì nằm trong ColumnScope
            .shadow(30.dp, RoundedCornerShape(40.dp), spotColor = Color(0xFF7C3AED).copy(alpha = 0.5f))
            .background(
                if (showBack) Brush.verticalGradient(listOf(Color(0xFF7C3AED), Color(0xFF06B6D4)))
                else Brush.verticalGradient(listOf(Color(0xFF161B2C), Color(0xFF161B2C))),
                shape = RoundedCornerShape(40.dp)
            )
            .clickable { onCardClick() }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (card.imageUri != null) {
                AsyncImage(
                    model = card.imageUri,
                    contentDescription = null,
                    modifier = Modifier.height(180.dp).clip(RoundedCornerShape(24.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(24.dp))
            }

            if (showBack) {
                val displayText = if (reviewType == ReviewType.TYPE_ANSWER) compareAnswers(userInput, card.back) else AnnotatedString(card.back)
                Text(displayText, textAlign = TextAlign.Center, style = MaterialTheme.typography.displaySmall, color = Color.White, fontWeight = FontWeight.Bold)
            } else {
                Text(card.front, textAlign = TextAlign.Center, style = MaterialTheme.typography.displaySmall, color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(40.dp))
            IconButton(onClick = { onSpeak(if (showBack) card.back else card.front) }, modifier = Modifier.background(Color.White.copy(alpha = 0.1f), CircleShape)) {
                Icon(Icons.Default.VolumeUp, null, tint = Color.White)
            }
        }
    }
}

@Composable
fun ActionArea(
    showBack: Boolean,
    reviewType: ReviewType,
    userInput: String,
    onUserInputChange: (String) -> Unit,
    onShowBack: () -> Unit,
    onRate: (Int) -> Unit
) {
    AnimatedContent(targetState = showBack, label = "") { backShown ->
        if (!backShown) {
            if (reviewType == ReviewType.TYPE_ANSWER) {
                Column {
                    OutlinedTextField(
                        value = userInput,
                        onValueChange = onUserInputChange,
                        placeholder = { Text("Nhập câu trả lời...", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color(0xFF7C3AED)),
                        shape = RoundedCornerShape(16.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onShowBack, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(Color(0xFF7C3AED)), shape = RoundedCornerShape(16.dp)) {
                        Text("Kiểm tra", fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Button(onClick = onShowBack, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(Color(0xFF7C3AED)), shape = RoundedCornerShape(16.dp)) {
                    Text("Lật thẻ", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DifficultyBtn("Khó", 1, Color(0xFFEF4444), Modifier.weight(1f)) { onRate(1) }
                DifficultyBtn("Tốt", 3, Color(0xFFF59E0B), Modifier.weight(1f)) { onRate(3) }
                DifficultyBtn("Dễ", 5, Color(0xFF10B981), Modifier.weight(1f)) { onRate(5) }
            }
        }
    }
}

@Composable
fun ReviewCompleteView(onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(100.dp), tint = Color(0xFF10B981))
            Spacer(Modifier.height(24.dp))
            Text("Tuyệt vời!", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Bạn đã hoàn thành các thẻ của hôm nay.", textAlign = TextAlign.Center, color = Color.Gray)
            Spacer(Modifier.height(40.dp))
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(Color(0xFF7C3AED)), shape = RoundedCornerShape(16.dp)) {
                Text("Quay lại màn hình chính", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun DifficultyBtn(label: String, quality: Int, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = modifier.height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = color), shape = RoundedCornerShape(16.dp)) {
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
