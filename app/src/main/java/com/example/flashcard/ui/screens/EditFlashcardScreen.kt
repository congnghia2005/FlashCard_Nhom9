package com.example.flashcard.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
// Import đúng Model và ViewModel của bạn
import com.example.flashcard.model.Flashcard
import com.example.flashcard.ui.FlashcardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditFlashcardScreen(
    flashcard: Flashcard,
    viewModel: FlashcardViewModel,
    onNavigateBack: () -> Unit
) {
    // 1. Khai báo các trạng thái để chỉnh sửa
    var front by remember { mutableStateOf(flashcard.front) }
    var back by remember { mutableStateOf(flashcard.back) }
    var imageUri by remember { mutableStateOf<Uri?>(flashcard.imageUri?.let { Uri.parse(it) }) }

    // 2. Trình chọn ảnh
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) imageUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chỉnh sửa thẻ") },
                actions = {
                    // NÚT XÓA
                    IconButton(onClick = {
                        viewModel.deleteFlashcard(flashcard)
                        onNavigateBack()
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = Color.Red)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Nhập mặt trước
            OutlinedTextField(
                value = front,
                onValueChange = { front = it },
                label = { Text("Mặt trước") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            // Nhập mặt sau
            OutlinedTextField(
                value = back,
                onValueChange = { back = it },
                label = { Text("Mặt sau") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Hiển thị ảnh (nếu có)
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Flashcard Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            // Nút chọn/đổi ảnh
            OutlinedButton(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Image, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Thay đổi ảnh minh họa")
            }

            Spacer(Modifier.height(24.dp))

            // Nút Lưu thay đổi
            Button(
                onClick = {
                    if (front.isNotBlank() && back.isNotBlank()) {
                        // Cập nhật flashcard thông qua hàm copy của Data Class
                        viewModel.updateFlashcard(
                            flashcard.copy(
                                front = front,
                                back = back,
                                imageUri = imageUri?.toString()
                            )
                        )
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = front.isNotBlank() && back.isNotBlank()
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Cập nhật thay đổi")
            }
        }
    }
}