package com.app.base.mvvm.data.local

import com.app.base.mvvm.model.UserDB
import javax.inject.Inject

class AppDbHelper @Inject constructor(private val mAppDatabase: AppDatabase) : DbHelper {

  override fun loadAll(): List<UserDB>? {
    return mAppDatabase.userDao().loadAll()
  }

  override fun insertWithCoroutine(user: UserDB): Long? {
    return mAppDatabase.userDao().insertWithCoroutine(user)
  }
}
