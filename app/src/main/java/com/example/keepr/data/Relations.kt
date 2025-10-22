package com.example.keepr.data

import androidx.room.Embedded
import androidx.room.Relation

data class CollectionWithItems(
    @Embedded val collection: CollectionEntity,
    @Relation(
        parentColumn = "collection_id",
        entityColumn = "collection_id"
    )
    val items: List<ItemEntity>
)

data class UserWithCollections(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "user_id",
        entityColumn = "user_id"
    )
    val collections: List<CollectionEntity>
)
