package com.ecocollect.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecocollect.app.data.model.PickupSchedule
import com.ecocollect.app.data.repository.ScheduleRepository
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {

    private val _schedules = MutableStateFlow<List<PickupSchedule>>(emptyList())
    val schedules: StateFlow<List<PickupSchedule>> = _schedules

    init {
        loadSchedules()
    }

    private fun loadSchedules() {
        viewModelScope.launch {
            scheduleRepository.getSchedules()
                .onSuccess { fetchedSchedules ->
                    _schedules.value = fetchedSchedules
                }
                .onFailure {
                    // TODO: Handle error
                }
        }
    }

    fun addSchedule(
        address: String,
        notes: String?,
        items: List<String>
    ) {
        viewModelScope.launch {
            val schedule = PickupSchedule(
                address = address,
                notes = notes,
                items = items
            )
            scheduleRepository.addSchedule(schedule)
                .onSuccess {
                    loadSchedules()
                }
                .onFailure {
                    // TODO: Handle error
                }
        }
    }
}
