package com.ecocollect.app.data.repository

import com.ecocollect.app.data.model.PickupSchedule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ScheduleRepository {

    override suspend fun getSchedules(): Result<List<PickupSchedule>> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
            val snapshot = firestore.collection("schedules")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            val schedules = snapshot.toObjects(PickupSchedule::class.java)
            Result.success(schedules)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addSchedule(schedule: PickupSchedule): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
            firestore.collection("schedules")
                .add(schedule.copy(userId = userId))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
