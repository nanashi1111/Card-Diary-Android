package com.cleanarchitectkotlinflowhiltsimplestway.presentation.unlock

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.cleanarchitectkotlinflowhiltsimplestway.R
import com.cleanarchitectkotlinflowhiltsimplestway.databinding.DialogUnlockBinding
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseDialogFragment
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.adjustWidth
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.safeCollectLatestFlow
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList

@AndroidEntryPoint
class UnlockDialog : BaseDialogFragment() {

  private lateinit var vb: DialogUnlockBinding

  private val vm: UnlockViewModel by activityViewModels()

  override val needAdjustWidth: Boolean = false

  override fun onStart() {
    super.onStart()
    dialog?.setCancelable(false)
    adjustWidth(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    vb = DataBindingUtil.inflate(inflater, R.layout.dialog_unlock, container, false)
    return vb.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    vb.patternLockView.setOnPatternListener(object :com.itsxtt.patternlock.PatternLockView.OnPatternListener {
      override fun onComplete(ids: ArrayList<Int>): Boolean {
        vm.onPatternComplete(ids)
        return true
      }

    })

    vb.ivCancel.setOnClickListener {
      when (patternData.purpose) {
        PatternPurpose.UNLOCK -> { requireActivity().finish() }
        PatternPurpose.SETUP -> { dismissAllowingStateLoss() }
      }

    }

    safeCollectLatestFlow(vm.patternPurpose) {
      when (it.purpose) {
        PatternPurpose.SETUP -> {
          when (it.step) {
            PatternStep.FIRST -> {
              //vb.patternLockView.clearPattern()
              vb.tvTitle.text = getString(R.string.txt_pattern_draw)
            }
            PatternStep.CONFIRM -> {
              //vb.patternLockView.clearPattern()
              vb.tvTitle.text = getString(R.string.txt_pattern_redraw)
            }
          }
        }

        PatternPurpose.UNLOCK -> {
          //vb.patternLockView.clearPattern()
          vb.tvTitle.text = getString(R.string.txt_pattern_require_draw)
        }
      }
    }

    safeCollectLatestFlow(vm.success) {
      if (it) {
        when (patternData.purpose) {
          PatternPurpose.SETUP -> {
            Toast.makeText(requireActivity(), getString(R.string.txt_setting_saved), Toast.LENGTH_SHORT).show()
            dismissAllowingStateLoss()
          }
          PatternPurpose.UNLOCK -> {
            dismissAllowingStateLoss()
          }
        }
      } else {
        when (patternData.purpose) {
          PatternPurpose.SETUP -> {
            Toast.makeText(requireActivity(), getString(R.string.txt_wrong_pattern_setup), Toast.LENGTH_SHORT).show()
            //vb.patternLockView.clearPattern()
          }
          PatternPurpose.UNLOCK -> {
            Toast.makeText(requireActivity(), getString(R.string.txt_wrong_pattern_unlock), Toast.LENGTH_SHORT).show()
            //vb.patternLockView.clearPattern()
          }
        }

      }
    }

    vm.initPattern(patternData)
  }

  private val patternData: PatternData by lazy {
    requireArguments().getSerializable(KEY_DATA) as PatternData
  }

  companion object {

    private const val KEY_DATA = "data"

    fun newInstance(data: PatternData): UnlockDialog {
      return UnlockDialog().apply {
        arguments = Bundle().apply {
          putSerializable(KEY_DATA, data)
        }
      }
    }
  }

}
