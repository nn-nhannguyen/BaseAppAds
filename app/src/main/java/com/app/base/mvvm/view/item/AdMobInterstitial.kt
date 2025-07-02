package com.app.base.mvvm.view.item

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.app.base.mvvm.R
import com.app.base.mvvm.repository.AppSettingsRepository
import com.app.base.mvvm.repository.AppSettingsRepositoryInterface
import com.app.base.mvvm.utils.ConstantUtil
import com.app.base.mvvm.utils.LogUtil
import com.app.base.mvvm.utils.NetworkHelper
import com.app.base.mvvm.view.dialog.LoadingAdsDialog
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class AdMobInterstitial {

  private var mInterstitialAd: InterstitialAd? = null
  private var onLoadAdInterstitialListener: OnLoadAdInterstitialListener? = null
  private var isShowing = false
  private var isAllowOpen = false
  private var interstitialAdLoadCallback: InterstitialAdLoadCallback? = null
  private var isLoadingAds = false
  private var appSettingsRepository: AppSettingsRepositoryInterface? = null
  private var mLoadingAdsDialog: LoadingAdsDialog? = null
  private var mTimeLoad = 0L
  var forceLoad = false

  fun loadAdMobFullScreen(context: Context, showAd: Boolean, isForceLoad: Boolean? = false) {
    forceLoad = isForceLoad == true

    if (appSettingsRepository == null) {
      appSettingsRepository = AppSettingsRepository(context)
    }

//    if (isRemovedAd or accountPremium) {
//      loadFail()
//      return
//    }

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

    if (!NetworkHelper.isNetworkConnected(context) || appSettingsRepository?.pullCanRequestAd() == false) {
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

      val isPersonalized = appSettingsRepository?.pullPersonalized()

      val adRequest = if (isPersonalized == true) {
        AdRequest.Builder().build()
      } else {
        val extras = Bundle().apply {
          putString("npa", "1")
        }
        AdRequest.Builder()
          .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
          .build()
      }

      isLoadingAds = true
      interstitialAdLoadCallback?.let {
        InterstitialAd.load(
          context,
          context.getString(R.string.ads_full_screen_id),
          adRequest,
          it,
        )
      }
      LogUtil.logMessage("AdMob", "loading ad fullscreen")
    } else {
      loadFail()
    }
  }

  private fun showInterstitial(context: Context) {
    val currentTime = System.currentTimeMillis()
    val deltaTime: Long = currentTime - mTimeLoad

    if (mInterstitialAd != null && !isShowing && isAllowOpen &&
      (deltaTime > ConstantUtil.AdConstant.TIME_SHOW_ADMOB_FULLSCREEN || forceLoad)
    ) {
      forceLoad = false
      mTimeLoad = currentTime
      showDialogAds(context)
    } else {
      loadFail()
    }
  }

  private fun showDialogAds(context: Context) {
    mLoadingAdsDialog = LoadingAdsDialog(
      context,
      object : LoadingAdsDialog.OnDialogPlayListener {
        override fun onDismiss() {
        }
      }).apply {
      show()
    }

    Handler(Looper.getMainLooper()).postDelayed({
      mLoadingAdsDialog?.dismiss()
      LogUtil.logMessage("AdMob", "show ad fullscreen")

      if (context is Activity) {
        mInterstitialAd?.show(context)
      } else {
        isShowing = true
      }

      isAllowOpen = false
    }, 1000)
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
