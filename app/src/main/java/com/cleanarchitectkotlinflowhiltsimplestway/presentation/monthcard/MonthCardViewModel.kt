package com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard

import androidx.lifecycle.viewModelScope
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MonthCardViewModel @Inject constructor() : BaseViewModel() {

  private val _monthCardStateFlow = MutableStateFlow(MonthCardState.FRONT)
  val monthCardStateFlow: Flow<MonthCardState> = _monthCardStateFlow

  fun toggleMonthCardState() {
    viewModelScope.launch {
      when (_monthCardStateFlow.value) {
        MonthCardState.BEHIND -> _monthCardStateFlow.value = MonthCardState.FRONT
        else -> _monthCardStateFlow.value = MonthCardState.BEHIND
      }
    }
  }


}

enum class MonthCardState {
  FRONT, BEHIND
}