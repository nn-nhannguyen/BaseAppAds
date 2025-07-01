package com.app.base.mvvm.utils

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.app.base.mvvm.R
import com.app.base.mvvm.view.dialog.AlertDialog

class PermissionManager constructor(private val context: Context) {

  fun checkPermissionGallery(): Boolean {
    return !checkAndroid6() || ActivityCompat.checkSelfPermission(
      context,
      Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
  }

  fun requestPermissionGallery(activity: Activity): Boolean {
    return if (ActivityCompat.shouldShowRequestPermissionRationale(
        activity,
        Manifest.permission.READ_EXTERNAL_STORAGE
      )
    ) {
      openAppSetting(activity)
      showDialogRequestPermission(
        activity.getString(R.string.please_turn_on_permission_storage),
        activity
      )
      false
    } else {
      ActivityCompat.requestPermissions(
        activity,
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
        PERMISSION_REQUEST_CODE_GALLERY
      )
      true
    }
  }

  fun checkPermissionCamera(): Boolean {
    return !checkAndroid6() || ActivityCompat.checkSelfPermission(
      context,
      Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
  }

  fun requestPermissionCamera(activity: Activity): Boolean {
    return if (ActivityCompat.shouldShowRequestPermissionRationale(
        activity,
        Manifest.permission.CAMERA
      )
    ) {
      Toast.makeText(
        context,
        context.getString(R.string.please_turn_on_permission_camera),
        Toast.LENGTH_SHORT
      ).show()
      openAppSetting(activity)
      showDialogRequestPermission(
        context.getString(R.string.please_turn_on_permission_camera),
        activity
      )
      false
    } else {
      ActivityCompat.requestPermissions(
        activity,
        arrayOf(Manifest.permission.CAMERA),
        PERMISSION_REQUEST_CODE_CAMERA
      )
      true
    }
  }

  fun checkPermissionNotification(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS
      ) == PackageManager.PERMISSION_GRANTED
    } else {
      return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }
  }

  fun requestPermissionNotification(activity: Activity): Boolean {
    return if (ActivityCompat.shouldShowRequestPermissionRationale(
        activity,
        Manifest.permission.POST_NOTIFICATIONS
      )
    ) {
      showDialogRequestPermissionNotification(activity)
      false
    } else {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ActivityCompat.requestPermissions(
          activity,
          arrayOf(Manifest.permission.POST_NOTIFICATIONS),
          PERMISSION_REQUEST_NOTIFICATION
        )
      } else {
        showDialogRequestPermissionNotification(activity)
      }
      true
    }
  }

  private fun openNotificationSetting(activity: Activity) {
    val intent = Intent()
    intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
    intent.putExtra("app_package", context.packageName)
    intent.putExtra("app_uid", context.applicationInfo.uid)
    intent.putExtra("android.provider.extra.APP_PACKAGE", context.packageName)
    activity.startActivityForResult(intent, PERMISSION_REQUEST_NOTIFICATION)
  }

  fun checkPermissionWrite(): Boolean {
    return !checkAndroid6() || ActivityCompat.checkSelfPermission(
      context,
      Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
  }

  fun requestPermissionWrite(activity: Activity): Boolean {
    return if (ActivityCompat.shouldShowRequestPermissionRationale(
        activity,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
      )
    ) {
      Toast.makeText(
        context,
        context.getString(R.string.please_turn_on_permission_storage),
        Toast.LENGTH_SHORT
      ).show()
      openAppSetting(activity)
      showDialogRequestPermission(
        context.getString(R.string.please_turn_on_permission_storage),
        activity
      )
      false
    } else {
      ActivityCompat.requestPermissions(
        activity,
        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
        PERMISSION_REQUEST_CODE_WRITE
      )
      true
    }
  }

  private fun checkAndroid6(): Boolean {
    return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M
  }

  private fun openAppSetting(activity: Activity) {
    Toast.makeText(
      activity,
      activity.getString(R.string.message_how_to_turn_on_permission),
      Toast.LENGTH_SHORT
    )
      .show()
    try {
      val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
      intent.data = Uri.parse("package:" + context.packageName)
      activity.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
      val intent = Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
      activity.startActivity(intent)
    }
  }

  private fun showDialogRequestPermissionNotification(activity: Activity) {
    AlertDialog.Builder(activity)
      .setCanceledOnTouchOutside(false)
      .setMessage(context.getString(R.string.please_turn_on_permission_notification))
      .setNegativeButtonText(
        context.getString(R.string.text_ok),
        object : AlertDialog.OnClickListener {
          override fun onClick(v: View) {
            openNotificationSetting(activity)
          }
        }
      )
      .setPositiveButtonText(context.getString(R.string.text_cancel), null)
      .create().show()
  }

  private fun showDialogRequestPermission(message: String, activity: Activity) {
    AlertDialog.Builder(context)
      .setCanceledOnTouchOutside(false)
      .setMessage(message)
      .setNegativeButtonText(
        context.getString(R.string.text_ok),
        object : AlertDialog.OnClickListener {
          override fun onClick(v: View) {
            openAppSetting(activity)
          }
        }
      )
      .setPositiveButtonText(context.getString(R.string.text_cancel), null)
      .create().show()
  }

  companion object {
    const val PERMISSION_REQUEST_CODE_GALLERY = 1
    const val PERMISSION_REQUEST_CODE_CAMERA = 2
    const val PERMISSION_REQUEST_CODE_WRITE = 3
    const val PERMISSION_REQUEST_NOTIFICATION = 4
  }
}
