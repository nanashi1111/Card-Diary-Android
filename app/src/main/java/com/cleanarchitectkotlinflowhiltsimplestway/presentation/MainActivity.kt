package com.cleanarchitectkotlinflowhiltsimplestway.presentation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cleanarchitectkotlinflowhiltsimplestway.R
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.dashboard.DashboardFragment
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.unlock.*
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.FileUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  private val vm: UnlockViewModel by viewModels()


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    FileUtils.printFolderContent(filesDir)
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


  private fun showAuthenticationDialog() {
    UnlockDialog.newInstance(PatternData(PatternPurpose.UNLOCK, PatternStep.CONFIRM)).apply {
      show(supportFragmentManager, "Unlock")
    }
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