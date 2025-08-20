package com.ecocollect.app.data.repository

import com.ecocollect.app.data.model.PickupSchedule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ScheduleRepository {

    override fun getSchedules(): Flow<Result<List<PickupSchedule>>> {
        val userId = auth.currentUser?.uid ?: return kotlinx.coroutines.flow.flowOf(Result.failure(Exception("User not logged in")))
        return firestore.collection("schedules")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot ->
                try {
                    Result.success(snapshot.toObjects(PickupSchedule::class.java))
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
    }

    override suspend fun addSchedule(schedule: PickupSchedule): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid
            if (userId == null) {
                return Result.failure(Exception("User not logged in"))
            }
            val scheduleWithUser = schedule.copy(userId = userId)
            firestore.collection("schedules").add(scheduleWithUser).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getScheduleById(scheduleId: String): Result<PickupSchedule> {
        return try {
            val document = firestore.collection("schedules").document(scheduleId).get().await()
            val schedule = document.toObject<PickupSchedule>()
            if (schedule != null) {
                Result.success(schedule.copy(id = document.id))
            } else {
                Result.failure(Exception("Schedule not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
