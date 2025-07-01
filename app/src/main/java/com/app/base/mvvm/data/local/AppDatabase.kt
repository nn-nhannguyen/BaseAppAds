package com.app.base.mvvm.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.base.mvvm.model.UserDB
import com.app.base.mvvm.model.UserDao

@Database(entities = [UserDB::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
  abstract fun userDao(): UserDao
}
