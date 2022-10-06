package com.cleanarchitectkotlinflowhiltsimplestway.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.DiaryPostData

@Database(entities = [DiaryPostData::class], version = 3)
@TypeConverters(TypesConverter::class)
abstract class AppDatabase: RoomDatabase() {
  abstract fun diaryDao(): DiaryDao
}