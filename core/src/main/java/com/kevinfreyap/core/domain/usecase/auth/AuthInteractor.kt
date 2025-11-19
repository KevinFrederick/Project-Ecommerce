package com.kevinfreyap.core.domain.usecase.auth

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.auth.LoginRequest
import com.kevinfreyap.core.domain.model.auth.RegisterRequest
import com.kevinfreyap.core.domain.model.user.UserProfile
import com.kevinfreyap.core.domain.repository.IAuthenticationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthInteractor @Inject constructor (
    private val authenticationRepository: IAuthenticationRepository
): AuthUseCase {
    override fun register(request: RegisterRequest): Flow<Resource<Boolean>> = authenticationRepository.register(request)

    override fun login(request: LoginRequest): Flow<Resource<Boolean>> = authenticationRepository.login(request)

    override suspend fun logout() = authenticationRepository.logout()

    override fun getUserProfile(): Flow<Resource<UserProfile>> = authenticationRepository.getUserProfile()

    override suspend fun refreshUserProfile() = authenticationRepository.refreshUserProfile()

    override fun updateUserName(newName: String): Flow<Resource<Unit>> = flow {
        if (newName.isBlank()) {
            emit(Resource.Error("ERROR_NO_NAME"))
            return@flow
        }

        if (newName.length > 20) {
            emit(Resource.Error("ERROR_NAME_TOO_LONG"))
            return@flow
        }

        emitAll(authenticationRepository.updateUserName(newName))
    }

    override fun isUserLoggedIn(): Boolean = authenticationRepository.isUserLoggedIn()
}