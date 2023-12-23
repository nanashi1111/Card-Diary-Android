package com.cleanarchitectkotlinflowhiltsimplestway.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.QuoteCategory
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.QuoteContent

@Dao
interface QuoteDao {
  @Query("select * from QuoteCategory")
  fun getQuoteCategories(): List<QuoteCategory>

  @Query("select * from QuoteContent where categoryId = :categoryId")
  fun getQuoteFromCategory(categoryId: Long): List<QuoteContent>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertQuoteCategories(categories: List<QuoteCategory>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertQuoteContent(quotes: List<QuoteContent>)

}