package com.ecocollect.app.data.local.dao

import androidx.room.*
import com.ecocollect.app.data.model.ImpactLog
import com.ecocollect.app.data.model.ImpactEventType
import kotlinx.coroutines.flow.Flow

@Dao
interface ImpactLogDao {
    
    @Query("SELECT * FROM impact_logs WHERE userId = :userId ORDER BY timestamp DESC")
    fun getUserImpactLogs(userId: String): Flow<List<ImpactLog>>
    
    @Query("SELECT * FROM impact_logs WHERE userId = :userId AND eventType = :eventType")
    fun getImpactLogsByType(userId: String, eventType: ImpactEventType): Flow<List<ImpactLog>>
    
    @Query("SELECT SUM(deltaCO2) FROM impact_logs WHERE userId = :userId")
    suspend fun getTotalCO2Saved(userId: String): Double?
    
    @Query("SELECT SUM(deltaPoints) FROM impact_logs WHERE userId = :userId")
    suspend fun getTotalPointsEarned(userId: String): Int?
    
    @Query("SELECT SUM(metalsRecoveredGrams) FROM impact_logs WHERE userId = :userId")
    suspend fun getTotalMetalsRecovered(userId: String): Double?
    
    @Query("SELECT SUM(treesEquivalent) FROM impact_logs WHERE userId = :userId")
    suspend fun getTotalTreesEquivalent(userId: String): Double?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImpactLog(impactLog: ImpactLog)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImpactLogs(impactLogs: List<ImpactLog>)
    
    @Update
    suspend fun updateImpactLog(impactLog: ImpactLog)
    
    @Delete
    suspend fun deleteImpactLog(impactLog: ImpactLog)
    
    @Query("DELETE FROM impact_logs")
    suspend fun deleteAllImpactLogs()
    
    @Query("""
        SELECT strftime('%Y-%m', datetime(timestamp, 'unixepoch')) as month, 
               SUM(deltaCO2) as totalCO2 
        FROM impact_logs 
        WHERE userId = :userId 
        GROUP BY month 
        ORDER BY month DESC 
        LIMIT 12
    """)
    suspend fun getMonthlyCO2Stats(userId: String): Map<String, Double>
}
