package com.cleanarchitectkotlinflowhiltsimplestway.presentation.settings

import androidx.lifecycle.viewModelScope
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase.DeleteAllUseCase
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
  private val deleteAllUseCase: DeleteAllUseCase
): BaseViewModel() {

  private val _deleteAllResult = MutableSharedFlow<State<Boolean>>()
  val deleteAllResult: Flow<State<Boolean>> = _deleteAllResult.distinctUntilChanged()

  fun deleteAll() {
    viewModelScope.launch {
      deleteAllUseCase.invoke(Unit).collectLatest { _deleteAllResult.emit(it) }
    }
  }
}