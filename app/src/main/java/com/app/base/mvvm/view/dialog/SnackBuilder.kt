package com.app.base.mvvm.view.dialog

import android.view.View
import com.google.android.material.snackbar.Snackbar

/**
 * SnackBuilder
 */
class SnackBuilder {
  var message: String? = null
  var primaryButtonTitle: String? = null
    private set
  var primaryButtonListener: View.OnClickListener? = null
    private set
  var time: Int = 0
    private set

  init {
    this.time = Snackbar.LENGTH_SHORT
  }

  fun setMessage(message: String): SnackBuilder {
    this.message = message
    return this
  }

  fun time(time: Int): SnackBuilder {
    this.time = time
    return this
  }

  fun button(btnTitle: String, listener: View.OnClickListener): SnackBuilder {
    this.primaryButtonTitle = btnTitle
    this.primaryButtonListener = listener
    return this
  }
}
