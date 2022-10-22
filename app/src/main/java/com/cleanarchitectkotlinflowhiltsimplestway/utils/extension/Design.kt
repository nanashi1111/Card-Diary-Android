package com.cleanarchitectkotlinflowhiltsimplestway.utils.extension

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.GradientDrawable

fun coloredCircle(backgroundColor: String): GradientDrawable {
  val shape = GradientDrawable()
  shape.shape = GradientDrawable.OVAL
  shape.cornerRadii = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
  shape.setColor(Color.parseColor(backgroundColor))
  return shape
}

fun coloredBackground(backgroundColor: String): Bitmap {
  val bitmap = Bitmap.createBitmap(5,5, Bitmap.Config.ARGB_8888)
  val canvas = Canvas(bitmap)
  canvas.drawColor(Color.parseColor(backgroundColor))
  return bitmap
}