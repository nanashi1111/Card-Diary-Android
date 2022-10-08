package com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard.design

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cleanarchitectkotlinflowhiltsimplestway.R
import com.cleanarchitectkotlinflowhiltsimplestway.databinding.ItemColorTemplateBinding
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.coloredCircle
import com.dtv.starter.presenter.utils.extension.setSafeOnClickListener

class ColorAdapter(val colors: List<String>, val onColorSelected: (String) -> Unit) : RecyclerView.Adapter<ColorAdapter.ViewHolder>() {

  class ViewHolder(val binding: ItemColorTemplateBinding) : RecyclerView.ViewHolder(binding.root)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_color_template, parent, false))
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.binding.color = colors[position]
    holder.binding.root.setSafeOnClickListener {
      onColorSelected(colors[position])
    }
    holder.binding.executePendingBindings()
  }

  override fun getItemCount() = colors.size
}

@BindingAdapter("circleShape")
fun View.circleShape(backgroundColor: String) {
  val d = coloredCircle(backgroundColor)
  setBackgroundDrawable(d)
}