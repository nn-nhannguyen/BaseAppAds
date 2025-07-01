package com.app.base.mvvm.arch.extensions

import android.content.Context
import android.os.Bundle
import android.os.ResultReceiver
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

// Hide keyboard from Fragment based on https://stackoverflow.com/a/45857155

fun Fragment.hideKeyboard() {
  view?.let { activity?.hideKeyboard(it) }
}

fun Context.hideKeyboard(view: View) {
  ContextCompat.getSystemService(this, InputMethodManager::class.java)?.apply {
    hideSoftInputFromWindow(view.windowToken, 0)
  }
}

// calls done() when hide keyboard is done
fun Fragment.hideKeyboard(done: (result: Boolean) -> Unit) {
  view?.let {
    activity?.hideKeyboard(view = it, done = done)
  }
}

fun Context.hideKeyboard(view: View, done: (result: Boolean) -> Unit) {
  ContextCompat.getSystemService(this, InputMethodManager::class.java)?.let {
    // ResultReceiver is not called when `hideSoftInputFromWindow` return false
    // ref: https://stackoverflow.com/questions/27858333/how-can-i-use-resultreceiver-in-inputmethodmanagerhidesoftinputfromwindow
    val hideCompleted = it.hideSoftInputFromWindow(
      view.windowToken,
      0,
      object : ResultReceiver(null) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
          done(true)
        }
      }
    )
    if (!hideCompleted) {
      done(hideCompleted)
    }
  }
}
