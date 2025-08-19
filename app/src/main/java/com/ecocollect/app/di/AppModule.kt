package com.ecocollect.app.di

import com.ecocollect.app.data.repository.ScheduleRepository
import com.ecocollect.app.data.repository.ScheduleRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideScheduleRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ): ScheduleRepository = ScheduleRepositoryImpl(firestore, auth)
}
