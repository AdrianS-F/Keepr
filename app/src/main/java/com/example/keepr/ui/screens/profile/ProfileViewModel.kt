package com.example.keepr.ui.screens.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.keepr.data.KeeprDatabase
import com.example.keepr.data.SessionManager
import com.example.keepr.data.UserEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.firstOrNull

data class ProfileUiState(
    val user: UserEntity? = null,
    val collectionCount: Int = 0
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val db = KeeprDatabase.get(application)
    private val usersDao = db.usersDao()
    private val collectionsDao = db.collectionsDao()
    private val sessionManager = SessionManager(application)

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            sessionManager.loggedInUserId
                .filterNotNull()
                .flatMapLatest { userId ->
                    combine(
                        usersDao.observeById(userId),
                        collectionsDao.observeForUser(userId)
                    ) { user, collections ->
                        ProfileUiState(user, collections.size)
                    }
                }
                .collect { _state.value = it }
        }
    }

    fun updateName(first: String, last: String) {
        viewModelScope.launch {
            val userId = sessionManager.loggedInUserId.firstOrNull() ?: return@launch

            val f = first.trim()
            val l = last.trim()
            if (f.isEmpty() || l.isEmpty()) return@launch

            usersDao.updateName(userId, f, l)
        }
    }
}