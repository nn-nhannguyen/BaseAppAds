package com.app.base.mvvm.model

import androidx.room.*
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface UserDao {
  @Delete
  fun delete(user: UserDB)

  @Query("SELECT * FROM users WHERE name LIKE :name LIMIT 1")
  fun findByName(name: String?): Single<UserDB?>?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(user: UserDB)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertAll(users: List<UserDB>)

  @Query("SELECT * FROM users")
  fun loadAll(): List<UserDB>?

  @Query("SELECT * FROM users")
  fun loadAllObservable(): Observable<List<UserDB?>>

  @Query("SELECT * FROM users WHERE id IN (:userIds)")
  fun loadAllByIds(userIds: List<Int?>?): List<UserDB?>?

  @Query("SELECT * FROM users WHERE name LIKE :name LIMIT 1")
  fun loadAllByName(name: String?): Observable<List<UserDB?>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertWithCoroutine(user: UserDB): Long?
}
