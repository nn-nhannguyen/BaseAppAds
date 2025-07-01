package com.app.base.mvvm

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.app.base.mvvm.view.item.AdMobInterstitial
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
  companion object {
    lateinit var instance: App private set
  }

  override fun onCreate() {
    super.onCreate()
    instance = this
    listerLifecycle()
    setUpAd()
  }

  private fun setUpAd() {
    MobileAds.initialize(this)
    val requestConfigurationBuilder = RequestConfiguration.Builder()
    val testDevices = resources.getStringArray(R.array.device_id).toMutableList()
    if (testDevices.size > 0) {
      requestConfigurationBuilder.setTestDeviceIds(testDevices).build()
    }
    val requestConfiguration = requestConfigurationBuilder.build()
    MobileAds.setRequestConfiguration(requestConfiguration)
  }

  private fun initAndLoadAdMobFullscreen() {
    val adMobInterstitial = AdMobInterstitial.instance
    adMobInterstitial.loadAdMobFullScreen(this, false)
  }

  private fun listerLifecycle() {
    registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
      override fun onActivityCreated(p0: Activity, p1: Bundle?) {
      }

      override fun onActivityStarted(p0: Activity) {
      }

      override fun onActivityResumed(p0: Activity) {
      }

      override fun onActivityPaused(p0: Activity) {
      }

      override fun onActivityStopped(p0: Activity) {
      }

      override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
      }

      override fun onActivityDestroyed(p0: Activity) {
      }
    })
  }
}
