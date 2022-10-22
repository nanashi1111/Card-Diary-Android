package com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase

import android.content.Context
import com.cleanarchitectkotlinflowhiltsimplestway.R
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.data.repository.DiaryRepository
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.ExportedFile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class GetExportedFileUseCase @Inject constructor(
  @ApplicationContext private val context: Context,
  private val diaryRepository: DiaryRepository): UseCase<List<ExportedFile>, Unit>() {
  override fun buildFlow(param: Unit): Flow<State<List<ExportedFile>>> {
    return flow {
      val exportedFiles = diaryRepository.getExportedFiles().map {
        val createdAt = it.lastModified()
        val sdf = SimpleDateFormat("MM-dd-yyyy HH:mm:ss")
        ExportedFile(it.name, String.format(context.getString(R.string.exported_file_description), sdf.format(Date(createdAt))))
      }
      emit(State.DataState(exportedFiles))
    }
  }
}