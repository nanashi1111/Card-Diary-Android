package com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard

import android.Manifest
import android.content.*
import android.net.Uri
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
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.*
import com.cleanarchitectkotlinflowhiltsimplestway.databinding.FragmentMonthCardFrontBinding
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.MainActivity
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewBindingFragment
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.create.photo.SelectedPhotoAdapter
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.dashboard.DashboardFragment
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard.design.CardDesignDialog
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard.design.CardDesignListener
import com.cleanarchitectkotlinflowhiltsimplestway.utils.datetime.monthInText
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.coloredBackground
import com.dtv.starter.presenter.utils.extension.*
import com.dtv.starter.presenter.utils.log.Logger
import com.google.android.material.tabs.TabLayoutMediator
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import dagger.hilt.android.AndroidEntryPoint
import gun0912.tedimagepicker.builder.TedImagePicker
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MonthCardFrontFragment : BaseViewBindingFragment<FragmentMonthCardFrontBinding, MonthCardViewModel>(FragmentMonthCardFrontBinding::inflate) {

  override val viewModel: MonthCardViewModel by lazy {
    (requireParentFragment() as MonthCardFragment).viewModel
  }

  private val monthPostsViewModel: MonthPostViewModel by lazy {
    (requireParentFragment() as MonthCardFragment).monthPostsViewModel
  }

  private val cardViewModel: FrontCardViewModel by viewModels()

  private var requestCameraPermissionLauncher: ActivityResultLauncher<Array<String>>? = null

  private val cardDesignChangedBroadcast = object: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
      p1?.let {
        val month = it.getIntExtra(KEY_MONTH, 1)
        val year = it.getIntExtra(KEY_YEAR, 1)
        val currentMonth = requireArguments().getInt(KEY_MONTH) + 1
        val currentYear = requireArguments().getInt(KEY_YEAR)
        if (month == currentMonth && year == currentYear) {
          val template = it.getSerializableExtra(KEY_CARD_DESIGN) as CardTemplate
          Logger.d("BindTemplateFromBroadCast: $currentMonth/$currentYear : $template")
          bindCardTemplate(template)
        }
      }
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
    lifecycleScope.launch {
      lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        monthPostsViewModel.monthData.collectLatest {
          viewBinding.pbLoading.beVisibleIf(it is State.LoadingState)
          when (it) {
            is State.ErrorState -> {
              it.exception.printStackTrace()
            }
            is State.DataState -> {
              val data = it.data
              viewBinding.tvPostData.text = "${data.generalDayData.dayWithPosts}/${data.generalDayData.totalDays}"
              viewBinding.pbProgress.progress = data.generalDayData.progress
            }
            else -> {}
          }
        }
      }

    }
    lifecycleScope.launch {
      lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        val month = 1 + requireArguments().getInt(KEY_MONTH)
        val year = requireArguments().getInt(KEY_YEAR)

        monthPostsViewModel.getPostInMonth(month, year)
      }
    }

    lifecycleScope.launch {
      lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        cardViewModel.getCardTemplate(1 + requireArguments().getInt(KEY_MONTH), requireArguments().getInt(KEY_YEAR))
      }
    }

    lifecycleScope.launch {
      lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        cardViewModel.template.collectLatest {
          if (it is State.DataState) {
            val template = it.data
            Logger.d("Collected template: $template")
            val time = String.format("%02d-%04d", 1 + requireArguments().getInt(KEY_MONTH), requireArguments().getInt(KEY_YEAR))
            if (time != template.time) {
              return@collectLatest
            }
            bindCardTemplate(template)
          }
        }
      }
    }
  }

  private fun bindCardTemplate(template: CardTemplate) {
    when (template.type) {
      TEMPLATE_DEFAULT -> {
        val month = 1 + requireArguments().getInt(KEY_MONTH)
        viewBinding.ivBackground.loadResource(resourceId(month), resources.getDimensionPixelSize(R.dimen.background_image_radius))
      }
      TEMPLATE_COLOR -> {
        val color = template.data
        viewBinding.ivBackground.setImageBitmap(coloredBackground(color))
      }
      TEMPLATE_PHOTO -> {
        val filePath = template.data
        viewBinding.ivBackground.loadBackgroundFile(filePath, resources.getDimensionPixelSize(R.dimen.background_image_radius))
      }
    }
  }

  override fun initView() {
    val month = requireArguments().getInt(KEY_MONTH)
    viewBinding.apply {
      tvMonthInNumber.text = "${1 + month}"
      tvMonthInText.text = monthInText(month)
      clContent.setSafeOnClickListener {
        ((requireParentFragment().requireParentFragment()) as DashboardFragment).showMonthPost(
          1 + month, requireArguments().getInt(KEY_YEAR)
        )
      }
      ivSetting.setSafeOnClickListener {
        val month = 1 + requireArguments().getInt(KEY_MONTH)
        val year = requireArguments().getInt(KEY_YEAR)
        CardDesignDialog.getInstance(month, year, object : CardDesignListener {
          override fun onSubmit(cardTemplate: CardTemplate) {
            cardViewModel.updateCard(month, year, cardTemplate.type, cardTemplate.data, null)
          }

          override fun onChooseBrowseGallery() {
            pickPhoto()
          }

        }).show(this@MonthCardFrontFragment.childFragmentManager, "CardDesign")
      }
    }

  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(cardDesignChangedBroadcast, cardDesignChangedIntentFilter)
    cardViewModel.getCardTemplate(1 + requireArguments().getInt(KEY_MONTH), requireArguments().getInt(KEY_YEAR))
  }

  override fun onDestroyView() {
    LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(cardDesignChangedBroadcast)
    super.onDestroyView()
  }

  override suspend fun subscribeData() {
  }

  private fun resourceId(month: Int) = when (month) {
    1 -> R.drawable.bg_jan
    2 -> R.drawable.bg_feb
    3 -> R.drawable.bg_mar
    4 -> R.drawable.bg_apr
    5 -> R.drawable.bg_may
    6 -> R.drawable.bg_jun
    7 -> R.drawable.bg_jul
    8 -> R.drawable.bg_aug
    9 -> R.drawable.bg_sep
    10 -> R.drawable.bg_oct
    11 -> R.drawable.bg_nov
    else -> R.drawable.bg_dec
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
          val month = 1 + requireArguments().getInt(KEY_MONTH)
          val year = requireArguments().getInt(KEY_YEAR)
          cardViewModel.updateCard(month, year, TEMPLATE_PHOTO, "", it)

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
      .setPositiveButton(R.string.label_ok_popup) { p0, p1 -> onPositive.invoke() }
      .setNegativeButton(R.string.cancel) { p0, p1 -> }
      .show()

  }

  companion object {

    const val KEY_CARD_DESIGN_CHANGED = "KEY_CARD_DESIGN_CHANGED"
    const val KEY_CARD_DESIGN = "KEY_CARD_DESIGN"

    fun getInstance(month: Int, year: Int): MonthCardFrontFragment {
      return MonthCardFrontFragment().apply {
        arguments = Bundle().apply {
          putInt(KEY_MONTH, month)
          putInt(KEY_YEAR, year)
        }
      }
    }
  }
}