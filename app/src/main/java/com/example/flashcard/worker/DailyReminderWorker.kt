package com.example.flashcard.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.flashcard.MainActivity
import com.example.flashcard.data.AppDatabase
import kotlinx.coroutines.flow.first

class DailyReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val database = AppDatabase.getDatabase(applicationContext)
        val flashcardDao = database.flashcardDao()
        val userDao = database.userDao()
        
        return try {
            // Lấy tất cả người dùng để kiểm tra xem có ai cần học không
            val users = userDao.getAllUsers().first()
            var totalCardsToReview = 0

            for (user in users) {
                // Kiểm tra xem có thẻ nào đến hạn review (nextReviewDate <= thời điểm hiện tại)
                val cards = flashcardDao.getFlashcardsToReviewByUser(user.username, System.currentTimeMillis()).first()
                totalCardsToReview += cards.size
            }

            if (totalCardsToReview > 0) {
                showNotification(totalCardsToReview)
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun showNotification(count: Int) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "daily_reminder_channel"

        // Tạo Intent để mở ứng dụng khi nhấn vào thông báo
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext, 
            0, 
            intent, 
            PendingIntent.FLAG_IMMUTABLE
        )

        // Tạo Channel cho Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, 
                "Học tập hàng ngày", 
                NotificationManager.IMPORTANCE_HIGH // Đặt mức độ ưu tiên cao để hiện banner
            ).apply {
                description = "Nhắc nhở ôn tập thẻ Flashcard đến hạn"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Xây dựng nội dung thông báo
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Đã đến lúc ôn tập rồi! 🧠")
            .setContentText("Bạn đang có $count thẻ cần được ôn tập ngay hôm nay. Đừng bỏ lỡ nhé!")
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Bạn có thể thay bằng icon app của mình
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // Tự động biến mất khi nhấn vào
            .build()

        // Hiển thị thông báo
        notificationManager.notify(1, notification)
    }
}
