package com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase

import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.data.repository.DiaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ImportUserData @Inject constructor (private val diaryRepository: DiaryRepository): UseCase<Boolean, String> () {
  override fun buildFlow(param: String): Flow<State<Boolean>> {
    return flow {
      val result = diaryRepository.importDbFromZipFile(param)
      emit(State.DataState(result))
    }
  }
}