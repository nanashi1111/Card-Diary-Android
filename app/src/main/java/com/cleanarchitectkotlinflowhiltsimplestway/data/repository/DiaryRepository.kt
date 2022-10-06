package com.cleanarchitectkotlinflowhiltsimplestway.data.repository

import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.DiaryPostData
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.WeatherType
import com.cleanarchitectkotlinflowhiltsimplestway.data.room.AppDatabase
import java.util.*

interface DiaryRepository {
  fun saveDiaryPost(
    id: Long,
    images: List<String>,
    title: String,
    content: String,
    weather: WeatherType
  ): Boolean

  fun updateDiaryPost(
    id: Long,
    images: List<String>,
    title: String,
    content: String,
    weather: WeatherType
  ): Boolean

  fun getDiaryPosts(startDate: Long, endDate: Long): List<DiaryPostData>

  fun getAll(): List<DiaryPostData>
}

class DiaryRepositoryImpl(private val appDatabase: AppDatabase): DiaryRepository {
  override fun saveDiaryPost(id: Long, images: List<String>, title: String, content: String, weather: WeatherType): Boolean {
    val post = DiaryPostData(
      date = id, images = images, title = title, content = content, weather = weather
    )
    appDatabase.diaryDao().saveDiaryPost(post)
    return true
  }

  override fun updateDiaryPost(id: Long, images: List<String>, title: String, content: String, weather: WeatherType): Boolean {
    val post = DiaryPostData(
      date = id, images = images, title = title, content = content, weather = weather
    )
    appDatabase.diaryDao().updateDiaryPost(post)
    return true
  }

  override fun getDiaryPosts(startDate: Long, endDate: Long)  = appDatabase.diaryDao().getDiaryPosts(startDate, endDate)

  override fun getAll(): List<DiaryPostData> {
    return appDatabase.diaryDao().getDiaryPosts(0L, Date().time)
  }

}