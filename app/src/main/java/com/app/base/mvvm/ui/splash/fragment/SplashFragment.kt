package com.app.base.mvvm.ui.splash.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import com.app.base.mvvm.R
import com.app.base.mvvm.base.BaseFragment
import com.app.base.mvvm.databinding.FragmentSplashBinding
import com.app.base.mvvm.ui.splash.navigator.SplashNavigator
import com.app.base.mvvm.ui.splash.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : BaseFragment(R.layout.fragment_splash) {

  private val viewModel by viewModels<SplashViewModel>()
  private lateinit var viewBinding: FragmentSplashBinding

  @Inject
  lateinit var navigator: SplashNavigator
  override fun applyBinding(viewDataBinding: ViewDataBinding) {
    viewBinding = viewDataBinding as FragmentSplashBinding
  }

  companion object {
    fun newInstance(): SplashFragment {
      val fragment = SplashFragment()
      val bundle = Bundle()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onInit(view: View, fragmentArg: Bundle?, saveInstance: Bundle?) {
    openHome()
  }

  private fun openHome() {
    Handler(Looper.getMainLooper()).postDelayed({
      navigator.openHome()
      activity?.finish()
    }, 4000)
  }
}
