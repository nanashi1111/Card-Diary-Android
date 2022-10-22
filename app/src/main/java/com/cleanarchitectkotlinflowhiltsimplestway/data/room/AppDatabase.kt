package com.cleanarchitectkotlinflowhiltsimplestway.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.CardTemplate
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.DiaryPostData

@Database(entities = [DiaryPostData::class, CardTemplate::class], version = 5)
@TypeConverters(TypesConverter::class)
abstract class AppDatabase: RoomDatabase() {
  abstract fun diaryDao(): DiaryDao
  abstract fun cardDao(): CardDao
}