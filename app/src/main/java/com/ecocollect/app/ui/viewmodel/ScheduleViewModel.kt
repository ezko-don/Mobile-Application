package com.ecocollect.app.ui.viewmodel

import android.app.Application
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocollect.app.data.model.PickupSchedule
import com.ecocollect.app.data.repository.ScheduleRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val auth: FirebaseAuth,
    private val application: Application
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _upcomingSchedules = MutableStateFlow<List<PickupSchedule>>(emptyList())
    val upcomingSchedules: StateFlow<List<PickupSchedule>> = _upcomingSchedules

    private val _historySchedules = MutableStateFlow<List<PickupSchedule>>(emptyList())
    val historySchedules: StateFlow<List<PickupSchedule>> = _historySchedules

    private val _selectedSchedule = MutableStateFlow<PickupSchedule?>(null)
    val selectedSchedule: StateFlow<PickupSchedule?> = _selectedSchedule

    private val _uiState = MutableStateFlow<ScheduleUiState>(ScheduleUiState.Idle)
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    private val _errorFlow = MutableSharedFlow<String>()
    val errorFlow = _errorFlow.asSharedFlow()

    private val _scheduleAdded = MutableSharedFlow<Unit>()
    val scheduleAdded = _scheduleAdded.asSharedFlow()

    init {
        loadSchedules()
    }

    private fun loadSchedules() {
        viewModelScope.launch {
            _isLoading.value = true
            scheduleRepository.getSchedules().collect { result ->
                result.onSuccess {
                    val (history, upcoming) = it.partition { s -> s.status == "Completed" }
                    _upcomingSchedules.value = upcoming
                    _historySchedules.value = history
                }.onFailure {
                    _errorFlow.emit("Failed to load schedules.")
                }
            }
            _isLoading.value = false
        }
    }

    fun addSchedule(address: String, notes: String?, items: List<String>) {
        if (address.isBlank()) {
            _errorFlow.emit("Please enter a pickup address")
            return
        }
        
        if (items.isEmpty()) {
            _errorFlow.emit("Please select at least one e-waste item")
            return
        }

        _uiState.value = ScheduleUiState.Loading
        viewModelScope.launch {
            val geocoder = Geocoder(application)
            var latitude = 0.0
            var longitude = 0.0

            try {
                val addressList = geocoder.getFromLocationName(address, 1)
                if (addressList?.isNotEmpty() == true) {
                    latitude = addressList[0].latitude
                    longitude = addressList[0].longitude
                }
            } catch (e: Exception) {
                // Handle geocoding error, maybe show a message to the user
            }

            val newSchedule = PickupSchedule(
                address = address,
                notes = notes,
                items = items,
                latitude = latitude,
                longitude = longitude
            )
            scheduleRepository.addSchedule(newSchedule)
                .onSuccess {
                    _scheduleAdded.emit(Unit)
                    loadSchedules() // Refresh the list
                    _uiState.value = ScheduleUiState.Success
                }
                .onFailure { exception ->
                    _errorFlow.emit(getErrorMessage(exception))
                    _uiState.value = ScheduleUiState.Error
                }
        }
    }

    fun loadScheduleById(scheduleId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            scheduleRepository.getScheduleById(scheduleId)
                .onSuccess { schedule ->
                    _selectedSchedule.value = schedule
                }
                .onFailure {
                    _errorFlow.emit("Failed to load schedule details.")
                }
            _isLoading.value = false
        }
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
