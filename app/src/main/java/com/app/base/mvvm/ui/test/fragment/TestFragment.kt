package com.app.base.mvvm.ui.test.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import com.app.base.mvvm.R
import com.app.base.mvvm.arch.extensions.beGone
import com.app.base.mvvm.arch.extensions.beInvisible
import com.app.base.mvvm.arch.extensions.beVisible
import com.app.base.mvvm.arch.extensions.setOnSingleClickListener
import com.app.base.mvvm.base.BaseFragment
import com.app.base.mvvm.databinding.FragmentTestBinding
import com.app.base.mvvm.model.AdOpenScreenAction
import com.app.base.mvvm.ui.test.navigator.TestNavigator
import com.app.base.mvvm.ui.test.viewmodel.TestFragmentViewModel
import com.app.base.mvvm.utils.ConstantUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TestFragment : BaseFragment(R.layout.fragment_test) {

  @Inject
  lateinit var navigator: TestNavigator
  private val viewModel by viewModels<TestFragmentViewModel>()

  private lateinit var viewBinding: FragmentTestBinding

  private var currentAdAction = AdOpenScreenAction.DEFAULT

  override fun applyBinding(viewDataBinding: ViewDataBinding) {
    viewBinding = viewDataBinding as FragmentTestBinding
    viewBinding.viewModel = viewModel
  }

  override fun onInit(view: View, fragmentArg: Bundle?, saveInstance: Bundle?) {
    initAdmobBanner()
    initAndLoadAdMobInterstitial()
    setupClick()
  }

  private fun initAdmobBanner() {
    activity?.let {
      if (!ConstantUtil.AdConstant.ALLOW_SHOW_ADMOB_BANNER) {
        viewBinding.layoutAd.beGone()
        return
      }
      viewBinding.layoutAd.beVisible()
      loadAdMobBanner()
    }
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
        )
      }
    }, 100)
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
      },
    )
    loadAdMobInterstitial(false)
  }

  private fun showNextScreen() {
    when (currentAdAction) {
      AdOpenScreenAction.DEFAULT -> return
      AdOpenScreenAction.OPEN_TEST_DATA_ROOM -> navigator.openTestRoom()
      AdOpenScreenAction.OPEN_TEST_API -> navigator.openTestApi()
    }
    currentAdAction = AdOpenScreenAction.DEFAULT
  }

  private fun setupClick() {
    viewBinding.btnTestGetApi.setOnSingleClickListener {
      currentAdAction = AdOpenScreenAction.OPEN_TEST_API
      loadAdMobInterstitial(true)
    }

    viewBinding.btnTestDataRoom.setOnSingleClickListener {
      currentAdAction = AdOpenScreenAction.OPEN_TEST_DATA_ROOM
      loadAdMobInterstitial(true)
    }
  }
}
