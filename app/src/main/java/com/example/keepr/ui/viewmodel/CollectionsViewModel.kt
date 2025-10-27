package com.example.keepr.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.keepr.data.CollectionEntity
import com.example.keepr.data.KeeprDatabase
import com.example.keepr.data.CollectionWithCount
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CollectionsViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = KeeprDatabase.get(app).collectionsDao()

    // Since we seeded a demo user, just use that (no login yet)
    private val demoUserId = 1L  // NOTE: If your insert auto-generated something else, we’ll still see data via the count query below;
    // but if you want to be exact, you can look up "demo@keepr.app" and cache its ID.

    val collections: StateFlow<List<CollectionWithCount>> =
        dao.observeWithCountForUser(demoUserId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addCollection(title: String) {
        viewModelScope.launch {
            val newCollection = CollectionEntity(
                title = title,
                description = null,
                userId = demoUserId
            )
            dao.insert(newCollection)
        }
    }

    fun deleteCollection(collectionId: Long) {
        viewModelScope.launch {
            dao.deleteById(collectionId)
        }
    }
}
