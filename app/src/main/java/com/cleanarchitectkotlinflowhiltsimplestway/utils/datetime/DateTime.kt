package com.cleanarchitectkotlinflowhiltsimplestway.utils.datetime

import java.text.SimpleDateFormat
import java.util.*

fun currentYear() = Calendar.getInstance().get(Calendar.YEAR)

fun currentMonth() = Calendar.getInstance().get(Calendar.MONTH) + 1

fun monthInText(month: Int) :String {
  val c = Calendar.getInstance()
  c.set(Calendar.MONTH, month)
  val sdf = SimpleDateFormat("MMM")
  return sdf.format(c.time)
}