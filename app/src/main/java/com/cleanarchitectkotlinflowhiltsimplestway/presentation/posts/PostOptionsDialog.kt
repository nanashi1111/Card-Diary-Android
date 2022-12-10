package com.cleanarchitectkotlinflowhiltsimplestway.presentation.posts

import android.os.Bundle
import android.view.View
import com.cleanarchitectkotlinflowhiltsimplestway.R
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.DiaryPost
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseBottomSheetFragment
import com.dtv.starter.presenter.utils.extension.setSafeOnClickListener

class PostOptionsDialog : BaseBottomSheetFragment(R.layout.dialog_post_options) {

  lateinit var post: DiaryPost
  var optionCallback: PostOptionCallback? = null

  companion object {
    fun newInstance(post: DiaryPost, callback: PostOptionCallback): PostOptionsDialog = PostOptionsDialog().apply {
      arguments = Bundle().apply {
        putSerializable("post", post)
        optionCallback = callback
      }
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    post = requireArguments().getSerializable("post") as DiaryPost
    requireView().findViewById<View>(R.id.tvCancel).setSafeOnClickListener {
      dismissAllowingStateLoss()
    }
    requireView().findViewById<View>(R.id.rlView).setSafeOnClickListener {
      optionCallback?.onView(post)
      dismissAllowingStateLoss()
    }
    requireView().findViewById<View>(R.id.rlRemove).setSafeOnClickListener {
      optionCallback?.onRemove(post)
      dismissAllowingStateLoss()
    }
  }
}

interface PostOptionCallback {
  fun onView(post: DiaryPost)
  fun onRemove(post: DiaryPost)
}