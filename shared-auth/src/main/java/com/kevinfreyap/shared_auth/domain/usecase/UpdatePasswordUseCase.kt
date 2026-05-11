package com.kevinfreyap.shared_auth.domain.usecase

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_auth.domain.repository.IAuthenticationRepository
import javax.inject.Inject

class UpdatePasswordUseCase @Inject constructor(
    private val authenticationRepository: IAuthenticationRepository
) {
    suspend operator fun invoke (
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
}