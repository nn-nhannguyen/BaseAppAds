package com.app.base.mvvm.repository

interface AppSettingsRepositoryInterface {
  fun pullAccessToken(): String?
  fun pushAccessToken(token: String?)
  fun clearAccessToken()

  fun pushCanRequestAd(bool: Boolean)
  fun pullCanRequestAd(): Boolean
  fun pushPersonalized(bool: Boolean)
  fun pullPersonalized(): Boolean

  fun pullThemeMode(): Int
  fun pushThemeMode(mode: Int)

  fun clearAll()
  fun pushFcmToken(string: String)
  fun pullFcmToken(): String?
}
