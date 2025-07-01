package com.app.base.mvvm.view.item

import android.app.Activity
import android.content.Context
import com.app.base.mvvm.R
import com.app.base.mvvm.repository.AppSettingsRepository
import com.app.base.mvvm.repository.AppSettingsRepositoryInterface
import com.app.base.mvvm.utils.LogUtil
import com.app.base.mvvm.utils.NetworkHelper
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class AdMobInterstitial {
  private var mInterstitialAd: InterstitialAd? = null
  private var onLoadAdInterstitialListener: OnLoadAdInterstitialListener? = null
  private var isShowing: Boolean = false
  private var isAllowOpen: Boolean = false
  private var interstitialAdLoadCallback: InterstitialAdLoadCallback? = null
  private var isLoadingAds = false
  private lateinit var appSettingsRepository: AppSettingsRepositoryInterface

  fun loadAdMobFullScreen(context: Context, showAd: Boolean) {
    appSettingsRepository = AppSettingsRepository(context)
    // if account is premium  ->loadFail()
    if (showAd) {
      isAllowOpen = true
    }
    if (mInterstitialAd != null && !isShowing) {
      LogUtil.logMessage("AdMob", "show ad fullscreen")
      showInterstitial(context)
      return
    }
    isAllowOpen = false

    if (isLoadingAds) {
      LogUtil.logMessage("AdMob", "Return because waiting loading ads....")
      loadFail()
      return
    }
    if (!NetworkHelper.isNetworkConnected(context)) {
      loadFail()
      return
    }
    setUpAdLoadCallBack(context)
    loadAds(context)
  }

  private fun setUpAdLoadCallBack(context: Context) {
    interstitialAdLoadCallback =
      object : InterstitialAdLoadCallback() {
        override fun onAdLoaded(interstitialAd: InterstitialAd) {
          isLoadingAds = false
          mInterstitialAd = interstitialAd
          LogUtil.logMessage("AdMob", "loading ad fullscreen success")
          showInterstitial(context)
          onLoadAdInterstitialListener?.loaded()
          isAllowOpen = false

          interstitialAd.fullScreenContentCallback =
            object : FullScreenContentCallback() {
              override fun onAdDismissedFullScreenContent() {
                mInterstitialAd = null
                onLoadAdInterstitialListener?.dismissAd()
                isShowing = false
                isAllowOpen = false
                loadAds(context)
                LogUtil.logMessage("AdMob", "The ad was dismissed.")
              }

              override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                mInterstitialAd = null
                isShowing = false
                loadFail()
                isAllowOpen = false
                LogUtil.logMessage("AdMob", "loading ad fullscreen fail")
              }

              override fun onAdShowedFullScreenContent() {
                LogUtil.logMessage("AdMob", "The ad was shown.")
                isShowing = true
                isAllowOpen = false
              }
            }
        }

        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
          isLoadingAds = false
          mInterstitialAd = null
          isShowing = false
          loadFail()
          isAllowOpen = false
          LogUtil.logMessage("AdMob", "loading ad fullscreen fail")
        }
      }
  }

  private fun loadAds(context: Context) {
    if (mInterstitialAd == null && NetworkHelper.isNetworkConnected(context) && !isLoadingAds) {
      val adRequest = AdRequest.Builder().build()
      isLoadingAds = true
      interstitialAdLoadCallback?.let {
        InterstitialAd.load(
          context,
          context.getString(R.string.ads_full_screen_id),
          adRequest,
          it
        )
      }
      LogUtil.logMessage("AdMob", "loading ad fullscreen")
    } else {
      loadFail()
    }
  }

  private fun showInterstitial(context: Context) {
    if (mInterstitialAd != null && !isShowing && isAllowOpen) {
      showDialogAds(context)
    } else {
      loadFail()
    }
  }

  private fun showDialogAds(context: Context) {
    LogUtil.logMessage("AdMob", "show ad fullscreen")
    if (context is Activity) {
      mInterstitialAd?.show(context)
    } else {
      isShowing = true
    }
    isAllowOpen = false
  }

  private fun loadFail() {
    onLoadAdInterstitialListener?.loadFail()
  }

  fun setOnLoadAdFullScreenListener(onLoadAdInterstitialListener: OnLoadAdInterstitialListener): AdMobInterstitial {
    this.onLoadAdInterstitialListener = onLoadAdInterstitialListener
    return this
  }

  interface OnLoadAdInterstitialListener {
    fun loaded()

    fun loadFail()

    fun dismissAd()
  }

  companion object {
    private var sInstance: AdMobInterstitial? = null

    val instance: AdMobInterstitial
      get() {
        if (sInstance == null) {
          sInstance = AdMobInterstitial()
        }
        return sInstance as AdMobInterstitial
      }
  }
}
