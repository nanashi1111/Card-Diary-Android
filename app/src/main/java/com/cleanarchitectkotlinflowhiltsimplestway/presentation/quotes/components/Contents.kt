package com.cleanarchitectkotlinflowhiltsimplestway.presentation.quotes.components

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.di.ViewModelProviders
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.QuoteCategoryView
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.QuoteContentView
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.quotes.QuotesContentViewModel
import com.dtv.starter.presenter.utils.log.Logger
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import dagger.hilt.android.EntryPointAccessors
import com.cleanarchitectkotlinflowhiltsimplestway.R
import com.cleanarchitectkotlinflowhiltsimplestway.utils.compose.quickSandFontFamily
import com.dtv.starter.presenter.utils.extension.copy
import com.dtv.starter.presenter.utils.extension.share
import com.guru.fontawesomecomposelib.FaIcon
import com.guru.fontawesomecomposelib.FaIcons

val viewModelMap = mutableMapOf<Long, QuotesContentViewModel>()

@OptIn(ExperimentalPagerApi::class)
@Composable
fun CategoryContent(modifier: Modifier = Modifier, pagerState: PagerState, quoteCategoryViews: List<QuoteCategoryView>, onPageChanged: ((Int) -> Unit)? = null) {
  Logger.d("CategoryContent: Rerendered")
  if (quoteCategoryViews.isEmpty()) {
    Box(
      modifier = modifier
        .background(Color.White)
    )
  } else {
    LaunchedEffect(key1 = pagerState.currentPage) {
      Logger.d("QuoteScreen_CategoryContent Render page changed: ${pagerState.currentPage}")
      onPageChanged?.invoke(pagerState.currentPage)
    }
    HorizontalPager(
      state = pagerState, modifier = modifier
        .background(Color.White)
    ) {
      val quoteCategoryView = quoteCategoryViews[it]
      QuoteList(quoteCategoryView = quoteCategoryView)
    }
  }
}


@Composable
fun QuoteList(quoteCategoryView: QuoteCategoryView) {
  val viewModel = provideQuotesContentViewModelById(quoteCategoryId = quoteCategoryView.id)
  val quotesState = viewModel.quotesState.collectAsState()

  val content = when (quotesState.value) {
    is State.LoadingState -> "Loading quotes from ${quoteCategoryView.name}"
    is State.DataState -> "Loaded ${quoteCategoryView.name} ; size: ${(quotesState.value as State.DataState<List<QuoteContentView>>).data.size}"
    is State.ErrorState -> "Error ${quoteCategoryView.name} ; ${(quotesState.value as State.ErrorState).exception} "
  }
  Logger.d("QuotesContentViewModel: ${viewModel.hashCode()}: $content")
  if (quotesState.value is State.DataState) {
    LazyColumn(modifier = Modifier
      .fillMaxSize()
      .background(Color.White), content = {
      itemsIndexed((quotesState.value as State.DataState<List<QuoteContentView>>).data) { index, quote ->
        val activity = LocalContext.current as Activity
        QuoteItem(quote = quote,
          index = index,
          onCopy = { quoteContentView ->
            activity.copy(quoteContentView.content)
          }, onShare = { quoteContentView ->
            activity.share(quoteContentView.content)
          })
      }
    })
  } else {
    CircularProgressIndicator(color = Color(40, 101, 201))
  }
}

@Composable
fun QuoteItem(quote: QuoteContentView, index: Int, onShare: ((QuoteContentView) -> Unit)? = null, onCopy: ((QuoteContentView) -> Unit)? = null) {
  val patterns = listOf<Int>(
    R.drawable.pattern_1,
    R.drawable.pattern_2,
    R.drawable.pattern_3,
    R.drawable.pattern_4,
    R.drawable.pattern_5,
    R.drawable.pattern_6
  )
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .aspectRatio(1f)
      .padding(horizontal = 20.dp, vertical = 10.dp)
      .shadow(10.dp, shape = RoundedCornerShape(10.dp))
      .clickable { onCopy?.invoke(quote) }
  ) {
    Image(painter = painterResource(id = patterns[index % patterns.size]), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)

    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha = 0.5f))
    )

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(20.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = quote.content, style = TextStyle(fontFamily = quickSandFontFamily, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 25.sp), textAlign = TextAlign.Center)
    }

    Row(
      modifier = Modifier
        .fillMaxSize()
        .padding(vertical = 8.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.Bottom
    ) {

      Row(
        modifier = Modifier
          .padding(horizontal = 8.dp)
          .width(35.dp)
          .height(35.dp)
          .clickable {
            onCopy?.invoke(quote)
          }, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center
      ) {
        FaIcon(
          faIcon = FaIcons.Copy,
          tint = Color.White,
        )
      }

      Row(
        modifier = Modifier
          .padding(horizontal = 8.dp)
          .width(35.dp)
          .height(35.dp)
          .clickable {
            onShare?.invoke(quote)
          }, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center
      ) {
        FaIcon(
          faIcon = FaIcons.Share,
          tint = Color.White,
        )
      }


    }
  }
}

@Composable
fun provideQuotesContentViewModelById(quoteCategoryId: Long): QuotesContentViewModel {
  var viewModel = viewModelMap[quoteCategoryId]
  return if (viewModel != null) {
    viewModel
  } else {
    val factory = EntryPointAccessors.fromActivity(LocalContext.current as Activity, ViewModelProviders::class.java).quotesContentViewModelFactory()
    viewModel = factory.create(quoteCategoryId)
    viewModelMap[quoteCategoryId] = viewModel
    viewModel
  }
}

@Composable
@Preview
fun testItem() {
  QuoteItem(QuoteContentView("Hihi hehe"), index = 3)
}