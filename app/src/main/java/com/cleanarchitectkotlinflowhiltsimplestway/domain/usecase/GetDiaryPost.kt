package com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase

import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.DiaryPostData
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.data.repository.DiaryRepository
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.DiaryPost
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.mapping
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetDiaryPost @Inject constructor(private val diaryRepository: DiaryRepository): UseCase<DiaryPost, GetDiaryPost.Params>() {
  class Params(val id: Long)

  override fun buildFlow(param: Params): Flow<State<DiaryPost>> {
    val f: Flow<DiaryPostData> = flow {
      emit(diaryRepository.getDiaryPost(param.id))
    }
    val result: Flow<State<DiaryPost>> = f.map {
      State.DataState(mapping(it))
    }
    return result
  }
}