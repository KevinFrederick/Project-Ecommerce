package com.kevinfreyap.ecommerce.di

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.kevinfreyap.core.domain.services.INotificationService
import com.kevinfreyap.core.worker.NotificationWorker
import com.kevinfreyap.ecommerce.notification.NotificationHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NotificationService @Inject constructor(
    @param:ApplicationContext private val context: Context
): INotificationService {

    override fun showNotification(title: String, message: String, type: String) {
        NotificationHelper.showNotification(context, title, message, type)
    }

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

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    override fun stopBackgroundSync() {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    companion object {
        const val WORK_NAME = "NOTIFICATION_WORKER"
    }
}