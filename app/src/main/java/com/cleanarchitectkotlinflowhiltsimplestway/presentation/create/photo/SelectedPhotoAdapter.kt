package com.cleanarchitectkotlinflowhiltsimplestway.presentation.create.photo

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.create.SelectedPhotoFragment

class SelectedPhotoAdapter(val f: Fragment, val uris: MutableList<Uri>) : FragmentStateAdapter(f) {
  override fun getItemCount() = uris.size

  override fun createFragment(position: Int) = SelectedPhotoFragment.getInstance(uris[position])

}

@BindingAdapter("bindUriToImage")
fun ImageView.bindUriToImage(uri: Uri) {
  Glide.with(context).load(uri).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(this)
}