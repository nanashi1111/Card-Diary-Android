package com.cleanarchitectkotlinflowhiltsimplestway.presentation.create.photo

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cleanarchitectkotlinflowhiltsimplestway.R
import com.cleanarchitectkotlinflowhiltsimplestway.databinding.ItemSelectedPhotoBinding

class SelectedPhotoAdapter (private val uris: MutableList<Uri> = mutableListOf()): RecyclerView.Adapter<SelectedPhotoAdapter.ViewHolder>() {

  class ViewHolder(val binding: ItemSelectedPhotoBinding): RecyclerView.ViewHolder(binding.root)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_selected_photo, parent, false))
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.binding.uri = uris[position]
    holder.binding.executePendingBindings()
  }

  override fun getItemCount() = uris.size

  fun append(uri: Uri) {
    uris.add(uri)
    notifyItemInserted(uris.size)
  }

  fun remove(position: Int) {
    if (position >= itemCount) {
      return
    }
    uris.removeAt(position)
    notifyItemRemoved(position)
  }

  fun provideImages() = uris
}

@BindingAdapter("bindUriToImage")
fun ImageView.bindUriToImage(uri: Uri) {
  Glide.with(context).load(uri).into(this)
}