package com.app.base.mvvm.base

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.app.base.mvvm.R
import com.app.base.mvvm.arch.extensions.hideKeyboard
import com.app.base.mvvm.data.source.LoadingState
import com.app.base.mvvm.view.dialog.AlertDialog
import com.app.base.mvvm.view.dialog.BaseDialogFragment
import com.app.base.mvvm.view.dialog.LoadingDialog
import com.app.base.mvvm.view.dialog.SnackBuilder
import com.app.base.mvvm.view.item.AdMobInterstitial
import com.app.base.mvvm.view.item.AdModBannerItem
import com.app.base.mvvm.view.item.AdmobReward
import com.google.android.gms.ads.AdView
import com.google.android.material.snackbar.Snackbar
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrInterface
import kotlin.math.abs

abstract class BaseActivity(@LayoutRes val layoutId: Int) : AppCompatActivity() {

  private var mFirstX: Float = 0f
  private var mFirstY: Float = 0f
  private var mTag: String = ""
  private var mLoadingDialog: LoadingDialog? = null
  private var mIsDestroy = false
  private var mSwipeInterface: SlidrInterface? = null
  private lateinit var mDataBinding: ViewDataBinding
  private var alertDialog: AlertDialog? = null
  private var onLoadAdInterstitialListener: OnLoadAdInterstitialListener? = null
  private var onLoadRewardAdListener: OnLoadRewardAdListener? = null
  private var adView: AdView? = null

  private val anchorSnackBar: View
    get() = findViewById(android.R.id.content)

  abstract fun applyBinding(viewDataBinding: ViewDataBinding)

  abstract fun onInit(arg: Bundle?, saveInstance: Bundle?)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    mDataBinding = DataBindingUtil.setContentView(this, layoutId)
    mDataBinding.lifecycleOwner = this

//        val view = findViewById<View>(android.R.id.content).rootView
//        view?.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBackgroundWhite))

    mLoadingDialog = LoadingDialog(this)
    mSwipeInterface = Slidr.attach(this)

    setEnableSlidr(false)

    applyBinding(mDataBinding)
    onInit(intent?.extras, savedInstanceState)
  }

  fun listenerLoading(viewModel: BaseViewModel) {
    viewModel.flagLoadingState.observe(this) { state ->
      state?.let {
        if (it == LoadingState.LOADING) {
          showLoading()
        } else {
          dismissLoading()
        }
      }
    }
  }

  fun setEnableSlidr(enableSlidr: Boolean) {
    mSwipeInterface?.apply {
      if (enableSlidr) {
        unlock()
      } else {
        lock()
      }
    }
  }

  fun showMessageDialog(message: String) {
    AlertDialog.Builder(this)
      .setMessage(message)
      .create().show()
  }

  fun showMessageDialog(message: String, onPositiveButtonListener: () -> Unit, allowCancel: Boolean? = false) {
    if (alertDialog != null && alertDialog?.isShowing == true) {
      alertDialog?.dismiss()
    }
    alertDialog =
      AlertDialog.Builder(this)
        .setMessage(message)
        .setCancelable(allowCancel ?: false)
        .setCanceledOnTouchOutside(allowCancel ?: false)
        .setPositiveButtonListener(
          object : AlertDialog.OnClickListener {
            override fun onClick(v: View) {
              onPositiveButtonListener.invoke()
            }
          }
        )
        .create()
    alertDialog?.show()
  }

  fun showMessageDialog(builder: AlertDialog.Builder) {
    builder.create().show()
  }

  fun showSnackMessage(message: String) {
    Snackbar.make(anchorSnackBar, message, Snackbar.LENGTH_SHORT).show()
  }

  fun showSnackMessage(msgRes: Int) {
    Snackbar.make(anchorSnackBar, getText(msgRes), Snackbar.LENGTH_SHORT).show()
  }

  fun showSnackMessage(builder: SnackBuilder) {
    val bar = Snackbar.make(anchorSnackBar, builder.message ?: "", builder.time)
    bar.setAction(builder.primaryButtonTitle) { view ->
      bar.dismiss()
      builder.primaryButtonListener?.onClick(view)
    }
    bar.show()
  }

  fun showErrorNetWork() {
    if (isFinishing) {
      return
    }
    AlertDialog.Builder(this)
      .setTitle(getString(R.string.title))
      .setCanceledOnTouchOutside(false)
      .setMessage(getString(R.string.error_network))
      .setPositiveButtonText(getString(R.string.ok), null)
      .create().show()
  }

  fun showLoading() {
    if (!mIsDestroy && !isFinishing && mLoadingDialog?.isShowing != true) {
      mLoadingDialog?.show()
    }
  }

  fun dismissLoading() {
    if (!isFinishing && mLoadingDialog?.isShowing != false) {
      mLoadingDialog?.dismiss()
    }
  }

  fun showDialogFragment(fragment: BaseDialogFragment, tag: String) {
    fragment.show(supportFragmentManager, tag)
  }

  fun gotoPermissionSettings() {
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    val uri = Uri.fromParts("package", packageName, null)
    intent.data = uri
    startActivity(intent)
  }

  fun finishedActivity() {
    Log.d(mTag, "finishedActivity: ")
    finish()
  }

  fun restartApp() {
    Log.d(mTag, "restartApp: ")
    baseContext.packageManager.getLaunchIntentForPackage(baseContext.packageName)?.apply {
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
      startActivity(this)
    }
  }

  /**
   * This method is used to hide keyboard when click outside EditText
   */
  override fun dispatchTouchEvent(event: MotionEvent): Boolean {
    val view = currentFocus
    if (view is EditText) {
      when (event.actionMasked) {
        MotionEvent.ACTION_DOWN -> {
          mFirstX = event.x
          mFirstY = event.y
        }

        MotionEvent.ACTION_UP -> {
          val x = event.x
          val y = event.y
          val touchPoint = abs(x - mFirstX) + abs(y - mFirstY)
          if (touchPoint < TOUCH_POINT) {
            // Case click: hide the keyboard
            checkToHideKeyboard(view, event)
          }
        }
      }
    }
    try {
      return super.dispatchTouchEvent(event)
    } catch (exception: IllegalArgumentException) {
      Log.d(mTag, "dispatch key event exception")
    }

    return false
  }

  fun checkToHideKeyboard(view: View, event: MotionEvent) {
    val scrCoordinates = IntArray(2)
    view.getLocationOnScreen(scrCoordinates)
    val x = event.rawX + view.left - scrCoordinates[0]
    val y = event.rawY + view.top - scrCoordinates[1]
    if (x < view.left || x >= view.right || y < view.top || y > view.bottom) {
      window?.apply {
        val inputMethodManager =
          getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(decorView.windowToken, 0)
        return
      }
      view.clearFocus()
    }
  }

  fun dismissKeyboard() {
    hideKeyboard()
  }

  fun getDataBinding(): ViewDataBinding {
    return mDataBinding
  }

  override fun onResume() {
    super.onResume()
    adView?.resume()
  }

  override fun onPause() {
    super.onPause()
    adView?.resume()
  }

  fun loadAdBanner(
    adModBannerItem: AdModBannerItem,
    onLoadAdBannerListener: BaseFragment.OnLoadAdBannerItemListener? = null
  ) {
    adModBannerItem.apply {
      setAdListener(
        object : AdModBannerItem.OnLoadListener {
          override fun loaded() {
            onLoadAdBannerListener?.loaded()
          }

          override fun loadFail() {
            onLoadAdBannerListener?.loadAdFailed()
          }
        }
      )
      loadAds(this@BaseActivity)
      updateAdView(adView)
    }
  }

  private fun updateAdView(adView: AdView?) {
    this.adView = adView
  }

  fun setOnLoadAdInterstitialListener(listener: OnLoadAdInterstitialListener) {
    onLoadAdInterstitialListener = listener
  }

  fun setOnLoadRewardAdListener(listener: OnLoadRewardAdListener) {
    onLoadRewardAdListener = listener
  }

  fun loadAdMobInterstitial(show: Boolean) {
    val adMobInterstitial = AdMobInterstitial.instance
    onLoadAdInterstitialListener?.let {
      adMobInterstitial.setOnLoadAdFullScreenListener(
        object : AdMobInterstitial.OnLoadAdInterstitialListener {
          override fun loaded() {
            it.loaded()
          }

          override fun loadFail() {
            it.loadFail()
          }

          override fun dismissAd() {
            it.dismissAd()
          }
        }
      )
    }
    adMobInterstitial.loadAdMobFullScreen(this, show)
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
    adMobReward.loadRewardAd(this, show)
  }

  override fun onDestroy() {
    super.onDestroy()
    mIsDestroy = true
  }

  companion object {
    private const val TOUCH_POINT = 20f
  }

  interface OnLoadAdInterstitialListener {
    fun loaded()

    fun loadFail()

    fun dismissAd()
  }

  interface OnLoadRewardAdListener {
    fun loadFailed()

    fun loaded()

    fun onEarnedReward()

    fun dismissAd()
  }
}
