package com.example.keepr.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UsersDao {
    @Insert suspend fun insert(user: UserEntity): Long

    @Query("SELECT * FROM user WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): UserEntity?

    @Query("SELECT * FROM user WHERE user_id = :id LIMIT 1")
    suspend fun getById(id: Long): UserEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM user WHERE email = :email)")
    suspend fun emailExists(email: String): Boolean

    @Delete suspend fun delete(user: UserEntity)
}
