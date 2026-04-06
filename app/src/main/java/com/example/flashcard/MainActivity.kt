package com.example.flashcard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.flashcard.ui.FlashcardViewModel
import com.example.flashcard.ui.screens.*
import com.example.flashcard.ui.theme.FlashCardTheme
import com.example.flashcard.worker.DailyReminderWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        scheduleDailyReminder()
        setContent {
            FlashCardTheme {
                FlashcardApp()
            }
        }
    }

    private fun scheduleDailyReminder() {
        val request = PeriodicWorkRequestBuilder<DailyReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(1, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "daily_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}

@Composable
fun FlashcardApp() {
    val navController = rememberNavController()
    val viewModel: FlashcardViewModel = viewModel()
    val currentUser by viewModel.currentUser.collectAsState()

    if (currentUser == null) {
        LoginScreen(
            viewModel = viewModel,
            onLoginSuccess = { /* State updates automatically via Flow */ }
        )
    } else {
        NavHost(navController = navController, startDestination = "home") {
            composable("home") {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToReview = { navController.navigate("review") },
                    onNavigateToAdd = { navController.navigate("add") },
                    onNavigateToEdit = { cardId -> navController.navigate("edit/$cardId") },
                    onNavigateToDeck = { deckId -> navController.navigate("deck/$deckId") }
                )
            }

            composable(
                route = "deck/{deckId}",
                arguments = listOf(navArgument("deckId") { type = NavType.LongType })
            ) { backStackEntry ->
                val deckId = backStackEntry.arguments?.getLong("deckId")
                val decks by viewModel.userDecks.collectAsState()
                val deck = decks.find { it.id == deckId }

                deck?.let {
                    DeckDetailScreen(
                        deck = it,
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToEditCard = { cardId -> navController.navigate("edit/$cardId") },
                        onNavigateToAddCard = { navController.navigate("add") }
                    )
                }
            }

            composable(
                route = "edit/{cardId}",
                arguments = listOf(navArgument("cardId") { type = NavType.LongType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("cardId")
                val allCards by viewModel.allFlashcards.collectAsState()
                val card = allCards.find { it.id == id }

                card?.let {
                    EditFlashcardScreen(
                        flashcard = it,
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }

            composable("review") {
                ReviewScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("add") {
                AddFlashcardScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
