package com.cleanarchitectkotlinflowhiltsimplestway.presentation.posts

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.databinding.FragmentMonthPostsBinding
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewBindingFragment
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.posts.dateselection.OnSelectDateToWrite
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.posts.dateselection.SelectDateToWriteDialog
import com.cleanarchitectkotlinflowhiltsimplestway.utils.datetime.monthInText
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.safeNavigate
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.safeNavigateUp
import com.dtv.starter.presenter.utils.extension.beVisibleIf
import com.dtv.starter.presenter.utils.extension.setSafeOnClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.util.*

@AndroidEntryPoint
class MonthPostsFragment : BaseViewBindingFragment<FragmentMonthPostsBinding, MonthPostsViewModel>(FragmentMonthPostsBinding::inflate) {
  override val viewModel: MonthPostsViewModel by viewModels()

  private val args: MonthPostsFragmentArgs by navArgs()

  private val adapter: PostAdapter by lazy {
    PostAdapter(mutableListOf()) { post ->
      findNavController().safeNavigate(MonthPostsFragmentDirections.actionMonthPostsFragmentToCreateDiaryPostFragment(post = post.simpleObject(), time = 0L))
    }
  }

  override fun initView() {

    viewBinding.apply {
      rvPosts.layoutManager = LinearLayoutManager(requireContext())
      rvPosts.adapter = adapter
      tvTitle.text = "${monthInText(args.month - 1, true)} / ${args.year}"
      ivBack.setSafeOnClickListener { findNavController().safeNavigateUp() }
      ivWritePost.setSafeOnClickListener { createPost() }
    }
    viewModel.getPosts(args.month, args.year)
  }

  private fun createPost() {
    SelectDateToWriteDialog.getInstance(args.month, args.year).apply {
      callback = object : OnSelectDateToWrite {
        override fun onSelected(timestamp: Long) {
          val c = Calendar.getInstance()
          val currentHour = c.get(Calendar.HOUR)
          val currentMinute = c.get(Calendar.MINUTE)
          val currentSecond = c.get(Calendar.SECOND)
          c.time = Date(timestamp)
          c.set(Calendar.HOUR, currentHour)
          c.set(Calendar.MINUTE, currentMinute)
          c.set(Calendar.SECOND, currentSecond)
          findNavController().safeNavigate(MonthPostsFragmentDirections.actionMonthPostsFragmentToCreateDiaryPostFragment(post = null, time = c.timeInMillis))
        }
      }
      show(this@MonthPostsFragment.childFragmentManager, "SelectDateToWriteDialog")
    }
  }

  override suspend fun subscribeData() {
    viewModel.post.collectLatest {
      if (it is State.DataState) {
        val data = it.data
        adapter.submit(data)
        viewBinding.emptyDataset.root.beVisibleIf(data.isEmpty())
      }
    }
  }

}