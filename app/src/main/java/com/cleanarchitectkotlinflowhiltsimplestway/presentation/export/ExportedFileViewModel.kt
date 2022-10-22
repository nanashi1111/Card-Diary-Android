package com.cleanarchitectkotlinflowhiltsimplestway.presentation.export

import androidx.lifecycle.viewModelScope
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.ExportedFile
import com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase.GetExportedFileUseCase
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExportedFileViewModel @Inject constructor(private val getExportedFileUseCase: GetExportedFileUseCase): BaseViewModel() {
  private val _exportedFiles = MutableSharedFlow<State<List<ExportedFile>>>()
  val exportedFiles: Flow<State<List<ExportedFile>>> = _exportedFiles

  init {
    getExportedFiles()
  }

  private fun getExportedFiles() = viewModelScope.launch {
    getExportedFileUseCase.invoke(Unit).collectLatest { _exportedFiles.emit(it) }
  }
}