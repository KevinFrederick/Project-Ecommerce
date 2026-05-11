package com.kevinfreyap.core.domain.network

import kotlinx.coroutines.flow.Flow

interface INetworkMonitor {
    // Snapshot
    fun isInternetAvailable(): Boolean
    // Stream
    fun isInternetAvailableFlow(): Flow<Boolean>
}