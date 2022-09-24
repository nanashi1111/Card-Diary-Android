package com.cleanarchitectkotlinflowhiltsimplestway.utils.extension

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

fun <T> Fragment.safeCollectFlow(flow: Flow<T>, action: suspend (T)->Unit) {
  viewLifecycleOwner.lifecycleScope.launch {
    flow.collect {
      action.invoke(it)
    }
  }
}

fun <T> Fragment.safeCollectLatestFlow(flow: Flow<T>, action: suspend (T)->Unit) {
  viewLifecycleOwner.lifecycleScope.launch {
    flow.collectLatest {
      action.invoke(it)
    }
  }
}