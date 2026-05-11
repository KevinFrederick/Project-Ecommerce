package com.kevinfreyap.background_work.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kevinfreyap.background_work.domain.usecase.NotificationDataUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val notificationData: NotificationDataUseCase
): CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            notificationData()
            Result.success()
        } catch (e: Exception) {
            Log.e("NotificationWorker", "Failed", e)
            Result.retry()
        }
    }
}