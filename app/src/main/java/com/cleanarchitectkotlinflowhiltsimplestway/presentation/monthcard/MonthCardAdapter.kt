package com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cleanarchitectkotlinflowhiltsimplestway.R
import com.cleanarchitectkotlinflowhiltsimplestway.databinding.ItemDayInMonthBinding
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.DayData
import com.dtv.starter.presenter.utils.extension.color
import com.dtv.starter.presenter.utils.extension.setSafeOnClickListener


class MonthCardAdapter(private val monthDayData: List<DayData>, val onDateSelected: (DayData) -> Unit) : RecyclerView.Adapter<MonthCardAdapter.ViewHolder>() {

  class ViewHolder(val binding: ItemDayInMonthBinding) : RecyclerView.ViewHolder(binding.root)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_day_in_month, parent, false))
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.binding.data = monthDayData[position]
    holder.binding.executePendingBindings()
    holder.binding.root.setSafeOnClickListener {
      onDateSelected(monthDayData[position])
    }
  }

  override fun getItemCount() = monthDayData.size
}

@BindingAdapter("bindCalendarDateColor")
fun TextView.bindCalendarDateColor(data: DayData) {
  if (data.isSunday()) {
    color(R.color.lb_text_end_week)
  } else if (data.isMonday()) {
    color(R.color.lb_text_start_week)
  } else {
    color(R.color.lb_text_middle_weel)
  }
}

@BindingAdapter("bindCalendarDateFont")
fun TextView.bindCalendarDateFont(data: DayData) {
  if (data.numberOfPosts > 0) {
    typeface = ResourcesCompat.getFont(context!!, R.font.quicksand_bold)
  } else {
    typeface = ResourcesCompat.getFont(context!!, R.font.quicksand_regular)
  }
}