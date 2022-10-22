package com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.cleanarchitectkotlinflowhiltsimplestway.R
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.CardTemplate
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.TEMPLATE_PHOTO
import com.cleanarchitectkotlinflowhiltsimplestway.databinding.FragmentMonthCardBinding
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewBindingFragment
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.dashboard.DashboardFragment
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard.design.CardDesignDialog
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard.design.CardDesignListener
import com.cleanarchitectkotlinflowhiltsimplestway.utils.FlipMonthViewCallback
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.safeCollectFlow
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.safeCollectLatestFlow
import com.dtv.starter.presenter.utils.extension.hasPermissions
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import dagger.hilt.android.AndroidEntryPoint
import gun0912.tedimagepicker.builder.TedImagePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MonthCardFragment : BaseViewBindingFragment<FragmentMonthCardBinding, MonthCardViewModel>(FragmentMonthCardBinding::inflate), FlipMonthViewCallback {

  override val viewModel: MonthCardViewModel by lazy {
    (requireParentFragment() as DashboardFragment).monthCardViewModel
  }

  val monthPostsViewModel: MonthPostViewModel by viewModels()

  private val month: Int by lazy {
    requireArguments().getInt(KEY_MONTH)
  }

  private val year: Int by lazy {
    requireArguments().getInt(KEY_YEAR)
  }

  private var requestCameraPermissionLauncher: ActivityResultLauncher<Array<String>>? = null

  private val cardDesignChangedBroadcast = object : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
      monthPostsViewModel.getCardTemplate(month, year)
    }
  }

  private val cardDesignChangedIntentFilter = IntentFilter().apply {
    addAction(KEY_CARD_DESIGN_CHANGED)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requestCameraPermissionLauncher =
      registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { isGranted ->
        if (isGranted.values.none { !it }) {
          pickPhoto()
        }
      }

  }

  override fun initView() {
    viewBinding.flipMonthView.setTime(month, year)
    viewBinding.flipMonthView.callback = this
    viewBinding.flipMonthView.showDateTime(month, year)
  }

  override suspend fun subscribeData() {

  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(cardDesignChangedBroadcast, cardDesignChangedIntentFilter)

    lifecycleScope.launch {
      lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        monthPostsViewModel.getPostInMonth(month + 1, year)
        monthPostsViewModel.getCardTemplate(month, year)
      }
    }

    lifecycleScope.launch {
      lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        safeCollectLatestFlow(monthPostsViewModel.template) {
          if (it is State.DataState) {
            val template = it.data
            val time = String.format("%02d-%04d", month, year)
            if (time != template.time) {
              return@safeCollectLatestFlow
            }
            withContext(Dispatchers.Main) { viewBinding.flipMonthView.bindCardTemplate(template) }

          }
        }
      }
    }

    lifecycleScope.launch {
      lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        safeCollectFlow(viewModel.monthCardStateFlow) {
          when (it) {
            MonthCardState.FRONT -> viewBinding.flipMonthView.showFront()
            else -> viewBinding.flipMonthView.showBehind()
          }
        }
      }
    }

    lifecycleScope.launch {
      lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        safeCollectLatestFlow(monthPostsViewModel.monthData) {
          when (it) {
            is State.LoadingState -> viewBinding.flipMonthView.showLoading(true)
            is State.ErrorState -> {
              viewBinding.flipMonthView.showLoading(false)
              it.exception.printStackTrace()
            }
            is State.DataState -> {
              viewBinding.flipMonthView.showLoading(false)
              //Behind
              val data = it.data.daysData
              viewBinding.flipMonthView.showMonthDataByDays(data)
              //Front
              viewBinding.flipMonthView.bindMonthGeneralData(it.data)
            }
          }
        }
      }
    }
  }

  companion object {
    const val KEY_CARD_DESIGN_CHANGED = "KEY_CARD_DESIGN_CHANGED"
    fun getInstance(month: Int, year: Int): MonthCardFragment {
      return MonthCardFragment().apply {
        arguments = Bundle().apply {
          putInt(KEY_MONTH, month)
          putInt(KEY_YEAR, year)
        }
      }
    }
  }

  override fun onClickDay(timestamp: Long) {
    (requireParentFragment() as? DashboardFragment)?.showCreatePostScreen(timestamp)
  }

  override fun onShowPostsOfMonth(month: Int, year: Int) {
    (requireParentFragment() as? DashboardFragment)?.showMonthPost(
      1 + month, year
    )
  }

  override fun onClickSettingFrontCard() {
    CardDesignDialog.getInstance(month, year, object : CardDesignListener {
      override fun onSubmit(cardTemplate: CardTemplate) {
        monthPostsViewModel.updateCard(month, year, cardTemplate.type, cardTemplate.data, null)
      }

      override fun onChooseBrowseGallery() {
        pickPhoto()
      }

    }).show(childFragmentManager, "CardDesign")
  }

  private fun pickPhoto() {
    checkPermission {
      TedImagePicker
        .with(requireContext())
        .title(R.string.image_picker_title)
        .buttonText(R.string.ted_image_picker_done)
        .dropDownAlbum()
        .max(10, getString(R.string.max_images_selected_warning))
        .image()
        .start {
          monthPostsViewModel.updateCard(month, year, TEMPLATE_PHOTO, "", it)
        }
    }
  }

  private fun checkPermission(onGranted: () -> Unit) {
    when {
      requireContext().hasPermissions(
        arrayOf(
          Manifest.permission.CAMERA
        )
      ) -> {
        onGranted.invoke()
      }
      else -> {
        showDialogCamera {
          Dexter.withContext(requireActivity()).withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
              override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                onGranted.invoke()
              }

              override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
              }

              override fun onPermissionRationaleShouldBeShown(
                p0: PermissionRequest?,
                p1: PermissionToken?
              ) {
              }

            }).check()
        }
      }
    }
  }

  private fun showDialogCamera(onPositive: () -> Unit) {
    AlertDialog.Builder(requireContext()).setTitle(R.string.title_camera_permission_required)
      .setMessage(R.string.message_camera_permission_required)
      .setPositiveButton(R.string.label_ok_popup) { _, _ -> onPositive.invoke() }
      .setNegativeButton(R.string.cancel) { _, _ -> }
      .show()

  }

  override fun onDestroyView() {
    super.onDestroyView()
    LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(cardDesignChangedBroadcast)
  }

}

const val KEY_MONTH = "month"
const val KEY_YEAR = "year"