package com.app.base.mvvm.data.error

import com.app.base.mvvm.R
import com.app.base.mvvm.utils.StringUtils
import java.net.HttpURLConnection

sealed class ErrorModel : Throwable() {
  open fun isCommonError(): Boolean = false

  open fun status(): Int? = 200

  var appApi: AppApi? = null

  companion object {
    const val API_ERROR_RESULT_CODE = "1"
    const val API_ERROR_NO_INTERNET = "1001"
    const val API_ERROR_TIME_OUT = "1002"
    const val API_ERROR_UNKNOWN = "1000"
    const val API_ERROR_SERVER_MAINTENANCE = "1000"
    const val API_ERROR_FORCE_UPDATE = "8001"
    const val API_ERROR_NO_RESPONSE = "9002"
    const val API_ERROR_ACCOUNT_DE_ACTIVE = "EM001"
    const val API_EXPIRED = "EM003"
    const val API_TOKEN_INVALID = "EM002"
    const val API_REQUIRE_LOGIN = "EA001"
    const val API_ERROR_PURCHASE_TOKEN_INVALID = "EU005"
  }

  sealed class Http : ErrorModel() {
    data class ApiError(
      val code: String?,
      override val message: String?,
      val apiUrl: String?,
      val status: Int? = 200,
      val errorCode: String? = "ER001"
    ) : Http() {
      override fun isCommonError(): Boolean {
        return code == HttpURLConnection.HTTP_UNAUTHORIZED.toString() ||
          code == HttpURLConnection.HTTP_INTERNAL_ERROR.toString() ||
          code == ApiErrorDetailCode.SERVER_MAINTENANCE_9001.code ||
          code == ApiErrorDetailCode.FORCE_UPDATE_8001.code ||
          code == ApiErrorDetailCode.NO_RESPONSE_FROM_SERVER_9002.code ||
          code == ApiErrorDetailCode.ACCOUNT_DE_ACTIVE.code ||
          code == ApiErrorDetailCode.ERROR_API_EXPIRED.code ||
          code == ApiErrorDetailCode.ERROR_API_TOKEN_INVALID.code ||
          code == ApiErrorDetailCode.ERROR_API_REQUIRE_LOGIN.code ||
          code == ApiErrorDetailCode.ERROR_PURCHASE_TOKEN_INVALID.code
      }

      override fun status(): Int? = status
    }
  }

  data class LocalError(val errorMessage: String, val code: String?) : ErrorModel()

  enum class LocalErrorException(val message: String, val code: String) {
    NO_INTERNET_EXCEPTION(StringUtils.getString(R.string.msg_no_internet_exception), API_ERROR_NO_INTERNET),
    REQUEST_TIME_OUT_EXCEPTION(StringUtils.getString(R.string.msg_request_time_out_exception), API_ERROR_TIME_OUT),
    UN_KNOW_EXCEPTION(StringUtils.getString(R.string.msg_unknown_exception), API_ERROR_UNKNOWN)
  }

  enum class ApiErrorDetailCode(val code: String) {
    SERVER_MAINTENANCE_9001(API_ERROR_SERVER_MAINTENANCE),
    FORCE_UPDATE_8001(API_ERROR_FORCE_UPDATE),
    NO_RESPONSE_FROM_SERVER_9002(API_ERROR_NO_RESPONSE),
    ACCOUNT_DE_ACTIVE(API_ERROR_ACCOUNT_DE_ACTIVE),
    ERROR_API_EXPIRED(API_EXPIRED),
    ERROR_API_TOKEN_INVALID(API_TOKEN_INVALID),
    ERROR_API_REQUIRE_LOGIN(API_REQUIRE_LOGIN),
    ERROR_PURCHASE_TOKEN_INVALID(API_ERROR_PURCHASE_TOKEN_INVALID)
  }

  enum class AppApi {
    LOGIN,
    UnClassified
  }
}
