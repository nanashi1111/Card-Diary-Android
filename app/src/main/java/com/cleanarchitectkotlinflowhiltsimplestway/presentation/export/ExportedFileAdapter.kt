package com.cleanarchitectkotlinflowhiltsimplestway.presentation.export

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cleanarchitectkotlinflowhiltsimplestway.R
import com.cleanarchitectkotlinflowhiltsimplestway.databinding.ItemExportedFileBinding
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.ExportedFile
import com.dtv.starter.presenter.utils.extension.setSafeOnClickListener

class ExportedFileAdapter(private val files: List<ExportedFile>, val action: (ExportedFile) -> Unit) : RecyclerView.Adapter<ExportedFileAdapter.ViewHolder>() {

  class ViewHolder(val binding: ItemExportedFileBinding) : RecyclerView.ViewHolder(binding.root)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_exported_file, parent, false))

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.binding.file = files[position]
    holder.binding.executePendingBindings()
    holder.binding.root.setSafeOnClickListener { action(files[position]) }
  }

  override fun getItemCount() = files.size
}