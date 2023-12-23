package com.cleanarchitectkotlinflowhiltsimplestway.presentation.quotes

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.QuoteCategoryView
import com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase.GetQuoteCategories
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewModel
import com.dtv.starter.presenter.utils.log.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class QuotesCategoryViewModel @Inject constructor(private val getQuoteCategories: GetQuoteCategories): BaseViewModel() {

  private val _quoteCategories: MutableState<List<QuoteCategoryView>> = mutableStateOf(emptyList())
  val quoteCategories: State<List<QuoteCategoryView>> = _quoteCategories

  init {
    Logger.d("QuotesViewModel: ${hashCode()}")
    getQuoteCategories()
  }

  private fun getQuoteCategories() {
    viewModelScope.launch {
      getQuoteCategories.invoke(Unit).collectLatest {
        delay(500)
        if (it is com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State.DataState) {
          _quoteCategories.value = it.data
        }
      }
    }
  }

  fun selectCategory(category: QuoteCategoryView, completion: () -> Unit) {
    viewModelScope.launch (Dispatchers.IO) {
      val categories = mutableListOf<QuoteCategoryView>()
      categories.addAll(_quoteCategories.value.map { it.copy() })
      categories.forEach { it.selected = it.id == category.id }
      _quoteCategories.value = categories

      withContext(Dispatchers.Main) {
        completion()
      }
    }
  }
}