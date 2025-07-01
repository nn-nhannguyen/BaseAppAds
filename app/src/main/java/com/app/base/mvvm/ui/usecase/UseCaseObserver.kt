package com.app.base.mvvm.ui.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.bindError
import androidx.lifecycle.bindLoading
import androidx.lifecycle.viewModelScope
import com.app.base.mvvm.arch.extensions.onError
import com.app.base.mvvm.arch.extensions.onSuccess
import com.app.base.mvvm.base.BaseViewModel
import com.app.base.mvvm.data.error.ErrorModel
import com.app.base.mvvm.data.source.LoadingState
import kotlinx.coroutines.flow.launchIn

/**
 * Used as a observer for SingleUseCase.
 * Especially when there are no need to map (T is going to be same source/return parameter type)
 */
class UseCaseObserver<T> {
  private val _succeed = MutableLiveData<T?>()
  val succeed: LiveData<T?> = _succeed

  private val _errorModel = MutableLiveData<ErrorModel?>()
  val errorModel: LiveData<ErrorModel?> = _errorModel

  fun <TArg> invokeUseCase(useCase: SingleUseCase<T, TArg>, params: TArg, viewModel: BaseViewModel) {
    viewModel.flagLoadingState.value = LoadingState.LOADING
    useCase.execute(params).onSuccess {
      viewModel.flagLoadingState.value = LoadingState.LOADED
      _succeed.value = it
      it?.let { response ->
        viewModel.handleResponseStatus(response)
      }
    }
      .onError(
        normalAction = {
          _errorModel.value = it
          viewModel.flagLoadingState.value = LoadingState.LOADED
        },
        commonAction = {
          viewModel.flagLoadingState.value = LoadingState.LOADED
          _errorModel.value = it
        }
      )
      .bindLoading(viewModel).bindError(viewModel).launchIn(viewModel.viewModelScope)
  }

  fun resetFlag() {
    _errorModel.value = null
//    _succeed.value = null
  }
}
