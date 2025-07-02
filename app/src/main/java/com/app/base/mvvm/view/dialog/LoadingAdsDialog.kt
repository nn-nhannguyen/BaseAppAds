package com.app.base.mvvm.view.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.app.base.mvvm.R

class LoadingAdsDialog(
  context: Context?,
  private val mOnDialogPlayListener: OnDialogPlayListener?
) : Dialog(context!!, R.style.FullScreenStatusBarColor) {

  init {
    setCancelable(false)
    setCanceledOnTouchOutside(false)
    window?.apply {
      setFlags(
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
      )
      setGravity(Gravity.CENTER)
      val params = attributes.apply {
        width = WindowManager.LayoutParams.MATCH_PARENT
        height = WindowManager.LayoutParams.MATCH_PARENT
      }
      attributes = params
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.dialog_loading_ads)
    setOnDismissListener { mOnDialogPlayListener?.onDismiss() }
  }

  interface OnDialogPlayListener {
    fun onDismiss()
  }
}
