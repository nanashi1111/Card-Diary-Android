package com.cleanarchitectkotlinflowhiltsimplestway.presentation.dashboard

import androidx.lifecycle.viewModelScope
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewModel
import com.cleanarchitectkotlinflowhiltsimplestway.utils.datetime.currentMonth
import com.cleanarchitectkotlinflowhiltsimplestway.utils.datetime.currentYear
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(): BaseViewModel() {

  val currentYearMonth = MutableStateFlow("${currentMonth()}-${currentYear()}")
  private var tempYear = currentYear()
  private var tempMonth = currentMonth()

  fun updateCurrentYearMonth(year: Int, month: Int) {
    viewModelScope.launch {
      currentYearMonth.emit("$month-$year")
    }
  }

  fun updateCurrentYearMonth() {
    viewModelScope.launch {
      currentYearMonth.emit("$tempMonth-$tempYear")
    }
  }

  fun updateTempYearMonth(year: Int, month: Int) {
    tempYear = year
    tempMonth = month
  }


}