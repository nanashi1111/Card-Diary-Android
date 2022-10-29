package com.cleanarchitectkotlinflowhiltsimplestway.di

import android.content.Context
import com.cleanarchitectkotlinflowhiltsimplestway.data.repository.*
import com.cleanarchitectkotlinflowhiltsimplestway.data.room.AppDatabase
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

  @Provides
  @Singleton
  fun provideGson(): Gson = Gson()

  @Provides
  @Singleton
  fun provideDiaryRepository(@ApplicationContext context: Context, appDatabase: AppDatabase, gson: Gson): DiaryRepository = DiaryRepositoryImpl(context, appDatabase, gson)

  @Provides
  @Singleton
  fun provideCardRepository(@ApplicationContext context: Context, appDatabase: AppDatabase): CardRepository = CardRepositoryImpl(context, appDatabase)

  @Provides @Singleton
  fun providePatternRepository(@ApplicationContext context: Context, gson: Gson): PatternRepository = PatternRepositoryImpl(context, gson)
}