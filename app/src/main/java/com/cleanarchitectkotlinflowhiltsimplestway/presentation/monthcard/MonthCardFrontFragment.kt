package com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard

import android.os.Bundle
import com.cleanarchitectkotlinflowhiltsimplestway.databinding.FragmentMonthCardFrontBinding
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewBindingFragment
import com.cleanarchitectkotlinflowhiltsimplestway.utils.datetime.monthInText
import com.dtv.starter.presenter.utils.log.Logger
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MonthCardFrontFragment : BaseViewBindingFragment<FragmentMonthCardFrontBinding, MonthCardViewModel>(FragmentMonthCardFrontBinding::inflate) {
  override val viewModel: MonthCardViewModel by lazy {
    (requireParentFragment() as MonthCardFragment).viewModel
  }

  override fun initView() {
    val month = requireArguments().getInt(KEY_MONTH)
    viewBinding.apply {
      tvMonthInNumber.text = "${1 + month}"
      tvMonthInText.text = monthInText(month)
    }
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