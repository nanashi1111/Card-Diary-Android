package com.cleanarchitectkotlinflowhiltsimplestway.domain.models

import androidx.compose.ui.text.font.FontFamily
import com.cleanarchitectkotlinflowhiltsimplestway.utils.compose.quickSandFontFamily

data class QuoteCreatorData(
  val quoteContentView: QuoteContentView?,
  val backgroundResource: Int?,
  val backgroundColor: QuoteColor = QuoteColor(0, 0, 0, 0.5f),
  val textColor: QuoteColor = QuoteColor(255, 255, 255, 1f),
  val fontFamily: FontFamily = quickSandFontFamily,
  val fontSize: Int = 25
)

data class QuoteColor(val r: Int, val g: Int, val b: Int, val o: Float)