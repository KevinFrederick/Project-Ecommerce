package com.kevinfreyap.shared_auth.domain.usecase

import com.kevinfreyap.shared_auth.domain.repository.IAuthenticationRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authenticationRepository: IAuthenticationRepository
) {
    suspend operator fun invoke() {
        authenticationRepository.logout()
    }
}