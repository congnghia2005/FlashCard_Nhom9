package com.example.flashcard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.flashcard.model.Deck
import com.example.flashcard.ui.FlashcardViewModel

// Sử dụng lại hệ màu từ HomeScreen
val DetailDeepDark = Color(0xFF0A0E1A)
val DetailCardSurface = Color(0xFF161B2C)
val DetailPrimaryNeon = Color(0xFF7C3AED)
val DetailAccentCyan = Color(0xFF06B6D4)
val DetailTextGray = Color(0xFF94A3B8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckDetailScreen(
    deck: Deck,
    viewModel: FlashcardViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEditCard: (Long) -> Unit,
    onNavigateToAddCard: () -> Unit
) {
    val allCards by viewModel.allFlashcards.collectAsState()
    val deckCards = allCards.filter { it.deckId == deck.id }
    
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    // Dialog xác nhận xóa
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            containerColor = DetailCardSurface,
            title = { Text("Xóa thư mục?", color = Color.White, fontWeight = FontWeight.ExtraBold) },
            text = { Text("Toàn bộ thẻ trong thư mục '${deck.name}' sẽ bị xóa vĩnh viễn. Bạn có chắc chắn không?", color = DetailTextGray) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteDeck(deck)
                        showDeleteConfirm = false
                        onNavigateBack() // Quay lại trang chủ sau khi xóa
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("Xóa vĩnh viễn", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Hủy", color = DetailTextGray) }
            }
        )
    }

    Scaffold(
        containerColor = DetailDeepDark,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                title = { 
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(deck.name, fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 20.sp)
                        Text("${deckCards.size} thẻ", style = MaterialTheme.typography.labelSmall, color = DetailAccentCyan)
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.padding(start = 8.dp).background(DetailCardSurface, CircleShape)
                    ) {
                        Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                },
                actions = {
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.padding(end = 8.dp).background(DetailCardSurface, CircleShape)
                        ) {
                            Icon(Icons.Rounded.MoreVert, null, tint = Color.White)
                        }
                        
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(DetailCardSurface)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Xóa thư mục", color = Color.Red) },
                                leadingIcon = { Icon(Icons.Rounded.Delete, contentDescription = null, tint = Color.Red) },
                                onClick = {
                                    showMenu = false
                                    showDeleteConfirm = true
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAddCard,
                containerColor = DetailPrimaryNeon,
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.shadow(12.dp, RoundedCornerShape(20.dp))
            ) {
                Icon(Icons.Rounded.Add, null)
                Spacer(Modifier.width(8.dp))
                Text("Thêm thẻ mới", fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (deckCards.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(modifier = Modifier.size(120.dp).background(DetailCardSurface, CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Rounded.LayersClear, null, modifier = Modifier.size(60.dp), tint = DetailPrimaryNeon.copy(alpha = 0.3f))
                        }
                        Spacer(Modifier.height(24.dp))
                        Text("Thư mục này còn trống", color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Hãy thêm những thẻ đầu tiên!", color = DetailTextGray, style = MaterialTheme.typography.bodySmall)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(deckCards) { card ->
                        PremiumCardItem(card = card, onClick = { onNavigateToEditCard(card.id) })
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
fun PremiumCardItem(card: com.example.flashcard.model.Flashcard, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().shadow(10.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        color = DetailCardSurface
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(70.dp).clip(RoundedCornerShape(18.dp)).background(DetailDeepDark)
            ) {
                if (card.imageUri != null) {
                    AsyncImage(
                        model = card.imageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.Style, null, tint = DetailPrimaryNeon.copy(alpha = 0.4f), modifier = Modifier.size(30.dp))
                    }
                }
            }

            Spacer(Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(card.front, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp, maxLines = 1)
                Spacer(Modifier.height(4.dp))
                Text(card.back, color = DetailTextGray, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
            }

            IconButton(
                onClick = onClick,
                modifier = Modifier.background(DetailDeepDark.copy(alpha = 0.5f), CircleShape).size(36.dp)
            ) {
                Icon(Icons.Rounded.ChevronRight, null, tint = DetailAccentCyan, modifier = Modifier.size(20.dp))
            }
        }
    }
}
