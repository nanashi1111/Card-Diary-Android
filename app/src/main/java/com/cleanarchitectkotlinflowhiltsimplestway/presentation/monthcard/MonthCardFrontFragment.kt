package com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.databinding.FragmentMonthCardFrontBinding
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewBindingFragment
import com.cleanarchitectkotlinflowhiltsimplestway.utils.datetime.monthInText
import com.dtv.starter.presenter.utils.extension.beVisibleIf
import com.dtv.starter.presenter.utils.log.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MonthCardFrontFragment : BaseViewBindingFragment<FragmentMonthCardFrontBinding, MonthCardViewModel>(FragmentMonthCardFrontBinding::inflate) {

  override val viewModel: MonthCardViewModel by lazy {
    (requireParentFragment() as MonthCardFragment).viewModel
  }

  private val monthPostsViewModel: MonthPostViewModel by lazy {
    (requireParentFragment() as MonthCardFragment).monthPostsViewModel
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    lifecycleScope.launch {
      lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        monthPostsViewModel.monthData.collectLatest {
          viewBinding.pbLoading.beVisibleIf(it is State.LoadingState)
          when (it) {
            is State.ErrorState -> {
              it.exception.printStackTrace()
            }
            is State.DataState -> {
              val data = it.data
              viewBinding.tvPostData.text = "${data.generalDayData.dayWithPosts}/${data.generalDayData.totalDays}"
              viewBinding.pbProgress.progress = data.generalDayData.progress
            }
            else -> {}
          }
        }
      }

    }
    lifecycleScope.launch {
      lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        val month = 1 + requireArguments().getInt(KEY_MONTH)
        val year = requireArguments().getInt(KEY_YEAR)
        monthPostsViewModel.getPostInMonth(month, year)
      }
    }


  }

  override fun initView() {
    val month = requireArguments().getInt(KEY_MONTH)
    viewBinding.apply {
      tvMonthInNumber.text = "${1 + month}"
      tvMonthInText.text = monthInText(month)
    }
  }

  override fun onResume() {
    super.onResume()

  }

  override suspend fun subscribeData() {
  }

  companion object {
    fun getInstance(month: Int, year: Int): MonthCardFrontFragment {
      return MonthCardFrontFragment().apply {
        arguments = Bundle().apply {
          putInt(KEY_MONTH, month)
          putInt(KEY_YEAR, year)
        }
      }
    }
  }
}