package com.app.base.mvvm.ui.test

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.databinding.ViewDataBinding
import com.app.base.mvvm.R
import com.app.base.mvvm.base.BaseActivity
import com.app.base.mvvm.databinding.ActivityTestBinding
import com.app.base.mvvm.ui.test.navigator.TestNavigator
import com.app.base.mvvm.ui.test.viewmodel.TestViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TestActivity : BaseActivity(R.layout.activity_test) {

  private val testViewModel: TestViewModel by viewModels()
  private lateinit var viewBinding: ActivityTestBinding

  @Inject
  lateinit var navigator: TestNavigator

  companion object {
    fun start(context: Context) {
      Intent(context, TestActivity::class.java).apply {
        // flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(this)
      }
    }
  }

  override fun applyBinding(viewDataBinding: ViewDataBinding) {
    viewBinding = viewDataBinding as ActivityTestBinding
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
