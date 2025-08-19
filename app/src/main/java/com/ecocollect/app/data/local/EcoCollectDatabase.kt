package com.ecocollect.app.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.ecocollect.app.data.local.dao.ImpactLogDao
import com.ecocollect.app.data.local.dao.PickupDao
import com.ecocollect.app.data.local.dao.RewardDao
import com.ecocollect.app.data.local.dao.UserDao
import com.ecocollect.app.data.model.ImpactLog
import com.ecocollect.app.data.model.Pickup
import com.ecocollect.app.data.model.Reward
import com.ecocollect.app.data.model.RewardTransaction
import com.ecocollect.app.data.model.User

@Database(
    entities = [
        User::class,
        Pickup::class,
        Reward::class,
        RewardTransaction::class,
        ImpactLog::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class EcoCollectDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun pickupDao(): PickupDao
    abstract fun rewardDao(): RewardDao
    abstract fun impactLogDao(): ImpactLogDao
    
    companion object {
        @Volatile
        private var INSTANCE: EcoCollectDatabase? = null
        
        fun getDatabase(context: Context): EcoCollectDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EcoCollectDatabase::class.java,
                    "ecocollect_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
