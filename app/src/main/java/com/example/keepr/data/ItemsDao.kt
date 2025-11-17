package com.example.keepr.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemsDao {
    @Query("SELECT * FROM items WHERE collection_id = :collectionId ORDER BY acquired, item_name")
    fun observeForCollection(collectionId: Long): Flow<List<ItemEntity>>

    @Update suspend fun update(item: ItemEntity)
    @Delete suspend fun delete(item: ItemEntity)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: ItemEntity): Long

    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM items
            WHERE collection_id = :collectionId
              AND LOWER(item_name) = LOWER(:name)
        )
    """)

    suspend fun existsNameInCollection(
        collectionId: Long,
        name: String
    ): Boolean

    @Query("UPDATE items SET acquired = :acquired WHERE item_id = :itemId")
    suspend fun setAcquired(itemId: Long, acquired: Boolean)

    @Query("UPDATE items SET item_name = :name, notes = :notes WHERE item_id = :itemId")
    suspend fun updateItemDetails(itemId: Long, name: String, notes: String?)

    @Query("UPDATE items SET img_uri = :uri WHERE item_id = :itemId")
    suspend fun updateImage(itemId: Long, uri: String?)

}

