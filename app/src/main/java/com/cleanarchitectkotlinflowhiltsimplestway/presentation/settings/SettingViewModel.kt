package com.cleanarchitectkotlinflowhiltsimplestway.presentation.settings

import androidx.lifecycle.viewModelScope
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase.DeleteAllUseCase
import com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase.ExportUserData
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewModel
import com.dtv.starter.presenter.utils.log.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
  private val deleteAllUseCase: DeleteAllUseCase,
  private val exportUserData: ExportUserData
): BaseViewModel() {

  private val _deleteAllResult = MutableSharedFlow<State<Boolean>>()
  val deleteAllResult: Flow<State<Boolean>> = _deleteAllResult.distinctUntilChanged()

  private val _exportSuccess = MutableSharedFlow<State<String>>()
  val exportSuccess: Flow<State<String>> = _exportSuccess

  fun deleteAll() {
    viewModelScope.launch {
      deleteAllUseCase.invoke(Unit).collectLatest { _deleteAllResult.emit(it) }
    }
  }

  fun exportUserData(folderPath: String) {
    Logger.d("FolderPath = $folderPath")
    viewModelScope.launch {
      exportUserData.invoke(folderPath).collectLatest { _exportSuccess.emit(it) }
    }
  }
}