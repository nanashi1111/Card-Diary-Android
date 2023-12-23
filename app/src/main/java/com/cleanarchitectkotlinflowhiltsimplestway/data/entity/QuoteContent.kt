package com.cleanarchitectkotlinflowhiltsimplestway.data.entity

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity
data class QuoteContent (
  @PrimaryKey
  val id: Long = QuoteData.randomId(),
  @ColumnInfo(name = "categoryId")
  val categoryId: Long,
  @ColumnInfo(name = "content")
  val content: String)