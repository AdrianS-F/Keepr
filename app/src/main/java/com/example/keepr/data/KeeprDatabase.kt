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
        ProfileEntity::class,
        SettingsEntity::class

    ],
    version = 2,
    exportSchema = false
)
abstract class KeeprDatabase : RoomDatabase() {
    abstract fun usersDao(): UsersDao
    abstract fun collectionsDao(): CollectionsDao
    abstract fun itemsDao(): ItemsDao
    abstract fun profileDao(): ProfileDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile private var INSTANCE: KeeprDatabase? = null

        fun get(context: Context): KeeprDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, KeeprDatabase::class.java, "keepr.db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
