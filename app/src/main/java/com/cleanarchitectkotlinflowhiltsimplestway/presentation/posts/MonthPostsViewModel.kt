package com.cleanarchitectkotlinflowhiltsimplestway.presentation.posts

import androidx.lifecycle.viewModelScope
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.DiaryPost
import com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase.DeletePost
import com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase.GetPostInMonthUseCase
import com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase.MarkPostTutorialShown
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MonthPostsViewModel @Inject constructor(
  private val getPostInMonthUseCase: GetPostInMonthUseCase,
  private val markPostTutorialShown: MarkPostTutorialShown,
  private val deletePost: DeletePost
) : BaseViewModel() {

  private val _posts = MutableSharedFlow<State<Pair<List<DiaryPost>, Boolean>>>()
  val post: Flow<State<Pair<List<DiaryPost>, Boolean>>> = _posts

  private val _deletedPost = MutableSharedFlow<State<Long>>()
  val deletedPost: Flow<State<Long>> = _deletedPost

  fun getPosts(month: Int, year: Int) = viewModelScope.launch {
    getPostInMonthUseCase.invoke(GetPostInMonthUseCase.Params(month, year)).collectLatest { _posts.emit(it) }
  }

  fun markPostTutorialShown() = viewModelScope.launch { markPostTutorialShown.invoke(Unit).collectLatest { } }

  fun deletePost(diaryPost: DiaryPost) = viewModelScope.launch { deletePost.invoke(diaryPost).collectLatest { _deletedPost.emit(it) } }
}