package com.app.base.mvvm.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.base.mvvm.arch.extensions.LoadingAware
import com.app.base.mvvm.arch.extensions.ViewErrorAware
import com.app.base.mvvm.data.error.AppApiStatus
import com.app.base.mvvm.data.source.LoadingState
import com.app.base.mvvm.model.DataResponse
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

abstract class BaseViewModel : ViewModel(), CoroutineScope, ViewErrorAware, LoadingAware {
  val flagLoadingState = MutableLiveData<LoadingState>()

  val eventFailure = MutableLiveData<Event<Throwable>>()

  private val _apiStatus = MutableLiveData<AppApiStatus?>()
  val apiStatus: LiveData<AppApiStatus?> = _apiStatus

  fun showLoading(loadingState: LoadingState) {
    flagLoadingState.postValue(loadingState)
  }

  fun showFailure(throwable: Throwable) {
    eventFailure.value = Event(throwable)
  }

  // Coroutine's background job
  private val job = Job()

  // Define default thread for Coroutine as Main and add job
  override val coroutineContext: CoroutineContext = job + Dispatchers.Main

  override fun onCleared() {
    super.onCleared()
    // Clear our job when the linked activity is destroyed to avoid memory leaks
    job.cancel()
  }

  fun <T> handleResponseStatus(response: T & Any) {
    if (response is DataResponse<*>) {
      _apiStatus.value = AppApiStatus.detectStatus(response.status, response.message)
    }
  }

  fun clearAppError() {
    _apiStatus.value = null
  }
}
