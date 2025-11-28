package com.kevinfreyap.core.domain.usecase.user

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.notification.NotificationPreferences
import com.kevinfreyap.core.domain.model.user.UserAddress
import com.kevinfreyap.core.domain.model.user.UserProfile
import com.kevinfreyap.core.domain.repository.IUserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserInteractor @Inject constructor(
    private val userRepository: IUserRepository,
): UserUseCase {
    override fun getUserProfile(): Flow<Resource<UserProfile>> = userRepository.getUserProfile()

    override fun updateUserName(newName: String): Flow<Resource<Unit>> = flow {
        if (newName.isBlank()) {
            emit(Resource.Error("ERROR_NO_NAME"))
            return@flow
        }

        if (newName.length > 30) {
            emit(Resource.Error("ERROR_NAME_TOO_LONG"))
            return@flow
        }

        emitAll(userRepository.updateUserName(newName))
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

        emitAll(userRepository.updateAddress(cleanAddress))
    }

    override fun getTheme(): Flow<Int> = userRepository.getTheme()

    override fun getNotificationSettings(): Flow<NotificationPreferences> = userRepository.getNotificationSettings()

    override suspend fun refreshUserProfile() = userRepository.refreshUserProfile()

    override suspend fun saveTheme(mode: Int) = userRepository.saveTheme(mode)

    override suspend fun updateNotificationSetting(
        isSystem: Boolean,
        isEnabled: Boolean
    ) {
        return userRepository.updateNotificationSetting(isSystem, isEnabled)
    }
}