package com.cleanarchitectkotlinflowhiltsimplestway.presentation.quotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.QuoteCategoryView
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.QuoteContentView
import com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase.GetQuotesFromCategory
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewModel
import dagger.Component.Factory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class QuotesContentViewModel @AssistedInject constructor(
  private val getQuotesFromCategory: GetQuotesFromCategory,
  @Assisted
  private val categoryId: Long
) : BaseViewModel() {

  private val _quotesState = MutableStateFlow<State<List<QuoteContentView>>>(State.LoadingState)
  val quotesState = _quotesState.asStateFlow()
  @AssistedFactory
  interface QuotesContentViewModelFactory {
    fun create(categoryId: Long): QuotesContentViewModel
  }

//  companion object {
//    fun provideQuotesContentViewModelFactory(factory: QuotesContentViewModelFactory, categoryId: Long): ViewModelProvider.Factory {
//      return object :ViewModelProvider.Factory {
//        override fun <T : ViewModel> create(modelClass: Class<T>): T {
//          return factory.create(categoryId) as T
//        }
//      }
//    }
//  }

  init {
    getQuotesFromCategory()
  }

  private fun getQuotesFromCategory() {
    viewModelScope.launch {
      getQuotesFromCategory.invoke(categoryId).collectLatest {
        _quotesState.emit(it)
      }
    }
  }

}