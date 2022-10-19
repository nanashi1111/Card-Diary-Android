package com.cleanarchitectkotlinflowhiltsimplestway.data.repository

import android.content.Context
import android.os.Environment
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.DiaryPostData
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.WeatherType
import com.cleanarchitectkotlinflowhiltsimplestway.data.room.AppDatabase
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.FileUtils
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.FileUtils.isExternalStorageAvailable
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.FileUtils.isExternalStorageReadOnly
import com.dtv.starter.presenter.utils.log.Logger
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

interface DiaryRepository {
  fun saveDiaryPost(id: Long, images: List<String>, title: String, content: String, weather: WeatherType): Boolean

  fun getDiaryPost(id: Long): DiaryPostData

  fun updateDiaryPost(id: Long, images: List<String>, title: String, content: String, weather: WeatherType): Boolean

  fun getDiaryPosts(startDate: Long, endDate: Long): List<DiaryPostData>

  fun searchPost(query: String): List<DiaryPostData>

  fun getAll(): List<DiaryPostData>

  fun deleteAll(): Int

  fun exportDbToTextFile(parentFolder: String):String?
}

class DiaryRepositoryImpl(private val context: Context, private val appDatabase: AppDatabase, private val gson: Gson) : DiaryRepository {
  override fun saveDiaryPost(id: Long, images: List<String>, title: String, content: String, weather: WeatherType): Boolean {
    val post = DiaryPostData(
      date = id, images = images, title = title, content = content, weather = weather
    )
    appDatabase.diaryDao().saveDiaryPost(post)
    return true
  }

  override fun getDiaryPost(id: Long) = appDatabase.diaryDao().getDiaryPost(id)

  override fun updateDiaryPost(id: Long, images: List<String>, title: String, content: String, weather: WeatherType): Boolean {
    val post = DiaryPostData(
      date = id, images = images, title = title, content = content, weather = weather
    )
    appDatabase.diaryDao().updateDiaryPost(post)
    return true
  }

  override fun getDiaryPosts(startDate: Long, endDate: Long) = appDatabase.diaryDao().getDiaryPosts(startDate, endDate)

  override fun searchPost(query: String): List<DiaryPostData> = appDatabase.diaryDao().searchPost(query)

  override fun getAll() = appDatabase.diaryDao().getDiaryPosts(0L, Date().time)

  override fun deleteAll() = appDatabase.diaryDao().deleteAll()

  override fun exportDbToTextFile(parentFolder: String): String? {
    if (isExternalStorageAvailable() && !isExternalStorageReadOnly()) {
      val allPosts = getAll()
      val allPostsData = gson.toJson(allPosts)
      val fileName = "${SimpleDateFormat("MMddyyyy_HHmmss").format(Date())}.txt"
      //val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
      val file = File("$parentFolder/$fileName")
      val fos = FileOutputStream(file)
      fos.write(allPostsData.toByteArray())
      fos.close()
      return file.absolutePath
    }
    return null
  }
}