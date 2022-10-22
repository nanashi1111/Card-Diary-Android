package com.cleanarchitectkotlinflowhiltsimplestway.data.repository

import android.content.Context
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.DiaryPostData
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.WeatherType
import com.cleanarchitectkotlinflowhiltsimplestway.data.room.AppDatabase
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.FileUtils
import com.dtv.starter.presenter.utils.log.Logger
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import net.lingala.zip4j.ZipFile
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

interface DiaryRepository {
  fun saveDiaryPost(id: Long, images: List<String>, title: String, content: String, weather: WeatherType): Boolean

  fun getDiaryPost(id: Long): DiaryPostData

  fun updateDiaryPost(id: Long, images: List<String>, title: String, content: String, weather: WeatherType): Boolean

  fun getDiaryPosts(startDate: Long, endDate: Long, fullData: Boolean): List<DiaryPostData>

  fun searchPost(query: String): List<DiaryPostData>

  fun getAll(): List<DiaryPostData>

  fun deleteAll(): Int

  fun exportDbToZipFile(): String

  fun importDbFromZipFile(path: String): Boolean

  fun getExportedFiles(): List<File>
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

  override fun getDiaryPosts(startDate: Long, endDate: Long, fullData: Boolean): List<DiaryPostData> {
    if (fullData) {
      return appDatabase.diaryDao().getDiaryPosts(startDate, endDate)
    } else {
      return appDatabase.diaryDao().getDiaryPostsMinimalData(startDate, endDate)
    }
  }

  override fun searchPost(query: String): List<DiaryPostData> = appDatabase.diaryDao().searchPost(query)

  override fun getAll() = appDatabase.diaryDao().getDiaryPosts(0L, Date().time)

  override fun deleteAll() = appDatabase.diaryDao().deleteAll()

  override fun importDbFromZipFile(path: String): Boolean {
    val zipFile = ZipFile(path)
    val targetFolder = FileUtils.getParentFolder(context)

    val tempFolder = File(targetFolder, "temp")

    if (!tempFolder.exists()) {
      tempFolder.mkdirs()
    }
    //
    tempFolder.listFiles()?.let {
      for (file in it) {
        if (file.absolutePath.endsWith("txt")) {
          file.delete()
        }
      }
    }

    try {
      zipFile.extractAll(tempFolder.absolutePath)
    } catch (e: Exception) {
      FileUtils.forceClearFolder(tempFolder)
      throw e
    }

    tempFolder.listFiles()?.firstOrNull {
      it.name.startsWith("Verify_")
    } ?: run {
      FileUtils.forceClearFolder(tempFolder)
      throw  InvalidPropertiesFormatException("Invalid zip file")
    }

    //Update db
    tempFolder.listFiles()?.first { it.absolutePath.endsWith("txt") }?.let {
      val posts = mutableListOf<DiaryPostData>()
      val reader = JsonReader(BufferedReader(InputStreamReader(FileInputStream(it))))
      reader.beginArray()
      while (reader.hasNext()) {
        val post = gson.fromJson<DiaryPostData>(reader, DiaryPostData::class.java)
        posts.add(post)
      }
      reader.endArray()
      appDatabase.diaryDao().saveDiaryPost(posts)
    } ?: run {
      FileUtils.forceClearFolder(tempFolder)
      throw  InvalidPropertiesFormatException("Invalid zip file")
    }

    //Handling images

    //Create folder for each posts
    val folderNames = tempFolder.listFiles()?.filter {
      !it.name.endsWith("txt") && !it.name.startsWith("Verify_")
    }?.map {
      it.name.split("_").first()
    }?.distinct() ?: emptyList()
    for(folderName in folderNames) {
      val folder = File(targetFolder, folderName)
      if (!folder.exists()) {
        folder.mkdirs()
      }
    }
    //Copy images to corresponding folder
    tempFolder.listFiles()?.filter {
      !it.name.endsWith("txt") && !it.name.startsWith("Verify_")
    }?.forEach {
      val folder = File(targetFolder, it.name.split("_").first())
      FileUtils.copySingleFile(it, File(folder, it.name))
    }
    FileUtils.forceClearFolder(tempFolder)
    return true
  }

  override fun exportDbToZipFile(): String {
    try {
      val extractingTargetFolder: String = FileUtils.getExportedFolder(context).absolutePath
      val parentFolder = FileUtils.getParentFolder(context)
      //Txt file
      val allPosts = getAll()
      val allPostsData = gson.toJson(allPosts)
      val fileName = "${SimpleDateFormat("MMddyyyy_HHmmss").format(Date())}"
      val txtFile = File("${parentFolder.absolutePath}/$fileName.txt")
      val fos = FileOutputStream(txtFile)
      fos.write(allPostsData.toByteArray())
      fos.close()
      //Zip file
      val verifyFolder = File(parentFolder, "Verify_$fileName")
      if (!verifyFolder.exists()) {
        verifyFolder.createNewFile()
      }
      val zipFile = ZipFile("$extractingTargetFolder/$fileName.zip")
      zipFile.addFile(verifyFolder)
      //zipFile.addFolder(parentFolder)
      parentFolder.listFiles()?.forEach { folder ->
        if (folder.isDirectory) {
          folder.listFiles()?.forEach { file ->
            if (file.isFile) {
              Logger.d("AddingFileToZip: ${file.absolutePath}")
              zipFile.addFile(file)
            }
          }
        }
      }
      zipFile.addFile(txtFile)

      txtFile.delete()

      return "$parentFolder/$fileName.zip"
    } catch (e: Exception) {
      throw e
    }

  }

  override fun getExportedFiles(): List<File> {
    val exportedFolder = FileUtils.getExportedFolder(context)
    return exportedFolder.listFiles()?.filter { it.isFile && it.name.endsWith("zip") } ?: emptyList()
  }
}