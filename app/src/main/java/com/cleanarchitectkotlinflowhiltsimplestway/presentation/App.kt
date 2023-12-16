package com.cleanarchitectkotlinflowhiltsimplestway.presentation

import android.app.Application
import com.cleanarchitectkotlinflowhiltsimplestway.BuildConfig
import com.cleanarchitectkotlinflowhiltsimplestway.utils.ads.AdsManager
import com.google.android.gms.ads.MobileAds
import com.gu.toolargetool.TooLargeTool
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class App : Application(){

    companion object {
        lateinit var app: App
    }

    @Inject lateinit var adsManager: AdsManager

    override fun onCreate() {
        super.onCreate()
        app = this
        adsManager.loadAds(this)
        if (BuildConfig.DEBUG) {
            TooLargeTool.startLogging(this)
        }
        Logger.addLogAdapter(AndroidLogAdapter())
        MobileAds.initialize(this) { }
    }

}