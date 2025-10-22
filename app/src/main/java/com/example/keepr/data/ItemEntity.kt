package com.example.keepr.data

import androidx.room.*

@Entity(
    tableName = "items",
    foreignKeys = [
        ForeignKey(
            entity = CollectionEntity::class,
            parentColumns = ["collection_id"],
            childColumns = ["collection_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("collection_id"),
        Index(value = ["collection_id", "item_name"], unique = true),
        Index("item_name")
    ]
)
data class ItemEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "item_id") val itemId: Long = 0,
    @ColumnInfo(name = "collection_id") val collectionId: Long,
    @ColumnInfo(name = "item_name") val itemName: String,
    // Boolean is stored as 0/1 by Room in SQLite
    @ColumnInfo(name = "acquired", defaultValue = "0") val acquired: Boolean = false,
    val notes: String? = null,
    @ColumnInfo(name = "img_uri") val imgUri: String? = null,
    @ColumnInfo(name = "added_at") val addedAt: Long = System.currentTimeMillis()
)
