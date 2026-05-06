package com.kevinfreyap.shared_auth.domain.usecase

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_auth.domain.model.AuthRequest
import com.kevinfreyap.shared_auth.domain.repository.IAuthenticationRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authenticationRepository: IAuthenticationRepository
) {
    suspend operator fun invoke(request: AuthRequest): Resource<Boolean> {
        return authenticationRepository.login(request)
    }
}