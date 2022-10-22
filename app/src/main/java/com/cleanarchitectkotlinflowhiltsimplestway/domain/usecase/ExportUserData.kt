package com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase

import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.data.repository.DiaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ExportUserData @Inject constructor(private val diaryRepository: DiaryRepository): UseCase<String, Unit>() {
  override fun buildFlow(param: Unit): Flow<State<String>> {
    return flow {
      val filePath = diaryRepository.exportDbToZipFile()
      if (filePath == null) {
        emit(State.ErrorState(Throwable("An error occured")))
      } else {
        emit(State.DataState(filePath))
      }
    }
  }

}