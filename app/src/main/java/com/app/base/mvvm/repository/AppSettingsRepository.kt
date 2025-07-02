package com.app.base.mvvm.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

@Suppress("SameParameterValue", "unused")
class AppSettingsRepository(private val context: Context) :
  AppSettingsRepositoryInterface {
  @Keep
  enum class Key {
    ACCESS_TOKEN,
    THEME_MODE,
    CAN_REQUEST_AD,
    PERSONALIZED,
    FCM_TOKEN
  }

  private val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
  private val mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

  private val prefs: SharedPreferences by lazy {
//        context.getSharedPreferences(context.packageName, AppCompatActivity.MODE_PRIVATE)
    EncryptedSharedPreferences.create(
      context.packageName,
      mainKeyAlias,
      context,
      EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
      EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
  }

  private fun saveString(key: Key, value: String?) {
    prefs.edit {
      putString(key.name, value)
    }
  }

  private fun loadString(key: Key, defaultValue: String?): String? {
    return prefs.getString(key.name, defaultValue)
  }

  private fun loadInt(key: Key, defaultValue: Int): Int {
    return prefs.getInt(key.name, defaultValue)
  }

  private fun saveInt(key: Key, value: Int) {
    prefs.edit { putInt(key.name, value) }
  }

  private fun loadLong(key: Key, defaultValue: Long): Long {
    return prefs.getLong(key.name, defaultValue)
  }

  private fun saveLong(key: Key, value: Long) {
    prefs.edit { putLong(key.name, value) }
  }

  private fun loadBoolean(key: Key, defaultValue: Boolean): Boolean {
    return prefs.getBoolean(key.name, defaultValue)
  }

  private fun saveBoolean(key: Key, value: Boolean) {
    prefs.edit { putBoolean(key.name, value) }
  }

  private fun contains(key: Key): Boolean {
    return prefs.contains(key.name)
  }

  private fun remove(key: Key) {
    prefs.edit {
      remove(key.name)
    }
  }

  override fun pullAccessToken(): String? {
    return loadString(Key.ACCESS_TOKEN, null)
  }

  override fun pushAccessToken(token: String?) {
    saveString(Key.ACCESS_TOKEN, token)
  }

  override fun clearAccessToken() {
    saveString(Key.ACCESS_TOKEN, null)
  }

  override fun pullThemeMode(): Int {
    return loadInt(Key.THEME_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
  }

  override fun pushThemeMode(mode: Int) {
    saveInt(Key.THEME_MODE, mode)
  }

  override fun pushCanRequestAd(value: Boolean) {
    saveBoolean(Key.CAN_REQUEST_AD, value)
  }

  override fun pullCanRequestAd(): Boolean {
    return loadBoolean(Key.CAN_REQUEST_AD, true)
  }

  override fun pushPersonalized(value: Boolean) {
    saveBoolean(Key.PERSONALIZED, true)
  }

  override fun pullPersonalized(): Boolean {
    return loadBoolean(Key.PERSONALIZED, true)
  }

  override fun pullFcmToken(): String {
    return loadString(Key.FCM_TOKEN, "") ?: ""
  }

  override fun pushFcmToken(token: String) {
    saveString(Key.FCM_TOKEN, token)
  }

  override fun clearAll() {
    clearAccessToken()
  }
}
