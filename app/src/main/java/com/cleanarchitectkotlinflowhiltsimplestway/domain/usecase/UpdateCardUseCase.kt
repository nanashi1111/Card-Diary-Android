package com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase

import android.content.Context
import android.net.Uri
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.*
import com.cleanarchitectkotlinflowhiltsimplestway.data.repository.CardRepository
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.FileUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

class UpdateCardUseCase @Inject constructor(
  @ApplicationContext
  private val context: Context,
  private val cardRepository: CardRepository): UseCase<CardTemplate, UpdateCardUseCase.Params>() {

  class Params(val month: Int, val year: Int, val type: String, val data: String, val uri: Uri?)

  override fun buildFlow(param: Params): Flow<State<CardTemplate>> {
    return flow {
      emit(State.LoadingState)
      val time = String.format("%02d-%04d", param.month, param.year)
      when (param.type) {
        TEMPLATE_COLOR, TEMPLATE_DEFAULT -> {
          cardRepository.updateCard(CardTemplate(time, param.type, param.data))
        }
        TEMPLATE_PHOTO -> {
          param.uri?.let {
            uri ->
            //Save file
            val targetFile = File(context.filesDir, "card-$time")
            FileUtils.saveFile(context, uri, targetFile)
            //Save db
            cardRepository.updateCard(CardTemplate(time, param.type, targetFile.absolutePath))
          }

        }
      }

      val data = cardRepository.getCardTemplate(param.month, param.year)
      emit(State.DataState(data))
    }
  }
}