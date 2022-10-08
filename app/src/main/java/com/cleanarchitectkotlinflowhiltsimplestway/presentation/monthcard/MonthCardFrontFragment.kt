package com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.cleanarchitectkotlinflowhiltsimplestway.R
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.*
import com.cleanarchitectkotlinflowhiltsimplestway.databinding.FragmentMonthCardFrontBinding
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewBindingFragment
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.dashboard.DashboardFragment
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard.design.CardDesignDialog
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard.design.CardDesignListener
import com.cleanarchitectkotlinflowhiltsimplestway.utils.datetime.monthInText
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.coloredBackground
import com.dtv.starter.presenter.utils.extension.beVisibleIf
import com.dtv.starter.presenter.utils.extension.loadImageFileFitToImageView
import com.dtv.starter.presenter.utils.extension.loadResource
import com.dtv.starter.presenter.utils.extension.setSafeOnClickListener
import com.dtv.starter.presenter.utils.log.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MonthCardFrontFragment : BaseViewBindingFragment<FragmentMonthCardFrontBinding, MonthCardViewModel>(FragmentMonthCardFrontBinding::inflate) {

  override val viewModel: MonthCardViewModel by lazy {
    (requireParentFragment() as MonthCardFragment).viewModel
  }

  private val monthPostsViewModel: MonthPostViewModel by lazy {
    (requireParentFragment() as MonthCardFragment).monthPostsViewModel
  }

  private val cardViewModel: FrontCardViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    lifecycleScope.launch {
      lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        monthPostsViewModel.monthData.collectLatest {
          viewBinding.pbLoading.beVisibleIf(it is State.LoadingState)
          when (it) {
            is State.ErrorState -> {
              it.exception.printStackTrace()
            }
            is State.DataState -> {
              val data = it.data
              viewBinding.tvPostData.text = "${data.generalDayData.dayWithPosts}/${data.generalDayData.totalDays}"
              viewBinding.pbProgress.progress = data.generalDayData.progress
            }
            else -> {}
          }
        }
      }

    }
    lifecycleScope.launch {
      lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        val month = 1 + requireArguments().getInt(KEY_MONTH)
        val year = requireArguments().getInt(KEY_YEAR)

        monthPostsViewModel.getPostInMonth(month, year)
      }
    }

    lifecycleScope.launch {
      lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        cardViewModel.template.collectLatest {
          if (it is State.DataState) {
            val template = it.data
            when (template.type) {
              TEMPLATE_DEFAULT -> {
                val month = 1 + requireArguments().getInt(KEY_MONTH)
                viewBinding.ivBackground.loadResource(resourceId(month), resources.getDimensionPixelSize(R.dimen.background_image_radius))
              }
              TEMPLATE_COLOR -> {
                val color = template.data
                viewBinding.ivBackground.setImageBitmap(coloredBackground(color))
              }
              TEMPLATE_PHOTO -> {
                val filePath = template.data
                viewBinding.ivBackground.loadImageFileFitToImageView(filePath)
              }
            }
          }
        }
      }
    }
  }

  override fun initView() {
    val month = requireArguments().getInt(KEY_MONTH)
    viewBinding.apply {
      tvMonthInNumber.text = "${1 + month}"
      tvMonthInText.text = monthInText(month)
      clContent.setSafeOnClickListener {
        ((requireParentFragment().requireParentFragment()) as DashboardFragment).showMonthPost(
          1 + month, requireArguments().getInt(KEY_YEAR)
        )
      }
      ivSetting.setSafeOnClickListener {
        val month = 1 + requireArguments().getInt(KEY_MONTH)
        val year = requireArguments().getInt(KEY_YEAR)
        CardDesignDialog.getInstance(month, year, object :CardDesignListener{
          override fun onSubmit(cardTemplate: CardTemplate) {
            cardViewModel.updateCard(month, year, cardTemplate.type, cardTemplate.data, null)
          }

          override fun onSubmit(uri: Uri) {
            cardViewModel.updateCard(month, year, TEMPLATE_PHOTO, "", uri)
          }
        }).show(this@MonthCardFrontFragment.childFragmentManager, "CardDesign")
      }
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    cardViewModel.getCardTemplate(1 + requireArguments().getInt(KEY_MONTH), requireArguments().getInt(KEY_YEAR))
  }

  override suspend fun subscribeData() {
  }

  private fun resourceId(month: Int) = when (month) {
    1 -> R.drawable.bg_jan
    2 -> R.drawable.bg_feb
    3 -> R.drawable.bg_mar
    4 -> R.drawable.bg_apr
    5 -> R.drawable.bg_may
    6 -> R.drawable.bg_jun
    7 -> R.drawable.bg_jul
    8 -> R.drawable.bg_aug
    9 -> R.drawable.bg_sep
    10 -> R.drawable.bg_oct
    11 -> R.drawable.bg_nov
    else -> R.drawable.bg_dec
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