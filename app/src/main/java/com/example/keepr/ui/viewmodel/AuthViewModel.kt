package com.example.keepr.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.keepr.data.AuthRepository
import com.example.keepr.data.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val repeatPassword: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val loading: Boolean = false,
    val error: String? = null
)

class AuthViewModel(
    private val repo: AuthRepository,
    private val session: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state

    // updaters
    fun updateEmail(v: String) { _state.value = _state.value.copy(email = v) }
    fun updatePassword(v: String) { _state.value = _state.value.copy(password = v) }
    fun updateRepeat(v: String) { _state.value = _state.value.copy(repeatPassword = v) }
    fun updateFirst(v: String) { _state.value = _state.value.copy(firstName = v) }
    fun updateLast(v: String) { _state.value = _state.value.copy(lastName = v) }

    fun resetState() {
        _state.value = AuthUiState()
    }

    fun signUp(onSuccess: () -> Unit) = viewModelScope.launch {
        val s = _state.value
        if (s.password != s.repeatPassword) {
            _state.value = s.copy(error = "PASSWORDS_MISMATCH")
            return@launch
        }

        val hasUppercase = s.password.any { it.isUpperCase() }
        val hasDigit = s.password.any { it.isDigit() }
        if (!hasUppercase || !hasDigit) {
            _state.value = s.copy(error = "PASSWORD_WEAK")
            return@launch
        }

        _state.value = s.copy(loading = true, error = null)
        val res = repo.signUp(s.email, s.firstName, s.lastName, s.password)
        _state.value = _state.value.copy(loading = false)
        res.onSuccess { userId ->
            session.setLoggedIn(userId)   // 🔗 KOBLINGEN: marker innlogget
            onSuccess()
        }.onFailure { e ->
            _state.value = _state.value.copy(error = "GENERIC_ERROR")
        }
    }

    fun signIn(onSuccess: () -> Unit) = viewModelScope.launch {
        val s = _state.value
        _state.value = s.copy(loading = true, error = null)
        val res = repo.signIn(s.email, s.password)
        _state.value = _state.value.copy(loading = false)
        res.onSuccess { user ->
            session.setLoggedIn(user.userId) // 🔗 KOBLINGEN
            onSuccess()
        }.onFailure { e ->
            _state.value = _state.value.copy(error = "WRONG_CREDENTIALS")
        }
    }
}