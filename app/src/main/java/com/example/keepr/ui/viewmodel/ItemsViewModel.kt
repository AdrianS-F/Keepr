package com.example.keepr.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.keepr.data.ItemEntity
import com.example.keepr.data.KeeprDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ItemsViewModel(app: Application, private val collectionId: Long) : AndroidViewModel(app) {
    private val db = KeeprDatabase.get(app)
    private val itemsDao = db.itemsDao()

    private val collectionsDao = db.collectionsDao()

    val items: StateFlow<List<ItemEntity>> =
        itemsDao.observeForCollection(collectionId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun toggleAcquired(item: ItemEntity, newValue: Boolean) = viewModelScope.launch {
        itemsDao.setAcquired(item.itemId, newValue)
    }

    fun addItem(name: String) = viewModelScope.launch {
        if (name.isNotBlank()) {
            itemsDao.insert(ItemEntity(collectionId = collectionId, itemName = name))
        }
    }

    fun delete(item: ItemEntity) = viewModelScope.launch {
        itemsDao.delete(item)
    }

    fun renameCurrentCollection(newTitle: String) {
        viewModelScope.launch {
            collectionsDao.renameCollection(collectionId, newTitle.trim())
        }
    }

    fun updateItem(itemId: Long, name: String, notes: String?) {
        viewModelScope.launch {
            itemsDao.updateItemDetails(itemId, name.trim(), notes?.trim())
        }
    }
}
