package com.cleanarchitectkotlinflowhiltsimplestway.presentation.dashboard

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.cleanarchitectkotlinflowhiltsimplestway.R
import com.cleanarchitectkotlinflowhiltsimplestway.databinding.FragmentDashboardBinding
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewBindingFragment
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard.MonthCardFragment
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard.MonthCardState
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard.MonthCardViewModel
import com.cleanarchitectkotlinflowhiltsimplestway.utils.datetime.OnYearSelected
import com.cleanarchitectkotlinflowhiltsimplestway.utils.datetime.YearPickerDialog
import com.cleanarchitectkotlinflowhiltsimplestway.utils.datetime.currentMonth
import com.cleanarchitectkotlinflowhiltsimplestway.utils.datetime.currentYear
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.safeCollectFlow
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.safeNavigate
import com.cleanarchitectkotlinflowhiltsimplestway.utils.viewpager.HorizontalMarginItemDecoration
import com.dtv.starter.presenter.utils.extension.setSafeOnClickListener
import com.dtv.starter.presenter.utils.extension.tint
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class DashboardFragment : BaseViewBindingFragment<FragmentDashboardBinding, DashboardViewModel>(FragmentDashboardBinding::inflate){

  override val viewModel: DashboardViewModel by viewModels()

  val monthCardViewModel: MonthCardViewModel by viewModels()

  override fun initView() {
    viewBinding.apply {
      setupCarousel(vpDashboard)
      tvYearSelector.setSafeOnClickListener {
        showYearSelector()
      }
      rlCalendar.setSafeOnClickListener {
        monthCardViewModel.toggleMonthCardState()
      }
      rlCreate.setSafeOnClickListener {
        findNavController().safeNavigate(DashboardFragmentDirections.actionDashboardFragmentToCreateDiaryPostFragment())
      }
      resetAndFocusCurrentMonth(currentYear(), currentMonth())
    }
  }

  private fun setupCarousel(viewPager:ViewPager2){
    viewPager.offscreenPageLimit = 1
    val nextItemVisiblePx = resources.getDimension(R.dimen.viewpager_next_item_visible)
    val currentItemHorizontalMarginPx = resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)
    val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
    val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
      page.translationX = -pageTranslationX * position
      page.scaleY = 1f
      page.alpha = 1f
    }
    viewPager.setPageTransformer(pageTransformer)
    val itemDecoration = HorizontalMarginItemDecoration(
      requireContext(),
      R.dimen.viewpager_current_item_horizontal_margin
    )
    viewPager.addItemDecoration(itemDecoration)
  }

  private fun showYearSelector() {
    YearPickerDialog()
      .apply {
        listener = object :OnYearSelected {
          override fun onYearSelected(year: Int) {
            viewBinding.tvYearSelector.text = "$year"
            resetAndFocusCurrentMonth(year, 1)
          }
        }
        show(this@DashboardFragment.childFragmentManager, "Y")
      }
  }

  override suspend fun subscribeData() {
    safeCollectFlow(monthCardViewModel.monthCardStateFlow) {
      when (it) {
        MonthCardState.FRONT -> {
          viewBinding.apply {
            rlCalendar.setBackgroundResource(R.drawable.bg_circled_dark_button)
            ivToggleCalendar.setImageResource(R.drawable.ic_diary_calendar)
            ivToggleCalendar.tint(R.color.tint_dark_background_icon_dashboard)
          }
        }
        else -> {
          viewBinding.apply {
            rlCalendar.setBackgroundResource(R.drawable.bg_circled_main_blue_button)
            ivToggleCalendar.setImageResource(R.drawable.ic_undo)
            ivToggleCalendar.tint(R.color.white)
          }
        }
      }
    }
  }

  private fun resetAndFocusCurrentMonth(year: Int, month: Int) {
    viewBinding.vpDashboard.apply {
      adapter = DashBoardAdapter(this@DashboardFragment, year)
      currentItem = month - 1
    }
  }

  fun currentItem() = viewBinding.vpDashboard.currentItem

}

class DashBoardAdapter(val f: Fragment, val year: Int): FragmentStateAdapter(f) {
  override fun getItemCount() = 12

  override fun createFragment(position: Int) = MonthCardFragment.getInstance(position, year)
}