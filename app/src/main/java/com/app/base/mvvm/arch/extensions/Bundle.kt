package com.app.base.mvvm.arch.extensions

import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable

inline fun <reified T : Parcelable> Bundle.getParcelableUtil(key: String): T? = when {
  SDK_INT >= 33 -> getParcelable(key, T::class.java)
  else ->
    @Suppress("DEPRECATION")
    getParcelable(key)
      as? T
}

inline fun <reified T : Parcelable> Bundle.getParcelableArrayListUtil(key: String): ArrayList<T>? = when {
  SDK_INT >= 33 -> getParcelableArrayList(key, T::class.java)
  else ->
    @Suppress("DEPRECATION")
    getParcelableArrayList(key)
}
