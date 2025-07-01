package com.app.base.mvvm.data.error

import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.app.base.mvvm.R
import com.app.base.mvvm.base.BaseActivity
import com.app.base.mvvm.base.BaseViewModel
import com.app.base.mvvm.repository.AppSettingsRepositoryInterface
import javax.inject.Inject

class ApiErrorHandler
@Inject
constructor(
  private val appSettingsRepositoryInterface: AppSettingsRepositoryInterface
) {
  fun showDialogError(
    fragment: Fragment,
    errorModel: ErrorModel?,
    viewModel: BaseViewModel,
    viewLifecycleOwner: LifecycleOwner,
    reLoadCallback: (() -> Unit)? = null
  ) {
    errorModel?.let {
      val message =
        if (it is ErrorModel.LocalError) {
          it.errorMessage
        } else {
          if (it is ErrorModel.Http.ApiError && it.errorCode == ErrorModel.API_REQUIRE_LOGIN
          ) {
            fragment.getString(R.string.message_session_expired)
          } else {
            it.message.toString()
          }
        }
      message.let { msg ->
        (fragment.activity as BaseActivity).showMessageDialog(msg, {
          if (it.isCommonError()) {
            if (it is ErrorModel.Http.ApiError) {
              when (it.errorCode) {
                ErrorModel.API_ERROR_ACCOUNT_DE_ACTIVE,
                ErrorModel.API_TOKEN_INVALID,
                ErrorModel.API_REQUIRE_LOGIN
                -> backToLogin(fragment)

                ErrorModel.API_EXPIRED -> {
                  backToLogin(fragment)
                }
              }
            }
          }
        })
      }
    }
  }

  private fun clearToken() {
    appSettingsRepositoryInterface.apply {
      clearAccessToken()
    }
  }

  private fun backToLogin(fragment: Fragment) {
    clearToken()
    // open Login
  }
}
