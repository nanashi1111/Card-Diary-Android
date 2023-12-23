package com.cleanarchitectkotlinflowhiltsimplestway.data.repository

import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.QuoteCategory
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.QuoteContent
import com.cleanarchitectkotlinflowhiltsimplestway.data.room.QuoteDatabase
import javax.inject.Inject

interface QuoteRepository {

  fun isDataAvailable(): Boolean
  fun getCategories(): List<QuoteCategory>
  fun getQuotes(categoryId: Long): List<QuoteContent>

  fun insertQuoteCategories(categories: List<QuoteCategory>)

  fun insertQuoteContent(quotes: List<QuoteContent>)
}

class QuoteRepositoryImpl constructor(private val quoteDatabase: QuoteDatabase): QuoteRepository {
  override fun isDataAvailable(): Boolean {
    return quoteDatabase.quoteDao().getQuoteCategories().isNotEmpty()
  }

  override fun getCategories(): List<QuoteCategory>  = quoteDatabase.quoteDao().getQuoteCategories()

  override fun getQuotes(categoryId: Long): List<QuoteContent> = quoteDatabase.quoteDao().getQuoteFromCategory(categoryId)
  override fun insertQuoteCategories(categories: List<QuoteCategory>) = quoteDatabase.quoteDao().insertQuoteCategories(categories)

  override fun insertQuoteContent(quotes: List<QuoteContent>)  = quoteDatabase.quoteDao().insertQuoteContent(quotes)

}