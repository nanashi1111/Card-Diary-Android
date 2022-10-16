package com.cleanarchitectkotlinflowhiltsimplestway.presentation.search

import com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase.SearchPostUseCase
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
  private val searchPostUseCase: SearchPostUseCase
) : BaseViewModel() {

  val query = MutableStateFlow("")

  val searchFlow = query.filter { it.isNotEmpty() }
    .debounce(500)
    .distinctUntilChanged()
    .flatMapLatest {
      searchPostUseCase.invoke(SearchPostUseCase.Params(it))
    }
}