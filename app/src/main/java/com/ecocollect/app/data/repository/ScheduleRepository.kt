package com.ecocollect.app.data.repository

import com.ecocollect.app.data.model.PickupSchedule
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {
    fun getSchedules(): Flow<Result<List<PickupSchedule>>>
    suspend fun addSchedule(schedule: PickupSchedule): Result<Unit>
    suspend fun getScheduleById(scheduleId: String): Result<PickupSchedule>
}
