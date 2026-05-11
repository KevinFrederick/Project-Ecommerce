package com.kevinfreyap.shared_user.domain.usecase

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_user.domain.model.UserAddress
import com.kevinfreyap.shared_user.domain.repository.IUserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateAddressUseCase @Inject constructor(
    private val userRepository: IUserRepository
) {
    operator fun invoke(
        newStreet: String,
        newCity: String,
        newState: String,
        newZip: String,
        newCountry: String
    ): Flow<Resource<Unit>> = flow {
        val street = newStreet.trim()
        val city = newCity.trim()
        val zip = newZip.trim()
        val country = newCountry.trim()
        val state = newState.trim()

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

        val cleanAddress = UserAddress(
            street = street,
            city = city,
            state = state,
            country = country,
            zipCode = zip
        )

        emitAll(userRepository.updateAddress(cleanAddress))
    }
}