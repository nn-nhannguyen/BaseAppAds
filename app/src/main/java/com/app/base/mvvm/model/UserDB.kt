package com.app.base.mvvm.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "users")
class UserDB {
  @JvmField
  @ColumnInfo(name = "created_at")
  var createdAt: String? = null

  @JvmField
  @PrimaryKey
  var id: Long? = null

  @JvmField
  var name: String? = null

  @JvmField
  var password: String? = null

  @JvmField
  var avatar: String? = null

  @JvmField
  var email: String? = null

  @JvmField
  @ColumnInfo(name = "updated_at")
  var updatedAt: String? = null
}
