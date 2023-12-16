package com.cleanarchitectkotlinflowhiltsimplestway.presentation.posts

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.databinding.FragmentMonthPostsBinding
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.DiaryPost
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewBindingFragment
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.dialog.ConfirmDialog
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.dialog.ConfirmListener
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.posts.dateselection.OnSelectDateToWrite
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.posts.dateselection.SelectDateToWriteDialog
import com.cleanarchitectkotlinflowhiltsimplestway.utils.ads.AdsManager
import com.cleanarchitectkotlinflowhiltsimplestway.utils.datetime.monthInText
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.safeNavigate
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.safeNavigateUp
import com.dtv.starter.presenter.utils.extension.beGone
import com.dtv.starter.presenter.utils.extension.beVisible
import com.dtv.starter.presenter.utils.extension.beVisibleIf
import com.dtv.starter.presenter.utils.extension.setSafeOnClickListener
import com.dtv.starter.presenter.utils.log.Logger
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MonthPostsFragment : BaseViewBindingFragment<FragmentMonthPostsBinding, MonthPostsViewModel>(FragmentMonthPostsBinding::inflate) {

  override val viewModel: MonthPostsViewModel by viewModels()

  @Inject
  lateinit var adsManager: AdsManager

  private val args: MonthPostsFragmentArgs by navArgs()

  private val adapter: PostAdapter by lazy {
    PostAdapter(mutableListOf(), onPostOptionSelected = { post ->
      PostOptionsDialog.newInstance(post, callback = object :PostOptionCallback {
        override fun onRemove(post: DiaryPost) {
          Logger.d("Removing: ${post.title}")
          ConfirmDialog.getInstance().apply {
            listener = object :ConfirmListener {
              override fun onConfirmed() {
                viewModel.deletePost(post)
              }

            }
          }.show(this@MonthPostsFragment.childFragmentManager, "Confirm")

        }

        override fun onView(post: DiaryPost) {
          adsManager.displayPopupAds {
            Logger.d("View: ${post.title}")
            findNavController().safeNavigate(MonthPostsFragmentDirections.actionMonthPostsFragmentToCreateDiaryPostFragment(post = post.simpleObject(), time = 0L))
          }
        }
      }).show(childFragmentManager, "Options")
    }, onPostSelected = { post ->
      adsManager.displayPopupAds {
        findNavController().safeNavigate(MonthPostsFragmentDirections.actionMonthPostsFragmentToCreateDiaryPostFragment(post = post.simpleObject(), time = 0L))
      }
    })
  }

  override fun initView() {
    viewBinding.apply {
      rvPosts.layoutManager = LinearLayoutManager(requireContext())
      rvPosts.adapter = adapter
      tvTitle.text = "${monthInText(args.month - 1, true)} / ${args.year}"
      ivBack.setSafeOnClickListener { findNavController().safeNavigateUp() }
      ivWritePost.setSafeOnClickListener { createPost() }
      llCloseTutorial.setOnClickListener { hideTutorial() }
      clTutorial.setOnClickListener { hideTutorial() }
      adView.loadAd(
        AdRequest.Builder().build()
      )
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

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
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
    viewModel.post.collectLatest {
      if (it is State.DataState) {
        val posts = it.data.first
        adapter.submit(posts)
        viewBinding.emptyDataset.root.beVisibleIf(posts.isEmpty())

        val tutorialShown = it.data.second

        if (posts.isNotEmpty() && !tutorialShown) {
          showTutorials(posts.first())
        }
      }
    }
  }

  private fun showTutorials(post: DiaryPost) {
    Logger.d("ShowTutorials")
    with(viewBinding) {
      clTutorial.beVisible()
      ivBackground.bindImages(post.images)
      tvDate.text = post.dayOfMonth
      tvWeekDay.text = post.dayOfWeek
      ivWeather.bindWeather(post.weather)
      tvTitle.text = post.title
      tvPostContent.text = post.content
    }

    viewModel.markPostTutorialShown()
  }

  private fun hideTutorial() = viewBinding.clTutorial.beGone()

}