package com.ecocollect.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocollect.app.data.model.User
import com.ecocollect.app.data.repository.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    init {
        checkAuthState()
    }
    
    private fun checkAuthState() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        
        if (authRepository.isUserLoggedIn()) {
            // User is logged in, navigate to main app
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isAuthenticated = true
            )
        } else {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isAuthenticated = false
            )
        }
    }
    
    fun signInWithGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            authRepository.signInWithGoogle(account)
                .onSuccess { user ->
                    _currentUser.value = user
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = true
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Google sign-in failed"
                    )
                }
        }
    }
    
    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            authRepository.signInWithEmail(email, password)
                .onSuccess { user ->
                    _currentUser.value = user
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = true
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Email sign-in failed"
                    )
                }
        }
    }
    
    fun signUpWithEmail(email: String, password: String, name: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            authRepository.signUpWithEmail(email, password, name)
                .onSuccess { user ->
                    _currentUser.value = user
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = true
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Sign up failed"
                    )
                }
        }
    }
    
    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _currentUser.value = null
            _uiState.value = _uiState.value.copy(
                isAuthenticated = false,
                errorMessage = null
            )
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val errorMessage: String? = null
)
