package com.cleanarchitectkotlinflowhiltsimplestway.presentation.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
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
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.safeNavigateUp
import com.dtv.starter.presenter.utils.extension.beVisibleIf
import com.dtv.starter.presenter.utils.extension.setSafeOnClickListener
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
  }

  private fun showDialogConfirmDelete() {
    AlertDialog.Builder(requireContext()).setTitle(getString(R.string.warning))
      .setMessage(getString(R.string.delete_all_warning))
      .setNegativeButton(R.string.cancel) { p0, p1 -> }
      .setPositiveButton(R.string.label_ok_popup) { p0, p1 -> viewModel.deleteAll() }
      .show()
  }
}