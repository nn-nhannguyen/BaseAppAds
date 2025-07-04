package com.app.base.mvvm.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.app.base.mvvm.data.source.LoadingState
import com.app.base.mvvm.view.dialog.AlertDialog
import com.app.base.mvvm.view.dialog.BaseDialogFragment
import com.app.base.mvvm.view.dialog.SnackBuilder
import com.app.base.mvvm.view.item.AdMobInterstitial
import com.app.base.mvvm.view.item.AdMobBannerItem
import com.app.base.mvvm.view.item.AdmobReward
import com.google.android.gms.ads.admanager.AdManagerAdView

abstract class BaseFragment(@LayoutRes val layoutId: Int) : Fragment(layoutId) {
  abstract fun onInit(view: View, fragmentArg: Bundle?, saveInstance: Bundle?)

  abstract fun applyBinding(viewDataBinding: ViewDataBinding)

  private var mActivity: BaseActivity? = null
  private lateinit var mDataBinding: ViewDataBinding
  private var adView: AdManagerAdView? = null
  private var onLoadAdInterstitialListener: OnLoadAdInterstitialListener? = null
  private var onLoadRewardAdListener: OnLoadRewardAdListener? = null

  override fun onAttach(context: Context) {
    super.onAttach(context)
    if (context is BaseActivity) {
      mActivity = context
    }
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    mDataBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
    applyBinding(mDataBinding)
    return mDataBinding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    mDataBinding.lifecycleOwner = viewLifecycleOwner
    onInit(view, arguments, savedInstanceState)
  }

  fun loadAdBanner(
    adMobBannerItem: AdMobBannerItem,
    onLoadAdBannerListener: OnLoadAdBannerItemListener? = null,
    isAdaptive: Boolean? = false
  ) {
    adMobBannerItem.apply {
      setAdListener(
        object : AdMobBannerItem.OnLoadListener {
          override fun loaded() {
            onLoadAdBannerListener?.loaded()
          }

          override fun loadFail() {
            onLoadAdBannerListener?.loadAdFailed()
          }
        }
      )
      loadAds(requireActivity(), isAdaptive)
      updateAdView(adView)
    }
  }

  fun setOnLoadAdInterstitialListener(listener: OnLoadAdInterstitialListener? = null) {
    onLoadAdInterstitialListener = listener
  }

  fun setOnLoadRewardAdListener(listener: OnLoadRewardAdListener? = null) {
    onLoadRewardAdListener = listener
  }

  fun loadAdMobInterstitial(show: Boolean, forceLoad: Boolean? = false) {
    val adMobInterstitial = AdMobInterstitial.instance
    onLoadAdInterstitialListener?.let {
      adMobInterstitial.setOnLoadAdFullScreenListener(
        object : AdMobInterstitial.OnLoadAdInterstitialListener {
          override fun loaded() {
            it.loaded()
          }

          override fun loadFail() {
            it.loadFailed()
          }

          override fun dismissAd() {
            it.dismissAd()
          }
        }
      )
    }
    adMobInterstitial.loadAdMobFullScreen(requireActivity(), show, forceLoad)
  }

  fun loadRewardAd(show: Boolean) {
    val adMobReward = AdmobReward.instance
    onLoadRewardAdListener?.let {
      adMobReward.setOnLoadRewardAdListener(
        object : AdmobReward.OnLoadRewardAdListener {
          override fun loaded() {
            it.loaded()
          }

          override fun loadFail() {
            it.loadFailed()
          }

          override fun onEarnedReward() {
            it.onEarnedReward()
          }

          override fun dismissAd() {
            it.dismissAd()
          }
        }
      )
    }
    activity?.let {
      adMobReward.loadRewardAd(it, show)
    }
  }

  fun listenerLoading(viewModel: BaseViewModel) {
    viewModel.flagLoadingState.observe(this) { state ->
      state?.let {
        if (it == LoadingState.LOADING) {
          showLoadingDialog()
        } else {
          hideLoadingDialog()
        }
      }
    }
  }

  fun showLoadingDialog() {
    if (isVisible) {
      activity?.apply {
        if (!isFinishing) {
          showLoading()
        }
      }
    }
  }

  fun hideLoadingDialog() {
    mActivity?.dismissLoading()
  }

  fun allowAutoDismissLoading(allow: Boolean = true) {
    mActivity?.updateFlagAllowDismissLoading(allow)
  }

  fun showMessageDialog(message: String, onPositiveButtonListener: () -> Unit) {
    mActivity?.showMessageDialog(message, onPositiveButtonListener)
  }

  fun showFullDialog(
    title: Int,
    message: Int,
    textNegative: Int,
    textPositive: Int,
    onPositiveButtonListener: () -> Unit
  ) {
    mActivity?.showFullDialog(title, message, textNegative, textPositive, onPositiveButtonListener)
  }

  fun showDialogCallback(
    title: Int,
    message: Int,
    textNegative: Int,
    textPositive: Int,
    onNegativeButtonListener: () -> Unit,
    onPositiveButtonListener: () -> Unit
  ) {
    mActivity?.showDialogCallback(
      title,
      message,
      textNegative,
      textPositive,
      onNegativeButtonListener,
      onPositiveButtonListener
    )
  }

  fun showMessageDialog(builder: AlertDialog.Builder) {
    mActivity?.showMessageDialog(builder)
  }

  fun showMessageDialog(
    title: String,
    message: String,
    textNegative: Int,
    textPositive: Int,
    onNegativeButtonListener: () -> Unit,
    onPositiveButtonListener: () -> Unit
  ) {
    mActivity?.showMessageDialog(
      title,
      message,
      textNegative,
      textPositive,
      onNegativeButtonListener,
      onPositiveButtonListener
    )
  }

  fun showSnackMessage(message: String) {
    mActivity?.showSnackMessage(message)
  }

  fun showSnackMessage(builder: SnackBuilder) {
    mActivity?.showSnackMessage(builder)
  }

  fun showLoading() {
    mActivity?.showLoading()
  }

  fun dismissLoading() {
    mActivity?.dismissLoading()
  }

  fun hideKeyboard() {
    mActivity?.dismissKeyboard()
  }

  fun showDialogFragment(fragment: BaseDialogFragment, tag: String) {
    mActivity?.showDialogFragment(fragment, tag)
  }

  fun gotoPermissionSettings() {
    mActivity?.gotoPermissionSettings()
  }

  fun showErrorNetWork(callBack: (() -> Unit)? = null) {
    mActivity?.showErrorNetWork(callBack)
  }

  fun finishedActivity() {
    mActivity?.finishedActivity()
  }

  fun showSnackMessage(msgRes: Int) {
    mActivity?.showSnackMessage(msgRes)
  }

  fun restartApp() {
    mActivity?.restartApp()
  }

  fun showMessageDialog(msgRes: Int) {
    mActivity?.showSnackMessage(msgRes)
  }

  fun destroyAdView() {
    adView?.destroy()
  }

  override fun onResume() {
    super.onResume()
    adView?.resume()
  }

  override fun onPause() {
    super.onPause()
    adView?.resume()
  }

  private fun updateAdView(adView: AdManagerAdView?) {
    this.adView = adView
  }

  override fun onDestroy() {
    super.onDestroy()
    adView?.destroy()
  }

  interface LoadAdBannerItemListener {
    fun loadAdFailed()

    fun loaded()
  }

  abstract class OnLoadAdBannerItemListener : LoadAdBannerItemListener {
    override fun loadAdFailed() {}

    override fun loaded() {}
  }

  interface LoadAdFullScreenListener {
    fun loadFailed()

    fun loaded()

    fun dismissAd()
  }

  interface LoadRewardAdListener {
    fun loadFailed()

    fun loaded()

    fun onEarnedReward()

    fun dismissAd()
  }

  abstract class OnLoadAdInterstitialListener : LoadAdFullScreenListener {
    override fun loadFailed() {}

    override fun loaded() {}

    override fun dismissAd() {}
  }

  abstract class OnLoadRewardAdListener : LoadRewardAdListener {
    override fun loadFailed() {}

    override fun loaded() {}

    override fun onEarnedReward() {}

    override fun dismissAd() {}
  }
}
