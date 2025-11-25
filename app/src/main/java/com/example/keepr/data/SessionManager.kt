package com.example.keepr.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private val Context.dataStore by preferencesDataStore(name = "session_prefs")

class SessionManager(private val context: Context) {

    private val KEY_USER_ID = longPreferencesKey("logged_user_id")

    val loggedInUserId: Flow<Long?> =
        context.dataStore.data.map { prefs -> prefs[KEY_USER_ID] }

    suspend fun setLoggedIn(userId: Long) {
        context.dataStore.edit { it[KEY_USER_ID] = userId }
    }

    suspend fun clear() {
        context.dataStore.edit { it.remove(KEY_USER_ID) }
    }
}
