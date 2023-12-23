package com.cleanarchitectkotlinflowhiltsimplestway.di

import android.content.Context
import androidx.room.Room
import com.cleanarchitectkotlinflowhiltsimplestway.data.room.AppDatabase
import com.cleanarchitectkotlinflowhiltsimplestway.data.room.QuoteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

  @Provides
  @Singleton
  fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
    return Room.databaseBuilder(context, AppDatabase::class.java, "CardDiary")
      .fallbackToDestructiveMigration().build()
  }

  @Provides
  @Singleton
  fun provideQuoteDatabase(@ApplicationContext context: Context): QuoteDatabase {
    return Room.databaseBuilder(context, QuoteDatabase::class.java, "QuoteDB")
      .fallbackToDestructiveMigration().build()
  }
}