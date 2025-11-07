package com.kevinfreyap.core.domain.usecase.auth

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.data.repository.AuthenticationRepository
import com.kevinfreyap.core.domain.model.auth.LoginRequest
import com.kevinfreyap.core.domain.model.auth.RegisterRequest
import com.kevinfreyap.core.domain.model.user.UserProfile
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthInteractor @Inject constructor (private val authenticationRepository: AuthenticationRepository): AuthUseCase {
    override fun register(request: RegisterRequest): Flow<Resource<Boolean>> = authenticationRepository.register(request)

    override fun login(request: LoginRequest): Flow<Resource<Boolean>> = authenticationRepository.login(request)

    override suspend fun logout() = authenticationRepository.logout()

    override fun getUserProfile(): Flow<Resource<UserProfile?>> = authenticationRepository.getUserProfile()

    override fun isUserLoggedIn(): Boolean = authenticationRepository.isUserLoggedIn()
}