package com.cleanarchitectkotlinflowhiltsimplestway.presentation.quotes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.quotes.components.AdmobBanner
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.quotes.components.CategoryContent
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.quotes.components.CategoryTabs
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.quotes.components.QuoteScreenAppBar
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun QuotesScreen(quotesCategoryViewModel: QuotesCategoryViewModel = viewModel()) {
  val categories by quotesCategoryViewModel.quoteCategories
  if (categories.isEmpty()) {
    Column (modifier = Modifier
      .fillMaxSize()
      .background(Color.White), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
      CircularProgressIndicator(color = Color(40, 101, 201))
    }
  } else {
    val pagerState = rememberPagerState(
      categories.size
    )
    val scope = rememberCoroutineScope()
    Scaffold(modifier = Modifier.fillMaxSize(),
      topBar = {
        QuoteScreenAppBar()
      }) {
      it.hashCode()
      Column(modifier = Modifier.padding(it)) {
        CategoryTabs(categories = categories, pagerState = pagerState) {
          category ->
          quotesCategoryViewModel.selectCategory(category) {
            scope.launch {
              val selectedIndex = categories.indexOfFirst { it.selected }
              if (selectedIndex >= 0) {
                pagerState.animateScrollToPage(selectedIndex)
              }
            }
          }
        }
        CategoryContent(modifier = Modifier
          .fillMaxWidth()
          .weight(1f), pagerState, categories) { selectedPageIndex ->
          quotesCategoryViewModel.selectCategory(categories[selectedPageIndex]) {}
        }
        AdmobBanner()
      }
    }
  }
}
