package com.example.flashcard.di

import android.content.Context
import com.example.flashcard.data.AppDatabase
import com.example.flashcard.data.FlashcardDao
import com.example.flashcard.data.FlashcardRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideFlashcardDao(database: AppDatabase): FlashcardDao {
        return database.flashcardDao()
    }

    @Provides
    @Singleton
    fun provideRepository(flashcardDao: FlashcardDao): FlashcardRepository {
        return FlashcardRepository(flashcardDao)
    }
}
