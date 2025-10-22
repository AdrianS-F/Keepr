package com.example.keepr.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ItemsViewModelFactory(
    private val app: Application,
    private val collectionId: Long
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ItemsViewModel(app, collectionId) as T
    }
}
