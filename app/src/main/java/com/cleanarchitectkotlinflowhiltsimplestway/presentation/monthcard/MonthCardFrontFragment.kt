package com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.cleanarchitectkotlinflowhiltsimplestway.R
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.databinding.FragmentMonthCardFrontBinding
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewBindingFragment
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.dashboard.DashboardFragment
import com.cleanarchitectkotlinflowhiltsimplestway.utils.datetime.monthInText
import com.dtv.starter.presenter.utils.extension.beVisibleIf
import com.dtv.starter.presenter.utils.extension.loadResource
import com.dtv.starter.presenter.utils.extension.setSafeOnClickListener
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
        viewBinding.ivBackground.loadResource(resourceId(month), resources.getDimensionPixelSize(R.dimen.background_image_radius))
        monthPostsViewModel.getPostInMonth(month, year)
      }
    }
  }

  override fun initView() {
    val month = requireArguments().getInt(KEY_MONTH)
    viewBinding.apply {
      tvMonthInNumber.text = "${1 + month}"
      tvMonthInText.text = monthInText(month)
      clContent.setSafeOnClickListener {
        ((requireParentFragment().requireParentFragment()) as DashboardFragment).showMonthPost(
          1 + month, requireArguments().getInt(KEY_YEAR)
        )
      }
    }
  }

  override suspend fun subscribeData() {
  }

  private fun resourceId(month: Int) = when (month) {
    1 -> R.drawable.bg_jan
    2 -> R.drawable.bg_feb
    3 -> R.drawable.bg_mar
    4 -> R.drawable.bg_apr
    5 -> R.drawable.bg_may
    6 -> R.drawable.bg_jun
    7 -> R.drawable.bg_jul
    8 -> R.drawable.bg_aug
    9 -> R.drawable.bg_sep
    10 -> R.drawable.bg_oct
    11 -> R.drawable.bg_nov
    else -> R.drawable.bg_dec
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