package com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cleanarchitectkotlinflowhiltsimplestway.R
import com.cleanarchitectkotlinflowhiltsimplestway.databinding.FragmentMonthCardBinding
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewBindingFragment
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.dashboard.DashboardFragment
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.safeCollectFlow
import com.dtv.starter.presenter.utils.extension.tint
import com.wajahatkarim3.easyflipviewpager.CardFlipPageTransformer2
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MonthCardFragment: BaseViewBindingFragment<FragmentMonthCardBinding, MonthCardViewModel>(FragmentMonthCardBinding::inflate) {

  override val viewModel: MonthCardViewModel by lazy {
    (requireParentFragment() as DashboardFragment).monthCardViewModel
  }

  private val month: Int by lazy {
    requireArguments().getInt(KEY_MONTH)
  }

  private val year: Int by lazy {
    requireArguments().getInt(KEY_YEAR)
  }

  override fun initView() {
    viewBinding.vpMonthCard.apply {
      offscreenPageLimit = 3
      isUserInputEnabled = false
      adapter = FlipMonthCardAdapter(this@MonthCardFragment)
      setPageTransformer(CardFlipPageTransformer2().apply {
        isScalable = false
      })
      //setPageTransformer (Pager2_FidgetSpinTransformer())
    }
  }

  override suspend fun subscribeData() {

  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    lifecycleScope.launch {
      lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        safeCollectFlow(viewModel.monthCardStateFlow) {
          when (it) {
            MonthCardState.FRONT -> setItem(0)
            else -> setItem(1)
          }
        }
      }
    }
  }

  companion object {
    fun getInstance(month: Int, year: Int): MonthCardFragment {
      return MonthCardFragment().apply {
        arguments = Bundle().apply {
          putInt(KEY_MONTH, month)
          putInt(KEY_YEAR, year)
        }
      }
    }
  }

  inner class FlipMonthCardAdapter(val f: Fragment): FragmentStateAdapter(f) {
    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
      return when (position) {
        0 -> MonthCardFrontFragment.getInstance(month, year)
        else -> MonthCardBehindFragment.getInstance(month, year)
      }
    }
  }

  private fun setItem(position: Int) {
    if (position > 1) {
      return
    }
    if (month == (requireParentFragment() as DashboardFragment).currentItem()) {
      viewBinding.vpMonthCard.setCurrentItem(position, true)
    } else {
      lifecycleScope.launch {
        //delay(200)
        viewBinding.vpMonthCard.setCurrentItem(position, true)
      }
    }

  }
}

const val KEY_MONTH = "month"
const val KEY_YEAR = "year"