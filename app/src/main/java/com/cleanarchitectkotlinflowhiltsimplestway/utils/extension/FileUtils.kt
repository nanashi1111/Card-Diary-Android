package com.cleanarchitectkotlinflowhiltsimplestway.utils.extension

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.net.toFile
import com.dtv.starter.presenter.utils.log.Logger
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

  fun copyFolderContent(sourceFolder: File, targetFolder: File) {
    for (targetFolderFile in targetFolder.listFiles()) {
      if (targetFolderFile.isFile) {
        targetFolderFile.delete()
      }
    }

    for (sourceFolderFile in sourceFolder.listFiles()) {
      val fileName = sourceFolderFile.name
      val targetFolderFile = File(targetFolder, fileName)
      copySingleFile(sourceFolderFile, targetFolderFile)
    }

  }

  private fun copySingleFile(sourceFile: File, destFile: File) {
    Logger.d(
      "COPY FILE: " + sourceFile.absolutePath
          + " TO: " + destFile.absolutePath
    )
    if (!destFile.exists()) {
      destFile.createNewFile()
    }
    var sourceChannel: FileChannel? = null
    var destChannel: FileChannel? = null
    try {
      sourceChannel = FileInputStream(sourceFile).channel
      destChannel = FileOutputStream(destFile).channel
      sourceChannel.transferTo(0, sourceChannel.size(), destChannel)
    } finally {
      sourceChannel?.close()
      destChannel?.close()
    }
  }

  fun clearFolder(folder: File) {
    folder.listFiles()?.let { listFile ->
      for (file in listFile) {
        if (file.isFile) {
          file.delete()
        }
      }
    }
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