package com.app.base.mvvm.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.app.base.mvvm.R
import com.app.base.mvvm.base.BaseActivity
import com.app.base.mvvm.base.BaseViewModel
import com.app.base.mvvm.data.source.LoadingState
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheetDialogFragment(
  @LayoutRes val layoutId: Int
) : BottomSheetDialogFragment(layoutId) {

  private lateinit var mDataBinding: ViewDataBinding

  private var baseActivity: BaseActivity? = null

  abstract fun applyBinding(viewDataBinding: ViewDataBinding)

  abstract fun onInit(view: View, fragmentArg: Bundle?, saveInstance: Bundle?)

  abstract fun applyHeightDialog()

  override fun onAttach(context: Context) {
    super.onAttach(context)
    if (context is BaseActivity) {
      baseActivity = context
    }
  }

  override fun getTheme(): Int {
    return R.style.AppBottomSheetDialogTheme
  }

  override fun onStart() {
    super.onStart()
    applyHeightDialog()
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    mDataBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
    applyBinding(mDataBinding)
    return mDataBinding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    mDataBinding.lifecycleOwner = viewLifecycleOwner
    onInit(view, arguments, savedInstanceState)
  }

  fun listenerLoading(viewModel: BaseViewModel) {
    viewModel.flagLoadingState.observe(this) { state ->
      state?.let {
        if (it == LoadingState.LOADING) {
          showLoadingDialog()
        } else {
          hideLoadingDialog()
        }
      }
    }
  }

  fun showLoadingDialog() {
    (activity as? BaseActivity)?.takeIf { !it.isFinishing }?.showLoading()
  }

  fun hideLoadingDialog() {
    baseActivity?.dismissLoading()
  }

  fun showLoading() {
    baseActivity?.showLoading()
  }

  fun showMessageDialog(message: String, onPositiveButtonListener: () -> Unit) {
    baseActivity?.showMessageDialog(message, onPositiveButtonListener)
  }

  fun showMessageDialog(
    title: String,
    message: String,
    textNegative: Int,
    textPositive: Int,
    onNegativeButtonListener: () -> Unit,
    onPositiveButtonListener: () -> Unit
  ) {
    baseActivity?.showMessageDialog(
      title,
      message,
      textNegative,
      textPositive,
      onNegativeButtonListener,
      onPositiveButtonListener
    )
  }

  fun showFullDialog(
    title: Int,
    message: Int,
    textNegative: Int,
    textPositive: Int,
    onPositiveButtonListener: () -> Unit
  ) {
    baseActivity?.showFullDialog(title, message, textNegative, textPositive, onPositiveButtonListener)
  }
}
