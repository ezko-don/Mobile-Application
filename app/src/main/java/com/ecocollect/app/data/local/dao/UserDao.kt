package com.ecocollect.app.data.local.dao

import androidx.room.*
import com.ecocollect.app.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    
    @Query("SELECT * FROM users WHERE uid = :uid")
    suspend fun getUserById(uid: String): User?
    
    @Query("SELECT * FROM users WHERE uid = :uid")
    fun getUserByIdFlow(uid: String): Flow<User?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)
    
    @Update
    suspend fun updateUser(user: User)
    
    @Delete
    suspend fun deleteUser(user: User)
    
    @Query("UPDATE users SET totalPoints = :points WHERE uid = :uid")
    suspend fun updateUserPoints(uid: String, points: Int)
    
    @Query("UPDATE users SET co2SavedKg = :co2Saved WHERE uid = :uid")
    suspend fun updateUserCO2Saved(uid: String, co2Saved: Double)
    
    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}
