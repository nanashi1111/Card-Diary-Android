package com.cleanarchitectkotlinflowhiltsimplestway.presentation.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.cleanarchitectkotlinflowhiltsimplestway.BuildConfig
import com.cleanarchitectkotlinflowhiltsimplestway.R
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.databinding.FragmentSettingsBinding
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewBindingFragment
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.FileUtils
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.safeCollectLatestFlow
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.safeNavigate
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.safeNavigateUp
import com.dtv.starter.presenter.utils.extension.beVisibleIf
import com.dtv.starter.presenter.utils.extension.setSafeOnClickListener
import com.dtv.starter.presenter.utils.log.Logger
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : BaseViewBindingFragment<FragmentSettingsBinding, SettingViewModel>(FragmentSettingsBinding::inflate) {

  override val viewModel: SettingViewModel by viewModels()

  var pickFileActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
  ) { result ->
    result?.data?.data?.also { uri ->
      val file = FileUtils.getFile(requireContext(), uri)
      viewModel.importUserData(file.absolutePath)
    }
  }

  override fun initView() {
    viewBinding.apply {
      ivBack.setOnClickListener {
        findNavController().safeNavigateUp()
      }

      tvClearData.setSafeOnClickListener {
        showDialogConfirmDelete()
      }

      ivGiveFeedback.setSafeOnClickListener {
        val packageName = requireContext().packageName
        try {
          startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (e: ActivityNotFoundException) {
          startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
        }
      }

      ivPolicy.setSafeOnClickListener {
        val url = "https://cungdev.com/max-diary-privacy-and-policy/"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
      }

      tvCreateABackup.setSafeOnClickListener {
        exportUserData()
      }

      tvRestoreABackup.setSafeOnClickListener {
        pickFileToImport()
      }

      tvBackupFiles.setSafeOnClickListener {
        findNavController().safeNavigate(SettingFragmentDirections.actionSettingFragmentToExportedFilesFragment())
      }

      tvVersion.text = BuildConfig.VERSION_NAME
    }
  }

  override suspend fun subscribeData() {
    safeCollectLatestFlow(viewModel.deleteAllResult) {
      viewBinding.pbLoading.beVisibleIf(it is State.LoadingState)
      if (it is State.LoadingState) {
        displayLoadingDialog(true)
      }
      if (it is State.DataState) {
        displayLoadingDialog(false)
        Toast.makeText(requireContext(), R.string.all_data_deleted, Toast.LENGTH_SHORT).show()
      }
      if (it is State.ErrorState) {
        displayLoadingDialog(false)
        Toast.makeText(requireContext(), R.string.all_data_not_deleted, Toast.LENGTH_SHORT).show()
      }
    }

    safeCollectLatestFlow(viewModel.exportSuccess) {
      if (it is State.LoadingState) {
        displayLoadingDialog(true)
      }
      if (it is State.DataState) {
        displayLoadingDialog(false)
        Logger.d("FilePath = ${it.data}")
        showExportedMessage()
      }
      if (it is State.ErrorState) {
        it.exception.printStackTrace()
        displayLoadingDialog(false)
        Toast.makeText(requireContext(), R.string.all_data_not_deleted, Toast.LENGTH_SHORT).show()
      }
    }

    safeCollectLatestFlow(viewModel.importSuccess) {
      if (it is State.LoadingState) {
        displayLoadingDialog(true)
      }
      if (it is State.DataState) {
        displayLoadingDialog(false)
        Toast.makeText(requireContext(), getString(R.string.data_imported), Toast.LENGTH_SHORT).show()
      }
      if (it is State.ErrorState) {
        displayLoadingDialog(false)
        it.exception.printStackTrace()
        Toast.makeText(requireContext(), R.string.all_data_not_deleted, Toast.LENGTH_SHORT).show()
      }
    }
  }

  private fun showDialogConfirmDelete() {
    AlertDialog.Builder(requireContext()).setTitle(getString(R.string.warning))
      .setMessage(getString(R.string.delete_all_warning))
      .setNegativeButton(R.string.cancel) { p0, p1 -> }
      .setPositiveButton(R.string.label_ok_popup) { p0, p1 -> viewModel.deleteAll() }
      .show()
  }

  private fun exportUserData() {
    viewModel.exportUserData()
  }

  private fun pickFileToImport() {
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
      addCategory(Intent.CATEGORY_OPENABLE)
      type = "application/zip"
    }
    pickFileActivityResultLauncher.launch(intent)
  }

  private fun showExportedMessage() {
    Snackbar.make(viewBinding.root, getString(R.string.data_exported), Snackbar.LENGTH_LONG)
      .setAction(getString(R.string.view_files)) {
        findNavController().safeNavigate(SettingFragmentDirections.actionSettingFragmentToExportedFilesFragment())
      }.show()
  }

}