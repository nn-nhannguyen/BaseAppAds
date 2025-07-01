package com.app.base.mvvm.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
@JsonClass(generateAdapter = true)
data class UserResponse(
  @Json(name = "id")
  val id: Int = 0,
  @Json(name = "name")
  val name: String,
  @Json(name = "email")
  val email: String,
  @Json(name = "avatar")
  val avatar: String = ""
)
