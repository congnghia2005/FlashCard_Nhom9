package com.example.flashcard.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcard.model.Deck
import com.example.flashcard.ui.FlashcardViewModel
import com.example.flashcard.ui.SuggestedDeck
import java.util.*

// Bảng màu 1000$ Premium
val DeepDark = Color(0xFF0A0E1A)
val CardSurface = Color(0xFF161B2C)
val PrimaryNeon = Color(0xFF7C3AED)
val AccentCyan = Color(0xFF06B6D4)
val TextGray = Color(0xFF94A3B8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: FlashcardViewModel,
    onNavigateToReview: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToDeck: (Long) -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val decks by viewModel.userDecks.collectAsState()
    val cardsToReview by viewModel.cardsToReview.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    
    var selectedTab by remember { mutableIntStateOf(0) }
    var fabExpanded by remember { mutableStateOf(false) }

    var showCreateDeckDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }
    var shareCodeInput by remember { mutableStateOf("") }
    var generatedCode by remember { mutableStateOf("") }

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    // LỌC THÔNG MINH: Chỉ hiện những bộ thẻ người dùng CHƯA CÓ
    val filteredSuggestions = remember(decks) {
        viewModel.suggestedDecks.filter { suggestion ->
            decks.none { it.name.trim().equals(suggestion.name.trim(), ignoreCase = true) }
        }
    }

    if (showCreateDeckDialog) {
        var newDeckNameState by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCreateDeckDialog = false },
            containerColor = CardSurface,
            title = { Text("Thư mục mới", color = Color.White, fontWeight = FontWeight.ExtraBold) },
            text = {
                OutlinedTextField(
                    value = newDeckNameState,
                    onValueChange = { newDeckNameState = it },
                    label = { Text("Tên thư mục", color = TextGray) },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = PrimaryNeon),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (newDeckNameState.isNotBlank()) {
                        viewModel.createDeck(newDeckNameState)
                        newDeckNameState = ""
                        showCreateDeckDialog = false
                    }
                }, colors = ButtonDefaults.buttonColors(PrimaryNeon)) { Text("Tạo") }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDeckDialog = false }) { Text("Hủy", color = TextGray) }
            }
        )
    }

    Scaffold(
        containerColor = DeepDark,
        floatingActionButton = {
            ActionSpeedDial(
                expanded = fabExpanded,
                onExpandChange = { fabExpanded = it },
                onImport = { showImportDialog = true; fabExpanded = false },
                onCreate = { showCreateDeckDialog = true; fabExpanded = false },
                onAddCard = { onNavigateToAdd(); fabExpanded = false }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                item { Spacer(Modifier.height(12.dp)) }
                
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Chào ngày mới,", style = MaterialTheme.typography.bodyLarge, color = TextGray)
                            Text(
                                "${currentUser?.displayName ?: "Người học"}! ✨",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Row {
                            IconButton(onClick = { viewModel.syncData() }, modifier = Modifier.background(CardSurface, CircleShape)) {
                                if (isSyncing) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = AccentCyan)
                                else Icon(Icons.Rounded.Sync, null, tint = AccentCyan)
                            }
                            Spacer(Modifier.width(12.dp))
                            IconButton(onClick = { viewModel.logout() }, modifier = Modifier.background(Color.Red.copy(alpha = 0.1f), CircleShape)) {
                                Icon(Icons.Rounded.Logout, null, tint = Color.Red)
                            }
                        }
                    }
                }

                item { PremiumStudyBanner(cardsCount = cardsToReview.size, onReviewClick = onNavigateToReview) }

                item {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        divider = {},
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(Modifier.tabIndicatorOffset(tabPositions[selectedTab]), height = 4.dp, color = PrimaryNeon)
                        }
                    ) {
                        listOf("Thư mục", "Khám phá").forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = { Text(text = title, fontSize = 16.sp, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium, color = if (selectedTab == index) Color.White else TextGray) }
                            )
                        }
                    }
                }

                if (selectedTab == 0) {
                    item {
                        if (decks.isEmpty()) Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) { Text("Chưa có dữ liệu cá nhân", color = TextGray) }
                        else {
                            LazyVerticalGrid(columns = GridCells.Fixed(2), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.heightIn(max = 2000.dp)) {
                                items(decks) { deck ->
                                    PremiumDeckCard(deck, onClick = { onNavigateToDeck(deck.id) }, onShare = { viewModel.generateShareCode(deck) { code -> generatedCode = code; showShareDialog = true } })
                                }
                            }
                        }
                    }
                } else {
                    if (filteredSuggestions.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                                Text("Bạn đã sở hữu tất cả bộ thẻ gợi ý! 🎉", color = AccentCyan, textAlign = TextAlign.Center)
                            }
                        }
                    } else {
                        items(filteredSuggestions) { suggestion ->
                            PremiumSuggestionCard(suggestion) { viewModel.importDeck(suggestion.shareCode) }
                        }
                    }
                }
                item { Spacer(Modifier.height(120.dp)) }
            }

            if (fabExpanded) Box(Modifier.fillMaxSize().background(DeepDark.copy(alpha = 0.8f)).clickable { fabExpanded = false })
        }
    }
}

@Composable
fun ActionSpeedDial(expanded: Boolean, onExpandChange: (Boolean) -> Unit, onImport: () -> Unit, onCreate: () -> Unit, onAddCard: () -> Unit) {
    Column(horizontalAlignment = Alignment.End) {
        AnimatedVisibility(visible = expanded, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
            Column(horizontalAlignment = Alignment.End) {
                SpeedDialItem("Nhập mã", Icons.Rounded.Download, onImport)
                Spacer(Modifier.height(16.dp))
                SpeedDialItem("Thư mục mới", Icons.Rounded.CreateNewFolder, onCreate)
                Spacer(Modifier.height(16.dp))
            }
        }
        ExtendedFloatingActionButton(onClick = { if (expanded) onAddCard() else onExpandChange(true) }, containerColor = if (expanded) Color.White else PrimaryNeon, contentColor = if (expanded) PrimaryNeon else Color.White, shape = RoundedCornerShape(20.dp), icon = { Icon(if (expanded) Icons.Rounded.Close else Icons.Rounded.Add, null) }, text = { Text(if (expanded) "Thêm thẻ" else "Tạo mới", fontWeight = FontWeight.Bold) })
    }
}

@Composable
fun SpeedDialItem(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onClick() }) {
        Surface(color = CardSurface, shape = RoundedCornerShape(12.dp), modifier = Modifier.shadow(4.dp)) {
            Text(label, color = Color.White, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.width(12.dp))
        FloatingActionButton(onClick = onClick, containerColor = CardSurface, contentColor = AccentCyan, shape = CircleShape, modifier = Modifier.size(48.dp)) { Icon(icon, null) }
    }
}

@Composable
fun PremiumDeckCard(deck: Deck, onClick: () -> Unit, onShare: () -> Unit) {
    Surface(onClick = onClick, modifier = Modifier.height(160.dp).shadow(15.dp, RoundedCornerShape(28.dp)), shape = RoundedCornerShape(28.dp), color = CardSurface) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Box(modifier = Modifier.size(48.dp).background(PrimaryNeon.copy(alpha = 0.15f), RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) { Icon(Icons.Rounded.Folder, null, tint = PrimaryNeon, modifier = Modifier.size(26.dp)) }
                IconButton(onClick = onShare) { Icon(Icons.Rounded.IosShare, null, tint = TextGray, modifier = Modifier.size(20.dp)) }
            }
            Spacer(Modifier.weight(1f))
            Text(deck.name, color = Color.White, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun PremiumSuggestionCard(suggestion: SuggestedDeck, onAdd: () -> Unit) {
    Surface(onClick = onAdd, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(28.dp), color = CardSurface) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(56.dp).background(DeepDark, CircleShape), contentAlignment = Alignment.Center) { Text(suggestion.icon, fontSize = 28.sp) }
            Spacer(Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(suggestion.name, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(suggestion.description, color = TextGray, style = MaterialTheme.typography.bodySmall)
            }
            Icon(Icons.Rounded.AddCircle, null, tint = AccentCyan, modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
fun PremiumStudyBanner(cardsCount: Int, onReviewClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(animation = tween(20000, easing = LinearEasing)), label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .shadow(25.dp, RoundedCornerShape(32.dp), ambientColor = PrimaryNeon, spotColor = AccentCyan)
            .clip(RoundedCornerShape(32.dp))
            .background(CardSurface)
    ) {
        Canvas(modifier = Modifier.fillMaxSize().alpha(0.1f)) {
            drawCircle(
                brush = Brush.radialGradient(listOf(PrimaryNeon, Color.Transparent), center = Offset(size.width, 0f), radius = size.width),
                radius = size.width, center = Offset(size.width, 0f)
            )
        }

        Icon(
            Icons.Rounded.Stream, null,
            modifier = Modifier.size(240.dp).align(Alignment.CenterEnd).offset(x = 80.dp, y = 40.dp).rotate(rotation),
            tint = AccentCyan.copy(alpha = 0.1f)
        )

        Row(modifier = Modifier.padding(28.dp).fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    color = (if (cardsCount > 0) PrimaryNeon else TextGray).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryNeon.copy(alpha = 0.3f))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Icon(Icons.Rounded.Whatshot, null, tint = if (cardsCount > 0) Color.Yellow else TextGray, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(if (cardsCount > 0) "$cardsCount THẺ ĐANG ĐỢI" else "ĐÃ HOÀN THÀNH", style = MaterialTheme.typography.labelLarge, color = Color.White, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text("Lộ trình hôm nay", style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.ExtraBold)
                Text("Hãy giữ vững ngọn lửa học tập!", style = MaterialTheme.typography.bodyMedium, color = TextGray)
                Spacer(Modifier.weight(1f))
                Button(onClick = onReviewClick, enabled = cardsCount > 0, colors = ButtonDefaults.buttonColors(containerColor = Color.White, disabledContainerColor = CardSurface), shape = RoundedCornerShape(16.dp), elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp), modifier = Modifier.height(48.dp)) {
                    Text("Bắt đầu ngay", color = DeepDark, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Rounded.ArrowForward, null, tint = DeepDark, modifier = Modifier.size(18.dp))
                }
            }

            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp)) {
                CircularProgressIndicator(progress = { if (cardsCount > 0) 0.4f else 1f }, modifier = Modifier.fillMaxSize(), color = if (cardsCount > 0) AccentCyan else Color.Green, strokeWidth = 8.dp, strokeCap = androidx.compose.ui.graphics.StrokeCap.Round, trackColor = DeepDark)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(if (cardsCount > 0) "40%" else "100%", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    Text("Tiến độ", color = TextGray, fontSize = 10.sp)
                }
            }
        }
    }
}
