package com.app.base.mvvm.view.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.WindowManager
import com.app.base.mvvm.R

/**
 * Base dialog
 */
abstract class BaseDialog(context: Context) : Dialog(context, R.style.AllDialogTransparent) {

  protected fun setAttributes() {
    window?.apply {
      setGravity(Gravity.CENTER)
      // setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
      val params = WindowManager.LayoutParams()
      params.copyFrom(attributes)
      params.width = WindowManager.LayoutParams.MATCH_PARENT
      params.height = WindowManager.LayoutParams.WRAP_CONTENT
      attributes = params
    }
  }
}
