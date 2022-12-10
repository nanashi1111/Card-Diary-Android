package com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase

import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.data.repository.AppPreferenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MarkPostTutorialShown @Inject constructor(private val appPreferenceRepository: AppPreferenceRepository): UseCase<Unit, Unit>() {
  override fun buildFlow(param: Unit): Flow<State<Unit>> {
    return flow {
      appPreferenceRepository.setPostTutorialShown()
      emit(State.DataState(Unit))
    }
  }
}