package com.app.base.mvvm.ui.api

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.base.mvvm.R
import com.app.base.mvvm.arch.extensions.beGone
import com.app.base.mvvm.arch.extensions.beVisible
import com.app.base.mvvm.base.BaseFragment
import com.app.base.mvvm.data.error.ErrorModel
import com.app.base.mvvm.data.source.LoadingState
import com.app.base.mvvm.data.source.Status
import com.app.base.mvvm.databinding.FragmentTestApiBinding
import com.app.base.mvvm.model.User
import com.app.base.mvvm.ui.api.adapter.ApiAdapter
import com.app.base.mvvm.ui.api.viewmodel.TestApiViewModel
import com.app.base.mvvm.ui.test.navigator.TestNavigator
import com.app.base.mvvm.view.item.Header
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TestApiFragment : BaseFragment(R.layout.fragment_test_api) {

  @Inject
  lateinit var navigator: TestNavigator
  private val viewModel: TestApiViewModel by viewModels()
  private lateinit var viewBinding: FragmentTestApiBinding
  private lateinit var apiAdapter: ApiAdapter
  override fun applyBinding(viewDataBinding: ViewDataBinding) {
    viewBinding = viewDataBinding as FragmentTestApiBinding
    viewBinding.viewModel = viewModel
  }

  override fun onInit(view: View, fragmentArg: Bundle?, saveInstance: Bundle?) {
    setupUI()
    setUpHeader()
    listenerLoadData()
    setupObserver()
  }

  private fun setUpHeader() {
    viewBinding.header.setOnItemClickListener(object : Header.SimpleHeaderClickListener() {
      override fun onBack() {
        navigator.back()
      }
    })
  }

  private fun setupUI() {
    viewBinding.recyclerView.apply {
      layoutManager = LinearLayoutManager(requireActivity())
      apiAdapter = ApiAdapter(arrayListOf())
      addItemDecoration(
        DividerItemDecoration(
          context,
          (layoutManager as LinearLayoutManager).orientation
        )
      )
      adapter = apiAdapter
    }
  }

  private fun listenerLoadData() {
    // listenerLoading(viewModel)
    viewModel.flagLoadingState.observe(viewLifecycleOwner) { state ->
      state?.let {
        if (it == LoadingState.LOADING) {
          showLoadingDialog()
        } else {
          hideLoadingDialog()
        }
      }
    }
  }

  private fun setupObserver() {
    // getDataWithLifeCycle()
    getDataSafe()
    // getDataNormal()

    viewModel.listenGetUser(viewLifecycleOwner)
  }

  private fun getDataSafe() {
    viewModel.simpleUsers.observe(viewLifecycleOwner) {
      it?.let {
        renderList(it)
        viewBinding.recyclerView.beVisible()
      }
    }

    viewModel.errorModel.observe(viewLifecycleOwner) {
      if (it is ErrorModel.LocalError) {
        val apiError: ErrorModel.LocalError = it
        Toast.makeText(requireActivity(), apiError.errorMessage, Toast.LENGTH_LONG).show()
      }
      viewModel.clearAppError()
    }
    // viewModel.getUserDataSafeDirectOnViewModel()
    viewModel.getUserWithUseCase()
  }

  private fun getDataNormal() {
    viewModel.users.observe(this) {
      when (it.status) {
        Status.SUCCESS -> {
          it.data?.let { users -> renderList(users) }
          viewBinding.recyclerView.beVisible()
        }

        Status.LOADING -> {
          viewBinding.recyclerView.beGone()
        }

        Status.ERROR -> {
          Toast.makeText(requireActivity(), it.message, Toast.LENGTH_LONG).show()
        }
      }
    }
    viewModel.fetchUsers()
  }

  private fun getDataWithLifeCycle() {
    viewModel.getUserFromApi().observe(this) {
      it?.let {
        when (it.status) {
          Status.SUCCESS -> {
            it.data?.let { users -> renderList(users) }
            viewBinding.recyclerView.beVisible()
            dismissLoading()
          }

          Status.LOADING -> {
            viewBinding.recyclerView.beVisible()
          }

          Status.ERROR -> {
            dismissLoading()
            Toast.makeText(requireActivity(), it.message, Toast.LENGTH_LONG).show()
          }
        }
      }
    }
  }

  @SuppressLint("NotifyDataSetChanged")
  private fun renderList(users: List<User>) {
    apiAdapter.addData(users)
    apiAdapter.notifyDataSetChanged()
  }
}
