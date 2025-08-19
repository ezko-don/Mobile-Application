package com.ecocollect.app.di

import android.content.Context
import androidx.room.Room
import com.ecocollect.app.data.local.EcoCollectDatabase
import com.ecocollect.app.data.local.dao.ImpactLogDao
import com.ecocollect.app.data.local.dao.PickupDao
import com.ecocollect.app.data.local.dao.RewardDao
import com.ecocollect.app.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideEcoCollectDatabase(@ApplicationContext context: Context): EcoCollectDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            EcoCollectDatabase::class.java,
            "ecocollect_database"
        ).build()
    }
    
    @Provides
    fun provideUserDao(database: EcoCollectDatabase): UserDao = database.userDao()
    
    @Provides
    fun providePickupDao(database: EcoCollectDatabase): PickupDao = database.pickupDao()
    
    @Provides
    fun provideRewardDao(database: EcoCollectDatabase): RewardDao = database.rewardDao()
    
    @Provides
    fun provideImpactLogDao(database: EcoCollectDatabase): ImpactLogDao = database.impactLogDao()
}
