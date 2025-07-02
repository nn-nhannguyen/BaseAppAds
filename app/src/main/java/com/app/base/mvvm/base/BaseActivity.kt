package com.app.base.mvvm.base

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.app.base.mvvm.R
import com.app.base.mvvm.arch.extensions.hideKeyboard
import com.app.base.mvvm.data.source.LoadingState
import com.app.base.mvvm.ui.splash.SplashAppActivity
import com.app.base.mvvm.utils.LogUtil
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
  private var onLoadAdInterstitialListener: OnLoadAdInterstitialListener? = null
  private var onLoadRewardAdListener: OnLoadRewardAdListener? = null
  private var adView: AdView? = null
  private var alertDialog: AlertDialog? = null
  private var allowDismissLoading = true

  private val anchorSnackBar: View
    get() = findViewById(android.R.id.content)

  abstract fun applyBinding(viewDataBinding: ViewDataBinding)

  abstract fun onInit(
    arg: Bundle?,
    saveInstance: Bundle?,
  )

  override fun attachBaseContext(newBase: Context) {
    super.attachBaseContext(FontScaleContextWrapper.wrap(newBase))
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    handleSplashScreen()
    super.onCreate(savedInstanceState)
    mDataBinding = DataBindingUtil.setContentView(this, layoutId)
    //configFullScreen(mDataBinding.root, true)
    mDataBinding.lifecycleOwner = this

//        val view = findViewById<View>(android.R.id.content).rootView
//        view?.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBackgroundWhite))

    mLoadingDialog = LoadingDialog(this)
    mSwipeInterface = Slidr.attach(this)

    setEnableSlidr(false)

    applyBinding(mDataBinding)
    onInit(intent?.extras, savedInstanceState)
  }

  private fun handleSplashScreen() {
    if (this is SplashAppActivity && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      val splashScreen = installSplashScreen()
      splashScreen.setKeepOnScreenCondition { true }
    }
  }

  private fun configFullScreen(
    mainContainer: View,
    isFullScreen: Boolean,
    showStatusBar: Boolean = true,
  ) {
    if (!isDarkMode()) {
      window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    } else {
      window.decorView.systemUiVisibility = 0
    }

    if (!isFullScreen) {
      if (Build.VERSION.SDK_INT in 21..29) {
        window.statusBarColor = Color.TRANSPARENT
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.setBackgroundDrawableResource(android.R.color.transparent)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
      } else if (Build.VERSION.SDK_INT >= 30) {
        window.statusBarColor = Color.TRANSPARENT
        showSystemUI(mainContainer)
      }
      return
    }
    if (Build.VERSION.SDK_INT in 21..29) {
      window.statusBarColor = Color.TRANSPARENT
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
      window.setBackgroundDrawableResource(android.R.color.transparent)
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
      window.decorView.systemUiVisibility =
        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    } else if (Build.VERSION.SDK_INT >= 30) {
      window.statusBarColor = Color.TRANSPARENT
      hideSystemUI(mainContainer, showStatusBar)
    }
  }

  fun isDarkMode(): Boolean {
    val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
  }

  private fun hideSystemUI(
    mainContainer: View,
    showStatusBar: Boolean = true,
  ) {
    if (Build.VERSION.SDK_INT < 30) {
      return
    }
    WindowCompat.setDecorFitsSystemWindows(window, false)
    WindowInsetsControllerCompat(window, mainContainer).let { controller ->
      if (showStatusBar) {
        controller.show(WindowInsetsCompat.Type.statusBars())
      } else {
        controller.hide(WindowInsetsCompat.Type.statusBars())
      }
      controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

      ViewCompat.setOnApplyWindowInsetsListener(
        mainContainer,
      ) { view: View, windowInsets: WindowInsetsCompat ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
        view.layoutParams =
          (view.layoutParams as FrameLayout.LayoutParams).apply {
            bottomMargin = insets.bottom
          }
        WindowInsetsCompat.CONSUMED
      }
    }
  }

  private fun showSystemUI(mainContainer: View) {
    if (Build.VERSION.SDK_INT < 30) {
      return
    }
    WindowCompat.setDecorFitsSystemWindows(window, true)
    WindowInsetsControllerCompat(window, mainContainer).let { controller ->
      controller.show(WindowInsetsCompat.Type.systemBars())
      ViewCompat.setOnApplyWindowInsetsListener(
        mainContainer,
      ) { view: View, _: WindowInsetsCompat ->
        view.layoutParams =
          (view.layoutParams as FrameLayout.LayoutParams).apply {
            bottomMargin = 0
          }
        WindowInsetsCompat.CONSUMED
      }
    }
  }

  fun setStatusBarLight(showLight: Boolean) {
    WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = showLight
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

  fun showMessageDialog(message: String, onPositiveButtonListener: () -> Unit, allowCancel: Boolean? = false) {
    if (alertDialog != null && alertDialog?.isShowing == true) {
      alertDialog?.dismiss()
    }
    alertDialog =
      AlertDialog.Builder(this)
        .setMessage(message)
        .setCancelable(allowCancel == true)
        .setCanceledOnTouchOutside(allowCancel == true)
        .setPositiveButtonListener(
          object : AlertDialog.OnClickListener {
            override fun onClick(v: View) {
              onPositiveButtonListener.invoke()
            }
          },
        )
        .create()
    alertDialog?.show()
  }

  fun showFullDialog(
    title: Int, message: Int,
    textNegative: Int, textPositive: Int,
    onPositiveButtonListener: () -> Unit
  ) {
    AlertDialog.Builder(this)
      .setMessage(message)
      .setTitle(title)
      .setNegativeButtonText(resId = textNegative, null)
      .setPositiveButtonText(resId = textPositive, null)
      .setPositiveButtonListener(
        object : AlertDialog.OnClickListener {
          override fun onClick(v: View) {
            onPositiveButtonListener.invoke()
          }
        },
      )
      .create().show()
  }

  fun showDialogCallback(
    title: Int, message: Int,
    textNegative: Int, textPositive: Int,
    onNegativeButtonListener: () -> Unit,
    onPositiveButtonListener: () -> Unit
  ) {
    AlertDialog.Builder(this)
      .setMessage(message)
      .setTitle(title)
      .setNegativeButtonText(
        resId = textNegative,
        object : AlertDialog.OnClickListener {
          override fun onClick(v: View) {
            onNegativeButtonListener.invoke()
          }
        },
      )
      .setPositiveButtonText(resId = textPositive, null)
      .setPositiveButtonListener(
        object : AlertDialog.OnClickListener {
          override fun onClick(v: View) {
            onPositiveButtonListener.invoke()
          }
        },
      )
      .create().show()
  }

  fun showMessageDialog(
    title: String, message: String,
    textNegative: Int, textPositive: Int,
    onNegativeButtonListener: () -> Unit,
    onPositiveButtonListener: () -> Unit
  ) {
    AlertDialog.Builder(this)
      .setMessage(message)
      .setTitle(title)
      .setNegativeButtonText(
        resId = textNegative,
        object : AlertDialog.OnClickListener {
          override fun onClick(v: View) {
            onNegativeButtonListener.invoke()
          }
        },
      )
      .setPositiveButtonText(resId = textPositive, null)
      .setPositiveButtonListener(
        object : AlertDialog.OnClickListener {
          override fun onClick(v: View) {
            onPositiveButtonListener.invoke()
          }
        },
      )
      .create().show()
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

  fun showErrorNetWork(callBack: (() -> Unit)? = null) {
    if (isFinishing) {
      return
    }
    AlertDialog.Builder(this)
      .setTitle(getString(R.string.title_error_network))
      .setCanceledOnTouchOutside(false)
      .setMessage(getString(R.string.error_network))
      .setPositiveButtonText(getString(R.string.text_ok), object : AlertDialog.OnClickListener {
        override fun onClick(v: View) {
          callBack?.invoke()
        }

      })
      .create().show()
  }

  fun showLoading() {
    if (!mIsDestroy && !isFinishing && mLoadingDialog?.isShowing != true) {
      mLoadingDialog?.show()
    }
  }

  fun dismissLoading() {
    if (!isFinishing && mLoadingDialog?.isShowing != false && allowDismissLoading) {
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
    LogUtil.logMessage(mTag, "finishedActivity: ")
    finish()
  }

  fun restartApp() {
    LogUtil.logMessage(mTag, "restartApp: ")
    baseContext.packageManager.getLaunchIntentForPackage(baseContext.packageName)?.apply {
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
      startActivity(this)
    }
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
            checkToHideKeyboard(view, event)
          }
        }
      }
    }
    try {
      return super.dispatchTouchEvent(event)
    } catch (_: IllegalArgumentException) {
      LogUtil.logMessage(mTag, "dispatch key event exception")
    }

    return false
  }

  fun checkToHideKeyboard(
    view: View,
    event: MotionEvent,
  ) {
    val scrCoordinates = IntArray(2)
    view.getLocationOnScreen(scrCoordinates)
    val x = event.rawX + view.left - scrCoordinates[0]
    val y = event.rawY + view.top - scrCoordinates[1]
    if (x < view.left || x >= view.right || y < view.top || y > view.bottom) {
      window?.apply {
        val inputMethodManager =
          getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
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
    onLoadAdBannerListener: BaseFragment.OnLoadAdBannerItemListener? = null,
    isAdaptive: Boolean? = false
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
        },
      )
      loadAds(this@BaseActivity, isAdaptive)
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

  fun loadAdMobInterstitial(show: Boolean, forceLoad: Boolean? = false) {
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
        },
      )
    }
    adMobInterstitial.loadAdMobFullScreen(this, show, forceLoad)
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
        },
      )
    }
    adMobReward.loadRewardAd(this, show)
  }

  override fun onDestroy() {
    super.onDestroy()
    mIsDestroy = true
    adView?.destroy()
  }

  fun allDismissLoading(): Boolean {
    return allowDismissLoading
  }

  fun updateFlagAllowDismissLoading(allow: Boolean) {
    allowDismissLoading = allow
  }

  interface DialogMessageDismissListener {
    fun onDismissListener()
  }

  companion object {
    private const val TOUCH_POINT = 20f
  }
}
