package com.cleanarchitectkotlinflowhiltsimplestway.domain.models

import java.util.*

data class MonthDayData(val year: Int, val month: Int, val day: Int, val numberOfDiary: Int) {

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