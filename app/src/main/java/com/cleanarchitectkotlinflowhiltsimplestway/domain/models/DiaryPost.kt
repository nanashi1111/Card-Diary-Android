package com.cleanarchitectkotlinflowhiltsimplestway.domain.models

import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.WeatherType
import java.io.Serializable

data class DiaryPost(val date: Long,
                     val dayOfMonth: String,
                     val dayOfWeek: String,
                     val images: List<String>,
                     val title: String,
                     val content: String,
                     val weather: WeatherType): Serializable