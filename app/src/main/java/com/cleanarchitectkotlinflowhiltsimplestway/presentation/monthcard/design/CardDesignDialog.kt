package com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard.design

import android.Manifest
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cleanarchitectkotlinflowhiltsimplestway.R
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.CardTemplate
import com.cleanarchitectkotlinflowhiltsimplestway.data.entity.TEMPLATE_COLOR
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseBottomSheetFragment
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard.KEY_MONTH
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.monthcard.KEY_YEAR
import com.dtv.starter.presenter.utils.extension.hasPermissions
import com.dtv.starter.presenter.utils.extension.setSafeOnClickListener
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import gun0912.tedimagepicker.builder.TedImagePicker
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CardDesignDialog private constructor(): BaseBottomSheetFragment(R.layout.dialog_card_design) {

  var listener: CardDesignListener? = null

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

  private fun pickPhoto() {
    checkPermission {
      TedImagePicker
        .with(requireContext())
        .title(R.string.image_picker_title)
        .buttonText(R.string.ted_image_picker_done)
        .dropDownAlbum()
        .image()
        .start {
          listener?.onSubmit(it)
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
      .setPositiveButton(R.string.label_ok_popup, object : DialogInterface.OnClickListener{
        override fun onClick(p0: DialogInterface?, p1: Int) {
          onPositive.invoke()
        }
      })
      .setNegativeButton(R.string.cancel, object : DialogInterface.OnClickListener {
        override fun onClick(p0: DialogInterface?, p1: Int) {
        }
      })
      .show()

  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    view.findViewById<RecyclerView>(R.id.rvColors).apply {
      layoutManager = GridLayoutManager(requireContext(), 6)
      adapter = provideColorAdapter()
    }
    view.findViewById<View>(R.id.tvPickPhoto).setSafeOnClickListener {
      pickPhoto()
    }
    view.findViewById<View>(R.id.tvCancel).setOnClickListener {
      dismissAllowingStateLoss()
    }
  }

  private fun provideColorAdapter(): ColorAdapter {
    val colors = mutableListOf(
      "#528B8B", "#A0522D", "#8B7D6B", "#1874CD",
      "#6E7B8B", "#00C5CD", "#00CD66", "#CDCD00",
      "#CD5555", "#CD919E", "#68228B", "#ECAB53",
      "#FFA07A", "#00008B", "#E6E6FA", "#CDC9C9"
    )
    return ColorAdapter(colors) {
      color ->
      val time = String.format("%02d-%04d", requireArguments().getInt(KEY_MONTH), requireArguments().getInt(KEY_YEAR))
      listener?.onSubmit(
        CardTemplate(time, type = TEMPLATE_COLOR, data = color)
      )
      dismissAllowingStateLoss()
    }
  }

  companion object {
    fun getInstance(mont: Int, year: Int, Cardlistener: CardDesignListener) : CardDesignDialog {
      return CardDesignDialog().apply {
        arguments = Bundle().apply {
          putInt(KEY_MONTH, mont)
          putInt(KEY_YEAR, year)
        }
        listener = Cardlistener
      }
    }
  }
}


interface CardDesignListener {
  fun onSubmit(cardTemplate: CardTemplate)
  fun onSubmit(uri: Uri)
}