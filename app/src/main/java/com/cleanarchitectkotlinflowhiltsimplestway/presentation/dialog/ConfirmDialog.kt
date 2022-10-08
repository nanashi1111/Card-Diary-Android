package com.cleanarchitectkotlinflowhiltsimplestway.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.cleanarchitectkotlinflowhiltsimplestway.R
import com.cleanarchitectkotlinflowhiltsimplestway.databinding.DialogConfirmBinding
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseDialogFragment

class ConfirmDialog: BaseDialogFragment() {

  lateinit var vb: DialogConfirmBinding

  var listener: ConfirmListener? = null

  override val needAdjustWidth = true

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    vb = DataBindingUtil.inflate(inflater, R.layout.dialog_confirm, container, false)
    return vb.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    var title = requireArguments().getString(KEY_TITLE) ?: ""
    if (title.isEmpty()) {
      title = getString(R.string.please_confirm)
    }
    var message = requireArguments().getString(KEY_MESSAGE) ?: ""
    if (message.isEmpty()) {
      message = getString(R.string.general_confirm)
    }
    vb.tvTitle.text = title
    vb.tvMessage.text = message
    vb.btCancel.setOnClickListener {
      dismissAllowingStateLoss()
    }
    vb.btOk.setOnClickListener {
      listener?.onConfirmed()
      dismissAllowingStateLoss()
    }
  }

  companion object {
    const val KEY_TITLE = "title"
    const val KEY_MESSAGE = "message"
    fun getInstance(title: String = "", message: String = ""): ConfirmDialog {
      return ConfirmDialog().apply {
        arguments = Bundle().apply {
          putString(KEY_TITLE, title)
          putString(KEY_MESSAGE, message)
        }
      }
    }
  }
}

interface ConfirmListener {
  fun onConfirmed()
}