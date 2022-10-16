package com.cleanarchitectkotlinflowhiltsimplestway.utils.extension

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.dtv.starter.presenter.utils.extension.getScreenWidth

fun DialogFragment.adjustWidth() {
  val dialog: Dialog? = dialog
  dialog?.let {
    val width = (requireActivity().getScreenWidth() * 0.9f).toInt()
    val height = ViewGroup.LayoutParams.WRAP_CONTENT
    dialog.window?.setLayout(width, height)
  }
}

fun DialogFragment.adjustWidth(width: Int, height: Int) {
  val dialog: Dialog? = dialog
  dialog?.let {
    dialog.window?.setLayout(width, height)
  }
}