package com.cleanarchitectkotlinflowhiltsimplestway.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

const val TEMPLATE_DEFAULT = "default"
const val TEMPLATE_PHOTO = "photo"
const val TEMPLATE_COLOR = "color"

@Entity
data class CardTemplate(
  @PrimaryKey
  val time: String,
  @ColumnInfo(name = "type")
  val type: String,
  @ColumnInfo(name = "data")
  val data: String = "")