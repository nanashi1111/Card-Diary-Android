package com.cleanarchitectkotlinflowhiltsimplestway.presentation.settings

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.cleanarchitectkotlinflowhiltsimplestway.BuildConfig
import com.cleanarchitectkotlinflowhiltsimplestway.R
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.databinding.FragmentSettingsBinding
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewBindingFragment
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.dialog.LoadingDialog
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.safeCollectLatestFlow
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.safeNavigateUp
import com.dtv.starter.presenter.utils.extension.beVisibleIf
import com.dtv.starter.presenter.utils.extension.setSafeOnClickListener
import com.dtv.starter.presenter.utils.log.Logger
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.rmartinper.filepicker.model.DialogConfigs
import com.rmartinper.filepicker.model.DialogProperties
import com.rmartinper.filepicker.view.FilePickerDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SettingFragment : BaseViewBindingFragment<FragmentSettingsBinding, SettingViewModel>(FragmentSettingsBinding::inflate) {

  private val loadingDialog: LoadingDialog by lazy {
    LoadingDialog()
  }

  override val viewModel: SettingViewModel by viewModels()

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

      tvExport.setSafeOnClickListener {
        exportUserData()
      }

      tvVersion.text = BuildConfig.VERSION_NAME
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    lifecycleScope.launch {
      lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.deleteAllResult.collectLatest {
          viewBinding.pbLoading.beVisibleIf(it is State.LoadingState)
          if (it is State.LoadingState) {
            loadingDialog.display(this@SettingFragment)
          }
          if (it is State.DataState) {
            loadingDialog.hide()
            Toast.makeText(requireContext(), R.string.all_data_deleted, Toast.LENGTH_SHORT).show()
          }
          if (it is State.ErrorState) {
            loadingDialog.hide()
            Toast.makeText(requireContext(), R.string.all_data_not_deleted, Toast.LENGTH_SHORT).show()
          }
        }
      }
    }
  }

  override suspend fun subscribeData() {
    safeCollectLatestFlow(viewModel.exportSuccess) {
      if (it is State.LoadingState) {
        loadingDialog.display(this@SettingFragment)
      }
      if (it is State.DataState) {
        loadingDialog.hide()
        Logger.d("FilePath = ${it.data}")
        Toast.makeText(requireContext(), getString(R.string.data_exported), Toast.LENGTH_SHORT).show()
      }
      if (it is State.ErrorState) {
        loadingDialog.hide()
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

  private val storageActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
  ) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      if (Environment.isExternalStorageManager()) {
        pickFolder {
          viewModel.exportUserData(it)
        }
      } else {
        Toast.makeText(requireContext(), "Manage External Storage Permission is denied", Toast.LENGTH_SHORT).show()
      }
    }
  }

  private fun exportUserData() {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      val intent = Intent().apply {
        setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
        setData(Uri.fromParts("package", requireContext().packageName, null))
      }
      storageActivityResultLauncher.launch(intent)
    } else {
      Dexter.withContext(requireContext()).withPermissions(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        .withListener(object : MultiplePermissionsListener {
          override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
            if (p0?.areAllPermissionsGranted() == true) {
              pickFolder {
                viewModel.exportUserData(it)
              }
            }
          }

          override fun onPermissionRationaleShouldBeShown(p0: MutableList<PermissionRequest>?, p1: PermissionToken?) {
          }

        }).check()
    }
  }

  private fun pickFolder(onComplete: (String) -> Unit) {
    val properties = DialogProperties(true)
    properties.selectionMode = DialogConfigs.SINGLE_MODE
    properties.selectionType = DialogConfigs.DIR_SELECT
    val dialog = FilePickerDialog(requireActivity(), properties)
    dialog.setTitle("Pick a folder")
    dialog.setDialogSelectionListener {
      if (it.isNotEmpty()) {
        onComplete(it.first())
      }
    }
    dialog.show()
    val lp = WindowManager.LayoutParams()
    lp.copyFrom(dialog.getWindow()?.getAttributes())
    lp.width = WindowManager.LayoutParams.MATCH_PARENT
    lp.height = WindowManager.LayoutParams.MATCH_PARENT
    dialog.getWindow()?.setAttributes(lp);
  }
}