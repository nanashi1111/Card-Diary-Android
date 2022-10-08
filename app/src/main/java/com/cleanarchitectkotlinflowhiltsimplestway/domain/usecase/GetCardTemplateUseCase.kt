package com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase

import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.CardTemplate
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.data.repository.CardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCardTemplateUseCase @Inject constructor(private val cardRepository: CardRepository): UseCase<CardTemplate, GetCardTemplateUseCase.Params>() {
  class Params(val month: Int, val year: Int)

  override fun buildFlow(param: Params): Flow<State<CardTemplate>> {
    return flow {
      emit(State.LoadingState)
      emit(State.DataState(cardRepository.getCardTemplate(param.month, param.year)))
    }
  }
}