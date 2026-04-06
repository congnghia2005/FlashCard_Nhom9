package com.example.flashcard.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcard.ui.FlashcardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: FlashcardViewModel,
    onLoginSuccess: (String) -> Unit
) {
    var isLoginMode by remember { mutableStateOf(true) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    
    val purplePrimary = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.tertiary
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo / Icon
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.FlashOn,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(Modifier.height(32.dp))

            Text(
                text = if (isLoginMode) "Welcome Back" else "Create Account",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = if (isLoginMode) "Sign in to continue your journey" else "Join us and start mastering today",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(40.dp))

            // Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (!isLoginMode) {
                        OutlinedTextField(
                            value = displayName,
                            onValueChange = { displayName = it },
                            label = { Text("Display Name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            leadingIcon = { Icon(Icons.Default.Person, null, tint = purplePrimary) },
                            textStyle = TextStyle(color = purplePrimary, fontWeight = FontWeight.Medium),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = purplePrimary,
                                unfocusedTextColor = purplePrimary,
                                focusedBorderColor = purplePrimary,
                                unfocusedBorderColor = purplePrimary.copy(alpha = 0.3f),
                                focusedLabelColor = purplePrimary,
                                unfocusedLabelColor = purplePrimary.copy(alpha = 0.7f)
                            )
                        )
                    }

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = { Icon(Icons.Default.AccountCircle, null, tint = purplePrimary) },
                        textStyle = TextStyle(color = purplePrimary, fontWeight = FontWeight.Medium),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = purplePrimary,
                            unfocusedTextColor = purplePrimary,
                            focusedBorderColor = purplePrimary,
                            unfocusedBorderColor = purplePrimary.copy(alpha = 0.3f),
                            focusedLabelColor = purplePrimary,
                            unfocusedLabelColor = purplePrimary.copy(alpha = 0.7f)
                        )
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = purplePrimary) },
                        textStyle = TextStyle(color = purplePrimary, fontWeight = FontWeight.Medium),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = purplePrimary,
                            unfocusedTextColor = purplePrimary,
                            focusedBorderColor = purplePrimary,
                            unfocusedBorderColor = purplePrimary.copy(alpha = 0.3f),
                            focusedLabelColor = purplePrimary,
                            unfocusedLabelColor = purplePrimary.copy(alpha = 0.7f)
                        )
                    )

                    if (errorMsg != null) {
                        Text(
                            text = errorMsg!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Button(
                        onClick = {
                            if (isLoginMode) {
                                viewModel.login(username, password) { success, msg ->
                                    if (success) onLoginSuccess(username)
                                    else errorMsg = msg
                                }
                            } else {
                                viewModel.register(username, password, displayName) { success, msg ->
                                    if (success) isLoginMode = true
                                    errorMsg = msg
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = purplePrimary
                        )
                    ) {
                        Text(if (isLoginMode) "Sign In" else "Sign Up", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            TextButton(onClick = { 
                isLoginMode = !isLoginMode 
                errorMsg = null
            }) {
                Text(
                    if (isLoginMode) "Don't have an account? Sign Up" else "Already have an account? Sign In",
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
