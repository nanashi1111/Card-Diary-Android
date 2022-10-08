package com.cleanarchitectkotlinflowhiltsimplestway.presentation.create

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.WeatherType
import com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase.SaveDiaryUseCase
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewModel
import com.dtv.starter.presenter.utils.log.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateDiaryPostViewModel @Inject constructor(
  private val saveDiaryUseCase: SaveDiaryUseCase
) : BaseViewModel() {

  private val _saveDiaryResultFlow = MutableSharedFlow<State<Boolean>>()
  val saveDiaryResultFlow: Flow<State<Boolean>> = _saveDiaryResultFlow.distinctUntilChanged()

  val selectedWeather = MutableStateFlow(WeatherType.SUNNY)
  val openningOptionMenu = MutableStateFlow(false)

  var postId: Long = 0L

  var focusedImagePosition = 0
    private set(value) {
      field = value
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

  fun toggleOpeningOptionMenu() {
    openningOptionMenu.value = !openningOptionMenu.value
  }

  fun focusImage(position: Int) {
    focusedImagePosition = position
  }

}