package com.kevinfreyap.shared_auth.domain.usecase

import com.kevinfreyap.shared_auth.domain.repository.IAuthenticationRepository
import javax.inject.Inject

class LoginStatusUseCase @Inject constructor(
    private val authenticationRepository: IAuthenticationRepository
) {
    operator fun invoke(): Boolean = authenticationRepository.isUserLoggedIn()
}