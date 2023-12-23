package com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase

import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.QuoteCategory
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.QuoteContent
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.QuoteData
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.data.repository.QuoteRepository
import com.dtv.starter.presenter.utils.log.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.system.measureTimeMillis

class PrepareQuoteDataIfNeeded @Inject constructor(private val quoteRepository: QuoteRepository) : UseCase<Unit, Unit>() {
  override fun buildFlow(param: Unit): Flow<State<Unit>> {
    return flow {
      val updateDBTime = measureTimeMillis {
        if (quoteRepository.isDataAvailable()) {
          emit(State.DataState(Unit))
        } else {
          val quoteCategories = mutableListOf<QuoteCategory>()
          quoteCategories.add(QuoteCategory(name = "Love"))
          quoteCategories.add(QuoteCategory(name = "Friendship"))
          quoteCategories.add(QuoteCategory(name = "Motivation"))
          quoteCategories.add(QuoteCategory(name = "Family"))
          quoteCategories.add(QuoteCategory(name = "Life"))
          quoteCategories.add(QuoteCategory(name = "Happiness"))
          quoteCategories.add(QuoteCategory(name = "Women"))
          quoteCategories.add(QuoteCategory(name = "Mother"))
          quoteCategories.add(QuoteCategory(name = "Sad"))
          quoteCategories.add(QuoteCategory(name = "Father"))
          quoteCategories.add(QuoteCategory(name = "Positive"))
          quoteCategories.add(QuoteCategory(name = "Alone"))
          quoteCategories.add(QuoteCategory(name = "Trust"))
          quoteCategories.add(QuoteCategory(name = "Funny"))

          val quotes = mutableListOf<QuoteContent>()
          quoteCategories.forEach {
              category ->
            quotes.addAll(QuoteData.getQuotesOfCategory(category.name).map {
              QuoteContent(categoryId = category.id, content = it)
            })
          }
          quoteRepository.insertQuoteCategories(quoteCategories)
          quoteRepository.insertQuoteContent(quotes)
          emit(State.DataState(Unit))
        }
      }
      Logger.d("PrepareDBTime = $updateDBTime")
    }
  }
}