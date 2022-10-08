package com.cleanarchitectkotlinflowhiltsimplestway.presentation.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cleanarchitectkotlinflowhiltsimplestway.R
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseDialogFragment
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.adjustWidth

class LoadingDialog: BaseDialogFragment() {

  override val needAdjustWidth = false

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.dialog_loading, container, false)
  }

  override fun onStart() {
    super.onStart()
    val size = requireActivity().resources.getDimensionPixelSize(R.dimen._100sdp)
    adjustWidth(size, size)
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val d = super.onCreateDialog(savedInstanceState)
    d.setCancelable(false)
    return d
  }

  fun display(fragment: Fragment) {
    try {
      if (isAdded || isVisible) {
        return
      }
      show(fragment.childFragmentManager, javaClass.canonicalName)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  fun hide() {
    try {
      dismissAllowingStateLoss()
    }catch (e: Exception) {
      e.printStackTrace()
    }
  }

}