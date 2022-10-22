package com.cleanarchitectkotlinflowhiltsimplestway.data.repository

import android.content.Context
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.CardTemplate
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.TEMPLATE_DEFAULT
import com.cleanarchitectkotlinflowhiltsimplestway.data.room.AppDatabase
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.FileUtils
import dagger.hilt.android.qualifiers.ApplicationContext

interface CardRepository {
  fun getCardTemplate(month: Int, year: Int): CardTemplate
  fun updateCard(card: CardTemplate)
  fun deleteAll(): Int
}

class CardRepositoryImpl constructor(
  @ApplicationContext private val context: Context,
  private val appDatabase: AppDatabase) : CardRepository {
  override fun getCardTemplate(month: Int, year: Int): CardTemplate {
    val time = String.format("%02d-%04d", month, year)
    val template = appDatabase.cardDao().getCard(time) ?: CardTemplate(time, TEMPLATE_DEFAULT)
    return template
  }

  override fun updateCard(card: CardTemplate) = appDatabase.cardDao().updateCard(card)

  override fun deleteAll() : Int {
    val dataFolder = FileUtils.getParentFolder(context)
    FileUtils.forceClearFolder(dataFolder)
    return appDatabase.cardDao().deleteAll()
  }
}