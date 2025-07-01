package com.app.base.mvvm.ui.splash

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.ViewDataBinding
import com.app.base.mvvm.R
import com.app.base.mvvm.base.BaseActivity
import com.app.base.mvvm.databinding.ActivitySplashBinding
import com.app.base.mvvm.ui.splash.fragment.SplashFragment
import com.app.base.mvvm.ui.splash.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashAppActivity : BaseActivity(R.layout.activity_splash) {

  private val viewModel: SplashViewModel by viewModels()
  private lateinit var viewBinding: ActivitySplashBinding
  override fun applyBinding(viewDataBinding: ViewDataBinding) {
    viewBinding = viewDataBinding as ActivitySplashBinding
  }

  override fun onInit(arg: Bundle?, saveInstance: Bundle?) {
    if (saveInstance == null) {
      supportFragmentManager.beginTransaction()
        .replace(R.id.container, SplashFragment.newInstance())
        .addToBackStack(null)
        .commit()
    }
  }
}
