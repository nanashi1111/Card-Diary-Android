package com.cleanarchitectkotlinflowhiltsimplestway.presentation.search

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cleanarchitectkotlinflowhiltsimplestway.R
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.databinding.FragmentSearchBinding
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.DiaryPost
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewBindingFragment
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.dialog.ConfirmDialog
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.dialog.ConfirmListener
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.posts.MonthPostsFragmentDirections
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.posts.PostAdapter
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.posts.PostOptionCallback
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.posts.PostOptionsDialog
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.safeNavigate
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.safeNavigateUp
import com.dtv.starter.presenter.utils.extension.beVisibleIf
import com.dtv.starter.presenter.utils.extension.showKeyboard
import com.dtv.starter.presenter.utils.log.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment: BaseViewBindingFragment<FragmentSearchBinding, SearchViewModel>(FragmentSearchBinding::inflate) {

  override val viewModel: SearchViewModel by viewModels()

  private val adapter: PostAdapter by lazy {
    PostAdapter(mutableListOf(), onPostOptionSelected = {
      post ->
      PostOptionsDialog.newInstance(post, callback = object : PostOptionCallback {
        override fun onRemove(post: DiaryPost) {
          Logger.d("Removing: ${post.title}")
          ConfirmDialog.getInstance().apply {
            listener = object : ConfirmListener {
              override fun onConfirmed() {
                viewModel.deletePost(post)
              }

            }
          }.show(this@SearchFragment.childFragmentManager, "Confirm")
        }

        override fun onView(post: DiaryPost) {
          Logger.d("View: ${post.title}")
          findNavController().safeNavigate(MonthPostsFragmentDirections.actionMonthPostsFragmentToCreateDiaryPostFragment(post = post.simpleObject(), time = 0L))
        }
      }).show(childFragmentManager, "Options")
    }) {
        post ->
        findNavController().safeNavigate(
          SearchFragmentDirections.actionSearchFragmentToCreateDiaryPostFragment(post.simpleObject(), 0L)
        )
    }
  }

  override fun initView() {
    viewBinding.apply {
      rvPosts.layoutManager = LinearLayoutManager(requireContext())
      rvPosts.adapter = adapter
      etSearch.doAfterTextChanged {
        viewModel.query.value = it.toString()
      }
      etSearch.requestFocus()
      requireActivity().showKeyboard(etSearch)
      ivBack.setOnClickListener {
        findNavController().safeNavigateUp()
      }
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    lifecycleScope.launch {
      lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.searchFlow.collectLatest {
          if (it is State.DataState) {
            val data = it.data
            adapter.submit(data)
            viewBinding.emptyDataset.apply {
              root.beVisibleIf(data.isEmpty())
              tvMessage.text = getString(R.string.message_no_result)
            }
          }
        }
      }
    }

    lifecycleScope.launch {
      lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.deletedPost.collect {
          if (it is State.DataState) {
            adapter.removePost(it.data)
          }
        }
      }
    }
  }

  override suspend fun subscribeData() {
  }
}