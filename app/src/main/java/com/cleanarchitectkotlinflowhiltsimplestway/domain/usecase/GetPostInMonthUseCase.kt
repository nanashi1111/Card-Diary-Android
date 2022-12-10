package com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase

import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.data.repository.AppPreferenceRepository
import com.cleanarchitectkotlinflowhiltsimplestway.data.repository.DiaryRepository
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.DiaryPost
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.mapping
import com.cleanarchitectkotlinflowhiltsimplestway.utils.datetime.getMonthTimeRange
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetPostInMonthUseCase @Inject constructor(private val diaryRepository: DiaryRepository, private val appPreferenceRepository: AppPreferenceRepository) : UseCase<Pair<List<DiaryPost>, Boolean>, GetPostInMonthUseCase.Params>() {

  class Params(val month: Int, val year: Int)

  override fun buildFlow(param: Params): Flow<State<Pair<List<DiaryPost>, Boolean>>> {
    return flow {
      emit(State.LoadingState)
      val timeRange = getMonthTimeRange(param.month, param.year)
      val data = diaryRepository.getDiaryPosts(timeRange.first, timeRange.second, fullData = true)
        .map {
          mapping(it)
        }

      val tutorialShown = appPreferenceRepository.isPostTutorialShown()
      emit(State.DataState(Pair(data, tutorialShown)))
    }
  }

}