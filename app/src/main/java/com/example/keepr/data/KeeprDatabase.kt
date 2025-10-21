package com.example.keepr.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        CollectionEntity::class,
        ItemEntity::class,
        ProfileEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class KeeprDatabase : RoomDatabase() {
    abstract fun usersDao(): UsersDao
    abstract fun collectionsDao(): CollectionsDao
    abstract fun itemsDao(): ItemsDao
    abstract fun profileDao(): ProfileDao

    companion object {
        @Volatile private var INSTANCE: KeeprDatabase? = null

        fun get(context: Context): KeeprDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, KeeprDatabase::class.java, "keepr.db")
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
