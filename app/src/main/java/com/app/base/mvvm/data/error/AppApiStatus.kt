package com.app.base.mvvm.data.error

class AppApiStatus(
  val status: Int,
  val message: String
) {
  companion object {
    const val API_ERROR_RESULT_CODE = 1
    const val API_ERROR_NO_INTERNET = 1001
    const val API_ERROR_UNKNOWN = 1000
    const val API_ERROR_SERVER_MAINTENANCE = 1000
    const val API_ERROR_FORCE_UPDATE = 8001
    const val API_ERROR_NO_RESPONSE = 9002
    private const val API_SUCCESSFULLY = 200

    fun detectStatus(status: Int, message: String?): AppApiStatus? {
      val apiStatus: AppApiStatus? =
        when (status) {
          API_ERROR_SERVER_MAINTENANCE -> AppApiStatus(status, message ?: "")
          API_SUCCESSFULLY -> AppApiStatus(status, message ?: "")
          else -> null
        }
      return apiStatus
    }
  }
}
