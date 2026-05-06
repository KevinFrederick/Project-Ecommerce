package com.kevinfreyap.ecommerce

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
open class MyApplication: Application(), Configuration.Provider {

    @Inject
    lateinit var migrationEventObserver: MigrationEventObserver

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        migrationEventObserver.startListening()
    }
}