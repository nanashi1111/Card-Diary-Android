package com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase

import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.data.repository.PatternRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SavePattern @Inject constructor(private val patternRepository: PatternRepository): UseCase<Unit, List<Int>>() {
  override fun buildFlow(param: List<Int>): Flow<State<Unit>> {
    return flow {
      patternRepository.savePattern(param)
      emit(State.DataState(Unit))
    }
  }
}