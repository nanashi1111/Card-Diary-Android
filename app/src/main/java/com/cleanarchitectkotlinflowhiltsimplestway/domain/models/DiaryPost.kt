package com.cleanarchitectkotlinflowhiltsimplestway.domain.models

import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.DiaryPostData
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.WeatherType
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

data class DiaryPost(
  val date: Long,
  val dayOfMonth: String,
  val dayOfWeek: String,
  val images: List<String>,
  val title: String,
  val content: String,
  val weather: WeatherType
) : Serializable {

  fun simpleObject(): DiaryPost {
    return DiaryPost(this.date, "", "", emptyList(), "", "", WeatherType.SUNNY)
  }

  fun equal(other: DiaryPost): Boolean {
    return equalDate(other) && equalImages(other) && equalTitle(other) && equalContent(other)
  }

  private fun equalDate(other: DiaryPost): Boolean {
    return other.date == date
  }

  private fun equalTitle(other: DiaryPost): Boolean {
    return other.title == title
  }

  private fun equalContent(other: DiaryPost): Boolean {
    return other.content == content
  }

  private fun equalImages(other: DiaryPost): Boolean {
    return "$images" == "${other.images}"
  }
}

fun mapping(entity: DiaryPostData): DiaryPost {
  val date = Date(entity.date)
  val dayOfMonth = SimpleDateFormat("dd").format(date)
  val dayOfWeek = SimpleDateFormat("EEE").format(date)
  return DiaryPost(
    date = entity.date,
    images = entity.images.filter { it.isNotEmpty() },
    title = entity.title,
    content = entity.content,
    weather = entity.weather,
    dayOfMonth = dayOfMonth,
    dayOfWeek = dayOfWeek
  )
}