package com.app.base.mvvm.navigator

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.app.base.mvvm.ui.home.HomeActivity
import com.app.base.mvvm.ui.test.TestActivity
import java.lang.ref.WeakReference
import javax.inject.Inject

open class AppNavigator @Inject constructor(fragmentActivity: FragmentActivity) {
  protected val activityReference = WeakReference(fragmentActivity)
  private val activityRef: FragmentActivity get() = activityReference.get()!!
  private val supportFragmentManager: FragmentManager get() = activityRef.supportFragmentManager

  fun navigateToTestData() {
    TestActivity.start(activityRef)
  }

  fun navigateToHome(bundle: Bundle? = null) {
    HomeActivity.start(activityRef, bundle)
  }

  fun navigateToTestApi(filePath: String) {
    // DrawActivity.start(activityRef, filePath)
  }
}
