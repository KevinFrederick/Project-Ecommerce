package com.kevinfreyap.core.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kevinfreyap.core.domain.repository.ITransactionRepository
import com.kevinfreyap.core.domain.repository.IVoucherRepository
import com.kevinfreyap.core.domain.repository.IWishlistRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val transactionRepository: ITransactionRepository,
    private val wishlistRepository: IWishlistRepository,
    private val voucherRepository: IVoucherRepository
): CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = coroutineScope {
        try {
            val jobs = listOf(
                async { transactionRepository.simulateOrderStatusUpdates() },
                async { wishlistRepository.validateWishlistAvailability() },
                async { voucherRepository.checkNewVoucherInBackground() }
            )

            jobs.forEach { it.await() }
            Result.success()
        } catch (e: Exception) {
            Log.e("NotificationWorker", "Failed", e)
            Result.retry()
        }
    }
}