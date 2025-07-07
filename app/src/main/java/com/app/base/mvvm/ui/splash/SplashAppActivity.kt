package com.app.base.mvvm.ui.splash

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.ViewDataBinding
import com.app.base.mvvm.App
import com.app.base.mvvm.App.OnShowAdCompleteListener
import com.app.base.mvvm.R
import com.app.base.mvvm.base.BaseActivity
import com.app.base.mvvm.databinding.ActivitySplashBinding
import com.app.base.mvvm.repository.AppSettingsRepositoryInterface
import com.app.base.mvvm.ui.splash.fragment.SplashFragment
import com.app.base.mvvm.ui.splash.navigator.SplashNavigator
import com.app.base.mvvm.ui.splash.viewmodel.SplashViewModel
import com.app.base.mvvm.utils.ConstantUtil
import com.app.base.mvvm.utils.LogUtil
import com.app.base.mvvm.view.item.GoogleMobileAdsConsentManager
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.ump.ConsentInformation
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@AndroidEntryPoint
class SplashAppActivity : BaseActivity(R.layout.activity_splash) {
  private val viewModel: SplashViewModel by viewModels()
  private lateinit var viewBinding: ActivitySplashBinding

  @Inject
  lateinit var appSettingsRepository: AppSettingsRepositoryInterface

  @Inject
  lateinit var navigator: SplashNavigator
  private var waitSplashScreen = true

  override fun onStart() {
    super.onStart()
    checkTheme()
  }

  override fun applyBinding(viewDataBinding: ViewDataBinding) {
    viewBinding = viewDataBinding as ActivitySplashBinding
  }

  override fun onInit(arg: Bundle?, saveInstance: Bundle?) {
    if (saveInstance == null) {
      val bundle = Bundle()
      intent?.extras?.let {
        if (it.containsKey(ConstantUtil.ArgConstant.ARG_TYPE)) {
          bundle.putBoolean(ConstantUtil.ArgConstant.ARG_OPEN_FROM_PUSH, true)
        }
      }

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition {
          waitSplashScreen
        }
        checkSetUpAds(bundle)
        // openHome(bundle)
        return
      }

      supportFragmentManager.beginTransaction()
        .replace(R.id.container, SplashFragment.newInstance(bundle))
        .addToBackStack(null)
        .commit()
    }
  }

  private fun openHome(bundle: Bundle?) {
    Handler(Looper.getMainLooper()).postDelayed({
      waitSplashScreen = false
      openNextScreen(bundle)
    }, 2500)
  }

  private fun openNextScreen(bundle: Bundle?) {
    val application = getApplication()
    (application as App)
      .showAdIfAvailable(
        this@SplashAppActivity,
        object : OnShowAdCompleteListener {
          override fun onShowAdComplete() {
            navigator.openHome(bundle)
            finish()
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

  private val isMobileAdsInitializeCalled = AtomicBoolean(false)
  private var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager? = null

  fun checkSetUpAds(bundle: Bundle?) {
    // TODO: add form on admob
    googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(applicationContext)
    googleMobileAdsConsentManager?.gatherConsent(this) { consentError ->
      if (consentError != null) {
        LogUtil.logMessage(
          "AdOpenApp",
          "${consentError.errorCode}: ${consentError.message}"
        )
      }

      if (googleMobileAdsConsentManager?.canRequestAds == true) {
        initializeMobileAdsSdk()
      }

      val canRequest = googleMobileAdsConsentManager?.canRequestAds == true
      val isPersonalized =
        googleMobileAdsConsentManager?.consentInformation?.consentStatus ==
            ConsentInformation.ConsentStatus.OBTAINED

      appSettingsRepository.pushCanRequestAd(canRequest)
      appSettingsRepository.pushPersonalized(isPersonalized)

      openHome(bundle)
    }
  }

  private fun initializeMobileAdsSdk() {
    if (isMobileAdsInitializeCalled.getAndSet(true)) {
      return
    }

    MobileAds.initialize(this) {
      val builder = RequestConfiguration.Builder()
      val testDevices = resources.getStringArray(R.array.device_id).toMutableList()
      if (testDevices.isNotEmpty()) {
        builder.setTestDeviceIds(testDevices)
      }
      MobileAds.setRequestConfiguration(builder.build())

      (application as App).loadAd(this)
    }
  }
}
