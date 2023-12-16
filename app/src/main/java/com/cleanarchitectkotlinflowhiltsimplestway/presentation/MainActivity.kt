package com.cleanarchitectkotlinflowhiltsimplestway.presentation

import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.cleanarchitectkotlinflowhiltsimplestway.BuildConfig
import com.cleanarchitectkotlinflowhiltsimplestway.R
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.dashboard.DashboardFragment
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.unlock.*
import com.cleanarchitectkotlinflowhiltsimplestway.utils.ads.AdsManager
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.FileUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.system.exitProcess

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  private val vm: UnlockViewModel by viewModels()

  @Inject
  lateinit var adsManager: AdsManager

  private var appLaunched = 0L

  override fun onCreate(savedInstanceState: Bundle?) {
    appLaunched = System.currentTimeMillis()
    installSplashScreen().apply {
      setKeepOnScreenCondition {
        !adsManager.openAdLoadResult
      }
    }
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    adsManager.bindActivity(this)
    waitAndDisplayOpenAds()
    showAuthenticationDialogIfNeeded()
    registerBackDispatcher()
    if (BuildConfig.DEBUG) {
      FileUtils.printFolderContent(filesDir)
    }
  }

  private fun showAuthenticationDialogIfNeeded() {
    vm.checkPatternSetup()
    lifecycleScope.launch {
      vm.patternSetup.collectLatest {
        if (it is State.DataState) {
          if (it.data) {
            showAuthenticationDialog()
          }
        }
      }
    }
  }

  private fun registerBackDispatcher() {
    onBackPressedDispatcher.addCallback {
      val currentFragment = supportFragmentManager.findFragmentById(R.id.my_nav_host_fragment)?.childFragmentManager?.fragments?.first()
      currentFragment?.let {
        if (it is DashboardFragment) {
          exitProcess(0)
        }
      }
    }
  }

  private fun waitAndDisplayOpenAds() {
    lifecycleScope.launch(Dispatchers.IO) {
      while (!adsManager.openAdLoadResult) {
        delay(200)
      }
      withContext(Dispatchers.Main) {
        adsManager.displayOpenAds()
      }
    }
  }

  private fun showAuthenticationDialog() {
    UnlockDialog.newInstance(PatternData(PatternPurpose.UNLOCK, PatternStep.CONFIRM)).apply {
      show(supportFragmentManager, "Unlock")
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    adsManager.destroy()
  }
}