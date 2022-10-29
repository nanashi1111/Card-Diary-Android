package com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase

import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.data.repository.PatternRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CheckPattern @Inject constructor(private val repository: PatternRepository): UseCase<Boolean, List<Int>>() {
  override fun buildFlow(param: List<Int>): Flow<State<Boolean>> {
    return flow {
      emit(State.DataState(repository.isPatternCorrect(param)))
    }
  }
}