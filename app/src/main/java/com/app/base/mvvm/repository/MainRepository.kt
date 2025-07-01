package com.app.base.mvvm.repository

import com.app.base.mvvm.arch.extensions.FlowResult
import com.app.base.mvvm.arch.extensions.safeFlow
import com.app.base.mvvm.model.DataResponse
import com.app.base.mvvm.model.User
import com.app.base.mvvm.network.api.ApiHelper
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class MainRepository @Inject constructor(private val apiHelper: ApiHelper) {
  suspend fun getUsers() = apiHelper.getUsers()

  fun getUsersSafe(): Flow<FlowResult<List<User>>> = safeFlow {
    apiHelper.getUsersSafe()
  }

  suspend fun getUserLists() = apiHelper.getUserLists()

  fun login(userName: String, password: String): Flow<FlowResult<DataResponse<Any?>>> = safeFlow {
    apiHelper.login(userName, password)
  }
}
