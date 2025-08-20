package com.ecocollect.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocollect.app.data.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {
    
    private val _profileState = MutableStateFlow(ProfileUiState())
    val profileState: StateFlow<ProfileUiState> = _profileState.asStateFlow()
    
    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile.asStateFlow()
    
    init {
        loadMockUserProfile() // Replace with actual user data loading
    }
    
    fun loadUserProfile() {
        viewModelScope.launch {
            _profileState.value = _profileState.value.copy(isLoading = true)
            
            try {
                // In a real app, you would load user data from your repository
                // For now, we'll use mock data
                loadMockUserProfile()
                
                _profileState.value = _profileState.value.copy(
                    isLoading = false,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _profileState.value = _profileState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load profile"
                )
            }
        }
    }
    
    fun updateUserProfile(updatedUser: User) {
        viewModelScope.launch {
            _profileState.value = _profileState.value.copy(isLoading = true)
            
            try {
                // In a real app, you would update user data in your repository
                // For now, we'll just update the local state
                _userProfile.value = updatedUser
                
                _profileState.value = _profileState.value.copy(
                    isLoading = false,
                    editMode = false,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _profileState.value = _profileState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to update profile"
                )
            }
        }
    }
    
    fun updateProfileFields(name: String, phone: String, address: String) {
        viewModelScope.launch {
            _profileState.value = _profileState.value.copy(isLoading = true)
            
            try {
                val currentUser = _userProfile.value
                if (currentUser != null) {
                    val updatedUser = currentUser.copy(
                        name = name,
                        phoneNumber = phone,
                        address = address
                    )
                    
                    // In a real app, you would save to your repository
                    _userProfile.value = updatedUser
                    
                    _profileState.value = _profileState.value.copy(
                        isLoading = false,
                        editMode = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _profileState.value = _profileState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to update profile"
                )
            }
        }
    }
    
    fun changePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            _profileState.value = _profileState.value.copy(isLoading = true)
            
            try {
                // In a real app, you would validate and change the password
                // through your authentication service
                
                // Simulate password change
                kotlinx.coroutines.delay(1000)
                
                _profileState.value = _profileState.value.copy(
                    isLoading = false,
                    showChangePasswordDialog = false,
                    errorMessage = null
                )
                
                // You could also show a success message here
                
            } catch (e: Exception) {
                _profileState.value = _profileState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to change password"
                )
            }
        }
    }
    
    fun toggleEditMode() {
        _profileState.value = _profileState.value.copy(
            editMode = !_profileState.value.editMode
        )
    }
    
    fun showChangePasswordDialog() {
        _profileState.value = _profileState.value.copy(
            showChangePasswordDialog = true
        )
    }
    
    fun hideChangePasswordDialog() {
        _profileState.value = _profileState.value.copy(
            showChangePasswordDialog = false
        )
    }
    
    fun clearError() {
        _profileState.value = _profileState.value.copy(errorMessage = null)
    }
    
    fun updateUserStats(points: Int, co2Saved: Double) {
        viewModelScope.launch {
            try {
                val currentUser = _userProfile.value
                if (currentUser != null) {
                    val updatedUser = currentUser.copy(
                        totalPoints = points,
                        co2SavedKg = co2Saved
                    )
                    _userProfile.value = updatedUser
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun getProfileStatistics(): Map<String, Any> {
        val user = _userProfile.value
        return mapOf(
            "totalPoints" to (user?.totalPoints ?: 0),
            "co2SavedKg" to (user?.co2SavedKg ?: 0.0),
            "memberSince" to (user?.createdAt?.toString() ?: "Unknown"),
            "isActive" to (user?.isActive ?: false)
        )
    }
    
    private fun loadMockUserProfile() {
        // Mock user data for demonstration
        // Replace this with actual user data loading from your repository
        val mockUser = User(
            uid = "mock_user_123",
            name = "John Doe",
            email = "john.doe@example.com",
            totalPoints = 1250,
            co2SavedKg = 45.7,
            phoneNumber = "+1 234 567 8900",
            address = "123 Eco Street, Green City, EC 12345",
            isActive = true
        )
        
        _userProfile.value = mockUser
    }
}

data class ProfileUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val editMode: Boolean = false,
    val showChangePasswordDialog: Boolean = false,
    val showSuccessMessage: Boolean = false,
    val successMessage: String = ""
)
