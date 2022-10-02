package com.cleanarchitectkotlinflowhiltsimplestway.utils.extension

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.net.toFile
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.FileChannel

object FileUtils {

  fun saveFile(sourceUri: Uri, destination: File): String {
    return try {
      val source = sourceUri.toFile()
      val src: FileChannel = FileInputStream(source).channel
      val des: FileChannel = FileOutputStream(destination).channel
      des.transferFrom(src, 0, src.size())
      src.close()
      des.close()
      destination.absolutePath
    } catch (e: Exception) {
      e.printStackTrace()
      ""
    }
  }

  fun createDiaryFolderForDate(context: Context, timestamp: Long): File {
    val folderName = "$timestamp"
    val folder = File(context.filesDir, folderName)
    if (!folder.exists()) {
      folder.mkdirs()
    }
    return folder
  }

  private fun isExternalStorageReadOnly(): Boolean {
    val extStorageState = Environment.getExternalStorageState()
    return Environment.MEDIA_MOUNTED_READ_ONLY == extStorageState
  }

  private fun isExternalStorageAvailable(): Boolean {
    val extStorageState = Environment.getExternalStorageState()
    return Environment.MEDIA_MOUNTED == extStorageState
  }
}