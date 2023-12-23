package com.cleanarchitectkotlinflowhiltsimplestway.presentation.quotes.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.TabRowDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.QuoteCategoryView
import com.cleanarchitectkotlinflowhiltsimplestway.utils.compose.quickSandFontFamily
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.max

@OptIn(ExperimentalPagerApi::class)
@Composable
fun CategoryTabs(categories: List<QuoteCategoryView>, pagerState: PagerState, onCategorySelected: (QuoteCategoryView) -> Unit) {
  if (categories.isEmpty()) {
    Box(modifier = Modifier.fillMaxSize())
  } else {
    val selectedTabIndex = categories.indexOfFirst { it.selected }
    val selectedTabTextColor = Color(40, 101, 201)
    val unSelectedTabTextColor = Color(13, 29, 47)
    ScrollableTabRow(selectedTabIndex = selectedTabIndex,
      modifier = Modifier.fillMaxWidth(),
      containerColor = Color.White,
      edgePadding = 0.dp,
      indicator = {
        TabRowDefaults.Indicator(
          //Modifier.tabIndicatorOffset(it[selectedTabIndex]),
          Modifier.pagerTabIndicatorOffsetM3(pagerState = pagerState, tabPositions = it),
          color = selectedTabTextColor
        )
      }) {
      categories.forEach {
        val tabTextColor = if (it.selected) {
          selectedTabTextColor
        } else {
          unSelectedTabTextColor
        }
        Tab(
          selected = it.selected,
          onClick = { onCategorySelected(it) },
          text = {
            Text(text = it.name.uppercase(),  fontFamily = quickSandFontFamily, fontWeight = FontWeight.SemiBold, style = TextStyle(color = tabTextColor, fontSize = 17.sp))
          },

          )
      }
    }
  }
}

@OptIn(ExperimentalPagerApi::class)
fun Modifier.pagerTabIndicatorOffsetM3(
  pagerState: PagerState,
  tabPositions: List<androidx.compose.material3.TabPosition>,
): Modifier = composed {
  val targetIndicatorOffset: Dp
  val indicatorWidth: Dp

  val currentTab = tabPositions[pagerState.currentPage]
  val targetPage = pagerState.targetPage
  val targetTab = targetPage?.let { tabPositions.getOrNull(it) }

  if (targetTab != null) {
    // The distance between the target and current page. If the pager is animating over many
    // items this could be > 1
    val targetDistance = (targetPage - pagerState.currentPage).absoluteValue
    // Our normalized fraction over the target distance
    val fraction = (pagerState.currentPageOffset / max(targetDistance, 1)).absoluteValue

    targetIndicatorOffset = lerp(currentTab.left, targetTab.left, fraction)
    indicatorWidth = abs(lerp(currentTab.width, targetTab.width, fraction).value.toDouble()).dp
  } else {
    // Otherwise we just use the current tab/page
    targetIndicatorOffset = currentTab.left
    indicatorWidth = currentTab.width
  }

  fillMaxWidth()
    .wrapContentSize(Alignment.BottomStart)
    .offset(x = targetIndicatorOffset)
    .width(indicatorWidth)
}
