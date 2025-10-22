package com.example.keepr.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Én DataStore pr. Context – toppnivå extension:
private val Context.dataStore by preferencesDataStore(name = "session_prefs")

class SessionManager(private val context: Context) {

    private val KEY_USER_ID = longPreferencesKey("logged_user_id")

    /** Flow av innlogget bruker-id (eller null hvis utlogget) */
    val loggedInUserId: Flow<Long?> =
        context.dataStore.data.map { prefs -> prefs[KEY_USER_ID] }

    /** Merk en bruker som innlogget */
    suspend fun setLoggedIn(userId: Long) {
        context.dataStore.edit { it[KEY_USER_ID] = userId }
    }

    /** Logg ut / tøm session */
    suspend fun clear() {
        context.dataStore.edit { it.remove(KEY_USER_ID) }
    }
}
