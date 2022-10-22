package com.cleanarchitectkotlinflowhiltsimplestway.presentation.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewModel
import com.cleanarchitectkotlinflowhiltsimplestway.utils.datetime.currentMonth
import com.cleanarchitectkotlinflowhiltsimplestway.utils.datetime.currentYear
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(): BaseViewModel() {

  private var tempYear = currentYear()
  private var tempMonth = currentMonth()
  val currentYearMonth = MutableLiveData<String>().apply {
    postValue("${currentMonth()}-${currentYear()}")
  }

  var needSmoothScroll = false

  fun updateCurrentYearMonth(year: Int, month: Int) {
    viewModelScope.launch {
      currentYearMonth.postValue("$month-$year")
    }
  }

  fun updateCurrentYearMonth() {
    viewModelScope.launch {
      currentYearMonth.postValue("$tempMonth-$tempYear")
    }
  }

  fun updateTempYearMonth(year: Int, month: Int) {
    tempYear = year
    tempMonth = month
  }

}