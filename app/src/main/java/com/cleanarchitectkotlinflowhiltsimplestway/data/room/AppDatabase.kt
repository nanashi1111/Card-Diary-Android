package com.cleanarchitectkotlinflowhiltsimplestway.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.CardTemplate
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.DiaryPostData
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.QuoteCategory
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.QuoteContent

@Database(entities = [DiaryPostData::class, CardTemplate::class], version = 5)
@TypeConverters(TypesConverter::class)
abstract class AppDatabase: RoomDatabase() {
  abstract fun diaryDao(): DiaryDao
  abstract fun cardDao(): CardDao
}

@Database(entities = [QuoteCategory::class, QuoteContent::class], version = 1)
abstract class QuoteDatabase: RoomDatabase() {
  abstract fun quoteDao(): QuoteDao
}