package com.kevinfreyap.shared_auth.domain.usecase

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_auth.domain.repository.IAuthenticationRepository
import javax.inject.Inject

class LoginWithGoogleUseCase @Inject constructor(
    private val authenticationRepository: IAuthenticationRepository
) {
    suspend operator fun invoke(idToken: String): Resource<Boolean> {
        if (idToken.isBlank()) {
            return Resource.Error("ERROR_GOOGLE_SIGN_IN_FAILED")
        }
        return authenticationRepository.loginWithGoogle(idToken)
    }
}