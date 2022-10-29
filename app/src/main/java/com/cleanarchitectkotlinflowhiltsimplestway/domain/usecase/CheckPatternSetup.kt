package com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase

import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.data.repository.PatternRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CheckPatternSetup @Inject constructor(private val patternRepository: PatternRepository): UseCase<Boolean, Unit>() {
  override fun buildFlow(param: Unit): Flow<State<Boolean>> {
    return flow {
      emit(State.DataState(patternRepository.isPatternSetup()))
    }
  }
}