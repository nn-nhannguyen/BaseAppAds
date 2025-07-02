package com.app.base.mvvm.ui.splash.fragment

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import com.app.base.mvvm.R
import com.app.base.mvvm.base.BaseFragment
import com.app.base.mvvm.databinding.FragmentSplashBinding
import com.app.base.mvvm.repository.AppSettingsRepositoryInterface
import com.app.base.mvvm.ui.splash.SplashAppActivity
import com.app.base.mvvm.ui.splash.navigator.SplashNavigator
import com.app.base.mvvm.ui.splash.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : BaseFragment(R.layout.fragment_splash) {
  private val viewModel by viewModels<SplashViewModel>()
  private lateinit var viewBinding: FragmentSplashBinding

  @Inject
  lateinit var navigator: SplashNavigator

  @Inject
  lateinit var appSettingsRepository: AppSettingsRepositoryInterface

  override fun applyBinding(viewDataBinding: ViewDataBinding) {
    viewBinding = viewDataBinding as FragmentSplashBinding
  }

  companion object {
    fun newInstance(bundle: Bundle? = null): SplashFragment {
      val fragment = SplashFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onInit(view: View, fragmentArg: Bundle?, saveInstance: Bundle?) {
    if (activity is SplashAppActivity) {
      (activity as SplashAppActivity).checkSetUpAds(fragmentArg)
    }
    // openNextScreen(fragmentArg)
  }

  private fun openNextScreen(bundle: Bundle?) {
    CoroutineScope(Dispatchers.Main).launch {
      delay(2500)
      navigator.openHome(bundle)
      activity?.finish()
    }
  }
}
