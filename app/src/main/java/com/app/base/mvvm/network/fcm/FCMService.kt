package com.app.base.mvvm.network.fcm

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import com.app.base.mvvm.BuildConfig
import com.app.base.mvvm.R
import com.app.base.mvvm.repository.AppSettingsRepositoryInterface
import com.app.base.mvvm.ui.home.HomeActivity
import com.app.base.mvvm.utils.ConstantUtil
import com.app.base.mvvm.utils.LogUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FCMService : FirebaseMessagingService() {

  @Inject
  lateinit var appSettingsRepository: AppSettingsRepositoryInterface

  override fun onNewToken(s: String) {
    super.onNewToken(s)
    appSettingsRepository.pushFcmToken(s)
  }

  override fun onMessageReceived(remoteMessage: RemoteMessage) {
    super.onMessageReceived(remoteMessage)
    LogUtil.logMessage(
      "onMessageReceived",
      "RemoteMessage= ${remoteMessage.notification}" +
        "\nRemoteMessageData= ${remoteMessage.data}"
    )

    remoteMessage.notification?.let {
      it.imageUrl?.apply {
        loadImageUrl(this, remoteMessage)
      } ?: run {
        sendNotification(remoteMessage)
      }
    }
  }

  private fun loadImageUrl(imageUrl: Uri, remoteMessage: RemoteMessage) {
    Glide.with(applicationContext)
      .asBitmap()
      .load(imageUrl)
      .listener(
        object : RequestListener<Bitmap> {
          override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Bitmap>?,
            isFirstResource: Boolean
          ): Boolean {
            sendNotification(remoteMessage)
            return false
          }

          override fun onResourceReady(
            resource: Bitmap?,
            model: Any?,
            target: Target<Bitmap>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
          ): Boolean {
            sendNotification(remoteMessage, resource)
            return false
          }
        }
      )
      .preload()
  }

  private fun sendNotification(remoteMessage: RemoteMessage, bitmap: Bitmap? = null) {
    val channelId = BuildConfig.APPLICATION_ID
    val title = remoteMessage.notification?.title
    val body = remoteMessage.notification?.body

    val intent = Intent(this, HomeActivity::class.java).apply {
      putExtras(getData(remoteMessage.data))
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    val pendingIntent = PendingIntent.getActivity(
      this,
      0,
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or
        PendingIntent.FLAG_IMMUTABLE or
        PendingIntent.FLAG_ONE_SHOT
    )

    val avatarBitmap = bitmap ?: BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)

    val notificationBuilder = NotificationCompat.Builder(this, channelId)
      .setContentTitle(title)
      .setContentText(body)
      .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
      .setLargeIcon(avatarBitmap)
      .setStyle(NotificationCompat.BigTextStyle().bigText(body))
      .setAutoCancel(true)
      .setLights(Color.RED, 1000, 2000)
      .setDefaults(NotificationCompat.DEFAULT_SOUND)
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setContentIntent(pendingIntent)
      .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

    val notificationManager = NotificationManagerCompat.from(this)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      createNotificationChannel(notificationManager, channelId)
    }

    if (ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.POST_NOTIFICATIONS
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      return
    }

    notificationManager.notify(0, notificationBuilder.build())
  }

  private fun createNotificationChannel(manager: NotificationManagerCompat, channelId: String) {
    val channel = NotificationChannel(
      channelId,
      channelId,
      NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
      enableVibration(true)
      enableLights(true)
      lightColor = Color.RED
      setShowBadge(true)
    }

    val systemManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    systemManager.createNotificationChannel(channel)
  }

  private fun getData(data: Map<String, String>): Bundle {
    /*if (data.containsKey(ConstantUtil.ARG_TYPE)) {
      //add follow get data
    }*/
    return bundleOf(
      ConstantUtil.ArgConstant.ARG_OPEN_FROM_PUSH to true
    )
  }
}
