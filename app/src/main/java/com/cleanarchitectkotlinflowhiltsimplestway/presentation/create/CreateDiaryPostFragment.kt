package com.cleanarchitectkotlinflowhiltsimplestway.presentation.create

import android.Manifest
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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.cleanarchitectkotlinflowhiltsimplestway.BuildConfig
import com.cleanarchitectkotlinflowhiltsimplestway.R
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.WeatherType
import com.cleanarchitectkotlinflowhiltsimplestway.databinding.FragmentCreateDiaryPostBinding
import com.cleanarchitectkotlinflowhiltsimplestway.domain.exception.CONTENT_EMPTY
import com.cleanarchitectkotlinflowhiltsimplestway.domain.exception.InvalidDiaryPostException
import com.cleanarchitectkotlinflowhiltsimplestway.domain.exception.TITLE_EMPTY
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewBindingFragment
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.create.photo.SelectedPhotoAdapter
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.create.weather.WeatherSelectedListener
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.create.weather.WeatherSelectorDialog
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.dialog.ConfirmDialog
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.dialog.ConfirmListener
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.posts.bindWeather
import com.cleanarchitectkotlinflowhiltsimplestway.utils.ads.AdsManager
import com.cleanarchitectkotlinflowhiltsimplestway.utils.datetime.dateTimeInCreateDiaryScreen
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.*
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
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class CreateDiaryPostFragment : BaseViewBindingFragment<FragmentCreateDiaryPostBinding, CreateDiaryPostViewModel>(FragmentCreateDiaryPostBinding::inflate),
  WeatherSelectedListener {

  override val viewModel: CreateDiaryPostViewModel by viewModels()

  private val args: CreateDiaryPostFragmentArgs by navArgs()

  private var requestCameraPermissionLauncher: ActivityResultLauncher<Array<String>>? = null

  @Inject
  lateinit var adsManager: AdsManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requestCameraPermissionLauncher =
      registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { isGranted ->
        if (isGranted.values.none { !it }) {
          pickPhoto()
        }
      }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    args.post?.let {
      viewModel.getDiaryPost(it.date)
      viewModel.postId = it.date
    } ?: run {
      viewModel.postId = args.time
      viewModel.toggleToEditMode()
    }

    viewBinding.tvDate.text = dateTimeInCreateDiaryScreen(d = Date(viewModel.postId))

    safeCollectFlow(viewModel.diaryMode) { mode ->
      when (mode) {
        DiaryMode.VIEW -> viewBinding.apply {
          llModifyImages.beGone()
          tvSave.setText(R.string.edit)
          etTitle.isEnabled = false
          etContent.isEnabled = false
        }

        DiaryMode.EDIT -> viewBinding.apply {
          llModifyImages.beVisible()
          ivRemovePhoto.beVisibleIf((viewBinding.vpSelectedImages.adapter?.itemCount ?: 0) > 0)
          tvSave.setText(R.string.save)
          etTitle.isEnabled = true
          etContent.isEnabled = true
          etTitle.requestFocus()
        }
      }
    }

    safeCollectFlow(viewModel.diaryPost) {
      if (it is State.DataState) {
        val post = it.data
        viewBinding.apply {
          etTitle.setText(post.title)
          etContent.setText(post.content)
          ivWeather.bindWeather(post.weather)
          if (post.images.isNotEmpty()) {
            val adapter = SelectedPhotoAdapter(this@CreateDiaryPostFragment, post.images.map { imageFile ->
              val f = File(imageFile)
              Uri.fromFile(f)
            }.toMutableList())
            vpSelectedImages.adapter = adapter
            TabLayoutMediator(indicator, viewBinding.vpSelectedImages) { _, _ ->

            }.attach()
            viewModel.focusImage(0)
            viewModel.selectedWeather.value = post.weather


            viewBinding.ivRemovePhoto.beVisibleIf((viewBinding.vpSelectedImages.adapter?.itemCount ?: 0) > 0)
          }
          viewBinding.indicator.beInvisibleIf(viewBinding.vpSelectedImages.adapter?.itemCount == 0)
        }
        viewModel.postId = post.date

      }
    }



  }

  override fun onDestroy() {
    super.onDestroy()
    requestCameraPermissionLauncher?.unregister()
  }

  override fun initView() {

    prepareMockDataIfNeeded()

    viewBinding.apply {
      ivBack.setSafeOnClickListener {
        ConfirmDialog.getInstance().apply {
          listener = object : ConfirmListener {
            override fun onConfirmed() {
              findNavController().safeNavigateUp()
            }
          }
          show(this@CreateDiaryPostFragment.childFragmentManager, "Confirm")
        }
      }
      ivWeather.setSafeOnClickListener {

        if (viewModel.diaryMode.value == DiaryMode.VIEW) {
          return@setSafeOnClickListener
        }

        WeatherSelectorDialog.newInstance(
          viewModel.selectedWeather.value.name
        ).apply {
          listener = this@CreateDiaryPostFragment
          show(this@CreateDiaryPostFragment.childFragmentManager, "Weather")
        }
      }
      ivAddPhoto.setSafeOnClickListener {
        if (viewModel.diaryMode.value == DiaryMode.VIEW) {
          return@setSafeOnClickListener
        }
        pickPhoto()
      }


      vpSelectedImages.apply {
        isSaveEnabled = false
        adapter = SelectedPhotoAdapter(this@CreateDiaryPostFragment, mutableListOf())
        registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
          override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            viewModel.focusImage(position)
          }
        })

        TabLayoutMediator(indicator, this) { _, _ ->

        }.attach()
      }


      tvSave.setSafeOnClickListener {

        when (viewModel.diaryMode.value) {
          DiaryMode.VIEW -> {
            viewModel.toggleToEditMode()
          }
          DiaryMode.EDIT -> {
            adsManager.displayPopupAds {
              viewModel.saveDiary(
                (viewBinding.vpSelectedImages.adapter as SelectedPhotoAdapter).uris,
                viewBinding.etTitle.text.toString(),
                viewBinding.etContent.text.toString(),
                viewModel.selectedWeather.value,
                updateExisting = args.post != null
              )
            }
          }
        }
      }

      viewBinding.ivRemovePhoto.setSafeOnClickListener {

        val currentFocused = viewModel.focusedImagePosition

        val currentList = (viewBinding.vpSelectedImages.adapter as SelectedPhotoAdapter).uris
        currentList.removeAt(viewModel.focusedImagePosition)
        val adapter = SelectedPhotoAdapter(this@CreateDiaryPostFragment, currentList)
        viewBinding.vpSelectedImages.adapter = adapter
        TabLayoutMediator(indicator, viewBinding.vpSelectedImages) { _, _ ->

        }.attach()
        viewBinding.ivRemovePhoto.beVisibleIf((viewBinding.vpSelectedImages.adapter?.itemCount ?: 0) > 0)
        viewBinding.indicator.beInvisibleIf(viewBinding.vpSelectedImages.adapter?.itemCount == 0)
        if (viewBinding.vpSelectedImages.adapter!!.itemCount > currentFocused) {
          viewBinding.vpSelectedImages.setCurrentItem(currentFocused, true)
        }
      }
    }
  }

  override suspend fun subscribeData() {
    //Save diary
    safeCollectLatestFlow(viewModel.saveDiaryResultFlow) {
      when (it) {
        is State.LoadingState -> {
          displayLoadingDialog(true)
        }
        is State.ErrorState -> {
          val error = it.exception
          error.printStackTrace()
          if (error is InvalidDiaryPostException) {
            when (error.errorCode) {
              CONTENT_EMPTY -> showErrorMessage(getString(R.string.error_empty_content))
              TITLE_EMPTY -> showErrorMessage(getString(R.string.error_empty_title))
            }
          }
          displayLoadingDialog(false)
        }
        is State.DataState -> {
          displayLoadingDialog(false)
          showSuccessMessage(getString(R.string.save_diary_success))
          findNavController().safeNavigateUp()
        }
      }
    }
    // Weather selected
    safeCollectFlow(viewModel.selectedWeather) {
      when (it) {
        WeatherType.SUNNY -> viewBinding.ivWeather.setImageResource(R.drawable.ic_weather_sun)
        WeatherType.CLOUDY -> viewBinding.ivWeather.setImageResource(R.drawable.ic_weather_cloudy)
        WeatherType.RAINY -> viewBinding.ivWeather.setImageResource(R.drawable.ic_weather_rainy)
        WeatherType.SNOWY -> viewBinding.ivWeather.setImageResource(R.drawable.ic_weather_snowy)
        WeatherType.LIGHTING -> viewBinding.ivWeather.setImageResource(R.drawable.ic_weather_thunder)
        WeatherType.STORMY -> viewBinding.ivWeather.setImageResource(R.drawable.ic_weather_tornado)
      }
    }

  }

  private fun pickPhoto() {
    checkPermission {
      TedImagePicker
        .with(requireContext())
        .title(R.string.image_picker_title)
        .buttonText(R.string.ted_image_picker_done)
        .dropDownAlbum()
        .max(10 - (viewBinding.vpSelectedImages.adapter?.itemCount ?: 0), getString(R.string.max_images_selected_warning))
        .image()
        .startMultiImage { uris ->
          val currentList = (viewBinding.vpSelectedImages.adapter as SelectedPhotoAdapter).uris
          currentList.addAll(uris)
          val adapter = SelectedPhotoAdapter(this@CreateDiaryPostFragment, currentList)
          viewBinding.vpSelectedImages.adapter = adapter
          TabLayoutMediator(viewBinding.indicator, viewBinding.vpSelectedImages) { _, _ ->

          }.attach()
          viewBinding.ivRemovePhoto.beVisibleIf((viewBinding.vpSelectedImages.adapter?.itemCount ?: 0) > 0)
          viewBinding.indicator.beInvisibleIf(viewBinding.vpSelectedImages.adapter?.itemCount == 0)
          lifecycleScope.launch {
            delay(200)
            viewBinding.vpSelectedImages.adapter?.let {
              if (it.itemCount > 0) {
                viewBinding.vpSelectedImages.setCurrentItem(it.itemCount - 1, true)
              }
            }
          }
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

  private fun prepareMockDataIfNeeded() {
    if (BuildConfig.DEBUG) {
      viewBinding.etTitle.setText(getString(R.string.mock_title))
      viewBinding.etContent.setText(getString(R.string.mock_content))
    }
  }

  override fun onSelected(weather: WeatherType) {
    viewModel.selectedWeather.value = weather
  }
}