package com.kevinfreyap.background_work.di

import com.kevinfreyap.background_work.scheduler.SyncSchedulerImpl
import com.kevinfreyap.core.domain.notification.ISyncScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SyncModule {
    @Binds
    @Singleton
    abstract fun bindSyncScheduler(
        impl: SyncSchedulerImpl
    ): ISyncScheduler
}