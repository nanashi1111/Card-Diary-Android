package com.cleanarchitectkotlinflowhiltsimplestway.utils

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.cleanarchitectkotlinflowhiltsimplestway.R
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.CardTemplate
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.TEMPLATE_COLOR
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.TEMPLATE_DEFAULT
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.TEMPLATE_PHOTO
import com.cleanarchitectkotlinflowhiltsimplestway.databinding.ViewFlipMonthBinding
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.DayData
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.MonthData
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard.MonthCardAdapter
import com.cleanarchitectkotlinflowhiltsimplestway.utils.datetime.monthInText
import com.cleanarchitectkotlinflowhiltsimplestway.utils.datetime.timeStamp
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.coloredBackground
import com.dtv.starter.presenter.utils.extension.*
import com.dtv.starter.presenter.utils.log.Logger

class FlipMonthView(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

  private val vb: ViewFlipMonthBinding = ViewFlipMonthBinding.inflate(LayoutInflater.from(context), this, true)

  private var flipped = false

  var month: Int = 1
  var year: Int = 1

  var callback: FlipMonthViewCallback? = null

  init {
    vb.apply {
      clContent.setSafeOnClickListener {
        callback?.onShowPostsOfMonth(month, year)
      }

      ivSetting.setSafeOnClickListener {
        callback?.onClickSettingFrontCard()

      }
    }
  }

  fun uiInformation(): String {
    return "${vb.tvMonthInNumber.text}"
  }

  fun setTime(month: Int, year: Int) {
    this.month = month
    this.year = year
  }

  fun showDateTime(month: Int, year: Int) {
    vb.apply {
      //Behind
      tvMonthInNumberBack.text = String.format("%d", 1 + month)
      tvMonthInTextBack.text = monthInText(month)
      rvDateInMonth.layoutManager = GridLayoutManager(context, 7)
      //Front
      tvMonthInNumber.text = String.format("%d", 1 + month)
      tvMonthInText.text = monthInText(month)
    }
  }

  fun showMonthDataByDays(data: List<DayData>) {
    val adapter = MonthCardAdapter(data) {
      val timeStamp = timeStamp(it.day, it.month, it.year)
      callback?.onClickDay(timeStamp)
    }
    vb.rvDateInMonth.adapter = adapter
  }

  fun bindCardTemplate(template: CardTemplate) {
    when (template.type) {
      TEMPLATE_DEFAULT -> {
        Logger.d("bindCardTemplate: ${vb.tvMonthInNumber.text}")
        val month = 1 + this@FlipMonthView.month
        vb.ivBackground.loadResource(resourceId(month), resources.getDimensionPixelSize(R.dimen.background_image_radius))
      }
      TEMPLATE_COLOR -> {
        val color = template.data
        Logger.d("bindCardTemplate: ${vb.tvMonthInNumber.text}: $color")
        Glide.with(context).clear(vb.ivBackground)
        vb.ivBackground.setImageBitmap(coloredBackground(color))
      }
      TEMPLATE_PHOTO -> {
        val filePath = template.data
        Logger.d("bindCardTemplate: ${vb.tvMonthInNumber.text}: $filePath")
        vb.ivBackground.loadBackgroundFile(filePath, resources.getDimensionPixelSize(R.dimen.background_image_radius))
      }
    }
  }

  fun bindMonthGeneralData(data: MonthData) {
    vb.tvPostData.text = String.format("%d/%d",data.generalDayData.dayWithPosts, data.generalDayData.totalDays )
    vb.pbProgress.progress = data.generalDayData.progress
  }

  fun showFront() {
    if (vb.frontView.isVisible()) {
      return
    }
    flipCard(vb.frontView, vb.behindView)
    vb.pbLoading.indeterminateTintList = ColorStateList.valueOf(Color.WHITE)
  }

  fun showBehind() {
    if (vb.behindView.isVisible()) {
      return
    }
    flipCard(vb.behindView, vb.frontView)
    vb.pbLoading.indeterminateTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.dashboard_card_default_background))
  }

  fun showLoading(visible: Boolean) {
    return
    vb.pbLoading.beVisibleIf(visible)
  }

  private fun flipCard(visibleView: View, inVisibleView: View) {
    try {
      visibleView.beVisible()
      val scale = context.resources.displayMetrics.density
      val cameraDist = 8000 * scale
      visibleView.cameraDistance = cameraDist
      inVisibleView.cameraDistance = cameraDist
      val flipOutAnimatorSet =
        AnimatorInflater.loadAnimator(
          context,
          R.animator.flip_out
        ) as AnimatorSet
      flipOutAnimatorSet.setTarget(inVisibleView)
      val flipInAnimatorSet =
        AnimatorInflater.loadAnimator(
          context,
          R.animator.flip_in
        ) as AnimatorSet
      flipInAnimatorSet.setTarget(visibleView)
      flipOutAnimatorSet.start()
      flipInAnimatorSet.start()
      flipInAnimatorSet.doOnEnd {
        inVisibleView.beGone()
        flipped = !flipped
      }

    } catch (e: Exception) {

    }
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

}

interface FlipMonthViewCallback {
  fun onClickDay(timestamp: Long)
  fun onShowPostsOfMonth(month: Int, year: Int)
  fun onClickSettingFrontCard()
}