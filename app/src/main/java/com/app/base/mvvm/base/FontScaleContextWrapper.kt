package com.app.base.mvvm.base

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration

class FontScaleContextWrapper(base: Context) : ContextWrapper(base) {
  companion object {
    fun wrap(context: Context): ContextWrapper {
      val config = Configuration(context.resources.configuration)
      config.fontScale = 1.0f
      return FontScaleContextWrapper(context.createConfigurationContext(config))
    }
  }
}
