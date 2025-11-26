package com.kevinfreyap.core.domain.usecase.auth

import android.util.Patterns
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.auth.LoginRequest
import com.kevinfreyap.core.domain.model.auth.RegisterRequest
import com.kevinfreyap.core.domain.repository.IAuthenticationRepository
import com.kevinfreyap.core.domain.repository.ICartRepository
import com.kevinfreyap.core.domain.repository.ITransactionRepository
import com.kevinfreyap.core.domain.repository.IUserRepository
import com.kevinfreyap.core.domain.repository.IVoucherRepository
import com.kevinfreyap.core.domain.repository.IWishlistRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthInteractor @Inject constructor (
    private val authenticationRepository: IAuthenticationRepository,
    private val cartRepository: ICartRepository,
    private val wishlistRepository: IWishlistRepository,
    private val transactionRepository: ITransactionRepository,
    private val voucherRepository: IVoucherRepository,
    private val userRepository: IUserRepository
): AuthUseCase {
    override fun loginWithGoogle(idToken: String): Flow<Resource<Boolean>> = flow {
        authenticationRepository.loginWithGoogle(idToken).collect { resource ->
            if (resource is Resource.Success) {
                syncUserData()
            }
            emit(resource)
        }
    }

    override fun register(request: RegisterRequest): Flow<Resource<Boolean>> = authenticationRepository.register(request)

    override fun login(request: LoginRequest): Flow<Resource<Boolean>> = flow {
        authenticationRepository.login(request).collect{ resource ->
            if (resource is Resource.Success) {
                syncUserData()
            }
            emit(resource)
        }
    }

    override suspend fun logout() {
        coroutineScope {
            val jobs = listOf(
                async { runCatching { cartRepository.clearCart() } },
                async { runCatching { wishlistRepository.clearWishlist() } },
                async { runCatching { transactionRepository.clearOrderHistory() } },
                async { runCatching { voucherRepository.clearVouchers() } }
            )
            jobs.awaitAll()

            authenticationRepository.logout()
        }
    }

    override suspend fun syncUserData() {
        coroutineScope {
            voucherRepository.listenToPublicVouchers()

            val jobs = listOf(
                launch { cartRepository.syncCartOnLogin() },
                launch { voucherRepository.syncVouchers() },
                launch { transactionRepository.syncTransactionHistoryOnLogin() },
                launch { wishlistRepository.syncWishlistOnLogin() },
                launch { userRepository.refreshUserProfile() }
            )

            jobs.joinAll()
        }
    }

    override fun isUserLoggedIn(): Boolean = authenticationRepository.isUserLoggedIn()
    override fun sendPasswordResetEmail(email: String): Flow<Resource<Unit>> = flow {
        if (email.isBlank()) {
            emit(Resource.Error("ERROR_EMAIL_IS_REQUIRED"))
            return@flow
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emit(Resource.Error("ERROR_INVALID_EMAIL"))
            return@flow
        }

        emitAll(authenticationRepository.sendPasswordResetEmail(email))
    }
}