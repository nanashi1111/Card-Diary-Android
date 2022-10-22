package com.cleanarchitectkotlinflowhiltsimplestway.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DiaryPostData(
  @PrimaryKey
  val date: Long = System.currentTimeMillis(),
  @ColumnInfo(name = "images")
  val images: List<String>? = emptyList(),
  @ColumnInfo(name = "title")
  val title: String? = "",
  @ColumnInfo(name = "content")
  val content: String? = "",
  @ColumnInfo(name = "weather")
  val weather: WeatherType? = WeatherType.SUNNY
)

enum class WeatherType {
  SUNNY, CLOUDY, RAINY, SNOWY, LIGHTING, STORMY
}