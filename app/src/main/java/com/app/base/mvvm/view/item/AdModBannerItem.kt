package com.app.base.mvvm.view.item

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.app.base.mvvm.R
import com.app.base.mvvm.utils.LogUtil
import com.app.base.mvvm.utils.NetworkHelper
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdView

class AdModBannerItem
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
  LinearLayout(context, attrs, defStyleAttr) {
  private var onLoadListener: OnLoadListener? = null
  private var adView: AdManagerAdView? = null
  private var adSize: AdSize? = null
  private var isSetAdSize: Boolean = false

  init {
    initLayout(context)
  }

  private fun initLayout(context: Context) {
    adView = AdManagerAdView(context)
    adView?.adUnitId = context.getString(R.string.ads_banner_adaptive_id)
    addView(adView)
  }

  fun loadAds(context: Context) {
    if (!NetworkHelper.isNetworkConnected(context)) {
      onLoadListener?.loadFail()
      return
    }

    if (adView?.adSize !== adSize && !isSetAdSize) {
      adSize?.let { adView?.setAdSize(it) }
      isSetAdSize = true
    }

    val adRequest = AdRequest.Builder().build()
    LogUtil.logMessage("AdMob", "Load banner ads")
    adView?.apply {
      loadAd(adRequest)
      adListener =
        object : AdListener() {
          override fun onAdLoaded() {
            onLoadListener?.loaded()
            LogUtil.logMessage("AdMob", "Load banner ads success")
            super.onAdLoaded()
          }

          override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            super.onAdFailedToLoad(loadAdError)
            LogUtil.logMessage("AdMob", "Load banner ads fail: ${loadAdError.message}")
            onLoadListener?.loadFail()
          }
        }
    }
  }

  fun onPause() {
    adView?.pause()
  }

  fun onResume() {
    adView?.resume()
  }

  fun onDestroy() {
    adView?.destroy()
  }

  fun setAdSize(adSize: AdSize) {
    this.adSize = adSize
  }

  fun setAdListener(onLoadListener: OnLoadListener): AdModBannerItem {
    this.onLoadListener = onLoadListener
    return this
  }

  interface OnLoadListener {
    fun loaded()

    fun loadFail()
  }
}
