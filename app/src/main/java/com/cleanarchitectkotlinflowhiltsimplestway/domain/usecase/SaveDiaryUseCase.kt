package com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase

import android.content.Context
import android.net.Uri
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.WeatherType
import com.cleanarchitectkotlinflowhiltsimplestway.data.repository.DiaryRepository
import com.cleanarchitectkotlinflowhiltsimplestway.domain.exception.CONTENT_EMPTY
import com.cleanarchitectkotlinflowhiltsimplestway.domain.exception.InvalidDiaryPostException
import com.cleanarchitectkotlinflowhiltsimplestway.domain.exception.TITLE_EMPTY
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.FileUtils
import com.dtv.starter.presenter.utils.log.Logger
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

class SaveDiaryUseCase @Inject constructor(
  @ApplicationContext
  private val context: Context,
  private val diaryRepository: DiaryRepository): UseCase<Boolean, SaveDiaryUseCase.Params>() {

  class Params(val id: Long, val images: List<Uri>, val title: String, val content: String, val weatherType: WeatherType)

  override fun buildFlow(param: Params): Flow<State<Boolean>> {
    return flow {
      emit(State.LoadingState)
      //validation
      val error = validate(param)
      if (error == null) {
        //Save file
        val folder = FileUtils.createDiaryFolderForDate(context, param.id)
        val savedFiles = param.images.mapIndexed { index, uri ->
          FileUtils.saveFile(uri, File(folder, "$index"))
        }

        diaryRepository.saveDiaryPost(
          id = param.id,
          images = savedFiles,
          title = param.title,
          content = param.content,
          weather = param.weatherType
        )

        val totalPosts = diaryRepository.getAll()
        Logger.d("TotalPost: ${totalPosts.size}")

        emit(State.DataState(true))
      }
      else {
        emit(State.ErrorState(error))
      }
    }
  }

  private fun validate(param: Params): Exception? {
    if (param.title.isEmpty()) {
      return InvalidDiaryPostException(TITLE_EMPTY)
    }
    if (param.content.isEmpty()) {
      return InvalidDiaryPostException(CONTENT_EMPTY)
    }
    return null
  }
}
