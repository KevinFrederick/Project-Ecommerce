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
import com.kevinfreyap.core.domain.services.INotificationService
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
    private val userRepository: IUserRepository,
    private val notificationService: INotificationService
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

    override suspend fun updatePassword(
        currentPass: String,
        newPass: String,
        confirmPass: String
    ): Resource<Unit> {
        if (currentPass.isBlank()) {
            return Resource.Error("ERROR_CURRENT_PASSWORD_IS_REQUIRED")
        }
        if (newPass.isBlank()) {
            return Resource.Error("ERROR_NEW_PASSWORD_IS_REQUIRED")
        }
        if (confirmPass.isBlank()) {
            return Resource.Error("ERROR_CONF_PASSWORD_IS_REQUIRED")
        }

        if (newPass != confirmPass) {
            return Resource.Error("ERROR_PASSWORD_NOT_MATCH")
        }
        if (newPass.length < 8) {
            return Resource.Error("ERROR_PASSWORD_TOO_SHORT")
        }
        if (currentPass == newPass) {
            return Resource.Error("ERROR_PASS_OLD_NEW_SAME")
        }

        return authenticationRepository.changePassword(currentPass, newPass)
    }

    override suspend fun syncUserData() {
        coroutineScope {
            notificationService.startBackgroundSync()
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

    override suspend fun logout() {
        coroutineScope {
            notificationService.stopBackgroundSync()

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

    override suspend fun reAuthAndDeleteWithPassword(password: String): Resource<Unit> {
        val result = authenticationRepository.reAuthAndDeleteWithPassword(password)
        if (result is Resource.Success) logout()
        return result
    }

    override suspend fun reAuthAndDeleteWithGoogle(idToken: String): Resource<Unit> {
        val result = authenticationRepository.reAuthAndDeleteWithGoogle(idToken)
        if (result is Resource.Success) logout()
        return result
    }
}