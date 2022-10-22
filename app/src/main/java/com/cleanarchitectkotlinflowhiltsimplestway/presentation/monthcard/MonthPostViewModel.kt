package com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.viewModelScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.CardTemplate
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.MonthData
import com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase.GetCardTemplateUseCase
import com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase.GetPostCountInMonthUseCase
import com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase.UpdateCardUseCase
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewModel
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard.MonthCardFragment.Companion.KEY_CARD_DESIGN_CHANGED
import com.dtv.starter.presenter.utils.log.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MonthPostViewModel @Inject constructor(
  @ApplicationContext private val context: Context,
  private val getPostInMonthUseCase: GetPostCountInMonthUseCase,
  private val getCardTemplateUseCase: GetCardTemplateUseCase, private val updateCardUseCase: UpdateCardUseCase) : BaseViewModel() {

  private val _monthData = MutableSharedFlow<State<MonthData>>()
  val monthData: Flow<State<MonthData>> = _monthData

  fun getPostInMonth(month: Int, year: Int) {
    viewModelScope.launch {
      Logger.d("getPostInMonth $month/$year : ${System.currentTimeMillis()} ; instance: ${hashCode()}")
      getPostInMonthUseCase.invoke(GetPostCountInMonthUseCase.Params(month, year)).collectLatest { _monthData.emit(it) }
    }
  }

  private val _template = MutableSharedFlow<State<CardTemplate>>(replay = Int.MAX_VALUE)
  val template: SharedFlow<State<CardTemplate>> = _template

  fun getCardTemplate(month: Int, year: Int) {
    viewModelScope.launch {
      Logger.d("getCardTemplate $month/$year : ${System.currentTimeMillis()} ; instance: ${hashCode()}")
      getCardTemplateUseCase.invoke(GetCardTemplateUseCase.Params(month, year)).collectLatest {
        _template.emit(it)
      }
    }
  }

  override fun onCleared() {
    Logger.d("getCardTemplate onCleared : ${System.currentTimeMillis()} ; instance: ${hashCode()}")
    super.onCleared()
  }

  fun updateCard(month: Int, year: Int, type: String, data: String, uri: Uri?) {
    GlobalScope.launch {
      updateCardUseCase.invoke(UpdateCardUseCase.Params(month, year, type, data, uri)).collectLatest {
        _template.emit(it)

        if (it is State.DataState) {
          val intent = Intent().apply {
            action = KEY_CARD_DESIGN_CHANGED
          }
          LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }
      }
    }
  }

}