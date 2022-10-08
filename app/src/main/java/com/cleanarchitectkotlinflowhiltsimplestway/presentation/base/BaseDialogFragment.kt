package com.cleanarchitectkotlinflowhiltsimplestway.presentation.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.fragment.app.DialogFragment
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.adjustWidth

abstract class BaseDialogFragment : DialogFragment() {

  abstract val needAdjustWidth: Boolean

  override fun onStart() {
    super.onStart()
    dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    if (needAdjustWidth) {
      adjustWidth()
    }
  }
}