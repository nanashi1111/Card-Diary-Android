package com.cleanarchitectkotlinflowhiltsimplestway.presentation.create

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.WeatherType
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.DiaryPost
import com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase.GetDiaryPost
import com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase.SaveDiaryUseCase
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewModel
import com.dtv.starter.presenter.utils.log.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateDiaryPostViewModel @Inject constructor(
  private val saveDiaryUseCase: SaveDiaryUseCase,
  private val getDiaryPost: GetDiaryPost
) : BaseViewModel() {

  private val _saveDiaryResultFlow = MutableSharedFlow<State<Boolean>>()
  val saveDiaryResultFlow: Flow<State<Boolean>> = _saveDiaryResultFlow.distinctUntilChanged()

  val selectedWeather = MutableStateFlow(WeatherType.SUNNY)

  private val _diaryPost = MutableSharedFlow<State<DiaryPost>>()
  val diaryPost: SharedFlow<State<DiaryPost>> = _diaryPost

  val diaryMode = MutableStateFlow(DiaryMode.VIEW)

  var postId: Long = 0L

  var focusedImagePosition = 0
    private set(value) {
      field = value
    }

  fun getDiaryPost(id: Long) {
    viewModelScope.launch {
      getDiaryPost.invoke(GetDiaryPost.Params(id)).collectLatest {
        _diaryPost.emit(it)
      }
    }
  }

  fun saveDiary(images: List<Uri>, title: String, content: String, weather: WeatherType, updateExisting: Boolean) {
    viewModelScope.launch {
      saveDiaryUseCase.invoke(
        SaveDiaryUseCase.Params(
          postId, images, title, content, weather, updateExisting
        )
      ).collectLatest {
        _saveDiaryResultFlow.emit(it)
      }
    }
  }

  fun focusImage(position: Int) {
    focusedImagePosition = position
  }

  fun toggleToEditMode() {
    viewModelScope.launch {
      diaryMode.emit(DiaryMode.EDIT)
    }

  }

}

enum class DiaryMode {
  VIEW, EDIT
}