package com.app.base.mvvm.ui.home.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewTreeObserver
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import com.app.base.mvvm.R
import com.app.base.mvvm.arch.extensions.beVisible
import com.app.base.mvvm.arch.extensions.setOnSingleClickListener
import com.app.base.mvvm.base.BaseFragment
import com.app.base.mvvm.databinding.FragmentHomeBinding
import com.app.base.mvvm.model.AdOpenScreenAction
import com.app.base.mvvm.ui.home.navigator.HomeNavigator
import com.app.base.mvvm.ui.home.viewmodel.HomeFragmentViewModel
import com.app.base.mvvm.utils.AppUtil
import com.app.base.mvvm.utils.LogUtil
import com.app.base.mvvm.utils.NetworkHelper
import com.google.android.gms.ads.AdSize
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment(R.layout.fragment_home) {

  @Inject
  lateinit var navigator: HomeNavigator
  private val viewModel by viewModels<HomeFragmentViewModel>()

  private lateinit var viewBinding: FragmentHomeBinding
  private var currentAdAction = AdOpenScreenAction.DEFAULT
  private var isEarnedReward = false
  private var bannerAdGlobalListener: ViewTreeObserver.OnGlobalLayoutListener? = null

  override fun applyBinding(viewDataBinding: ViewDataBinding) {
    viewBinding = viewDataBinding as FragmentHomeBinding
    viewBinding.viewModel = viewModel
  }

  override fun onInit(view: View, fragmentArg: Bundle?, saveInstance: Bundle?) {
    setupClick()
    initAdmobBanner()
    initAndLoadRewardAd() // or initAndLoadAdMobInterstitial
  }

  private fun setupClick() {
    viewBinding.btnTestData.setOnSingleClickListener {
      currentAdAction = AdOpenScreenAction.OPEN_TEST
      loadRewardAd(true)
    }
  }

  private fun loadAdMobBanner() {
//    if (isPremiumAccount()) {
//      return
//    }
    Handler(Looper.getMainLooper()).postDelayed({
      loadAdBanner(
        viewBinding.bannerView.adModBanner,
        object : OnLoadAdBannerItemListener() {}
      )
    }, 100)
  }

  private fun initAdmobBanner() {
    activity?.let {
//      if (isPremiumAccount()) {
//        viewBinding.layoutAd.beGone()
//        return
//      }
      viewBinding.layoutAd.beVisible()
      bannerAdGlobalListener = ViewTreeObserver.OnGlobalLayoutListener {
        val density = AppUtil.getScreenDensity(it)
        val width = viewBinding.layoutAd.width
        val height = viewBinding.layoutAd.height
        LogUtil.logMessage("Admob", "Size banner width =$width maxHeight=$height")
        if (width == 0 || height == 0) {
          return@OnGlobalLayoutListener
        }
        viewBinding.bannerView.adModBanner.setAdSize(
          AdSize.getInlineAdaptiveBannerAdSize((width / density).toInt(), (height / density).toInt())
        )
        bannerAdGlobalListener?.let { listener ->
          viewBinding.layoutAd.viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
        bannerAdGlobalListener = null
        loadAdMobBanner()
      }

      viewBinding.layoutAd.viewTreeObserver.addOnGlobalLayoutListener(bannerAdGlobalListener)
    }
  }

  private fun initAndLoadAdMobInterstitial() {
    setOnLoadAdInterstitialListener(
      object : OnLoadAdInterstitialListener() {
        override fun loaded() {
        }

        override fun loadFailed() {
          showNextScreen()
        }

        override fun dismissAd() {
          showNextScreen()
        }
      }
    )
    loadAdMobInterstitial(false)
  }

  private fun initAndLoadRewardAd() {
    setOnLoadRewardListener(
      object : OnLoadRewardAdListener() {
        override fun loaded() {
        }

        override fun loadFailed() {
          context?.let {
            if (NetworkHelper.isNetworkConnected(it)) {
              showNextScreen()
            }
          }
        }

        override fun onEarnedReward() {
          isEarnedReward = true
        }

        override fun dismissAd() {
          if (isEarnedReward) {
            isEarnedReward = false
            showNextScreen()
          }
        }
      }
    )
    loadRewardAd(false)
  }

  private fun showNextScreen() {
    when (currentAdAction) {
      AdOpenScreenAction.DEFAULT -> return
      AdOpenScreenAction.OPEN_TEST -> {
        navigator.openTestData()
      }
    }
    currentAdAction = AdOpenScreenAction.DEFAULT
  }
}
