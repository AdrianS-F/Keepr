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
import com.example.keepr.data.SessionManager
import kotlinx.coroutines.flow.*

import kotlinx.coroutines.launch

class CollectionsViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = KeeprDatabase.get(app).collectionsDao()
    private val sessionManager = SessionManager(app)

    val collections: StateFlow<List<CollectionWithCount>> =
        sessionManager.loggedInUserId
            .filterNotNull()
            .flatMapLatest { userId ->
                dao.observeWithCountForUser(userId)
            }
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
