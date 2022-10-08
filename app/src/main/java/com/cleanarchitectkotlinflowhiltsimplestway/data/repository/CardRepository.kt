package com.cleanarchitectkotlinflowhiltsimplestway.data.repository

import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.CardTemplate
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.TEMPLATE_DEFAULT
import com.cleanarchitectkotlinflowhiltsimplestway.data.room.AppDatabase
import com.dtv.starter.presenter.utils.log.Logger
import kotlin.system.measureTimeMillis

interface CardRepository {
  fun getCardTemplate(month: Int, year: Int): CardTemplate
  fun updateCard(card: CardTemplate)
  fun deleteAll(): Int
}

class CardRepositoryImpl constructor(private val appDatabase: AppDatabase) : CardRepository {
  override fun getCardTemplate(month: Int, year: Int): CardTemplate {
    val time = String.format("%02d-%04d", month, year)
    val template = appDatabase.cardDao().getCard(time) ?: CardTemplate(time, TEMPLATE_DEFAULT)
    Logger.d("CardTemplate: $time : $template")
    return template
  }

  override fun updateCard(card: CardTemplate) = appDatabase.cardDao().updateCard(card)

  override fun deleteAll() = appDatabase.cardDao().deleteAll()
}