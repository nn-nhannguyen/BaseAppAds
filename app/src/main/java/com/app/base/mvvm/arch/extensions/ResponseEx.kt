package com.app.base.mvvm.arch.extensions

import com.app.base.mvvm.data.error.ErrorModel
import com.app.base.mvvm.data.error.RepositoryException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import org.json.JSONObject
import retrofit2.Response

inline fun <T, R> Response<T>.mapSuccess(crossinline block: (T) -> R): R {
  val safeBody = body()
  if (this.isSuccessful && safeBody != null) {
    return block(safeBody)
  } else {
    throw toError()
  }
}

fun <T> Response<T>.mapToRepositoryException(): RepositoryException {
  return RepositoryException(
    code = code(),
    errorBody = errorBody()?.string(),
    msg = message()
  )
}

fun <T> Response<T>.exceptionOnSuccessResponse(): ErrorModel.Http? {
  this.errorBody()?.let {
    val jObjError = JSONObject(it.string())
    return ErrorModel.Http.ApiError(
      code = this.code().toString(),
      message = jObjError.getString("message"),
      apiUrl = this.raw().request.url.toString(),
      status = jObjError.getInt("status"),
      errorCode = jObjError.getString("error_code")
    )
  }
  return null
}

@Suppress("JSON_FORMAT_REDUNDANT")
fun <T> Response<T>.toError(): ErrorModel.Http {
  try {
    return exceptionOnSuccessResponse() ?: ErrorModel.Http.ApiError(
      code = code().toString(),
      message =
      errorBody()?.string()
        ?: ErrorModel.LocalErrorException.UN_KNOW_EXCEPTION.message,
      apiUrl = this.raw().request.url.toString()
    )
  } catch (ex: Exception) {
    return ErrorModel.Http.ApiError(
      code = code().toString(),
      message =
      errorBody()?.string()
        ?: ErrorModel.LocalErrorException.UN_KNOW_EXCEPTION.message,
      apiUrl = this.raw().request.url.toString()
    )
  }
}

fun Throwable.toError(): ErrorModel {
  return when (this) {
    is SocketTimeoutException ->
      ErrorModel.LocalError(
        ErrorModel.LocalErrorException.REQUEST_TIME_OUT_EXCEPTION.message,
        ErrorModel.LocalErrorException.REQUEST_TIME_OUT_EXCEPTION.code
      )

    is UnknownHostException ->
      ErrorModel.LocalError(
        ErrorModel.LocalErrorException.NO_INTERNET_EXCEPTION.message,
        ErrorModel.LocalErrorException.NO_INTERNET_EXCEPTION.code
      )

    is ConnectException ->
      ErrorModel.LocalError(
        ErrorModel.LocalErrorException.NO_INTERNET_EXCEPTION.message,
        ErrorModel.LocalErrorException.NO_INTERNET_EXCEPTION.code
      )

    else -> ErrorModel.LocalError(this.message.toString(), "1014")
  }
}
