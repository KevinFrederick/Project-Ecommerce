package com.kevinfreyap.ecommerce

import com.kevinfreyap.core.di.ApplicationScope
import com.kevinfreyap.core.domain.repository.ICartRepository
import com.kevinfreyap.core.domain.repository.ITransactionRepository
import com.kevinfreyap.core.domain.repository.IUserRepository
import com.kevinfreyap.core.domain.repository.IVoucherRepository
import com.kevinfreyap.core.domain.repository.IWishlistRepository
import com.kevinfreyap.core.domain.services.INotificationService
import com.kevinfreyap.shared_events.AppEvent
import com.kevinfreyap.shared_events.AppEventBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MigrationEventObserver @Inject constructor(
    private val cartRepository: ICartRepository,
    private val wishlistRepository: IWishlistRepository,
    private val transactionRepository: ITransactionRepository,
    private val voucherRepository: IVoucherRepository,
    private val userRepository: IUserRepository,
    private val notificationService: INotificationService,
    @param:ApplicationScope private val externalScope: CoroutineScope
) {
    fun startListening() {
        externalScope.launch {
            AppEventBus.events.collect { event ->
                when(event) {
                    AppEvent.UserLoggedIn -> {
                        notificationService.startBackgroundSync()
                        voucherRepository.listenToPublicVouchers(externalScope)

                        val jobs = listOf(
                            launch { cartRepository.syncCartOnLogin() },
                            launch { voucherRepository.syncVouchers() },
                            launch { transactionRepository.syncTransactionHistoryOnLogin() },
                            launch { wishlistRepository.syncWishlistOnLogin() },
                            launch { userRepository.refreshUserProfile() }
                        )

                        jobs.joinAll()
                    }
                    AppEvent.UserLoggedOut -> {
                        notificationService.stopBackgroundSync()

                        val jobs = listOf(
                            async { runCatching { cartRepository.clearCart() } },
                            async { runCatching { wishlistRepository.clearWishlist() } },
                            async { runCatching { transactionRepository.clearOrderHistory() } },
                            async { runCatching { voucherRepository.clearVouchers() } }
                        )
                        jobs.awaitAll()
                    }
                }
            }
        }
    }
}