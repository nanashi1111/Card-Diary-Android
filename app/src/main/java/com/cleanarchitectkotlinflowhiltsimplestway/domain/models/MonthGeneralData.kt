package com.cleanarchitectkotlinflowhiltsimplestway.domain.models

import java.util.*

data class MonthGeneralData(val dayWithPosts: Int, val totalDays: Int, val progress: Int)
data class DayData(val day: Int, val month: Int, val year: Int, val numberOfPosts: Int) {
  fun dayInString() = "$day"

  fun isMonday(): Boolean {
    val c = Calendar.getInstance()
    c.set(Calendar.YEAR, year)
    c.set(Calendar.MONTH, month - 1)
    c.set(Calendar.DAY_OF_MONTH, day)
    val dayOfWeek = c.get(Calendar.DAY_OF_WEEK)
    return dayOfWeek == Calendar.MONDAY
  }
  fun isSunday(): Boolean {
    val c = Calendar.getInstance()
    c.set(Calendar.YEAR, year)
    c.set(Calendar.MONTH, month - 1)
    c.set(Calendar.DAY_OF_MONTH, day)
    val dayOfWeek = c.get(Calendar.DAY_OF_WEEK)
    return dayOfWeek == Calendar.SUNDAY
  }
}
data class MonthData(val generalDayData: MonthGeneralData, val daysData: List<DayData>)