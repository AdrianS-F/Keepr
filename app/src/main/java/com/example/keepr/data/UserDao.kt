package com.example.keepr.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow



@Dao
interface UsersDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity): Long

    @Query("SELECT * FROM `user` WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): UserEntity?

    @Query("SELECT * FROM `user` WHERE user_id = :id LIMIT 1")
    suspend fun getById(id: Long): UserEntity?

    // Live-observasjon
    @Query("SELECT * FROM `user` WHERE user_id = :id LIMIT 1")
    fun observeById(id: Long): Flow<UserEntity?>

    @Query("SELECT EXISTS(SELECT 1 FROM `user` WHERE email = :email)")
    suspend fun emailExists(email: String): Boolean

    @Update
    suspend fun update(user: UserEntity)

    @Query("UPDATE `user` SET first_name = :first, last_name = :last WHERE user_id = :userId")
    suspend fun updateName(userId: Long, first: String, last: String)


    @Delete
    suspend fun delete(user: UserEntity)

    @Query("DELETE FROM `user` WHERE user_id = :id")
    suspend fun deleteById(id: Long)

}
