package com.example.flashcard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.flashcard.ui.FlashcardViewModel
import com.example.flashcard.ui.screens.AddFlashcardScreen
import com.example.flashcard.ui.screens.HomeScreen
import com.example.flashcard.ui.screens.ReviewScreen
import com.example.flashcard.ui.theme.FlashCardTheme
import com.example.flashcard.worker.DailyReminderWorker

import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint

import java.util.concurrent.TimeUnit


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        

        scheduleDailyReminder()

        setContent {
            val viewModel = FlashcardViewModel(LocalContext.current.applicationContext as Application)

            LaunchedEffect(Unit) {
                viewModel.fetchFlashcardsFromApi()
            }
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

    // Sử dụng hiltViewModel() thay vì viewModel() để Hilt tự động inject
    val viewModel: FlashcardViewModel = hiltViewModel()

    // ViewModel sẽ quản lý dữ liệu và logic nghiệp vụ
    val viewModel: FlashcardViewModel = viewModel()


    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToReview = { navController.navigate("review") },
                onNavigateToAdd = { navController.navigate("add") }
            )
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
