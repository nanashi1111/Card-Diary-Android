package com.cleanarchitectkotlinflowhiltsimplestway.presentation.create

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.cleanarchitectkotlinflowhiltsimplestway.R
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.State
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.WeatherType
import com.cleanarchitectkotlinflowhiltsimplestway.databinding.FragmentCreateDiaryPostBinding
import com.cleanarchitectkotlinflowhiltsimplestway.domain.exception.CONTENT_EMPTY
import com.cleanarchitectkotlinflowhiltsimplestway.domain.exception.InvalidDiaryPostException
import com.cleanarchitectkotlinflowhiltsimplestway.domain.exception.TITLE_EMPTY
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewBindingFragment
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.create.photo.PhotoSelectorDialog
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.create.photo.PhotoSelectorListener
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.create.photo.PhotoSelectorMethod
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.create.photo.SelectedPhotoAdapter
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.create.weather.WeatherSelectedListener
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.create.weather.WeatherSelectorDialog
import com.cleanarchitectkotlinflowhiltsimplestway.utils.datetime.dateTimeInCreateDiaryScreen
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.safeCollectFlow
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.safeNavigateUp
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.showErrorMessage
import com.dtv.starter.presenter.utils.extension.beGone
import com.dtv.starter.presenter.utils.extension.beVisible
import com.dtv.starter.presenter.utils.extension.beVisibleIf
import com.dtv.starter.presenter.utils.extension.setSafeOnClickListener
import com.dtv.starter.presenter.utils.log.Logger
import com.github.drjacky.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateDiaryPostFragment: BaseViewBindingFragment<FragmentCreateDiaryPostBinding, CreateDiaryPostViewModel>(FragmentCreateDiaryPostBinding::inflate),
  PhotoSelectorListener, WeatherSelectedListener {

  override val viewModel: CreateDiaryPostViewModel by viewModels()

  private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
    if (it.resultCode == Activity.RESULT_OK) {
      val uri = it.data?.data!!
      Logger.d("Selected: ${uri.encodedPath}")
      selectedPhotoAdapter.append(uri)
      lifecycleScope.launch {
        delay(200)
        viewBinding.rvSelectedImages.smoothScrollToPosition(selectedPhotoAdapter.itemCount - 1)
        viewBinding.ivAddPhoto.beVisibleIf(selectedPhotoAdapter.itemCount == 0)
      }
    }
  }

  private val selectedPhotoAdapter: SelectedPhotoAdapter by lazy {
    SelectedPhotoAdapter()
  }

  override fun initView() {
    viewBinding.apply {
      ivBack.setSafeOnClickListener {
        findNavController().safeNavigateUp()
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
        showPhotoPickerChooserDialog()
      }
      tvDate.text = dateTimeInCreateDiaryScreen()

      rvSelectedImages.apply {
        val mLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        layoutManager = mLayoutManager
        adapter = selectedPhotoAdapter
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(this)
        addOnScrollListener(object :RecyclerView.OnScrollListener(){
          override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState === RecyclerView.SCROLL_STATE_IDLE) {
              val centerView: View? = snapHelper.findSnapView(mLayoutManager)
              centerView?.let {
                val pos: Int = mLayoutManager.getPosition(centerView)
                Logger.d("Snapped Item Position: $pos")
                viewModel.focusImage(pos)
              }
            }
          }
        })
      }

      tvSave.setSafeOnClickListener {
        viewModel.saveDiary(
          selectedPhotoAdapter.provideImages(),
          viewBinding.etTitle.text.toString(),
          viewBinding.etContent.text.toString(),
          WeatherType.SUNNY
        )
      }

      llOptions.btToggleOptions.setSafeOnClickListener {
        viewModel.toggleOpeningOptionMenu()
      }

      llOptions.llRemoveImage.setSafeOnClickListener {
        selectedPhotoAdapter.remove(viewModel.focusedImagePosition)
        viewModel.toggleOpeningOptionMenu()
        viewBinding.ivAddPhoto.beVisibleIf(selectedPhotoAdapter.itemCount == 0)
        if (selectedPhotoAdapter.itemCount > 0) {
          viewBinding.rvSelectedImages.smoothScrollToPosition(0)
          viewModel.focusImage(0)
        }
      }

      llOptions.llAddImage.setSafeOnClickListener {
        showPhotoPickerChooserDialog()
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
          Logger.d("Saving diary success")
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
        else -> {}
      }
    }
    // Toggle options menu
    safeCollectFlow(viewModel.openningOptionMenu) {
      opening ->
      viewBinding.apply {
        if (opening) {
          llOptions.root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.bg_create_post_options_layout_open))
          llOptions.llAddImage.beVisible()
          llOptions.llRemoveImage.beVisible()
          llOptions.btToggleOptions.setImageResource(R.drawable.ic_cancel)
          llOptions.btToggleOptions.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#65dbff"))
        } else {
          llOptions.root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.bg_create_post_options_layout_close))
          llOptions.llAddImage.beGone()
          llOptions.llRemoveImage.beGone()
          llOptions.btToggleOptions.setImageResource(R.drawable.ic_options_create_post)
          llOptions.btToggleOptions.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#373737"))
        }
      }
    }
  }

  private fun showPhotoPickerChooserDialog() {
    PhotoSelectorDialog().apply {
      listener = this@CreateDiaryPostFragment
      show(this@CreateDiaryPostFragment.childFragmentManager, "PhotoPicker")
    }
  }

  override fun onSelectPhotoPicker(picker: PhotoSelectorMethod) {
    when (picker) {
      PhotoSelectorMethod.CAMERA -> {
        imagePickerLauncher.launch(
          ImagePicker.with(requireActivity())
            .cameraOnly()
            .crop()
            .createIntent()
        )
      }
      PhotoSelectorMethod.GALLERY -> {
        imagePickerLauncher.launch(
          ImagePicker.with(requireActivity())
            .galleryOnly()
            .crop()
            .createIntent()
        )
      }
    }
  }

  override fun onSelected(weather: WeatherType) {
    viewModel.selectedWeather.value = weather
  }
}