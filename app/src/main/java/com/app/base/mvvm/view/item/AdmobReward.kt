package com.app.base.mvvm.view.item

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.app.base.mvvm.R
import com.app.base.mvvm.repository.AppSettingsRepository
import com.app.base.mvvm.repository.AppSettingsRepositoryInterface
import com.app.base.mvvm.utils.LogUtil
import com.app.base.mvvm.utils.NetworkHelper
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class AdmobReward {

  private var mRewardAd: RewardedAd? = null
  private var onLoadRewardAdListener: OnLoadRewardAdListener? = null
  private var isShowing: Boolean = false
  private var isAllowOpen: Boolean = false
  private var rewardAdLoadCallback: RewardedAdLoadCallback? = null
  private var isLoadingAds = false
  private var appSettingsRepository: AppSettingsRepositoryInterface? = null

  fun loadRewardAd(context: Context, showAd: Boolean) {
    if (appSettingsRepository == null) {
      appSettingsRepository = AppSettingsRepository(context)
    }

    /*if (isRemovedAd or accountPremium) {
      loadFail()
      return
    }*/

    if (showAd) {
      isAllowOpen = true
    }

    if (mRewardAd != null && !isShowing) {
      LogUtil.logMessage("AdMob", "show reward ad")
      showRewardAd(context)
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
    rewardAdLoadCallback =
      object : RewardedAdLoadCallback() {
        override fun onAdLoaded(rewardedAd: RewardedAd) {
          isLoadingAds = false
          mRewardAd = rewardedAd
          LogUtil.logMessage("AdMob", "loading reward ad success")
          showRewardAd(context)
          onLoadRewardAdListener?.loaded()
          isAllowOpen = false

          rewardedAd.fullScreenContentCallback =
            object : FullScreenContentCallback() {
              override fun onAdDismissedFullScreenContent() {
                mRewardAd = null
                onLoadRewardAdListener?.dismissAd()
                isShowing = false
                isAllowOpen = false
                loadAds(context)
                LogUtil.logMessage("AdMob", "The reward ad was dismissed.")
              }

              override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                mRewardAd = null
                isShowing = false
                loadFail()
                isAllowOpen = false
                LogUtil.logMessage("AdMob", "loading reward ad fail")
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
          mRewardAd = null
          isShowing = false
          loadFail()
          isAllowOpen = false
          LogUtil.logMessage("AdMob", "loading reward ad failed")
        }
      }
  }

  private fun loadAds(context: Context) {
    if (mRewardAd == null && NetworkHelper.isNetworkConnected(context) && !isLoadingAds) {
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
      rewardAdLoadCallback?.let {
        RewardedAd.load(
          context,
          context.getString(R.string.ads_reward),
          adRequest,
          it,
        )
      }
      LogUtil.logMessage("AdMob", "loading reward ad")
    } else {
      loadFail()
    }
  }

  private fun showRewardAd(context: Context) {
    if (mRewardAd != null && !isShowing && isAllowOpen) {
      showDialogAds(context)
    } else {
      loadFail()
    }
  }

  private fun showDialogAds(context: Context) {
    LogUtil.logMessage("AdMob", "show reward ad")
    if (context is Activity) {
      mRewardAd?.show(
        context,
      ) { rewardItem ->
        // Handle the reward.
        val rewardAmount = rewardItem.amount
        val rewardType = rewardItem.type
        onLoadRewardAdListener?.onEarnedReward()
        LogUtil.logMessage("Admob", "User earned the reward.")
      }
    } else {
      isShowing = true
    }
    isAllowOpen = false
  }

  private fun loadFail() {
    onLoadRewardAdListener?.loadFail()
  }

  fun setOnLoadRewardAdListener(onLoadRewardAdListener: OnLoadRewardAdListener): AdmobReward {
    this.onLoadRewardAdListener = onLoadRewardAdListener
    return this
  }

  interface OnLoadRewardAdListener {
    fun loaded()

    fun loadFail()

    fun onEarnedReward()

    fun dismissAd()
  }

  companion object {
    private var sInstance: AdmobReward? = null

    val instance: AdmobReward
      get() {
        if (sInstance == null) {
          sInstance = AdmobReward()
        }
        return sInstance as AdmobReward
      }
  }
}
