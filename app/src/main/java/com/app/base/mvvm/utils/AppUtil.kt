package com.app.base.mvvm.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager

object AppUtil {
  fun isNetworkConnected(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val linkProperty = cm.getLinkProperties(cm.activeNetwork)
    return linkProperty != null
  }

  fun openStore(activity: Activity) {
    val uri = Uri.parse("market://details?id=" + activity.packageName)
    val goToMarket = Intent(Intent.ACTION_VIEW, uri)
    try {
      activity.startActivity(goToMarket)
    } catch (e: ActivityNotFoundException) {
      activity.startActivity(
        Intent(
          Intent.ACTION_VIEW,
          Uri.parse("http://play.google.com/store/apps/details?id=" + activity.packageName)
        )
      )
    }
  }

  fun getScreenDensity(activity: Activity): Float {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
      activity.getSystemService(WindowManager::class.java).currentWindowMetrics.density
    } else {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val display = activity.display
        val displayMetrics = DisplayMetrics()
        display?.getRealMetrics(displayMetrics)
        displayMetrics.density
      } else {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        displayMetrics.density
      }
    }
  }
}
