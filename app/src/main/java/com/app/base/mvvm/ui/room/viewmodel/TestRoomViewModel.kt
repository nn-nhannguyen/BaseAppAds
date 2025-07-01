package com.app.base.mvvm.ui.room.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.base.mvvm.base.BaseViewModel
import com.app.base.mvvm.data.source.Resource
import com.app.base.mvvm.model.UserDB
import com.app.base.mvvm.repository.RoomRepository
import com.app.base.mvvm.utils.ConstantUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class TestRoomViewModel @Inject constructor(private val roomRepository: RoomRepository) :
  BaseViewModel() {

  private val _users = MutableLiveData<Resource<List<UserDB>>>()
  val users: LiveData<Resource<List<UserDB>>>
    get() = _users

  init {
    getAllUser()
  }

  fun insertUser(userDB: UserDB) {
    viewModelScope.launch(Dispatchers.Default) {
      roomRepository.insertUser(userDB).let {
        Log.d(ConstantUtil.TAG_GLOBAL, "Insert data success!")
      }
    }
  }

  fun getAllUser() {
    viewModelScope.launch(Dispatchers.Default) {
      _users.postValue(Resource.loading(null))
      roomRepository.getAllUsers().let {
        _users.postValue(Resource.success(it))
      }
    }
  }
}
