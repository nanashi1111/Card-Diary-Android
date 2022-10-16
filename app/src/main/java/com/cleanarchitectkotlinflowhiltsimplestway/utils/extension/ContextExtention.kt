package com.dtv.starter.presenter.utils.extension

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast

/**
 * Created by Ege Kuzubasioglu on 10.06.2018 at 01:00.
 * Copyright (c) 2018. All rights reserved.
 */


fun Context.showKeyboard(editText: EditText) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
}

fun EditText.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun Context.isKeyboardVisible(): Boolean {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    return imm.hideSoftInputFromWindow((this as Activity).currentFocus?.windowToken, 0)
}

fun Context.convertDpToPixel(dp: Float): Float {
    return dp * (this.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun Context.toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
fun Context.toast(msg: Int) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

fun Context.longToast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
fun Context.longToast(msg: Int) = Toast.makeText(this, msg, Toast.LENGTH_LONG).show()



fun Context.getScreenWidth(): Float {
    val windowManager = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val dm = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(dm)
    return dm.widthPixels / resources.displayMetrics.density
}

fun Context.getScreenWidthInt(): Int {
    val windowManager = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.widthPixels
}

fun Context.showWebsiteDefault(url: String) {
    var urlString = url
    try {
        if (!urlString.startsWith("http://") && !urlString.startsWith("https://")) {
            urlString = "http://$urlString"
        }

        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(urlString))
        startActivity(browserIntent)
    } catch (e: Exception) {

    }
}

fun Context.sendEmailDefault(email: String) {
    val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null))
    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject")
    emailIntent.putExtra(Intent.EXTRA_TEXT, "Body")
    startActivity(Intent.createChooser(emailIntent, "Send email..."))
}

fun Activity.openAppSetting() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", packageName, null)
    intent.data = uri
    startActivity(intent)
}

fun Activity.copyText(text: String) {
    val clipboard = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip: ClipData = ClipData.newPlainText("simple text", text)
    clipboard.setPrimaryClip(clip)
}

fun Context.spToPx(sp: Float): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        sp,
        resources.displayMetrics
    ).toInt()
}





