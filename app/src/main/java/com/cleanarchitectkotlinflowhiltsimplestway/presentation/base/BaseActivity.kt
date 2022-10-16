package com.dtv.starter.presenter.base

import androidx.appcompat.app.AppCompatActivity
import com.dtv.starter.presenter.utils.extension.displayKeyboard

abstract class BaseActivity : AppCompatActivity() {

    override fun onPause() {
        super.onPause()
        displayKeyboard(false)
    }
}