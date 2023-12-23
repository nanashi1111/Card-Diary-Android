package com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase

import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.data.repository.QuoteRepository
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.QuoteContentView
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetQuotesFromCategory @Inject constructor(private val quoteRepository: QuoteRepository): UseCase<List<QuoteContentView>, Long>() {
  override fun buildFlow(param: Long): Flow<State<List<QuoteContentView>>> {
    return flow {
      val result = quoteRepository.getQuotes(categoryId = param).map { QuoteContentView(it.content) }
      emit(State.DataState(result))
    }
  }
}