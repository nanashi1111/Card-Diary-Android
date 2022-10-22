package com.cleanarchitectkotlinflowhiltsimplestway.presentation.settings

import androidx.lifecycle.viewModelScope
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase.DeleteAllUseCase
import com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase.ExportUserData
import com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase.ImportUserData
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
  private val deleteAllUseCase: DeleteAllUseCase,
  private val exportUserData: ExportUserData,
  private val importUserData: ImportUserData
): BaseViewModel() {

  private val _deleteAllResult = MutableSharedFlow<State<Boolean>>(replay = 0)
  val deleteAllResult: Flow<State<Boolean>> = _deleteAllResult.distinctUntilChanged()

  private val _exportSuccess = MutableSharedFlow<State<String>>(replay = 0)
  val exportSuccess: Flow<State<String>> = _exportSuccess

  private val _importSuccess = MutableSharedFlow<State<Boolean>>(replay = 0)
  val importSuccess: Flow<State<Boolean>> = _importSuccess

  fun deleteAll() {
    viewModelScope.launch {
      deleteAllUseCase.invoke(Unit).collectLatest { _deleteAllResult.emit(it) }
    }
  }

  fun exportUserData() {
    viewModelScope.launch {
      exportUserData.invoke(Unit).collectLatest {
        _exportSuccess.emit(it)
      }
    }
  }

  fun importUserData(filePath: String) {
    viewModelScope.launch {
      importUserData.invoke(filePath).collectLatest { _importSuccess.emit(it) }
    }
  }
}