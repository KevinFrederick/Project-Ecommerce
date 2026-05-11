package com.kevinfreyap.ecommerce

import com.kevinfreyap.core.di.ApplicationScope
import com.kevinfreyap.core.domain.event.IAuthEvenListener
import com.kevinfreyap.core.domain.notification.ISyncScheduler
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
class AuthListenerManager @Inject constructor(
    private val authListeners: Set<@JvmSuppressWildcards IAuthEvenListener>,
    private val syncScheduler: ISyncScheduler,
    @param:ApplicationScope private val externalScope: CoroutineScope
) {
    fun startListening() {
        externalScope.launch {
            AppEventBus.events.collect { event ->
                when(event) {
                    AppEvent.UserLoggedIn -> {
                        syncScheduler.startBackgroundSync()

                        // Fire every module's login logic concurrently!
                        val jobs = authListeners.map { listener ->
                            launch { listener.onUserLoggedIn() }
                        }

                        jobs.joinAll()
                    }
                    AppEvent.UserLoggedOut -> {
                        syncScheduler.stopBackgroundSync()

                        // Fire every module's logout logic concurrently!
                        val jobs = authListeners.map { listener ->
                            async { listener.onUserLoggedOut() }
                        }
                        jobs.awaitAll()
                    }
                }
            }
        }
    }
}