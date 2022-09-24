package com.cleanarchitectkotlinflowhiltsimplestway.presentation.dashboard

import androidx.fragment.app.viewModels
import com.cleanarchitectkotlinflowhiltsimplestway.databinding.FragmentDashboardBinding
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewBindingFragment

class DashboardFragment : BaseViewBindingFragment<FragmentDashboardBinding, DashboardViewModel>(FragmentDashboardBinding::inflate){

  override val viewModel: DashboardViewModel by viewModels()

  override fun initView() {
  }

  override suspend fun subscribeData() {

  }
}