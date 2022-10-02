package com.cleanarchitectkotlinflowhiltsimplestway.utils.datetime

import com.dtv.starter.presenter.utils.log.Logger
import java.text.SimpleDateFormat
import java.util.*

fun currentYear() = Calendar.getInstance().get(Calendar.YEAR)

fun currentMonth() = Calendar.getInstance().get(Calendar.MONTH) + 1

fun monthInText(month: Int): String {
  val c = Calendar.getInstance()
  c.set(Calendar.MONTH, month)
  val sdf = SimpleDateFormat("MMM")
  return sdf.format(c.time)
}

fun dateTimeInCreateDiaryScreen(d: Date? = null): String {
  val date = d ?: Date()
  val sdf = SimpleDateFormat("EEE, MMM dd/yyyy")
  return sdf.format(date)
}

fun numberOfDayInMonth(month: Int, year: Int): Int {
  return when (month) {
    1, 3, 5, 7, 8, 10, 12 -> 31
    4, 6, 9, 11 -> 30
    2 -> if (year % 4 == 0) {
      29
    } else {
      28
    }
    else -> 0
  }
}

fun getDayTimeRange(day: Int, month: Int, year: Int): Pair<Long, Long> {
  val sdf = SimpleDateFormat("dd/MM/yyyy - HH:mm:ss")
  val startDateDataFormat = "%02d/%02d/%d - 00:00:00"
  val startDateData = String.format(startDateDataFormat, day, month, year)
  val startDate = sdf.parse(startDateData).time

  val endDateDataFormat = "%02d/%02d/%d - 23:59:59"
  val endDateData = String.format(endDateDataFormat, day, month, year)
  val endDate = sdf.parse(endDateData).time

  /*val sD = Date(startDate)
  val eD = Date(endDate)
  Logger.d("getPostInMonth getDayTimeRange: ${sdf.format(sD)} --- ${sdf.format(eD)}")*/
  return Pair(startDate, endDate)
}

fun getMonthTimeRange(month: Int, year: Int): Pair<Long, Long> {
  val sdf = SimpleDateFormat("dd/MM/yyyy - HH:mm:ss")
  val startDateDataFormat = "%02d/%02d/%d - 00:00:00"
  val startDateData = String.format(startDateDataFormat, 1, month, year)
  val startDate = sdf.parse(startDateData).time
  val endDateDataFormat = "%02d/%02d/%d - 23:59:59"
  val numberOfDay = numberOfDayInMonth(month, year)
  val endDateData = String.format(endDateDataFormat, numberOfDay, month, year)
  val endDate = sdf.parse(endDateData).time

  /*val sD = Date(startDate)
  val eD = Date(endDate)
  Logger.d("getPostInMonth getMonthTimeRange: ${sdf.format(sD)} --- ${sdf.format(eD)}")*/
  return Pair(startDate, endDate)
}