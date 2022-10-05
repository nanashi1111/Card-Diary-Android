package com.cleanarchitectkotlinflowhiltsimplestway.presentation.posts

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.databinding.FragmentMonthPostsBinding
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewBindingFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MonthPostsFragment: BaseViewBindingFragment<FragmentMonthPostsBinding, MonthPostsViewModel>(FragmentMonthPostsBinding::inflate) {
  override val viewModel: MonthPostsViewModel by viewModels()

  private val args: MonthPostsFragmentArgs by navArgs()

  override fun initView() {

    viewBinding.apply {
      rvPosts.layoutManager = LinearLayoutManager(requireContext())
    }
    viewModel.getPosts(args.month, args.year)
  }

  override suspend fun subscribeData() {
    viewModel.post.collectLatest {
      if (it is State.DataState) {
        val data = it.data
        val adapter = MonthPostAdapter(data)
        viewBinding.rvPosts.adapter = adapter
      }
    }
  }

}