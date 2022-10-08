package com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.CardTemplate
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase.GetCardTemplateUseCase
import com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase.UpdateCardUseCase
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewModel
import com.dtv.starter.presenter.utils.log.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MonthCardViewModel @Inject constructor(
  private val getCardTemplateUseCase: GetCardTemplateUseCase,
  private val updateCardUseCase: UpdateCardUseCase) : BaseViewModel() {

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

  private val _template = MutableSharedFlow<State<CardTemplate>>()
  val template: SharedFlow<State<CardTemplate>> = _template

  fun getCardTemplate(month: Int, year: Int) {
    viewModelScope.launch {
      getCardTemplateUseCase.invoke(GetCardTemplateUseCase.Params(month, year)).collectLatest {
        _template.emit(it)
      }
    }
  }

  fun updateCard(month: Int, year: Int, type: String, data: String, uri: Uri?) {
    Logger.d("UpdateCardUseCase VM: $uri")
    viewModelScope.launch {
      updateCardUseCase.invoke(UpdateCardUseCase.Params(month, year, type, data, uri)).collectLatest {
        _template.emit(it)
      }
    }
  }

}

public enum class MonthCardState {
  FRONT, BEHIND
}