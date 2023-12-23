package com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase

import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.data.repository.QuoteRepository
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.QuoteCategoryView
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetQuoteCategories @Inject constructor(private val quoteRepository: QuoteRepository) : UseCase<List<QuoteCategoryView>, Unit>() {
  override fun buildFlow(param: Unit): Flow<State<List<QuoteCategoryView>>> {
    return flow {
      val result = quoteRepository.getCategories().mapIndexed {
        index, category ->
        QuoteCategoryView(category.id, category.name, index == 0)
      }
      emit(State.DataState(result))
    }
  }
}