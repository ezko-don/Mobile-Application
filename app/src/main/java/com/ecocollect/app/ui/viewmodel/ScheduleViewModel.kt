package com.ecocollect.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocollect.app.data.model.PickupSchedule
import com.ecocollect.app.data.repository.ScheduleRepository
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {

    private val _schedules = MutableStateFlow<List<PickupSchedule>>(emptyList())
    val schedules: StateFlow<List<PickupSchedule>> = _schedules

    private val _uiState = MutableStateFlow<ScheduleUiState>(ScheduleUiState.Idle)
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadSchedules()
    }

    private fun loadSchedules() {
        _uiState.value = ScheduleUiState.Loading
        viewModelScope.launch {
            scheduleRepository.getSchedules()
                .onSuccess { fetchedSchedules ->
                    _schedules.value = fetchedSchedules
                    _uiState.value = ScheduleUiState.Success
                }
                .onFailure { exception ->
                    _errorMessage.value = getErrorMessage(exception)
                    _uiState.value = ScheduleUiState.Error
                }
        }
    }

    fun addSchedule(
        address: String,
        notes: String?,
        items: List<String>
    ) {
        if (address.isBlank()) {
            _errorMessage.value = "Please enter a pickup address"
            return
        }
        
        if (items.isEmpty()) {
            _errorMessage.value = "Please select at least one e-waste item"
            return
        }

        _uiState.value = ScheduleUiState.Loading
        viewModelScope.launch {
            val schedule = PickupSchedule(
                address = address,
                notes = notes,
                items = items
            )
            scheduleRepository.addSchedule(schedule)
                .onSuccess {
                    loadSchedules()
                    _uiState.value = ScheduleUiState.Success
                }
                .onFailure { exception ->
                    _errorMessage.value = getErrorMessage(exception)
                    _uiState.value = ScheduleUiState.Error
                }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    private fun getErrorMessage(exception: Throwable): String {
        return when {
            exception.message?.contains("network") == true -> "Network error. Please check your connection."
            exception.message?.contains("timeout") == true -> "Request timed out. Please try again."
            exception.message?.contains("permission") == true -> "Permission denied. Please check your account."
            else -> "An error occurred. Please try again."
        }
    }
}

sealed class ScheduleUiState {
    object Idle : ScheduleUiState()
    object Loading : ScheduleUiState()
    object Success : ScheduleUiState()
    object Error : ScheduleUiState()
}
