package com.example.keepr.data

import androidx.room.*

@Entity(
    tableName = "profile",
    primaryKeys = ["user_id"],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("user_id")]
)
data class ProfileEntity(
    @ColumnInfo(name = "user_id") val userId: Long,
    @ColumnInfo(name = "first_name") val firstName: String? = null,
    @ColumnInfo(name = "last_name") val lastName: String? = null,
    @ColumnInfo(name = "avatar_uri") val avatarUri: String? = null
)
