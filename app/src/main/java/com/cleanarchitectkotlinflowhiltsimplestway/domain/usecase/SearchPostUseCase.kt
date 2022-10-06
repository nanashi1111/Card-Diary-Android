package com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase

import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.data.repository.DiaryRepository
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.DiaryPost
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.mapping
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SearchPostUseCase @Inject constructor(private val diaryRepository: DiaryRepository) : UseCase<List<DiaryPost>, SearchPostUseCase.Params>() {
  class Params(val query: String)

  override fun buildFlow(param: Params): Flow<State<List<DiaryPost>>> {
    return flow {
      emit(State.LoadingState)
      emit(State.DataState(diaryRepository.searchPost(param.query).map {
        mapping(it)
      }))
    }
  }
}