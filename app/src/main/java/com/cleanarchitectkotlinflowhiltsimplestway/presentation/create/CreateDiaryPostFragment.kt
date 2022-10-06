package com.cleanarchitectkotlinflowhiltsimplestway.presentation.create

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
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
import com.cleanarchitectkotlinflowhiltsimplestway.utils.datetime.dateTimeInCreateDiaryScreen
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.safeCollectFlow
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.safeNavigateUp
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.showErrorMessage
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.showSuccessMessage
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

@AndroidEntryPoint
class CreateDiaryPostFragment : BaseViewBindingFragment<FragmentCreateDiaryPostBinding, CreateDiaryPostViewModel>(FragmentCreateDiaryPostBinding::inflate),
   WeatherSelectedListener {

  override val viewModel: CreateDiaryPostViewModel by viewModels()

  private val args: CreateDiaryPostFragmentArgs by navArgs()

  private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
    if (it.resultCode == Activity.RESULT_OK) {
      val uri = it.data?.data!!
      Logger.d("Selected: ${uri.encodedPath}")

      val currentList = (viewBinding.vpSelectedImages.adapter as SelectedPhotoAdapter).uris
      currentList.add(uri)
      val adapter = SelectedPhotoAdapter(this@CreateDiaryPostFragment, currentList)
      viewBinding.vpSelectedImages.adapter = adapter
      TabLayoutMediator(viewBinding.indicator, viewBinding.vpSelectedImages) { _, _ ->

      }.attach()
      viewBinding.ivAddPhoto.beVisibleIf(viewBinding.vpSelectedImages.adapter?.itemCount == 0)
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

  private var requestCameraPermissionLauncher: ActivityResultLauncher<Array<String>>? = null

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
      Logger.d("Input Post: $it")
      viewBinding.apply {
        etTitle.setText(it.title)
        etContent.setText(it.content)
        ivWeather.bindWeather(it.weather)
        if (it.images.isNotEmpty()) {
          val adapter = SelectedPhotoAdapter(this@CreateDiaryPostFragment, it.images.map {
            imageFile ->
            val f = File(imageFile)
            Uri.fromFile(f)
          }.toMutableList())
          vpSelectedImages.adapter = adapter
          TabLayoutMediator(indicator, viewBinding.vpSelectedImages) { _, _ ->

          }.attach()
          viewModel.focusImage(0)
          viewModel.selectedWeather.value = it.weather
          ivAddPhoto.beGone()
        }
        viewBinding.indicator.beInvisibleIf(viewBinding.vpSelectedImages.adapter?.itemCount == 0)
      }
      viewModel.postId = it.date
    } ?: run {
      viewModel.postId = args.time
    }

  }

  override fun onDestroy() {
    super.onDestroy()
    requestCameraPermissionLauncher?.unregister()
  }

  override fun initView() {
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
        WeatherSelectorDialog.newInstance(
          viewModel.selectedWeather.value.name
        ).apply {
          listener = this@CreateDiaryPostFragment
          show(this@CreateDiaryPostFragment.childFragmentManager, "Weather")
        }
      }
      ivAddPhoto.setSafeOnClickListener {
        pickPhoto()
      }
      tvDate.text = dateTimeInCreateDiaryScreen()

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
        viewModel.saveDiary(
          (viewBinding.vpSelectedImages.adapter as SelectedPhotoAdapter).uris,
          viewBinding.etTitle.text.toString(),
          viewBinding.etContent.text.toString(),
          viewModel.selectedWeather.value,
          updateExisting = args.post != null
        )
      }

      llOptions.btToggleOptions.setSafeOnClickListener {
        viewModel.toggleOpeningOptionMenu()
      }

      llOptions.llRemoveImage.setSafeOnClickListener {

        viewModel.toggleOpeningOptionMenu()
        val currentList = (viewBinding.vpSelectedImages.adapter as SelectedPhotoAdapter).uris
        currentList.removeAt(viewModel.focusedImagePosition)
        val adapter = SelectedPhotoAdapter(this@CreateDiaryPostFragment, currentList)
        viewBinding.vpSelectedImages.adapter = adapter
        TabLayoutMediator(indicator, viewBinding.vpSelectedImages) { _, _ ->

        }.attach()
        viewBinding.ivAddPhoto.beVisibleIf(viewBinding.vpSelectedImages.adapter?.itemCount == 0)
        viewBinding.indicator.beInvisibleIf(viewBinding.vpSelectedImages.adapter?.itemCount == 0)
        if (viewBinding.vpSelectedImages.adapter!!.itemCount > 0) {
          viewBinding.vpSelectedImages.setCurrentItem(0, true)
        }
      }

      llOptions.llAddImage.setSafeOnClickListener {
        pickPhoto()
        viewModel.toggleOpeningOptionMenu()
      }

      llOptions.vContainer.setSafeOnClickListener {
        viewModel.toggleOpeningOptionMenu()
      }
    }
  }

  override suspend fun subscribeData() {
    //Save diary
    safeCollectFlow(viewModel.saveDiaryResultFlow) {
      when (it) {
        is State.LoadingState -> {
          Logger.d("Saving diary")
        }
        is State.ErrorState -> {
          val error = it.exception
          if (error is InvalidDiaryPostException) {
            when (error.errorCode) {
              CONTENT_EMPTY -> showErrorMessage(getString(R.string.error_empty_content))
              TITLE_EMPTY -> showErrorMessage(getString(R.string.error_empty_title))
            }
          }
          Logger.d("Saving diary error: ${it.exception}")
        }
        is State.DataState -> {
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
    // Toggle options menu
    safeCollectFlow(viewModel.openningOptionMenu) { opening ->
      viewBinding.apply {
        if (opening) {
          llOptions.root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.bg_create_post_options_layout_open))
          llOptions.llAddImage.beVisible()
          llOptions.llRemoveImage.beVisible()
          llOptions.btToggleOptions.setImageResource(R.drawable.ic_cancel)
          llOptions.btToggleOptions.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#65dbff"))
          llOptions.vContainer.beVisible()
        } else {
          llOptions.root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.bg_create_post_options_layout_close))
          llOptions.llAddImage.beGone()
          llOptions.llRemoveImage.beGone()
          llOptions.btToggleOptions.setImageResource(R.drawable.ic_options_create_post)
          llOptions.btToggleOptions.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#373737"))
          llOptions.vContainer.beGone()
        }
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
        .image()

        .startMultiImage { uris ->
          val currentList = (viewBinding.vpSelectedImages.adapter as SelectedPhotoAdapter).uris
          currentList.addAll(uris)
          val adapter = SelectedPhotoAdapter(this@CreateDiaryPostFragment, currentList)
          viewBinding.vpSelectedImages.adapter = adapter
          TabLayoutMediator(viewBinding.indicator, viewBinding.vpSelectedImages) { _, _ ->

          }.attach()
          viewBinding.ivAddPhoto.beVisibleIf(viewBinding.vpSelectedImages.adapter?.itemCount == 0)
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
      .setPositiveButton(R.string.label_ok_popup, object :DialogInterface.OnClickListener{
        override fun onClick(p0: DialogInterface?, p1: Int) {
          onPositive.invoke()
        }
      })
      .setNegativeButton(R.string.cancel, object :DialogInterface.OnClickListener {
        override fun onClick(p0: DialogInterface?, p1: Int) {
        }
      })
      .show()

  }

  override fun onSelected(weather: WeatherType) {
    viewModel.selectedWeather.value = weather
  }
}