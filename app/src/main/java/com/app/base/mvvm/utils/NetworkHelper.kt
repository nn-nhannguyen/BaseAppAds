package com.app.base.mvvm.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object NetworkHelper {

  fun isNetworkConnected(context: Context): Boolean {
    var result: Boolean
    val connectivityManager =
      context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    val networkCapabilities = connectivityManager.activeNetwork ?: return false
    val activeNetwork =
      connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
    result = when {
      activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
      activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
      activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
      else -> false
    }
//    } else {
//      connectivityManager.run {
//        connectivityManager.activeNetworkInfo?.run {
//          result = when (type) {
//            ConnectivityManager.TYPE_WIFI -> true
//            ConnectivityManager.TYPE_MOBILE -> true
//            ConnectivityManager.TYPE_ETHERNET -> true
//            else -> false
//          }
//        }
//      }
//    }

    return result
  }
}
