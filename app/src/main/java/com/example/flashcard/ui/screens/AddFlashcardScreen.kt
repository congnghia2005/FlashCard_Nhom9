package com.example.flashcard.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.flashcard.ui.FlashcardViewModel
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFlashcardScreen(
    viewModel: FlashcardViewModel,
    onNavigateBack: () -> Unit
) {
    var front by remember { mutableStateOf("") }
    var back by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    
    val decks by viewModel.userDecks.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    var selectedDeckId by remember { mutableStateOf<Long?>(null) }
    var selectedDeckName by remember { mutableStateOf("Select Folder") }

    val context = LocalContext.current
    val purplePrimary = MaterialTheme.colorScheme.primary

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> selectedImageUri = uri }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("New Flashcard", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null) }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Folder Selector
            Column {
                Text("Study Folder", style = MaterialTheme.typography.labelLarge, color = purplePrimary)
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .clickable { expanded = true }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FolderOpen, null, tint = purplePrimary)
                            Spacer(Modifier.width(12.dp))
                            Text(selectedDeckName, fontWeight = FontWeight.Medium)
                        }
                        Icon(Icons.Default.ArrowDropDown, null)
                    }
                    
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        if (decks.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("No folders found. Create one first!") },
                                onClick = { expanded = false }
                            )
                        }
                        decks.forEach { deck ->
                            DropdownMenuItem(
                                text = { Text(deck.name) },
                                onClick = {
                                    selectedDeckId = deck.id
                                    selectedDeckName = deck.name
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Input Fields
            OutlinedTextField(
                value = front,
                onValueChange = { front = it },
                label = { Text("Question") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = purplePrimary,
                    unfocusedTextColor = purplePrimary,
                    focusedBorderColor = purplePrimary
                )
            )

            OutlinedTextField(
                value = back,
                onValueChange = { back = it },
                label = { Text("Answer") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                minLines = 2,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = purplePrimary,
                    unfocusedTextColor = purplePrimary,
                    focusedBorderColor = purplePrimary
                )
            )

            // Image Area
            if (selectedImageUri != null) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            OutlinedButton(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Image, null)
                Spacer(Modifier.width(8.dp))
                Text("Add Illustration Image")
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    if (front.isNotBlank() && back.isNotBlank() && selectedDeckId != null) {
                        val finalImageUri = selectedImageUri?.let { saveImageToInternalStorage(context, it) }
                        viewModel.addFlashcard(front, back, selectedDeckId!!, finalImageUri)
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                enabled = front.isNotBlank() && back.isNotBlank() && selectedDeckId != null,
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(Icons.Default.Save, null)
                Spacer(Modifier.width(8.dp))
                Text("Save Flashcard", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

private fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val fileName = "img_${UUID.randomUUID()}.jpg"
        val file = File(context.filesDir, fileName)
        val outputStream = FileOutputStream(file)
        inputStream?.use { input -> outputStream.use { output -> input.copyTo(output) } }
        file.absolutePath
    } catch (e: Exception) { null }
}
