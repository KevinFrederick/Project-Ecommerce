package com.kevinfreyap.ecommerce.domain.event

import com.kevinfreyap.core.di.ApplicationScope
import com.kevinfreyap.core.domain.event.IAuthEvenListener
import com.kevinfreyap.core.domain.notification.ISyncScheduler
import com.kevinfreyap.ecommerce.domain.usecase.ListenToPublicVoucherUseCase
import com.kevinfreyap.shared_transaction.domain.usecase.ListenToTransactionUpdateUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class NotificationAuthListener @Inject constructor(
    private val listenToPublicVoucher: ListenToPublicVoucherUseCase,
    private val listenToTransactionUpdate: ListenToTransactionUpdateUseCase,
    private val syncScheduler: ISyncScheduler,
    @param:ApplicationScope private val externalScope: CoroutineScope
): IAuthEvenListener {
    override suspend fun onUserLoggedIn() {
        listenToPublicVoucher()

        externalScope.launch {
            listenToTransactionUpdate()
        }

        syncScheduler.startBackgroundSync()
    }

    override suspend fun onUserLoggedOut() {
        syncScheduler.startBackgroundSync()
    }

}