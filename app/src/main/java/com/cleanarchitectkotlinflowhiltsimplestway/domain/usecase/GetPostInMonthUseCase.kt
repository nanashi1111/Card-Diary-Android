package com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase

import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.DiaryPostData
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.data.repository.DiaryRepository
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.DayData
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.MonthData
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.MonthGeneralData
import com.cleanarchitectkotlinflowhiltsimplestway.utils.datetime.getDayTimeRange
import com.cleanarchitectkotlinflowhiltsimplestway.utils.datetime.getMonthTimeRange
import com.cleanarchitectkotlinflowhiltsimplestway.utils.datetime.numberOfDayInMonth
import com.dtv.starter.presenter.utils.log.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class GetPostInMonthUseCase @Inject constructor (private val diaryRepository: DiaryRepository): UseCase<MonthData, GetPostInMonthUseCase.Params>() {
  class Params(val month: Int, val year: Int)

  override fun buildFlow(param: Params): Flow<State<MonthData>> {
    return flow {
      Logger.d("getPostInMonth : ${param.month}/${param.year}")
      emit(State.LoadingState)
      val timeRange = getMonthTimeRange(param.month, param.year)
      val data = diaryRepository.getDiaryPosts(timeRange.first, timeRange.second)
      val numberOfDayInMonth = numberOfDayInMonth(param.month, param.year)
      val daysData = mutableListOf<DayData>()
      for (i in 1..numberOfDayInMonth) {
        daysData.add(DayData(day = i, month = param.month, year = param.year, numberOfPosts = numberOfPostsInDay(data, i, param.month, param.year)))
      }
      val numberOfDayWithPosts = daysData.filter {
        it.numberOfPosts > 0
      }.size
      val generalData = MonthGeneralData(dayWithPosts = numberOfDayWithPosts, totalDays = numberOfDayInMonth, progress = 100 * numberOfDayWithPosts / numberOfDayInMonth)
      emit(State.DataState(MonthData(generalDayData = generalData, daysData = daysData)))
    }
  }

  private fun numberOfPostsInDay(posts: List<DiaryPostData>,day: Int, month: Int, year: Int): Int {
    val timeRange = getDayTimeRange(day, month, year)
    return posts.filter {
      it.date >= timeRange.first && it.date <= timeRange.second
    }.size
  }

}