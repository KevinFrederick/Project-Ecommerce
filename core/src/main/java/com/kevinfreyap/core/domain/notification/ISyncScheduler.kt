package com.kevinfreyap.core.domain.notification

interface ISyncScheduler {
    fun startBackgroundSync()
    fun stopBackgroundSync()
}