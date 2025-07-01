package com.app.base.mvvm.network.api

import com.app.base.mvvm.model.DataResponse
import com.app.base.mvvm.model.User
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

  @GET("users")
  suspend fun getUsers(): Response<List<User>>

  @GET("users")
  suspend fun getUserLists(): List<User>

  @GET("login")
  suspend fun login(userName: String, pass: String): Response<DataResponse<Any?>>
}
