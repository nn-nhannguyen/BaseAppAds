package com.app.base.mvvm.utils

import android.util.Log
import com.app.base.mvvm.BuildConfig

object LogUtil {
  fun logMessage(tag: String, msg: String) {
    if (BuildConfig.DEBUG || BuildConfig.FLAVOR == "dev") {
      run {
        Log.d(tag, msg)
      }
    }
  }
}
