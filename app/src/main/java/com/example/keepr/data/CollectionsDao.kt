package com.example.keepr.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow



data class CollectionWithCount(
    @Embedded val collection: CollectionEntity,
    @ColumnInfo(name = "item_count") val itemCount: Int
)
@Dao
interface CollectionsDao {
    @Query("SELECT * FROM collections WHERE user_id = :userId ORDER BY created_at DESC")
    fun observeForUser(userId: Long): Flow<List<CollectionEntity>>

    @Query("""
        SELECT c.*, COUNT(i.item_id) AS item_count
        FROM collections AS c
        LEFT JOIN items AS i ON i.collection_id = c.collection_id
        WHERE c.user_id = :userId
        GROUP BY c.collection_id
        ORDER BY c.created_at DESC
    """)
    fun observeWithCountForUser(userId: Long): Flow<List<CollectionWithCount>>
    @Transaction
    @Query("SELECT * FROM collections WHERE collection_id = :collectionId")
    fun observeWithItems(collectionId: Long): Flow<CollectionWithItems?>

    @Insert suspend fun insert(collection: CollectionEntity): Long
    @Update suspend fun update(collection: CollectionEntity)
    @Delete suspend fun delete(collection: CollectionEntity)

    @Query("DELETE FROM collections WHERE collection_id = :id")
    suspend fun deleteById(id: Long)
}
