package com.example.keepr.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.keepr.data.KeeprDatabase
import com.example.keepr.data.CollectionWithCount
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
}
