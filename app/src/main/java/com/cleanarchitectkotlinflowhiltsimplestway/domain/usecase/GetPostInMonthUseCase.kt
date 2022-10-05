package com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase

import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.DiaryPostData
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.data.repository.DiaryRepository
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.DiaryPost
import com.cleanarchitectkotlinflowhiltsimplestway.utils.datetime.getMonthTimeRange
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class GetPostInMonthUseCase @Inject constructor(private val diaryRepository: DiaryRepository) : UseCase<List<DiaryPost>, GetPostInMonthUseCase.Params>() {

  class Params(val month: Int, val year: Int)

  override fun buildFlow(param: Params): Flow<State<List<DiaryPost>>> {
    return flow {
      emit(State.LoadingState)
      val timeRange = getMonthTimeRange(param.month, param.year)
      val data = diaryRepository.getDiaryPosts(timeRange.first, timeRange.second)
        .map {
          mapping(it)
        }
      emit(State.DataState(data))
    }
  }

  private fun mapping(entity: DiaryPostData): DiaryPost {
    val date = Date(entity.date)
    val dayOfMonth = SimpleDateFormat("mm").format(date)
    val dayOfWeek = SimpleDateFormat("EEE").format(date)
    return DiaryPost(
      date = entity.date,
      images = entity.images,
      title = entity.title,
      content = entity.content,
      weather = entity.weather,
      dayOfMonth = dayOfMonth,
      dayOfWeek = dayOfWeek
    )
  }
}