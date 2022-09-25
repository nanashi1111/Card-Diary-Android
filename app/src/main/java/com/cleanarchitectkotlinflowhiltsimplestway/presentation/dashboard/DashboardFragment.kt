package com.cleanarchitectkotlinflowhiltsimplestway.presentation.dashboard

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.cleanarchitectkotlinflowhiltsimplestway.R
import com.cleanarchitectkotlinflowhiltsimplestway.databinding.FragmentDashboardBinding
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewBindingFragment
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard.MonthCardFragment
import com.cleanarchitectkotlinflowhiltsimplestway.utils.viewpager.HorizontalMarginItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : BaseViewBindingFragment<FragmentDashboardBinding, DashboardViewModel>(FragmentDashboardBinding::inflate){

  override val viewModel: DashboardViewModel by viewModels()

  override fun initView() {
    viewBinding.apply {
      vpDashboard.adapter = DashBoardAdapter(this@DashboardFragment)
      setupCarousel(vpDashboard)
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

  override suspend fun subscribeData() {

  }


}

class DashBoardAdapter(val f: Fragment): FragmentStateAdapter(f) {
  override fun getItemCount() = 10

  override fun createFragment(position: Int) = MonthCardFragment()
}