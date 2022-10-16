package com.cleanarchitectkotlinflowhiltsimplestway.di

import com.cleanarchitectkotlinflowhiltsimplestway.data.repository.CardRepository
import com.cleanarchitectkotlinflowhiltsimplestway.data.repository.CardRepositoryImpl
import com.cleanarchitectkotlinflowhiltsimplestway.data.repository.DiaryRepository
import com.cleanarchitectkotlinflowhiltsimplestway.data.repository.DiaryRepositoryImpl
import com.cleanarchitectkotlinflowhiltsimplestway.data.room.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

  @Provides @Singleton
  fun provideDiaryRepository(appDatabase: AppDatabase): DiaryRepository = DiaryRepositoryImpl(appDatabase)

  @Provides @Singleton
  fun provideCardRepository(appDatabase: AppDatabase): CardRepository = CardRepositoryImpl(appDatabase)
}