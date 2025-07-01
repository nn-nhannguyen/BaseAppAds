package com.app.base.mvvm.repository

interface AppSettingsRepositoryInterface {
  fun pullAccessToken(): String?
  fun pushAccessToken(token: String?)
  fun clearAccessToken()

  fun pullThemeMode(): Int
  fun pushThemeMode(mode: Int)

  fun clearAll()
}
