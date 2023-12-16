package com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard.design

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cleanarchitectkotlinflowhiltsimplestway.R
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.CardTemplate
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.TEMPLATE_COLOR
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseBottomSheetFragment
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard.KEY_MONTH
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard.KEY_YEAR
import com.dtv.starter.presenter.utils.extension.setSafeOnClickListener

class CardDesignDialog private constructor(): BaseBottomSheetFragment(R.layout.dialog_card_design) {

  var listener: CardDesignListener? = null

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    view.findViewById<RecyclerView>(R.id.rvColors).apply {
      layoutManager = GridLayoutManager(requireContext(), 6)
      adapter = provideColorAdapter()
    }
    view.findViewById<View>(R.id.tvPickPhoto).setSafeOnClickListener {
      dismissAllowingStateLoss()
      listener?.onChooseBrowseGallery()
    }
    view.findViewById<View>(R.id.tvCancel).setOnClickListener {
      dismissAllowingStateLoss()
    }
  }

  private fun provideColorAdapter(): ColorAdapter {
    val colors = mutableListOf(
      "#528B8B", "#A0522D", "#8B7D6B", "#1874CD",
      "#6E7B8B", "#00C5CD", "#00CD66", "#CDCD00",
      "#CD5555", "#CD919E", "#68228B", "#ECAB53",
      "#FFA07A", "#00008B", "#E6E6FA", "#CDC9C9"
    )
    return ColorAdapter(colors) {
      color ->
      val time = String.format("%02d-%04d", requireArguments().getInt(KEY_MONTH), requireArguments().getInt(KEY_YEAR))
      listener?.onSubmit(
        CardTemplate(time, type = TEMPLATE_COLOR, data = color)
      )
      dismissAllowingStateLoss()
    }
  }

  companion object {
    fun getInstance(mont: Int, year: Int, Cardlistener: CardDesignListener) : CardDesignDialog {
      return CardDesignDialog().apply {
        arguments = Bundle().apply {
          putInt(KEY_MONTH, mont)
          putInt(KEY_YEAR, year)
        }
        listener = Cardlistener
      }
    }
  }
}


interface CardDesignListener {
  fun onSubmit(cardTemplate: CardTemplate)
  fun onChooseBrowseGallery()
}