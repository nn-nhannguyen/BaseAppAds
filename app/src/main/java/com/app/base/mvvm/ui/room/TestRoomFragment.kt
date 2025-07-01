package com.app.base.mvvm.ui.room

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.base.mvvm.R
import com.app.base.mvvm.base.BaseFragment
import com.app.base.mvvm.data.source.Status
import com.app.base.mvvm.databinding.FragmentRoomBinding
import com.app.base.mvvm.model.UserDB
import com.app.base.mvvm.ui.room.adapter.RoomAdapter
import com.app.base.mvvm.ui.room.viewmodel.TestRoomViewModel
import com.app.base.mvvm.view.item.Header
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TestRoomFragment : BaseFragment(R.layout.fragment_room) {

  private val viewModel: TestRoomViewModel by viewModels()
  private lateinit var viewBinding: FragmentRoomBinding

  private lateinit var roomAdapter: RoomAdapter

  override fun applyBinding(viewDataBinding: ViewDataBinding) {
    viewBinding = viewDataBinding as FragmentRoomBinding
    viewBinding.viewModel = viewModel
  }

  override fun onInit(view: View, fragmentArg: Bundle?, saveInstance: Bundle?) {
    setupUI()
    setUpHeader()
    setupClick()
    setupObserver()
  }

  private fun setUpHeader() {
    viewBinding.header.setOnItemClickListener(object : Header.SimpleHeaderClickListener() {
      override fun onBack() {
        activity?.onBackPressedDispatcher?.onBackPressed()
      }
    })
  }

  private fun setupUI() {
    viewBinding.recyclerView.apply {
      layoutManager = LinearLayoutManager(requireActivity())
      roomAdapter = RoomAdapter(arrayListOf())
      addItemDecoration(
        DividerItemDecoration(
          context,
          (layoutManager as LinearLayoutManager).orientation
        )
      )
      adapter = roomAdapter
    }
  }

  private fun setupClick() {
    viewBinding.btnInsertData.setOnClickListener {
      insertUser()
    }
  }

  private fun insertUser() {
    if (TextUtils.isEmpty(viewBinding.itemName.edtInput().text) ||
      TextUtils.isEmpty(viewBinding.itemEmail.edtInput().text) ||
      TextUtils.isEmpty(viewBinding.itemPassword.edtInput().text)
    ) {
      Toast.makeText(requireActivity(), "Can't input empty", Toast.LENGTH_SHORT).show()
      return
    }

    val userDB = UserDB()
    userDB.name = viewBinding.itemName.edtInput().text.toString()
    userDB.email = viewBinding.itemEmail.edtInput().text.toString()
    userDB.password = viewBinding.itemPassword.edtInput().text.toString()
    viewModel.insertUser(userDB)
    viewModel.getAllUser()
  }

  private fun setupObserver() {
    viewModel.users.observe(
      this,
      Observer {
        when (it.status) {
          Status.SUCCESS -> {
            it.data?.let { users -> renderList(users) }
            viewBinding.recyclerView.visibility = View.VISIBLE
          }

          Status.LOADING -> {
            viewBinding.recyclerView.visibility = View.GONE
          }

          Status.ERROR -> {
            Toast.makeText(requireActivity(), it.message, Toast.LENGTH_LONG).show()
          }
        }
      }
    )
  }

  @SuppressLint("NotifyDataSetChanged")
  private fun renderList(users: List<UserDB>) {
    roomAdapter.addData(users)
    roomAdapter.notifyDataSetChanged()
  }
}
