package com.cleanarchitectkotlinflowhiltsimplestway.presentation.create

import android.net.Uri
import android.os.Bundle
import com.cleanarchitectkotlinflowhiltsimplestway.databinding.FragmentSelectedPhotoBinding
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewBindingFragment
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.create.photo.bindUriToImage

class SelectedPhotoFragment : BaseViewBindingFragment<FragmentSelectedPhotoBinding, CreateDiaryPostViewModel>(FragmentSelectedPhotoBinding::inflate) {
  override val viewModel: CreateDiaryPostViewModel by lazy {
    (requireParentFragment() as CreateDiaryPostFragment).viewModel
  }

  override fun initView() {
    viewBinding.apply {
      val uri = requireArguments().getParcelable(KEY_IMAGE_PATH) as? Uri
      uri?.let {
        ivSelectedImage.bindUriToImage(it)
      }
    }
  }

  override suspend fun subscribeData() {
  }

  companion object {
    const val KEY_IMAGE_PATH = "image_path"
    fun getInstance(uri: Uri): SelectedPhotoFragment {
      return SelectedPhotoFragment().apply {
        arguments = Bundle().apply {
          putParcelable(KEY_IMAGE_PATH, uri)
        }
      }
    }
  }
}