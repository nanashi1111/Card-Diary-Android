package com.cleanarchitectkotlinflowhiltsimplestway.utils.extension

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import androidx.core.net.toFile
import com.dtv.starter.presenter.utils.log.Logger
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.channels.FileChannel


object FileUtils {

  fun saveFile(context: Context, sourceUri: Uri, destination: File): String {
    return try {
      Logger.d("SaveFile Uri = ${sourceUri.path} ; authority = ${sourceUri.authority}")
      val source = when (sourceUri.authority) {
        "media" -> getFile(context, sourceUri)
        else -> sourceUri.toFile()
      }
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

  private fun forceClearFolder(folder: File) {
    folder.listFiles()?.let {
      for (file in it) {
        if (file.isFile) {
          Logger.d("Deleting: ${file.absolutePath}")
          file.delete()
        } else if (file.isDirectory){
          forceClearFolder(file)
        }
      }
    }
  }

  fun forceClearAllData(context: Context) {
    val folder = context.filesDir
    forceClearFolder(folder)
  }

  private fun isExternalStorageReadOnly(): Boolean {
    val extStorageState = Environment.getExternalStorageState()
    return Environment.MEDIA_MOUNTED_READ_ONLY == extStorageState
  }

  private fun isExternalStorageAvailable(): Boolean {
    val extStorageState = Environment.getExternalStorageState()
    return Environment.MEDIA_MOUNTED == extStorageState
  }

  private fun getFile(context: Context, uri: Uri): File {
    val destinationFilename = File(context.filesDir.path + File.separatorChar + queryName(context, uri))
    try {
      context.contentResolver.openInputStream(uri).use { ins ->
        ins?.let {
          createFileFromStream(ins, destinationFilename)
        }
      }
    } catch (ex: java.lang.Exception) {
      ex.printStackTrace()
    }
    return destinationFilename
  }

  fun createFileFromStream(ins: InputStream, destination: File?) {
    try {
      FileOutputStream(destination).use { os ->
        val buffer = ByteArray(4096)
        var length: Int
        while (ins.read(buffer).also { length = it } > 0) {
          os.write(buffer, 0, length)
        }
        os.flush()
      }
    } catch (ex: java.lang.Exception) {
      ex.printStackTrace()
    }
  }

  private fun queryName(context: Context, uri: Uri): String {
    val returnCursor: Cursor = context.contentResolver.query(uri, null, null, null, null)!!
    val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    returnCursor.moveToFirst()
    val name: String = returnCursor.getString(nameIndex)
    returnCursor.close()
    return name
  }
}