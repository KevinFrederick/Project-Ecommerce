package com.kevinfreyap.core.di

import com.kevinfreyap.core.data.network.NetworkMonitorImpl
import com.kevinfreyap.core.domain.network.INetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkMonitorModule {
    @Binds
    @Singleton
    abstract fun provideNetworkMonitor(
        networkMonitorImpl: NetworkMonitorImpl
    ): INetworkMonitor
}