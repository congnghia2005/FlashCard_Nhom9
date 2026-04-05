package com.example.flashcard.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.flashcard.model.Flashcard

@Database(entities = [Flashcard::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun flashcardDao(): FlashcardDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "flashcard_db")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
