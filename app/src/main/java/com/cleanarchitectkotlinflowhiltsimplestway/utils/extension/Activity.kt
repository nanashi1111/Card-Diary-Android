package com.dtv.starter.presenter.utils.extension

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.cleanarchitectkotlinflowhiltsimplestway.R

fun AppCompatActivity.replace(fragment: Fragment, tag: String) {
    val fragmentManager = supportFragmentManager
        .beginTransaction()
        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
        .replace(R.id.container, fragment, tag)
    fragmentManager.commit()
}

fun AppCompatActivity.loadFragment(
    currentFragment: Fragment?,
    fragment: Fragment?,
    tag: String = "",
    replace: Boolean = false
): Fragment? {
    fragment?.let {
        var animationId = 0
        currentFragment?.let {
            animationId = R.anim.fade_in
        }
        //animationId = ANIMATION_FADE
        val transaction = it.childFragmentManager.beginTransaction()
        if (supportFragmentManager.fragments.contains(fragment)) {
            currentFragment?.let {
                transaction.hide(it)
            }
            transaction.show(fragment)
            transaction.commitAllowingStateLoss()
        } else {
            val removeFrag =
                supportFragmentManager.fragments.firstOrNull { x -> x.javaClass == fragment.javaClass }
            removeFrag?.let {
                if (replace)
                    transaction.remove(removeFrag)
            }
            transaction.add(R.id.container, fragment, tag)
            currentFragment?.let {
                transaction.hide(it)
            }
            transaction.commit()
        }

        return fragment
    }
    return null
}

fun AppCompatActivity.add(
    fragment: Fragment,
    tag: String? = null,
    addToBackStack: Boolean = true,
    bundle: Bundle? = null,
    containerId: Int? = null
) {
    bundle?.let {
        fragment.arguments = it
    }

    val fragmentManager = supportFragmentManager
        .beginTransaction()
        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
        .replace(containerId ?: R.id.container, fragment, tag ?: fragment.javaClass.name)

    if (addToBackStack) {
        fragmentManager.addToBackStack(tag ?: fragment.javaClass.name)
    }

    fragmentManager.commit()
}

fun Activity.displayKeyboard(show: Boolean) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm?.let { imm ->
        when (show) {
            true -> imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
            else -> {
                val view = currentFocus
                view?.let {
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
        }
    }
}

fun Activity.getScreenWidth(): Int {
    val displayMetrics = DisplayMetrics()
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
        val display = display
        display?.getRealMetrics(displayMetrics)
    } else {
        @Suppress("DEPRECATION")
        val display = windowManager.defaultDisplay
        @Suppress("DEPRECATION")
        display.getMetrics(displayMetrics)
    }
    return displayMetrics.widthPixels
}

fun Activity.getScreenHeight(): Int {
    val displayMetrics = DisplayMetrics()
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
        val display = display
        display?.getRealMetrics(displayMetrics)
    } else {
        @Suppress("DEPRECATION")
        val display = windowManager.defaultDisplay
        @Suppress("DEPRECATION")
        display.getMetrics(displayMetrics)
    }
    return displayMetrics.heightPixels
}

fun Activity.openURL(url: String) = startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))

fun Activity.showKeyboard(editText: EditText) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
}

fun Activity.hideKeyboard() {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = currentFocus
    if (view == null) {
        view = View(this)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}