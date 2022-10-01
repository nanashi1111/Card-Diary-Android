package com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.cleanarchitectkotlinflowhiltsimplestway.databinding.FragmentMonthCardBehindBinding
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.MonthDayData
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewBindingFragment
import com.cleanarchitectkotlinflowhiltsimplestway.utils.datetime.monthInText
import com.dtv.starter.presenter.utils.log.Logger
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MonthCardBehindFragment: BaseViewBindingFragment<FragmentMonthCardBehindBinding, MonthCardViewModel>(FragmentMonthCardBehindBinding::inflate) {

  override val viewModel: MonthCardViewModel by lazy {
    (requireParentFragment() as MonthCardFragment).viewModel
  }

  override fun initView() {
    val month = requireArguments().getInt(KEY_MONTH)
    val year = requireArguments().getInt(KEY_YEAR)
    viewBinding.apply {
      tvMonthInNumberBack.text = "${1 + month}"
      tvMonthInTextBack.text = monthInText(month)
      rvDateInMonth.layoutManager = GridLayoutManager(requireContext(), 7)
      val data = mutableListOf<MonthDayData>()
      for (i in 0..30) {
        data.add(MonthDayData(year, month, i + 1, Random().nextInt(2)))
      }
      val adapter = MonthCardAdapter(data) {
        Logger.d("SelectedDate: $it")
      }
      rvDateInMonth.adapter = adapter
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