package com.app.base.mvvm.ui.home.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewTreeObserver
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import com.app.base.mvvm.R
import com.app.base.mvvm.arch.extensions.beInvisible
import com.app.base.mvvm.arch.extensions.beVisible
import com.app.base.mvvm.arch.extensions.setOnSingleClickListener
import com.app.base.mvvm.base.BaseActivity
import com.app.base.mvvm.base.BaseFragment
import com.app.base.mvvm.databinding.FragmentHomeBinding
import com.app.base.mvvm.model.AdOpenScreenAction
import com.app.base.mvvm.ui.home.navigator.HomeNavigator
import com.app.base.mvvm.ui.home.viewmodel.HomeFragmentViewModel
import com.app.base.mvvm.utils.AppUtil
import com.app.base.mvvm.utils.ConstantUtil
import com.app.base.mvvm.utils.LogUtil
import com.app.base.mvvm.view.dialog.RequestViewAdDialog
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
    initAndLoadRewardAd()
    initAndLoadAdMobInterstitial()
  }

  private fun setupClick() {
    viewBinding.btnTestData.setOnSingleClickListener {
      currentAdAction = AdOpenScreenAction.REWARD_CONTINUE
      showDialogViewAdReward()
    }
  }

  private fun showDialogViewAdReward() {
    val dialog = RequestViewAdDialog()
    dialog.setOnMusicListener(
      object : RequestViewAdDialog.RequestViewAdListener {
        override fun watchAd() {
          currentAdAction = AdOpenScreenAction.REWARD_CONTINUE
          if (activity is BaseActivity) {
            (activity as BaseActivity).loadRewardAd(true)
          }
        }
      },
    )
    dialog.show(childFragmentManager, RequestViewAdDialog::class.java.name)
  }

  private fun loadAdMobBanner() {
    if (!ConstantUtil.AdConstant.ALLOW_SHOW_ADMOB_BANNER) {
      return
    }

//    if (isPremiumAccount()) {
//      return
//    }

    Handler(Looper.getMainLooper()).postDelayed({
      viewBinding.bannerView.apply {
        tvLoading.beVisible()
        tvLoading.text = getString(R.string.loading_ad)
        tvAd.beVisible()
        loadAdBanner(
          adModBanner,
          object : OnLoadAdBannerItemListener() {

            override fun loaded() {
              tvLoading.beInvisible()
              tvAd.beInvisible()
            }

            override fun loadAdFailed() {
              tvLoading.beVisible()
              tvLoading.text = getString(R.string.failed_to_load_ad)
              tvAd.beInvisible()
            }
          },
          isAdaptive = true
        )
      }
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
          if (currentAdAction == AdOpenScreenAction.REWARD_CONTINUE) {
            currentAdAction = AdOpenScreenAction.DEFAULT
            (activity as BaseActivity).showMessageDialog(getString(R.string.ad_not_available), {
              // or continue without ad
            }, true)
          } else {
            showNextScreen()
          }
        }

        override fun dismissAd() {
          showNextScreen()
        }
      }
    )
    loadAdMobInterstitial(false)
  }

  private fun initAndLoadRewardAd() {
    setOnLoadRewardAdListener(
      object : OnLoadRewardAdListener() {
        override fun loaded() {
        }

        override fun loadFailed() {
          context?.let {

            if (currentAdAction != AdOpenScreenAction.DEFAULT) {
//              currentAdAction = AdOpenScreenAction.DEFAULT
//              (activity as BaseActivity).showMessageDialog(getString(R.string.ad_not_available), {}, true)
              (activity as BaseActivity).loadAdMobInterstitial(true, true)
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
      AdOpenScreenAction.REWARD_CONTINUE -> {
        navigator.openTestData()
      }
    }
    currentAdAction = AdOpenScreenAction.DEFAULT
  }
}
