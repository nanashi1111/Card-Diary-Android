package com.cleanarchitectkotlinflowhiltsimplestway.presentation

import android.app.Application
import com.cleanarchitectkotlinflowhiltsimplestway.BuildConfig
import com.gu.toolargetool.TooLargeTool
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application(){

    companion object {
        lateinit var app: App
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        if (BuildConfig.DEBUG) {
            TooLargeTool.startLogging(this)
        }
        Logger.addLogAdapter(AndroidLogAdapter())
    }

}