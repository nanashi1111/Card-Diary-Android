package com.cleanarchitectkotlinflowhiltsimplestway.presentation.search

import androidx.lifecycle.viewModelScope
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.DiaryPost
import com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase.DeletePost
import com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase.SearchPostUseCase
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
  private val searchPostUseCase: SearchPostUseCase,
  private val deletePost: DeletePost
) : BaseViewModel() {

  val query = MutableStateFlow("")

  val searchFlow = query.filter { it.isNotEmpty() }
    .debounce(500)
    .distinctUntilChanged()
    .flatMapLatest {
      searchPostUseCase.invoke(SearchPostUseCase.Params(it))
    }

  private val _deletedPost = MutableSharedFlow<State<Long>>()
  val deletedPost: Flow<State<Long>> = _deletedPost

  fun deletePost(diaryPost: DiaryPost) = viewModelScope.launch { deletePost.invoke(diaryPost).collectLatest { _deletedPost.emit(it) } }

}