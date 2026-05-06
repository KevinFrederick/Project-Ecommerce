package com.kevinfreyap.shared_auth.domain.usecase

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_auth.domain.repository.IAuthenticationRepository
import javax.inject.Inject

class SendResetPassEmailUseCase @Inject constructor(
    private val authenticationRepository: IAuthenticationRepository
) {
    suspend operator fun invoke(email: String): Resource<Unit> {
        if (email.isBlank()) {
            return Resource.Error("ERROR_FAILED_RESET_PASSWORD")
        }

        return authenticationRepository.sendPasswordResetEmail(email)
    }
}