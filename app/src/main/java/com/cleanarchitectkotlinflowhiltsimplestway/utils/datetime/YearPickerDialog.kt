package com.cleanarchitectkotlinflowhiltsimplestway.utils.datetime

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.cleanarchitectkotlinflowhiltsimplestway.R
import com.cleanarchitectkotlinflowhiltsimplestway.databinding.DialogYearPickerBinding
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseDialogFragment
import com.dtv.starter.presenter.utils.extension.setSafeOnClickListener
import com.dtv.starter.presenter.utils.extension.showKeyboard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


class YearPickerDialog: BaseDialogFragment() {

  lateinit var vb: DialogYearPickerBinding

  var listener: OnYearSelected? = null

  private val blockCharacterSet = "-"

  private val filter = InputFilter { source, start, end, dest, dstart, dend ->
    if (source != null && blockCharacterSet.contains("" + source)) {
      ""
    } else null
  }
  override val needAdjustWidth = true

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    vb = DataBindingUtil.inflate(inflater, R.layout.dialog_year_picker, container, false)
    return vb.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    vb.etYear.apply {
      filters = listOf(filter).toTypedArray()
      setText("${Calendar.getInstance().get(Calendar.YEAR)}")
    }
    setListeners()
    lifecycleScope.launch {
      delay(200)
      vb.etYear.requestFocus()
      requireActivity().showKeyboard(vb.etYear)
    }
  }

  private fun setListeners() {
    vb.btOk.setSafeOnClickListener {
      val yearInText = vb.etYear.text.toString()
      if (yearInText.isEmpty()) {
        Toast.makeText(context, context?.getString(R.string.msg_invalid_year_empty), Toast.LENGTH_SHORT).show()
        return@setSafeOnClickListener
      }
      val year = yearInText.toInt()
      if (year <= 0) {
        Toast.makeText(context, context?.getString(R.string.msg_invalid_year_invalid), Toast.LENGTH_SHORT).show()
        return@setSafeOnClickListener
      }
      listener?.onYearSelected(year)
      dismissAllowingStateLoss()
    }
    vb.btCancel.setSafeOnClickListener {
      dismissAllowingStateLoss()
    }
  }

}

interface OnYearSelected {
  fun onYearSelected(year: Int)
}