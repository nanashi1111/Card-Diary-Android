package com.dtv.starter.presenter.utils.extension

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.SystemClock
import android.view.*
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.cleanarchitectkotlinflowhiltsimplestway.R

fun View.beInvisibleIf(beInvisible: Boolean) = if (beInvisible) beInvisible() else beVisible()

fun View.beVisibleIf(beVisible: Boolean) = if (beVisible) beVisible() else beGone()

fun View.beGoneIf(beGone: Boolean) = beVisibleIf(!beGone)

fun View.beInvisible() {
    visibility = View.INVISIBLE
}

fun View.beVisible() {
    visibility = View.VISIBLE
}

fun View.beGone() {
    visibility = View.GONE
}

fun View.beGoneWithAnimation() {
    animate().translationX(-width.toFloat()).alpha(0f).setDuration(300)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                beGone()
            }
        })
}

fun View.disappear(duration: Long) {
    animate().alpha(0f).setDuration(duration).setListener(object : Animator.AnimatorListener {
        override fun onAnimationRepeat(p0: Animator?) {
        }

        override fun onAnimationEnd(p0: Animator?) {
            beGone()
        }

        override fun onAnimationCancel(p0: Animator?) {
        }

        override fun onAnimationStart(p0: Animator?) {
        }
    })
}

fun View.appear(duration: Long) {
    animate().alpha(1f).setDuration(duration).setListener(object : Animator.AnimatorListener {
        override fun onAnimationRepeat(p0: Animator?) {
        }

        override fun onAnimationEnd(p0: Animator?) {

        }

        override fun onAnimationCancel(p0: Animator?) {
        }

        override fun onAnimationStart(p0: Animator?) {
            beVisible()
        }
    })
}


fun View.isVisible() = visibility == View.VISIBLE

fun View.isInvisible() = visibility == View.INVISIBLE

fun View.isGone() = visibility == View.GONE


val Float.toPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun ImageView.loadImageFitToImageView(
    url: String?,
    placeholderId: Int = R.drawable.ic_loading_non_rounded_placeholder,
    errorId: Int = R.drawable.ic_loading_non_rounded_error
) {
    Glide.with(context).clear(this)
    val options = RequestOptions().centerCrop()
    Glide.with(context).load(url).apply(options)
        .placeholder(placeholderId)
        .error(errorId)
        .transition(DrawableTransitionOptions.withCrossFade()).into(this)
}

fun ImageView.loadImageFitToImageViewWithCorder(
    url: String?,
    topLeft: Float = 0f,
    topRight: Float = 0f,
    bottomLeft: Float = 0f,
    bottomRight: Float = 0f,
    placeholderId: Int = R.drawable.ic_loading_non_rounded_placeholder,
    errorId: Int = R.drawable.ic_loading_non_rounded_error
) {
    Glide.with(context).clear(this)
    val transitionOptions = MultiTransformation(CenterCrop(),GranularRoundedCorners(topLeft, topRight, bottomRight, bottomLeft) )
    Glide.with(context).load(url)
        .transform(transitionOptions)
        .placeholder(placeholderId)
        .error(errorId)
        .transition(DrawableTransitionOptions.withCrossFade()).into(this)
}

fun TextView.color(@ColorRes resId: Int) {
    setTextColor(ContextCompat.getColor(context, resId))
}

fun ImageView.tint(@ColorRes resId: Int) {
    setColorFilter(ContextCompat.getColor(context, resId))
}

class SafeClickListener(
    private var defaultInterval: Int = 500,
    private val onSafeCLick: (View) -> Unit
) : View.OnClickListener {
    private var lastTimeClicked: Long = 0
    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
            return
        }
        lastTimeClicked = SystemClock.elapsedRealtime()
        onSafeCLick(v)
    }
}


fun View.setSafeOnClickListener(delay: Int? = null, block: (View) -> Unit) {
    val safeClickListener = SafeClickListener(delay ?: 500) {
        block(it)
    }
    setOnClickListener(safeClickListener)
}

