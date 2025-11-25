package com.example.keepr.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.keepr.data.CollectionEntity
import com.example.keepr.data.ItemEntity
import com.example.keepr.data.KeeprDatabase
import com.example.keepr.data.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


sealed class AddItemResult {
    data class Success(val id: Long) : AddItemResult()
    object Duplicate : AddItemResult()
}

class AddViewModel(application: Application) : AndroidViewModel(application) {

    private val db = KeeprDatabase.get(application)
    private val collectionsDao = db.collectionsDao()
    private val itemsDao = db.itemsDao()
    private val sessionManager = SessionManager(application)

    private val _collections = MutableStateFlow<List<CollectionEntity>>(emptyList())
    val collections: StateFlow<List<CollectionEntity>> = _collections.asStateFlow()

    private val _userId = MutableStateFlow<Long?>(null)

    init {
        viewModelScope.launch {
            sessionManager.loggedInUserId.collect { userId ->
                if (userId != null) {
                    _userId.value = userId
                    collectionsDao.observeForUser(userId).collect { list ->
                        _collections.value = list
                    }
                }
            }
        }
    }

    fun addCollection(title: String, description: String = "") {
        viewModelScope.launch {
            _userId.value?.let { userId ->
                val newCollection = CollectionEntity(
                    userId = userId,
                    title = title
                )
                collectionsDao.insert(newCollection)
            }
        }
    }

    suspend fun addItem(
        collectionId: Long,
        itemName: String,
        description: String? = null,
        imgUri: String? = null
    ): AddItemResult {
        val trimmedName = itemName.trim()
        if (trimmedName.isEmpty()) {
            return AddItemResult.Duplicate
        }

        if (itemsDao.existsNameInCollection(collectionId, trimmedName)) {
            return AddItemResult.Duplicate
        }

        val id = itemsDao.insert(
            ItemEntity(
                collectionId = collectionId,
                itemName = trimmedName,
                notes = description,
                imgUri = imgUri
            )
        )

        return if (id == -1L) {
            AddItemResult.Duplicate
        } else {
            AddItemResult.Success(id)
        }
    }
}
