package com.cleanarchitectkotlinflowhiltsimplestway.presentation.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.cleanarchitectkotlinflowhiltsimplestway.R
import com.cleanarchitectkotlinflowhiltsimplestway.databinding.FragmentDashboardBinding
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.QuotesActivity
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewBindingFragment
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard.MonthCardFragment
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard.MonthCardState
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard.MonthCardViewModel
import com.cleanarchitectkotlinflowhiltsimplestway.utils.datetime.*
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.safeCollectFlow
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.safeCollectLatestFlow
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.safeNavigate
import com.cleanarchitectkotlinflowhiltsimplestway.utils.viewpager.HorizontalMarginItemDecoration
import com.dtv.starter.presenter.utils.extension.setSafeOnClickListener
import com.dtv.starter.presenter.utils.extension.tint
import com.dtv.starter.presenter.utils.log.Logger
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
      rlCalendar.setOnClickListener {
        monthCardViewModel.toggleMonthCardState()
      }
      rlCreate.setSafeOnClickListener {
        showCreatePostScreen(System.currentTimeMillis())
      }
      ivSearch.setSafeOnClickListener {
        findNavController().safeNavigate(DashboardFragmentDirections.actionDashboardFragmentToSearchFragment())
      }

      ivSetting.setSafeOnClickListener {
        findNavController().safeNavigate(DashboardFragmentDirections.actionDashboardFragmentToNavigationSetting())
      }

      rlQuote.setSafeOnClickListener {
        startActivity(Intent(context, QuotesActivity::class.java))
      }

      tvDate.text = dateTimeInDashboard()
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    lifecycleScope.launch {
      lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.updateCurrentYearMonth()
      }
    }

    viewModel.currentYearMonth.observe(viewLifecycleOwner) {
      val currentMonth  = it.split("-").first().toInt()
      val currentYear  = it.split("-")[1].toInt()
      Logger.d("SettingDate: $currentMonth / $currentYear")
      resetAndFocusCurrentMonth(currentYear, currentMonth)
      viewBinding.tvYearSelector.text = "$currentYear"
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
    viewPager.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
      override fun onPageSelected(position: Int) {
        val year = viewModel.currentYearMonth.value!!.split("-")[1].toInt()
        val month = 1 + position
        viewModel.updateTempYearMonth(year, month)
      }
    })
  }

  private fun showYearSelector() {
    val calendar: Calendar = Calendar.getInstance()
    val yearSelected = calendar.get(Calendar.YEAR)
    val monthSelected = calendar.get(Calendar.MONTH)

    val dialogFragment = MonthYearPickerDialogFragment
      .getInstance(monthSelected, yearSelected).apply {
        setOnDateSetListener { year, monthOfYear ->
          viewModel.needSmoothScroll = true
          viewModel.updateCurrentYearMonth(year, 1 + monthOfYear)
        }
      }

    dialogFragment.show(childFragmentManager, null)
  }

  override suspend fun subscribeData() {

  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    lifecycleScope.launch {
      lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        safeCollectFlow(monthCardViewModel.monthCardStateFlow) {
          withContext(Dispatchers.Main) {
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
      }
    }
  }

  private fun resetAndFocusCurrentMonth(year: Int, month: Int) {
    viewBinding.vpDashboard.apply {
      try {
//        if (adapter == null || viewBinding.tvYearSelector.text.toString().toInt() != year) {
//          adapter = DashBoardAdapter(this@DashboardFragment, year)
//        }
        adapter = DashBoardAdapter(this@DashboardFragment, year)
        setCurrentItem(month - 1, viewModel.needSmoothScroll)
        lifecycleScope.launch {
          delay(500)
          viewModel.needSmoothScroll = false
        }
      }catch (e: Exception) {
        e.printStackTrace()
      }


    }
  }

  fun showMonthPost(month: Int, year: Int) {
    findNavController().safeNavigate(DashboardFragmentDirections.actionDashboardFragmentToMonthPostsFragment(month, year))
  }

  fun showCreatePostScreen(time: Long) {
    findNavController().safeNavigate(DashboardFragmentDirections.actionDashboardFragmentToCreateDiaryPostFragment(time = time, post = null))
  }

}

class DashBoardAdapter(f: Fragment, val year: Int): FragmentStateAdapter(f) {
  override fun getItemCount() = 12

  override fun createFragment(position: Int) = MonthCardFragment.getInstance(position, year)
}