package com.kevinfreyap.background_work.scheduler

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.kevinfreyap.background_work.worker.NotificationWorker
import com.kevinfreyap.core.domain.notification.ISyncScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SyncSchedulerImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
): ISyncScheduler {

    private val workManager = WorkManager.getInstance(context)

    override fun startBackgroundSync() {
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            15, TimeUnit.MINUTES
        )
            .setConstraints(
                androidx.work.Constraints.Builder()
                    .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
                    .build()
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    override fun stopBackgroundSync() {
        workManager.cancelUniqueWork(WORK_NAME)
    }

    companion object {
        const val WORK_NAME = "NOTIFICATION_WORKER"
    }
}