package com.kevinfreyap.ecommerce

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

@HiltAndroidApp
open class MyApplication: Application(), Configuration.Provider {

    @Inject
    lateinit var authListenerManager: AuthListenerManager

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WorkerFactoryEntryPoint {
        fun getWorkerFactory(): HiltWorkerFactory
    }

    override val workManagerConfiguration: Configuration
        get() {
            val entryPoint = EntryPointAccessors.fromApplication(
                applicationContext,
                WorkerFactoryEntryPoint::class.java
            )
            return Configuration.Builder()
                .setWorkerFactory(entryPoint.getWorkerFactory())
                .build()
        }

    override fun onCreate() {
        super.onCreate()
        authListenerManager.startListening()
    }
}