package com.cleanarchitectkotlinflowhiltsimplestway.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.quotes.QuotesScreen
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.quotes.QuotesCategoryViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuotesActivity : ComponentActivity() {
  private val quotesCategoryViewModel: QuotesCategoryViewModel by viewModels()
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        val systemUiController = rememberSystemUiController()
        systemUiController.setSystemBarsColor(
          color = Color.White,
          darkIcons = true
        )
        QuotesScreen(quotesCategoryViewModel = quotesCategoryViewModel)
      }
    }
  }
}
