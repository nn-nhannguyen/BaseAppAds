package com.app.base.mvvm.ui.home.navigator

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import com.app.base.mvvm.R
import com.app.base.mvvm.navigator.AppNavigator
import javax.inject.Inject

class HomeNavigator @Inject constructor(fragmentActivity: FragmentActivity) :
  AppNavigator(fragmentActivity) {

  fun setGraph() {
    val graph = R.navigation.home_navigation
    val bundle = Bundle()
    activityReference.get()
      ?.findNavController()
      ?.setGraph(graph, bundle)
  }

  fun openTestData() {
    navigateToTestData()
  }

  fun back() {
    activityReference.get()?.apply { if (!findNavController().popBackStack()) finish() }
  }

  private fun FragmentActivity.findNavController(): NavController = findNavController(R.id.nav_host_fragment)

  private fun navigate(factory: () -> NavDirections) {
    activityReference.get()
      ?.findNavController()
      ?.navigate(factory())
  }
}
