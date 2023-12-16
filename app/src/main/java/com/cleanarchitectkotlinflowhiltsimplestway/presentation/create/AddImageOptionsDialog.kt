package com.cleanarchitectkotlinflowhiltsimplestway.presentation.create

import android.os.Bundle
import android.view.View
import com.cleanarchitectkotlinflowhiltsimplestway.R
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.DiaryPost
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseBottomSheetFragment
import com.dtv.starter.presenter.utils.extension.setSafeOnClickListener

class AddImageOptionsDialog : BaseBottomSheetFragment(R.layout.dialog_add_image_options) {

  lateinit var post: DiaryPost
  var optionCallback: AddImageCallback? = null

  companion object {
    fun newInstance(callback: AddImageCallback): AddImageOptionsDialog = AddImageOptionsDialog().apply {
      arguments = Bundle().apply {
        optionCallback = callback
      }
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    requireView().findViewById<View>(R.id.tvCancel).setSafeOnClickListener {
      dismissAllowingStateLoss()
    }
    requireView().findViewById<View>(R.id.rlCamera).setSafeOnClickListener {
      optionCallback?.onCamera()
      dismissAllowingStateLoss()
    }
    requireView().findViewById<View>(R.id.rlGallery).setSafeOnClickListener {
      optionCallback?.onGallery()
      dismissAllowingStateLoss()
    }
  }
}

interface AddImageCallback {
  fun onCamera()
  fun onGallery()
}