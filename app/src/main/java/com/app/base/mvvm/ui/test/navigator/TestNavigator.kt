package com.app.base.mvvm.ui.test.navigator

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import com.app.base.mvvm.R
import com.app.base.mvvm.navigator.AppNavigator
import com.app.base.mvvm.ui.test.fragment.TestFragmentDirections
import javax.inject.Inject

class TestNavigator @Inject constructor(fragmentActivity: FragmentActivity) :
  AppNavigator(fragmentActivity) {

  fun setGraph() {
    val graph = R.navigation.test_navigation
    val bundle = Bundle()
    activityReference.get()
      ?.findNavController()
      ?.setGraph(graph, bundle)
  }

  fun openTestApi() {
    navigate {
      TestFragmentDirections.actionOpenTestApi()
    }
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

  fun openTestRoom() {
    navigate {
      TestFragmentDirections.actionOpenTestRoom()
    }
  }
}
