package com.cleanarchitectkotlinflowhiltsimplestway.presentation

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import com.cleanarchitectkotlinflowhiltsimplestway.R
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.dashboard.DashboardFragment
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.FileUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlin.system.exitProcess

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    setLightStatusBar()
    FileUtils.printFolderContent(filesDir)
  }

  private fun setLightStatusBar() {
//    val decorView = window.decorView
//    val wic = WindowInsetsControllerCompat(window, decorView)
//    wic?.isAppearanceLightStatusBars = true
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//      window.statusBarColor = Color.WHITE
//    }
  }

  override fun onBackPressed() {
    super.onBackPressed()
    val currentFragment = supportFragmentManager.findFragmentById(R.id.my_nav_host_fragment)?.childFragmentManager?.fragments?.first()
    currentFragment?.let {
      if (it is DashboardFragment) {
        exitProcess(0)
      }
    }
  }
}