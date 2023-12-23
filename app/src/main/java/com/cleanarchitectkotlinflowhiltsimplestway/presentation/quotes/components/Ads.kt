package com.cleanarchitectkotlinflowhiltsimplestway.presentation.quotes.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.cleanarchitectkotlinflowhiltsimplestway.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun AdmobBanner () {
  AndroidView(
    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
    factory = { context ->
      AdView(context).apply {
        setAdSize(AdSize.BANNER)
        adUnitId = context.getString(R.string.ads_banner)
        // calling load ad to load our ad.
        loadAd(AdRequest.Builder().build())
      }
    }
  )
}