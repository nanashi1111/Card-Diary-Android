package com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.cleanarchitectkotlinflowhiltsimplestway.databinding.FragmentMonthCardBinding
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.MonthDayData
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewBindingFragment
import com.cleanarchitectkotlinflowhiltsimplestway.utils.datetime.monthInText
import com.dtv.starter.presenter.utils.log.Logger
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MonthCardFragment: BaseViewBindingFragment<FragmentMonthCardBinding, MonthCardViewModel>(FragmentMonthCardBinding::inflate) {

  override val viewModel: MonthCardViewModel by viewModels()

  override fun initView() {
    viewBinding.apply {
      //Texts
      val month = requireArguments().getInt(KEY_MONTH)
      tvMonthInNumber.text = "$month"
      tvMonthInNumberBack.text = "$month"
      tvMonthInText.text = monthInText(month)
      tvMonthInTextBack.text = monthInText(month)
      //Calendar
      rvDateInMonth.layoutManager = GridLayoutManager(requireContext(), 7)
      //fake data
      val data = mutableListOf<MonthDayData>()
      for (i in 0..30) {
        data.add(MonthDayData(2022, 10, i + 1, Random().nextInt(2)))
      }
      val adapter = MonthCardAdapter(data) {
        Logger.d("SelectedDate: $it")
      }
      rvDateInMonth.adapter = adapter
      shadowView.setOnClickListener {
        flipView.flipTheView(true)
      }
      shadowViewBack.setOnClickListener {
        flipView.flipTheView(true)
      }
    }
  }

  override suspend fun subscribeData() {
  }

  companion object {

    const val KEY_MONTH = "month"

    fun getInstance(month: Int): MonthCardFragment {
      return MonthCardFragment().apply {
        arguments = Bundle().apply {
          putInt(KEY_MONTH, month)
        }
      }
    }
  }
}