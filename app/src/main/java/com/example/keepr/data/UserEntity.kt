package com.example.keepr.data

import androidx.room.*

@Entity(
    tableName = "user",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "user_id") val userId: Long = 0,
    val email: String,
    @ColumnInfo(name = "first_name") val firstName: String,
    @ColumnInfo(name = "last_name") val lastName: String,
    // Store a HASH here, not plain text. Name kept as "password" to match your diagram.
    val password: String,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)
