package com.app.base.mvvm.arch.extensions

import retrofit2.Response

inline fun <T> apiCall(block: () -> Response<T>): T {
  val response = block()
  return when (response.isSuccessful) {
    true -> {
      response.body() ?: throw response.toError()
    }

    false -> throw response.toError()
  }
}
