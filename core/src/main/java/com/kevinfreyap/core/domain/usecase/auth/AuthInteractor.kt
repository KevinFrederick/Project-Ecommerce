package com.kevinfreyap.core.domain.usecase.auth

import android.util.Patterns
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.auth.LoginRequest
import com.kevinfreyap.core.domain.model.auth.RegisterRequest
import com.kevinfreyap.core.domain.model.user.UserAddress
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

        if (newName.length > 30) {
            emit(Resource.Error("ERROR_NAME_TOO_LONG"))
            return@flow
        }

        emitAll(authenticationRepository.updateUserName(newName))
    }

    override fun updateAddress(newAddress: UserAddress): Flow<Resource<Unit>> = flow {
        val street = newAddress.street.trim()
        val city = newAddress.city.trim()
        val zip = newAddress.zipCode.trim()
        val country = newAddress.country.trim()
        val state = newAddress.state.trim()

        if (street.isBlank()) {
            emit(Resource.Error("ERROR_STREET_BLANK"))
            return@flow
        }

        if (city.isBlank()) {
            emit(Resource.Error("ERROR_CITY_BLANK"))
            return@flow
        }

        if (country.isBlank()) {
            emit(Resource.Error("ERROR_COUNTRY_BLANK"))
            return@flow
        }

        if (zip.length < 3 || zip.length > 12) {
            emit(Resource.Error("ERROR_INVALID_ZIP"))
            return@flow
        }

        val cleanAddress = newAddress.copy(
            street = street,
            city = city,
            state = state,
            country = country,
            zipCode = zip
        )

        emitAll(authenticationRepository.updateAddress(cleanAddress))
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