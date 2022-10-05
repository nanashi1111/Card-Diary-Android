package com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.databinding.FragmentMonthCardBehindBinding
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.MonthDayData
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewBindingFragment
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.dashboard.DashboardFragment
import com.cleanarchitectkotlinflowhiltsimplestway.utils.datetime.monthInText
import com.cleanarchitectkotlinflowhiltsimplestway.utils.datetime.timeStamp
import com.dtv.starter.presenter.utils.extension.beVisibleIf
import com.dtv.starter.presenter.utils.log.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class MonthCardBehindFragment: BaseViewBindingFragment<FragmentMonthCardBehindBinding, MonthCardViewModel>(FragmentMonthCardBehindBinding::inflate) {

  override val viewModel: MonthCardViewModel by lazy {
    (requireParentFragment() as MonthCardFragment).viewModel
  }

  val monthPostsViewModel: MonthPostViewModel by lazy {
    (requireParentFragment() as MonthCardFragment).monthPostsViewModel
  }

  override fun initView() {
    val month = requireArguments().getInt(KEY_MONTH)
    val year = requireArguments().getInt(KEY_YEAR)
    viewBinding.apply {
      tvMonthInNumberBack.text = "${1 + month}"
      tvMonthInTextBack.text = monthInText(month)
      rvDateInMonth.layoutManager = GridLayoutManager(requireContext(), 7)

    }

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
              val data = it.data.daysData
              val adapter = MonthCardAdapter(data) {
                Logger.d("SelectedDate: $it")
                val timeStamp = timeStamp(it.day, it.month, it.year)
                ((requireParentFragment().requireParentFragment()) as? DashboardFragment)?.showCreatePostScreen(timeStamp)
              }
              viewBinding.rvDateInMonth.adapter = adapter

            }
            else -> {}
          }
        }
      }

    }
  }

  override suspend fun subscribeData() {
  }

  companion object {
    fun getInstance(month: Int, year: Int): MonthCardBehindFragment {
      return MonthCardBehindFragment().apply {
        arguments = Bundle().apply {
          putInt(KEY_MONTH, month)
          putInt(KEY_YEAR, year)
        }
      }
    }
  }
}