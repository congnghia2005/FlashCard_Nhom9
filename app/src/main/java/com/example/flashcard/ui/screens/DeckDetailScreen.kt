package com.example.flashcard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
<<<<<<< HEAD
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
=======
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Style
>>>>>>> 27d4e2849a9709f1e2be39e4ce2aed2922d414bf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
<<<<<<< HEAD
import androidx.compose.ui.draw.shadow
=======
>>>>>>> 27d4e2849a9709f1e2be39e4ce2aed2922d414bf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
<<<<<<< HEAD
import androidx.compose.ui.unit.sp
=======
>>>>>>> 27d4e2849a9709f1e2be39e4ce2aed2922d414bf
import coil.compose.AsyncImage
import com.example.flashcard.model.Deck
import com.example.flashcard.ui.FlashcardViewModel

<<<<<<< HEAD
// Sử dụng lại hệ màu từ HomeScreen
val DetailDeepDark = Color(0xFF0A0E1A)
val DetailCardSurface = Color(0xFF161B2C)
val DetailPrimaryNeon = Color(0xFF7C3AED)
val DetailAccentCyan = Color(0xFF06B6D4)
val DetailTextGray = Color(0xFF94A3B8)

=======
>>>>>>> 27d4e2849a9709f1e2be39e4ce2aed2922d414bf
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
<<<<<<< HEAD
    
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
=======

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(deck.name, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
>>>>>>> 27d4e2849a9709f1e2be39e4ce2aed2922d414bf
                    }
                }
            )
        },
        floatingActionButton = {
<<<<<<< HEAD
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
=======
            FloatingActionButton(
                onClick = onNavigateToAddCard,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Card")
            }
        }
    ) { padding ->
        if (deckCards.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Style,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "No cards in this folder yet.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(deckCards) { card ->
                    Surface(
                        onClick = { onNavigateToEditCard(card.id) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 2.dp,
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (card.imageUri != null) {
                                AsyncImage(
                                    model = card.imageUri,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .background(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                            RoundedCornerShape(8.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Style,
                                        null,
                                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                    )
                                }
                            }

                            Spacer(Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    card.front,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                                Text(
                                    card.back,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }

                            IconButton(onClick = { onNavigateToEditCard(card.id) }) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
>>>>>>> 27d4e2849a9709f1e2be39e4ce2aed2922d414bf
            }
        }
    }
}
