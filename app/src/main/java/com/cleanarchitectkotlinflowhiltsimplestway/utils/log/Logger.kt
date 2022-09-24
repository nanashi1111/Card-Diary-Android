package com.dtv.starter.presenter.utils.log

import com.cleanarchitectkotlinflowhiltsimplestway.BuildConfig


object Logger {

    private val LOG = BuildConfig.LOGGABLE
    private val TAG = "TIMVIEC"

    /**
     * Log debug

     * @param message message to log
     */
    fun d(message: String) {
        if (LOG) {
            com.orhanobut.logger.Logger.d(message)
        }
    }

    fun json(jsonObject: String) {
        if (LOG) {
            com.orhanobut.logger.Logger.json(jsonObject)
        }
    }
}