package com.cleanarchitectkotlinflowhiltsimplestway.presentation.export

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.databinding.FragmentExportedFileBinding
import com.cleanarchitectkotlinflowhiltsimplestway.domain.models.ExportedFile
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewBindingFragment
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.FileUtils
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.safeCollectLatestFlow
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.safeNavigateUp
import com.dtv.starter.presenter.utils.extension.setSafeOnClickListener
import com.dtv.starter.presenter.utils.log.Logger
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class ExportedFilesFragment: BaseViewBindingFragment<FragmentExportedFileBinding, ExportedFileViewModel>(FragmentExportedFileBinding::inflate) {

  override val viewModel: ExportedFileViewModel by viewModels()

  override fun initView() {
    viewBinding.rvExportedFiles.apply {
      layoutManager = LinearLayoutManager(requireContext())
    }
    viewBinding.ivBack.setSafeOnClickListener { findNavController().safeNavigateUp() }
  }

  override suspend fun subscribeData() {
    safeCollectLatestFlow(viewModel.exportedFiles) {
      if (it is State.DataState) {
        viewBinding.rvExportedFiles.adapter = ExportedFileAdapter(it.data) {
          shareFile(it)
        }
      }
    }
  }

  private fun shareFile(exportedFile: ExportedFile) {
    val sendIntent =  Intent();
    sendIntent.action = Intent.ACTION_SEND;
    val fileToShare = File(FileUtils.getExportedFolder(requireContext()), exportedFile.fileName)
    val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", fileToShare)
    sendIntent.putExtra(Intent.EXTRA_STREAM, uri)
    sendIntent.type = "application/zip";
    startActivity(sendIntent)
  }

}