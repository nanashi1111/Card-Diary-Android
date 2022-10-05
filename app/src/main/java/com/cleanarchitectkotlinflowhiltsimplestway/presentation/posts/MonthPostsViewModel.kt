package com.cleanarchitectkotlinflowhiltsimplestway.presentation.posts

import androidx.lifecycle.viewModelScope
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.DiaryPost
import com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase.GetPostInMonthUseCase
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MonthPostsViewModel @Inject constructor(private val getPostInMonthUseCase: GetPostInMonthUseCase): BaseViewModel() {

  private val _posts = MutableSharedFlow<State<List<DiaryPost>>>()
  val post: Flow<State<List<DiaryPost>>> = _posts

  fun getPosts(month: Int, year: Int) {
    viewModelScope.launch {
      getPostInMonthUseCase.invoke(GetPostInMonthUseCase.Params(month, year)).collectLatest { _posts.emit(it) }
    }
  }
}