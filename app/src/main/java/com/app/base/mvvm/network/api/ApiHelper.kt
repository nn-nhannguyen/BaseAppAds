package com.app.base.mvvm.network.api

import com.app.base.mvvm.model.DataResponse
import com.app.base.mvvm.model.User
import retrofit2.Response

interface ApiHelper {

  suspend fun getUsers(): Response<List<User>>

  suspend fun getUserLists(): List<User>

  suspend fun getUsersSafe(): List<User>
  suspend fun login(userName: String, password: String): DataResponse<Any?>
}
