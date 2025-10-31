package com.example.keepr.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profile WHERE user_id = :userId")
    fun observe(userId: Long): Flow<ProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(profile: ProfileEntity)

    @Query("UPDATE profile SET avatar_uri = :uri WHERE user_id = :userId")
    suspend fun updateAvatar(userId: Long, uri: String?)

    @Query("DELETE FROM profile WHERE user_id = :userId")
    suspend fun deleteByUser(userId: Long)

    @Query("SELECT * FROM profile WHERE user_id = :userId")
    suspend fun getOnce(userId: Long): ProfileEntity?

}
