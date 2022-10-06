package com.cleanarchitectkotlinflowhiltsimplestway.presentation.posts

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.cleanarchitectkotlinflowhiltsimplestway.R
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.WeatherType
import com.cleanarchitectkotlinflowhiltsimplestway.databinding.ItemMonthPostBinding
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.DiaryPost
import com.dtv.starter.presenter.utils.extension.*

class PostAdapter(val posts: MutableList<DiaryPost>, val onPostSelected: (DiaryPost) -> Unit) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

  class ViewHolder(val binding: ItemMonthPostBinding) : RecyclerView.ViewHolder(binding.root)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_month_post, parent, false))
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.binding.post = posts[position]
    holder.binding.executePendingBindings()
    holder.binding.root.setSafeOnClickListener {
      onPostSelected(posts[position])
    }
  }

  override fun getItemCount() = posts.size

  fun submit(newPost: List<DiaryPost>) {
    val diffResult = DiffUtil.calculateDiff(PostDiffUtils(posts, newPost))
    diffResult.dispatchUpdatesTo(this)
    posts.clear()
    posts.addAll(newPost)
  }
}

class PostDiffUtils(val oldList: List<DiaryPost>, val newList: List<DiaryPost>): DiffUtil.Callback() {
  override fun getOldListSize() = oldList.size

  override fun getNewListSize() = newList.size

  override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
    return oldList[oldItemPosition].date == newList[newItemPosition].date
  }

  override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
    return oldList[oldItemPosition].equal(newList[newItemPosition])
  }

}

@BindingAdapter("bindImages")
fun ImageView.bindImages(images: List<String>) {
  if (images.isEmpty()) {
    beGone()
  } else {
    beVisible()
    loadImageFileFitToImageView(images.first())
  }
}

@BindingAdapter("bindWeather")
fun ImageView.bindWeather(weather: WeatherType) {
  when (weather) {
    WeatherType.SUNNY -> setImageResource(R.drawable.ic_weather_sun)
    WeatherType.CLOUDY -> setImageResource(R.drawable.ic_weather_cloudy)
    WeatherType.RAINY -> setImageResource(R.drawable.ic_weather_rainy)
    WeatherType.SNOWY -> setImageResource(R.drawable.ic_weather_snowy)
    WeatherType.LIGHTING -> setImageResource(R.drawable.ic_weather_thunder)
    WeatherType.STORMY -> setImageResource(R.drawable.ic_weather_tornado)
  }
}