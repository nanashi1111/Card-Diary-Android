package com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase

import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.data.repository.DiaryRepository
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.DiaryPost
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.mapping
import com.cleanarchitectkotlinflowhiltsimplestway.utils.datetime.getMonthTimeRange
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetPostInMonthUseCase @Inject constructor(private val diaryRepository: DiaryRepository) : UseCase<List<DiaryPost>, GetPostInMonthUseCase.Params>() {

  class Params(val month: Int, val year: Int)

  override fun buildFlow(param: Params): Flow<State<List<DiaryPost>>> {
    return flow {
      emit(State.LoadingState)
      val timeRange = getMonthTimeRange(param.month, param.year)
      val data = diaryRepository.getDiaryPosts(timeRange.first, timeRange.second, fullData = true)
        .map {
          mapping(it)
        }
      emit(State.DataState(data))
    }
  }

}