package com.cleanarchitectkotlinflowhiltsimplestway.presentation.posts.dateselection

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cleanarchitectkotlinflowhiltsimplestway.R
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseBottomSheetFragment
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard.KEY_MONTH
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard.KEY_YEAR
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard.MonthCardAdapter
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard.MonthPostViewModel
import com.cleanarchitectkotlinflowhiltsimplestway.utils.datetime.timeStamp
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.safeCollectLatestFlow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SelectDateToWriteDialog : BaseBottomSheetFragment(R.layout.dialog_select_date_to_write) {

  val monthPostsViewModel: MonthPostViewModel by viewModels()

  var callback: OnSelectDateToWrite? = null

  private val recyclerView: RecyclerView by lazy {
    requireView().findViewById(R.id.rvDateInMonth)
  }

  private val month: Int by lazy {
    requireArguments().getInt(KEY_MONTH)
  }

  private val year: Int by lazy {
    requireArguments().getInt(KEY_YEAR)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    recyclerView. layoutManager = GridLayoutManager(context, 7)
    lifecycleScope.launch {
      safeCollectLatestFlow(monthPostsViewModel.monthData) {
        if (it is State.DataState) {
          val daysData = it.data.daysData
          val adapter = MonthCardAdapter(daysData) {
            val timeStamp = timeStamp(it.day, it.month, it.year)
            callback?.onSelected(timeStamp)
            dismissAllowingStateLoss()
          }
          recyclerView.adapter = adapter
        }
      }
    }

    monthPostsViewModel.getPostInMonth(month, year)
  }

  companion object {
    fun getInstance(month:Int, year: Int): SelectDateToWriteDialog {
      return SelectDateToWriteDialog().apply {
        arguments = Bundle().apply {
          putInt(KEY_MONTH, month)
          putInt(KEY_YEAR, year)
        }
      }
    }
  }
}

interface OnSelectDateToWrite {
  fun onSelected(timestamp: Long)
}