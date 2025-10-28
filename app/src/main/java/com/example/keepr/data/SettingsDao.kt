package com.example.keepr.data

import androidx.room.*

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE id = 1")
    fun getSettings(): SettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(settings: SettingsEntity)

    @Update
    fun update(settings: SettingsEntity)

}