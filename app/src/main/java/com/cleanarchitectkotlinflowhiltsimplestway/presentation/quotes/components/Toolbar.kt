package com.cleanarchitectkotlinflowhiltsimplestway.presentation.quotes.components

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cleanarchitectkotlinflowhiltsimplestway.utils.compose.quickSandFontFamily

@Composable
fun QuoteScreenAppBar() {
  TopAppBar(
    title = {
      Text("Quotes", fontFamily = quickSandFontFamily, fontWeight = FontWeight.Bold, fontSize = 20.sp)
    },
    backgroundColor = Color.White,
    navigationIcon = {
      Box(modifier = Modifier.padding(0.dp)) {
        val activity = (LocalContext.current as ComponentActivity)
        IconButton(onClick = {
          activity.finish()
        }, content = { Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = "") })

      }
    },
    elevation = 0.dp
  )
}