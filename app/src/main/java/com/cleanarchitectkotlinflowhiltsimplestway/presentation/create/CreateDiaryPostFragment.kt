package com.cleanarchitectkotlinflowhiltsimplestway.presentation.create

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.cleanarchitectkotlinflowhiltsimplestway.databinding.FragmentCreateDiaryPostBinding
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewBindingFragment
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.create.weather.WeatherSelectorDialog
import com.cleanarchitectkotlinflowhiltsimplestway.utils.datetime.dateTimeInCreateDiaryScreen
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.safeNavigateUp
import com.dtv.starter.presenter.utils.extension.setSafeOnClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateDiaryPostFragment: BaseViewBindingFragment<FragmentCreateDiaryPostBinding, CreateDiaryPostViewModel>(FragmentCreateDiaryPostBinding::inflate) {

  override val viewModel: CreateDiaryPostViewModel by viewModels()

  override fun initView() {
    viewBinding.apply {
      ivBack.setSafeOnClickListener {
        findNavController().safeNavigateUp()
      }
      ivWeather.setSafeOnClickListener {
        WeatherSelectorDialog().show(childFragmentManager, "Weather")
      }
      tvDate.text = dateTimeInCreateDiaryScreen()
    }
  }

  override suspend fun subscribeData() {
  }
}