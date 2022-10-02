package com.cleanarchitectkotlinflowhiltsimplestway.presentation.create.weather

import android.os.Bundle
import android.view.View
import com.cleanarchitectkotlinflowhiltsimplestway.R
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.WeatherType
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseBottomSheetFragment
import com.dtv.starter.presenter.utils.extension.beVisible
import com.dtv.starter.presenter.utils.extension.setSafeOnClickListener

class WeatherSelectorDialog: BaseBottomSheetFragment(R.layout.dialog_select_weather) {

  var listener: WeatherSelectedListener? = null

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    when (requireArguments().getString(KEY_WEATHER) ?: WeatherType.SUNNY.name) {
      WeatherType.CLOUDY.name -> requireView().findViewById<View>(R.id.ivSelectedCloudy).beVisible()
      WeatherType.RAINY.name -> requireView().findViewById<View>(R.id.ivSelectedRainy).beVisible()
      WeatherType.SNOWY.name -> requireView().findViewById<View>(R.id.ivSelectedSnowy).beVisible()
      WeatherType.LIGHTING.name -> requireView().findViewById<View>(R.id.ivSelectedLighting).beVisible()
      WeatherType.STORMY.name -> requireView().findViewById<View>(R.id.ivSelectedStormy).beVisible()
      else -> requireView().findViewById<View>(R.id.ivSelectedSunny).beVisible()
    }

    requireView().findViewById<View>(R.id.rlSunny).setSafeOnClickListener {
      listener?.onSelected(WeatherType.SUNNY)
      dismissAllowingStateLoss()
    }
    requireView().findViewById<View>(R.id.rlCloudy).setSafeOnClickListener {
      listener?.onSelected(WeatherType.CLOUDY)
      dismissAllowingStateLoss()
    }
    requireView().findViewById<View>(R.id.rlRainy).setSafeOnClickListener {
      listener?.onSelected(WeatherType.RAINY)
      dismissAllowingStateLoss()
    }
    requireView().findViewById<View>(R.id.rlSnowy).setSafeOnClickListener {
      listener?.onSelected(WeatherType.SNOWY)
      dismissAllowingStateLoss()
    }
    requireView().findViewById<View>(R.id.rlLighting).setSafeOnClickListener {
      listener?.onSelected(WeatherType.LIGHTING)
      dismissAllowingStateLoss()
    }
    requireView().findViewById<View>(R.id.rlStormy).setSafeOnClickListener {
      listener?.onSelected(WeatherType.STORMY)
      dismissAllowingStateLoss()
    }
    requireView().findViewById<View>(R.id.tvCancel).setSafeOnClickListener {
      dismissAllowingStateLoss()
    }
  }

  companion object {
    const val KEY_WEATHER = "weather"
    fun newInstance(weather: String): WeatherSelectorDialog {
      return WeatherSelectorDialog().apply {
        arguments = Bundle().apply {
          putString(KEY_WEATHER, weather)
        }
      }
    }
  }
}

interface WeatherSelectedListener {
  fun onSelected(weather: WeatherType)
}