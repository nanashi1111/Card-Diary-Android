package com.cleanarchitectkotlinflowhiltsimplestway.presentation.base

import androidx.fragment.app.DialogFragment
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.adjustWidth

abstract class BaseDialogFragment: DialogFragment() {
  override fun onStart() {
    super.onStart()
    adjustWidth()
  }
}