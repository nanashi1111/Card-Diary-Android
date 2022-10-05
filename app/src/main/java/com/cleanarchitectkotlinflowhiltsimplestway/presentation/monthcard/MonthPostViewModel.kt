package com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard

import androidx.lifecycle.viewModelScope
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.MonthData
import com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase.GetPostCountInMonthUseCase
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MonthPostViewModel @Inject constructor(private val getPostInMonthUseCase: GetPostCountInMonthUseCase): BaseViewModel() {

  private val _monthData = MutableSharedFlow<State<MonthData>>()
  val monthData: Flow<State<MonthData>> = _monthData

  fun getPostInMonth(month: Int, year: Int) {
    viewModelScope.launch {

      getPostInMonthUseCase.invoke(GetPostCountInMonthUseCase.Params(month, year)).collectLatest { _monthData.emit(it) }
    }
  }
}