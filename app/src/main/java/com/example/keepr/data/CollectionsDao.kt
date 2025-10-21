package com.example.keepr.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionsDao {
    @Query("SELECT * FROM collections WHERE user_id = :userId ORDER BY created_at DESC")
    fun observeForUser(userId: Long): Flow<List<CollectionEntity>>

    @Transaction
    @Query("SELECT * FROM collections WHERE collection_id = :collectionId")
    fun observeWithItems(collectionId: Long): Flow<CollectionWithItems?>

    @Insert suspend fun insert(collection: CollectionEntity): Long
    @Update suspend fun update(collection: CollectionEntity)
    @Delete suspend fun delete(collection: CollectionEntity)
}
