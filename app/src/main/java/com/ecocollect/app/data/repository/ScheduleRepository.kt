package com.ecocollect.app.data.repository

import com.ecocollect.app.data.model.PickupSchedule

interface ScheduleRepository {
    suspend fun getSchedules(): Result<List<PickupSchedule>>
    suspend fun addSchedule(schedule: PickupSchedule): Result<Unit>
}
