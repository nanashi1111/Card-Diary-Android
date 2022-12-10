package com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase

import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.data.repository.DiaryRepository
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.DiaryPost
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeletePost @Inject constructor(private val diaryRepository: DiaryRepository): UseCase<Long, DiaryPost>() {
  override fun buildFlow(param: DiaryPost): Flow<State<Long>> {
    return flow {
      diaryRepository.deletePost(param.date)
      emit(State.DataState(param.date))
    }
  }
}