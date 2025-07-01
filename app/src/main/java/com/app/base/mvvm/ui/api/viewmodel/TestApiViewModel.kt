package com.app.base.mvvm.ui.api.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.bindCommonError
import androidx.lifecycle.bindLoading
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.app.base.mvvm.arch.extensions.FlowResult
import com.app.base.mvvm.arch.extensions.onError
import com.app.base.mvvm.arch.extensions.onSuccess
import com.app.base.mvvm.base.BaseViewModel
import com.app.base.mvvm.data.error.ErrorModel
import com.app.base.mvvm.data.source.LoadingState
import com.app.base.mvvm.data.source.Resource
import com.app.base.mvvm.model.User
import com.app.base.mvvm.repository.MainRepository
import com.app.base.mvvm.ui.usecase.GetUserUseCase
import com.app.base.mvvm.ui.usecase.UseCaseObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

@HiltViewModel
class TestApiViewModel @Inject constructor(
  private val mainRepository: MainRepository,
  private val getUserUseCase: GetUserUseCase
) : BaseViewModel() {

  val getUserObserver = UseCaseObserver<List<User>>()
  private val _users = MutableLiveData<Resource<List<User>>>()
  val users: LiveData<Resource<List<User>>>
    get() = _users

  private val _simpleUsers = MutableLiveData<List<User>>()
  val simpleUsers: LiveData<List<User>> = _simpleUsers

  private val _errorModel = MutableLiveData<ErrorModel?>()
  val errorModel: LiveData<ErrorModel?> = _errorModel

  fun fetchUsers() {
    viewModelScope.launch {
      _users.postValue(Resource.loading(null))
      showLoading(LoadingState.LOADING)

      mainRepository.getUsers().let {
        if (it.isSuccessful) {
          _users.postValue(Resource.success(it.body()))
        } else {
          _users.postValue(Resource.error(it.errorBody().toString(), null))
        }
        showLoading(LoadingState.LOADED)
      }
    }
  }

  fun getUserDataSafeDirectOnViewModel() {
    showLoading(LoadingState.LOADING)
    fetchUsersSafe().onSuccess {
      _simpleUsers.value = it
      showLoading(LoadingState.LOADED)
    }.onError(
      normalAction = {
        _errorModel.value = it
        showLoading(LoadingState.LOADED)
      }
    ).launchIn(viewModelScope)
  }

  fun getUserWithUseCase() {
    getUserObserver.invokeUseCase(getUserUseCase, Unit, this)
  }

  private fun fetchUsersSafe(): Flow<FlowResult<List<User>?>> = mainRepository.getUsersSafe().bindLoading(this)
    .bindCommonError(this)

  fun getUserFromApi() = liveData(Dispatchers.IO) {
    showLoading(LoadingState.LOADING)
    emit(Resource.loading(data = null))
    try {
      emit(Resource.success(data = mainRepository.getUserLists()))
    } catch (exception: Exception) {
      emit(Resource.error(msg = exception.message ?: "Error Occurred!", data = null))
    }
  }

  fun listenGetUser(viewLifecycleOwner: LifecycleOwner) {
    getUserObserver.apply {
      succeed.observe(viewLifecycleOwner) {
        _simpleUsers.value = it
      }

      errorModel.observe(viewLifecycleOwner) {
        _errorModel.value = it
      }
    }
  }

  override fun onCleared() {
    _errorModel.value = null
    getUserObserver.resetFlag()
    super.clearAppError()
  }
}
