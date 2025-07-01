package com.app.base.mvvm.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.databinding.ViewDataBinding
import com.app.base.mvvm.R
import com.app.base.mvvm.base.BaseActivity
import com.app.base.mvvm.databinding.ActivityHomeBinding
import com.app.base.mvvm.ui.home.navigator.HomeNavigator
import com.app.base.mvvm.ui.home.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : BaseActivity(R.layout.activity_home) {

  private val viewModel: HomeViewModel by viewModels()
  private lateinit var viewBinding: ActivityHomeBinding

  @Inject
  lateinit var navigator: HomeNavigator

  companion object {
    fun start(context: Context) {
      Intent(context, HomeActivity::class.java).apply {
        // flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(this)
      }
    }
  }

  override fun applyBinding(viewDataBinding: ViewDataBinding) {
    viewBinding = viewDataBinding as ActivityHomeBinding
  }

  override fun onInit(arg: Bundle?, saveInstance: Bundle?) {
    navigator.setGraph()
    // listenerOnBackPress()
  }

  private fun listenerOnBackPress() {
    onBackPressedDispatcher.addCallback(
      this,
      object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
          supportFragmentManager.fragments.let {
            if (it.size > 0) {
            }
            isEnabled = false
            onBackPressedDispatcher.onBackPressed()
            listenerOnBackPress()
          }
        }
      }
    )
  }
}
