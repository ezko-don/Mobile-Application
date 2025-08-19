package com.ecocollect.app.data.local.dao

import androidx.room.*
import com.ecocollect.app.data.model.Pickup
import com.ecocollect.app.data.model.PickupStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface PickupDao {
    
    @Query("SELECT * FROM pickups WHERE userId = :userId ORDER BY createdAt DESC")
    fun getUserPickups(userId: String): Flow<List<Pickup>>
    
    @Query("SELECT * FROM pickups WHERE pickupId = :pickupId")
    suspend fun getPickupById(pickupId: String): Pickup?
    
    @Query("SELECT * FROM pickups WHERE pickupId = :pickupId")
    fun getPickupByIdFlow(pickupId: String): Flow<Pickup?>
    
    @Query("SELECT * FROM pickups WHERE userId = :userId AND status = :status")
    fun getPickupsByStatus(userId: String, status: PickupStatus): Flow<List<Pickup>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPickup(pickup: Pickup)
    
    @Update
    suspend fun updatePickup(pickup: Pickup)
    
    @Delete
    suspend fun deletePickup(pickup: Pickup)
    
    @Query("UPDATE pickups SET status = :status WHERE pickupId = :pickupId")
    suspend fun updatePickupStatus(pickupId: String, status: PickupStatus)
    
    @Query("UPDATE pickups SET weightKg = :weight, rewardPoints = :points WHERE pickupId = :pickupId")
    suspend fun updatePickupWeightAndPoints(pickupId: String, weight: Double, points: Int)
    
    @Query("SELECT COUNT(*) FROM pickups WHERE userId = :userId AND status = 'COLLECTED'")
    suspend fun getCompletedPickupsCount(userId: String): Int
    
    @Query("SELECT SUM(weightKg) FROM pickups WHERE userId = :userId AND status = 'COLLECTED'")
    suspend fun getTotalWeightCollected(userId: String): Double?
    
    @Query("DELETE FROM pickups")
    suspend fun deleteAllPickups()
}
