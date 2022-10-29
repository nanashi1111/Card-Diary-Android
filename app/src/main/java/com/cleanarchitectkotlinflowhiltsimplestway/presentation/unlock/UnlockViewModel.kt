package com.cleanarchitectkotlinflowhiltsimplestway.presentation.unlock

import androidx.lifecycle.viewModelScope
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase.CheckPattern
import com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase.CheckPatternSetup
import com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase.SavePattern
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.Serializable
import javax.inject.Inject

@HiltViewModel
class UnlockViewModel @Inject constructor(
  private val savePattern: SavePattern,
  private val checkPattern: CheckPattern,
  private val isPatternSetup: CheckPatternSetup
): BaseViewModel() {

  private val _patternSetup: MutableSharedFlow<State<Boolean>> = MutableSharedFlow()
  val patternSetup: Flow<State<Boolean>> = _patternSetup

  private val _patternData: MutableStateFlow<PatternData> = MutableStateFlow(PatternData.default())
  val patternPurpose: StateFlow<PatternData> = _patternData

  private val _success = MutableSharedFlow<Boolean>()
  val success: Flow<Boolean> = _success

  private var cachedPattern: MutableList<Int> = mutableListOf()


  fun initPattern(data: PatternData) {
    _patternData.value = data
  }

  fun onPatternComplete(pattern: MutableList<Int>) {
    val data = _patternData.value
    when (data.purpose) {
      PatternPurpose.SETUP -> {
        when (data.step) {
          PatternStep.FIRST -> {
            cachedPattern.clear()
            cachedPattern.addAll(pattern)
            data.step = PatternStep.CONFIRM
            val newData = PatternData(data.purpose, data.step)
            _patternData.value = newData
          }

          PatternStep.CONFIRM -> {
            if (cachedPattern != null && com.cleanarchitectkotlinflowhiltsimplestway.utils.pattern.equals(cachedPattern!!, pattern)) {
              viewModelScope.launch {
                savePattern.invoke(pattern).collectLatest {
                  if (it is State.DataState) {
                    _success.emit(true)
                  }
                }

              }
            } else {
              _patternData.value = PatternData.default()
              viewModelScope.launch { _success.emit(false) }
            }
          }
        }
      }

      PatternPurpose.UNLOCK -> {
        viewModelScope.launch {
          checkPattern.invoke(pattern).collectLatest {
            if (it is State.DataState) {
              _patternData.value = PatternData(purpose = PatternPurpose.UNLOCK, step = PatternStep.CONFIRM)
              _success.emit( it.data)
            }
          }
        }
      }
    }
  }

  fun checkPatternSetup() {
    viewModelScope.launch { isPatternSetup.invoke(Unit).collectLatest { _patternSetup.emit(it) } }
  }

}

class PatternData(var purpose: PatternPurpose, var step: PatternStep): Serializable {
  companion object {
    fun default(): PatternData {
      return PatternData(PatternPurpose.SETUP, PatternStep.FIRST)
    }
  }
}

enum class PatternPurpose {
  SETUP, UNLOCK
}

enum class PatternStep {
  FIRST, CONFIRM
}