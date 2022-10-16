package com.cleanarchitectkotlinflowhiltsimplestway.utils.extension

import android.app.Activity
import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.showErrorMessage(message: String) {
  activity?.let {
    Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
  }
}

fun Fragment.showSuccessMessage(message: String) {
  activity?.let {
    Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
  }
}

fun Fragment.showInfoMessage(message: String) {
  activity?.let {
    Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
  }
}

fun Activity.showSuccessMessage(message: String) {
  Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Activity.showErrorMessage(message: String) {
  Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Activity.showInfoMessage(message: String) {
  Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}