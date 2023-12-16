package com.cleanarchitectkotlinflowhiltsimplestway.utils.ads

import android.app.Activity
import android.content.Context
import com.cleanarchitectkotlinflowhiltsimplestway.R
import com.cleanarchitectkotlinflowhiltsimplestway.data.repository.AppPreferenceRepository
import com.dtv.starter.presenter.utils.log.Logger
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdsManager @Inject constructor(private val appPreferenceRepository: AppPreferenceRepository) {

  private var activity: Activity? = null
  private var mInterstitialAd: InterstitialAd? = null
  private var mOpenAd: AppOpenAd? = null

  var openAdLoadResult = false

  companion object {
    private const val MIN_INTERVAL_SHOW_POPUP = 60 * 3 * 1L
  }

  fun bindActivity(activity: Activity) {
    this.activity = activity
  }

  fun destroy() {
    activity = null
  }

  fun displayPopupAds(callback: () -> Unit) {
    if (activity == null) {
      return;
    }
    if (mInterstitialAd == null) {
      callback()
      return
    }
    if (System.currentTimeMillis() - appPreferenceRepository.lastPopupAdsShown() < MIN_INTERVAL_SHOW_POPUP) {
      callback()
      return
    }
    mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
      override fun onAdClicked() {
      }

      override fun onAdDismissedFullScreenContent() {
        mInterstitialAd = null
        loadPopupAds(activity!!)
        callback()
      }

      override fun onAdFailedToShowFullScreenContent(adError: AdError) {
        mInterstitialAd = null
        callback()
      }

      override fun onAdImpression() {
      }

      override fun onAdShowedFullScreenContent() {
        Logger.d("onAdShowedFullScreenContent")
        appPreferenceRepository.setPopupAdsShown()
      }
    }
    mInterstitialAd?.show(activity!!)
  }

  fun displayOpenAds() {
    if (activity == null) {
      return
    }
    if (mOpenAd == null) {
      return
    }
    mOpenAd!!.show(activity!!)
  }

  fun loadAds(context: Context) {
    loadPopupAds(context)
    loadOpenAppAds(context)
  }

  private fun loadPopupAds(context: Context) {
    val adRequest = AdRequest.Builder().build()
    InterstitialAd.load(context, context.getString(R.string.ads_popup), adRequest, object : InterstitialAdLoadCallback() {
      override fun onAdFailedToLoad(adError: LoadAdError) {
        Logger.d("Adsmanager: popupAdFailed: ${adError?.toString()}")

        mInterstitialAd = null
      }

      override fun onAdLoaded(interstitialAd: InterstitialAd) {
        Logger.d("Adsmanager: popupAdLoaded")
        mInterstitialAd = interstitialAd
      }
    })
  }

  private fun loadOpenAppAds(context: Context) {
    AppOpenAd.load(context, context.getString(R.string.ads_open), AdRequest.Builder().build(), object :AppOpenAdLoadCallback() {
      override fun onAdLoaded(p0: AppOpenAd) {
        super.onAdLoaded(p0)
        Logger.d("Adsmanager: openAdLoaded")
        openAdLoadResult = true
        mOpenAd = p0
      }

      override fun onAdFailedToLoad(p0: LoadAdError) {
        super.onAdFailedToLoad(p0)
        openAdLoadResult = true
        Logger.d("Adsmanager: Openads failed: ${p0.toString()}")
      }
    })
  }


}