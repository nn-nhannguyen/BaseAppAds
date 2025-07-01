package com.app.base.mvvm.repository

import com.app.base.mvvm.data.local.DbHelper
import com.app.base.mvvm.model.UserDB
import javax.inject.Inject

class RoomRepository @Inject constructor(private val dbHelper: DbHelper) {
  fun getAllUsers() = dbHelper.loadAll()
  fun insertUser(userDB: UserDB) = dbHelper.insertWithCoroutine(userDB)
}
