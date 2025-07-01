package com.app.base.mvvm.ui.splash.navigator

import androidx.fragment.app.FragmentActivity
import com.app.base.mvvm.navigator.AppNavigator
import javax.inject.Inject

class SplashNavigator @Inject constructor(fragmentActivity: FragmentActivity) :
  AppNavigator(fragmentActivity) {

  fun openHome() {
    navigateToHome()
  }
}
