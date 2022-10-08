package com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.CardTemplate
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase.GetCardTemplateUseCase
import com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase.UpdateCardUseCase
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FrontCardViewModel @Inject constructor(
  private val getCardTemplateUseCase: GetCardTemplateUseCase, private val updateCardUseCase: UpdateCardUseCase
) : BaseViewModel() {

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
    viewModelScope.launch {
      updateCardUseCase.invoke(UpdateCardUseCase.Params(month, year, type, data, uri)).collectLatest {
        _template.emit(it)
      }
    }
  }
}