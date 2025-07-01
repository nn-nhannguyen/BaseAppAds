package com.app.base.mvvm.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DataResponse<T>(
  @Json(name = "code")
  val status: Int,
  @Json(name = "message")
  val message: String?,
  val data: T? = null
)
