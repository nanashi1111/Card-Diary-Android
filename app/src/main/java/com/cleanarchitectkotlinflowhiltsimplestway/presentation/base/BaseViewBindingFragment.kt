package com.cleanarchitectkotlinflowhiltsimplestway.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.dialog.LoadingDialog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

abstract class BaseViewBindingFragment<T : ViewBinding, VM : BaseViewModel>(private val initVb: (LayoutInflater, ViewGroup?, Boolean) -> T) :
  Fragment() {

  protected var loadingDialog: LoadingDialog? = null

  private var _viewBinding: T? = null
  val viewBinding: T
    get() = _viewBinding!!

  abstract val viewModel: VM

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    _viewBinding = initVb.invoke(inflater, container, false)
    return viewBinding.root
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    /*lifecycleScope.launch {
      lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        //lifecycleScope.launchWhenStarted { subscribeData() }
        subscribeData()
      }
    }*/
    /*lifecycleScope.launchWhenStarted {
      subscribeData()
    }*/
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    lifecycleScope.launch { subscribeData() }
    initView()
  }

  override fun onDestroyView() {
    _viewBinding = null
    super.onDestroyView()
  }

  abstract fun initView()
  abstract suspend fun subscribeData()

  protected fun displayLoadingDialog(show: Boolean) {
    if (show) {
      loadingDialog?.hide()
      loadingDialog = LoadingDialog()
      loadingDialog?.display(this)
    } else {
      loadingDialog?.hide()
      loadingDialog = null
    }
  }

}
