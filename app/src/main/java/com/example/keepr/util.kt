package com.example.keepr.util

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.keepr.data.KeeprDatabase
import java.io.File

object DatabaseUtils {

    /**
     * Opens the Room database (forcing it to create if needed)
     * and logs the actual file path and existence.
     */
    fun verifyDatabase(context: Context) {
        val db = Room.databaseBuilder(
            context.applicationContext,
            KeeprDatabase::class.java,
            "keepr.db"
        ).build()

        // Open it by reading the underlying SQLite database once
        val openHelper = db.openHelper
        val path = openHelper.readableDatabase.path

        val file = File(path)
        Log.d("KeeprDB", "Database path: $path")
        Log.d("KeeprDB", "Database exists: ${file.exists()} (size=${file.length()} bytes)")
    }
}
