package com.app.base.mvvm.ui.splash.navigator

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.app.base.mvvm.navigator.AppNavigator
import javax.inject.Inject

class SplashNavigator @Inject constructor(fragmentActivity: FragmentActivity) :
  AppNavigator(fragmentActivity) {

  fun openHome(bundle: Bundle? = null) {
    navigateToHome(bundle)
  }
}
