package com.example.keepr.data

import androidx.room.*

@Entity(
    tableName = "collections",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("user_id"), Index(value = ["user_id", "title"], unique = true)]
)
data class CollectionEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "collection_id") val collectionId: Long = 0,
    @ColumnInfo(name = "user_id") val userId: Long,
    val title: String,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)
