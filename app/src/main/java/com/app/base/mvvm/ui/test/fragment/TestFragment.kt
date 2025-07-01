package com.app.base.mvvm.ui.test.fragment

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import com.app.base.mvvm.R
import com.app.base.mvvm.arch.extensions.setOnSingleClickListener
import com.app.base.mvvm.base.BaseFragment
import com.app.base.mvvm.databinding.FragmentTestBinding
import com.app.base.mvvm.ui.test.navigator.TestNavigator
import com.app.base.mvvm.ui.test.viewmodel.TestFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TestFragment : BaseFragment(R.layout.fragment_test) {

  @Inject
  lateinit var navigator: TestNavigator
  private val viewModel by viewModels<TestFragmentViewModel>()

  private lateinit var viewBinding: FragmentTestBinding
  override fun applyBinding(viewDataBinding: ViewDataBinding) {
    viewBinding = viewDataBinding as FragmentTestBinding
    viewBinding.viewModel = viewModel
  }

  override fun onInit(view: View, fragmentArg: Bundle?, saveInstance: Bundle?) {
    setupClick()
  }

  private fun setupClick() {
    viewBinding.btnTestGetApi.setOnSingleClickListener {
      navigator.openTestApi()
    }

    viewBinding.btnTestDataRoom.setOnSingleClickListener {
      navigator.openTestRoom()
    }
  }
}
