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

class AddViewModel(application: Application) : AndroidViewModel(application) {

    // Henter database og DAO-er
    private val db = KeeprDatabase.Companion.get(application)
    private val collectionsDao = db.collectionsDao()
    private val itemsDao = db.itemsDao()
    private val sessionManager = SessionManager(application)

    // Bruker Flow for å observere collections
    private val _collections = MutableStateFlow<List<CollectionEntity>>(emptyList())
    val collections: StateFlow<List<CollectionEntity>> = _collections.asStateFlow()

    private val _userId = MutableStateFlow<Long?>(null)

    init {
        // Starter å lytte etter endringer i databasen (collections)
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

    // 👉 Legger til en ny collection i databasen
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

    // 👉 Legger til et nytt item i databasen
    fun addItem(collectionId: Long, itemName: String, description: String? = null, imgUri: String? = null) {
        viewModelScope.launch {
            val newItem = ItemEntity(
                collectionId = collectionId,
                itemName = itemName,
                notes = description,
                imgUri = imgUri
            )
            itemsDao.insert(newItem)
        }
    }
}