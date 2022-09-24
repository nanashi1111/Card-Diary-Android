package com.cleanarchitectkotlinflowhiltsimplestway.utils.extension

import android.content.Context
import android.os.Environment
import com.dtv.starter.presenter.utils.log.Logger
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object FileUtils {
  fun saveFile(context: Context, body: ResponseBody?, path: String):String{
    if (body==null)
      return ""
    if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
      return ""
    }
    var input: InputStream? = null
    try {
      input = body.byteStream()
      //val directory = File (context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath + "/UnsplashDemo" )
      val directory = File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)?.absolutePath + "/UnsplashDemo" )
      if (!directory.exists()) {
        directory.mkdirs()
      }
      val filePath = directory.absolutePath +"/"+path
      val fos = FileOutputStream(filePath)
      fos.use { output ->
        val buffer = ByteArray(4 * 1024) // or other buffer size
        var read: Int
        while (input.read(buffer).also { read = it } != -1) {
          output.write(buffer, 0, read)
        }
        output.flush()
      }
      return filePath
    }catch (e:Exception){
      Logger.d("saveFile: ${e}")
    }
    finally {
      input?.close()
    }
    return ""
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