package com.app.base.mvvm

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.app.base.mvvm.repository.AppSettingsRepositoryInterface
import com.app.base.mvvm.utils.LogUtil
import com.app.base.mvvm.view.item.GoogleMobileAdsConsentManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import java.lang.ref.WeakReference
import java.util.Date
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class App : Application(), DefaultLifecycleObserver {

  @Inject
  lateinit var appSettingsRepository: AppSettingsRepositoryInterface

  private lateinit var appOpenAdManager: AppOpenAdManager

  private var _currentActivity = WeakReference<Activity?>(null)
  val currentActivity: Activity?
    get() = _currentActivity.get()

  fun setCurrentActivity(activity: Activity?) {
    _currentActivity = WeakReference(activity)
  }

  companion object {
    lateinit var instance: App private set
  }

  override fun onStart(owner: LifecycleOwner) {
    super.onStart(owner)
    currentActivity?.let {
      appOpenAdManager.showAdIfAvailable(it)
    }
  }

  override fun onCreate() {
    super<Application>.onCreate()
    instance = this
    checkTheme()

    FirebaseApp.initializeApp(this)
    listerLifecycle()
    // setUpAd()
    appOpenAdManager = AppOpenAdManager()
  }

  private fun listerLifecycle() {
    registerActivityLifecycleCallbacks(
      object : ActivityLifecycleCallbacks {
        override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        }

        override fun onActivityStarted(activity: Activity) {
          if (!appOpenAdManager.isShowingAd) {
            setCurrentActivity(activity)
          }
        }

        override fun onActivityResumed(p0: Activity) {
        }

        override fun onActivityPaused(p0: Activity) {
        }

        override fun onActivityStopped(p0: Activity) {
        }

        override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}

        override fun onActivityDestroyed(p0: Activity) {
        }
      }
    )
  }

  private fun checkTheme() {
    when (appSettingsRepository.pullThemeMode()) {
      AppCompatDelegate.MODE_NIGHT_NO -> {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
      }

      AppCompatDelegate.MODE_NIGHT_YES -> {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
      }

      else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
  }

  private fun setUpAd() {
    val backgroundScope = CoroutineScope(Dispatchers.IO)
    backgroundScope.launch {
      MobileAds.initialize(this@App)
      val requestConfigurationBuilder = RequestConfiguration.Builder()
      val testDevices = resources.getStringArray(R.array.device_id).toMutableList()
      if (testDevices.isNotEmpty()) {
        requestConfigurationBuilder.setTestDeviceIds(testDevices).build()
      }
      val requestConfiguration = requestConfigurationBuilder.build()
      MobileAds.setRequestConfiguration(requestConfiguration)
    }
  }

  fun refreshToken() {
    FirebaseMessaging.getInstance().token
      .addOnCompleteListener(
        OnCompleteListener { task ->
          if (!task.isSuccessful) {
            LogUtil.logMessage("FirebaseMessaging", "Registration token failed ${task.exception}")
            return@OnCompleteListener
          }
          val token = task.result
          appSettingsRepository.pushFcmToken(token)
          LogUtil.logMessage("FirebaseMessaging", "FcmToken = $token")
        }
      )
  }

  fun showAdIfAvailable(activity: Activity, onShowAdCompleteListener: OnShowAdCompleteListener) {
    appOpenAdManager.showAdIfAvailable(activity, onShowAdCompleteListener)
  }

  fun loadAd(context: Context) {
    appOpenAdManager.loadAd(context)
  }

  interface OnShowAdCompleteListener {
    fun onShowAdComplete()
  }

  /** Inner class that loads and shows app open ads. */
  private inner class AppOpenAdManager {

    private var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager =
      GoogleMobileAdsConsentManager.getInstance(applicationContext)
    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    var isShowingAd = false

    /** Keep track of the time an app open ad is loaded to ensure you don't show an expired ad. */
    private var loadTime: Long = 0

    fun loadAd(context: Context) {
      if (isLoadingAd || isAdAvailable()) {
        return
      }

      isLoadingAd = true
      val request = AdRequest.Builder().build()
      AppOpenAd.load(
        context,
        getString(R.string.ads_open_app_id),
        request,
        object : AppOpenAdLoadCallback() {

          override fun onAdLoaded(ad: AppOpenAd) {
            appOpenAd = ad
            isLoadingAd = false
            loadTime = Date().time
            LogUtil.logMessage("AppOpenAdManager", "onAdLoaded.")
          }

          override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            isLoadingAd = false
            LogUtil.logMessage("AppOpenAdManager", "onAdFailedToLoad: " + loadAdError.message)
          }
        }
      )
    }

    /** Check if ad was loaded more than n hours ago. */
    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
      val dateDifference: Long = Date().time - loadTime
      val numMilliSecondsPerHour: Long = 3600000
      return dateDifference < numMilliSecondsPerHour * numHours
    }

    /** Check if ad exists and can be shown. */
    private fun isAdAvailable(): Boolean {
      // https://support.google.com/admob/answer/9341964?hl=en
      return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
    }

    fun showAdIfAvailable(activity: Activity) {
      showAdIfAvailable(
        activity,
        object : OnShowAdCompleteListener {
          override fun onShowAdComplete() {
          }
        }
      )
    }

    fun showAdIfAvailable(activity: Activity, onShowAdCompleteListener: OnShowAdCompleteListener) {
      // If the app open ad is already showing, do not show the ad again.
      if (isShowingAd) {
        LogUtil.logMessage("AppOpenAdManager", "The app open ad is already showing.")
        onShowAdCompleteListener.onShowAdComplete()
        return
      }

      // If the app open ad is not available yet, invoke the callback.
      if (!isAdAvailable()) {
        LogUtil.logMessage("AppOpenAdManager", "The app open ad is not ready yet.")
        onShowAdCompleteListener.onShowAdComplete()
        if (googleMobileAdsConsentManager.canRequestAds) {
          loadAd(activity)
        }
        return
      }

      LogUtil.logMessage("AppOpenAdManager", "Will show ad.")

      appOpenAd?.fullScreenContentCallback =
        object : FullScreenContentCallback() {
          /** Called when full screen content is dismissed. */
          override fun onAdDismissedFullScreenContent() {
            // Set the reference to null so isAdAvailable() returns false.
            appOpenAd = null
            isShowingAd = false
            LogUtil.logMessage("AppOpenAdManager", "onAdDismissedFullScreenContent.")

            onShowAdCompleteListener.onShowAdComplete()
            if (googleMobileAdsConsentManager.canRequestAds) {
              loadAd(activity)
            }
          }

          /** Called when fullscreen content failed to show. */
          override fun onAdFailedToShowFullScreenContent(adError: AdError) {
            appOpenAd = null
            isShowingAd = false
            LogUtil.logMessage("AppOpenAdManager", "onAdFailedToShowFullScreenContent: " + adError.message)

            onShowAdCompleteListener.onShowAdComplete()
            if (googleMobileAdsConsentManager.canRequestAds) {
              loadAd(activity)
            }
          }

          /** Called when fullscreen content is shown. */
          override fun onAdShowedFullScreenContent() {
            LogUtil.logMessage("AppOpenAdManager", "onAdShowedFullScreenContent.")
          }
        }
      isShowingAd = true
      appOpenAd?.show(activity)
    }
  }
}
