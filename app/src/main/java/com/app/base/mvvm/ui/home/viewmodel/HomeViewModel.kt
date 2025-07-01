package com.app.base.mvvm.ui.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.base.mvvm.data.source.Resource
import com.app.base.mvvm.model.User
import com.app.base.mvvm.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val mainRepository: MainRepository) : ViewModel() {

  private val _users = MutableLiveData<Resource<List<User>>>()
  val users: LiveData<Resource<List<User>>>
    get() = _users

  init {
    getInformation()
  }

  private fun getInformation() {
        /*   viewModelScope.launch {
           }*/
  }
}
