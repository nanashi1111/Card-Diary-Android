package com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase

import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.data.repository.DiaryRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import javax.inject.Inject

class ExportUserData @Inject constructor(private val diaryRepository: DiaryRepository): UseCase<String, String>() {
  override fun buildFlow(param: String): Flow<State<String>> {
    return flow {
      val filePath = diaryRepository.exportDbToTextFile(param)
      if (filePath == null) {
        emit(State.ErrorState(Throwable("An error occured")))
      } else {
        emit(State.DataState(filePath))
      }
    }
  }

}