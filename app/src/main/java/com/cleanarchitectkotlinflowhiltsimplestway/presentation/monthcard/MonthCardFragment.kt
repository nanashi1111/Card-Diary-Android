package com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard

import androidx.fragment.app.viewModels
import com.cleanarchitectkotlinflowhiltsimplestway.databinding.FragmentMonthCardBinding
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewBindingFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MonthCardFragment: BaseViewBindingFragment<FragmentMonthCardBinding, MonthCardViewModel>(FragmentMonthCardBinding::inflate) {

  override val viewModel: MonthCardViewModel by viewModels()

  override fun initView() {
  }

  override suspend fun subscribeData() {
  }
}