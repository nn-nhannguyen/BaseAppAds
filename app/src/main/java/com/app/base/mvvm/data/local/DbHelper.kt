package com.app.base.mvvm.data.local

import com.app.base.mvvm.model.UserDB

interface DbHelper {

  fun loadAll(): List<UserDB>?

  fun insertWithCoroutine(user: UserDB): Long?
}
