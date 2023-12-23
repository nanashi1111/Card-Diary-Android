package com.cleanarchitectkotlinflowhiltsimplestway.presentation.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase.PrepareQuoteDataIfNeeded
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewModel
import com.cleanarchitectkotlinflowhiltsimplestway.utils.datetime.currentMonth
import com.cleanarchitectkotlinflowhiltsimplestway.utils.datetime.currentYear
import com.dtv.starter.presenter.utils.log.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(private val prepareQuoteDataIfNeeded: PrepareQuoteDataIfNeeded): BaseViewModel() {

  private var tempYear = currentYear()
  private var tempMonth = currentMonth()
  val currentYearMonth = MutableLiveData<String>().apply {
    postValue("${currentMonth()}-${currentYear()}")
  }

  init {
    prepareQuoteDataIfNeeded()
  }

  var needSmoothScroll = false

  private fun prepareQuoteDataIfNeeded() = viewModelScope.launch {
    prepareQuoteDataIfNeeded.invoke(Unit).collectLatest {}
  }

  fun updateCurrentYearMonth(year: Int, month: Int) = viewModelScope.launch {
    currentYearMonth.postValue("$month-$year")
  }

  fun updateCurrentYearMonth() = viewModelScope.launch {
    currentYearMonth.postValue("$tempMonth-$tempYear")
  }

  fun updateTempYearMonth(year: Int, month: Int) {
    tempYear = year
    tempMonth = month
  }

}