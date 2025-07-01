package com.app.base.mvvm.network.api

import com.app.base.mvvm.arch.extensions.apiCall
import com.app.base.mvvm.model.DataResponse
import com.app.base.mvvm.model.User
import javax.inject.Inject
import retrofit2.Response

class ApiHelperImpl @Inject constructor(private val apiService: ApiService) : ApiHelper {

  override suspend fun getUsers(): Response<List<User>> = apiService.getUsers()

  override suspend fun getUserLists(): List<User> = apiService.getUserLists()

  override suspend fun getUsersSafe(): List<User> = apiCall {
    apiService.getUsers()
  }

  override suspend fun login(userName: String, password: String): DataResponse<Any?> = apiCall {
    apiService.login(userName, password)
  }
}
