package com.cleanarchitectkotlinflowhiltsimplestway.presentation.create.photo

import android.os.Bundle
import android.view.View
import com.cleanarchitectkotlinflowhiltsimplestway.R
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseBottomSheetFragment
import com.dtv.starter.presenter.utils.extension.setSafeOnClickListener

class PhotoSelectorDialog: BaseBottomSheetFragment(R.layout.dialog_select_photo_chooser) {
  var listener: PhotoSelectorListener? = null

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    requireView().findViewById<View>(R.id.tvCancel).setSafeOnClickListener { dismissAllowingStateLoss() }
    requireView().findViewById<View>(R.id.llCamera).setSafeOnClickListener {
      listener?.onSelectPhotoPicker(PhotoSelectorMethod.CAMERA)
      dismissAllowingStateLoss()
    }
    requireView().findViewById<View>(R.id.llGallery).setSafeOnClickListener {
      listener?.onSelectPhotoPicker(PhotoSelectorMethod.GALLERY)
      dismissAllowingStateLoss()
    }
  }
}

enum class PhotoSelectorMethod {
  GALLERY, CAMERA
}

interface PhotoSelectorListener {
  fun onSelectPhotoPicker(picker: PhotoSelectorMethod)
}