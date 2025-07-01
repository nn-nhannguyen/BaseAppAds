package com.app.base.mvvm.arch.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import java.io.File

fun Context.fileAtDownloads(filename: String): File = File(
  getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
  filename
)

fun Context.getAppVersion(): String {
  var packageInfo: PackageInfo? = null
  try {
    packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA)
  } catch (e: PackageManager.NameNotFoundException) {
    e.printStackTrace()
  }
  return packageInfo?.versionName ?: ""
}

@SuppressLint("QueryPermissionsNeeded")
fun Context.doDial(number: String): Boolean {
  val intent =
    Intent(Intent.ACTION_DIAL).apply {
      data = Uri.parse("tel:$number")
    }

  packageManager?.let {
    if (intent.resolveActivity(it) != null) {
      startActivity(intent)
      return true
    }
  }

  return false
}

fun Context.showToast(message: String) {
  Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
