package com.example.keepr.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.keepr.data.CollectionEntity
import com.example.keepr.data.CollectionWithCount
import com.example.keepr.data.KeeprDatabase
import com.example.keepr.data.SessionManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class AddResult {
    data class Success(val id: Long) : AddResult()
    object Duplicate : AddResult()
    object NoUser : AddResult()
}

class CollectionsViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = KeeprDatabase.get(app).collectionsDao()
    private val sessionManager = SessionManager(app)


    val collections: StateFlow<List<CollectionWithCount>> =
        sessionManager.loggedInUserId
            .filterNotNull()
            .flatMapLatest { userId -> dao.observeWithCountForUser(userId) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    suspend fun addCollection(title: String): AddResult {
        val uid = sessionManager.loggedInUserId.firstOrNull() ?: return AddResult.NoUser


        if (dao.existsTitleForUser(uid, title)) return AddResult.Duplicate

        val id = dao.insert(
            CollectionEntity(
                title = title,
                userId = uid
            )
        )
        return if (id == -1L) AddResult.Duplicate else AddResult.Success(id)
    }

    fun deleteCollection(collectionId: Long) {
        viewModelScope.launch { dao.deleteById(collectionId) }

    }

}
