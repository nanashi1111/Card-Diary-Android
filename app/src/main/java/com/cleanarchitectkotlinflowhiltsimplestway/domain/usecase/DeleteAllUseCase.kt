package com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase

import android.content.Context
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.data.repository.CardRepository
import com.cleanarchitectkotlinflowhiltsimplestway.data.repository.DiaryRepository
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.FileUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteAllUseCase @Inject constructor(
  @ApplicationContext private val context: Context,
  private val diaryRepository: DiaryRepository,
  private val cardRepository: CardRepository
) : UseCase<Boolean, Unit>() {
  override fun buildFlow(param: Unit): Flow<State<Boolean>> {
    return flow {
      emit(State.LoadingState)
      diaryRepository.deleteAll()
      cardRepository.deleteAll()
      FileUtils.forceClearAllData(context)
      emit(State.DataState(true))
    }
  }
}