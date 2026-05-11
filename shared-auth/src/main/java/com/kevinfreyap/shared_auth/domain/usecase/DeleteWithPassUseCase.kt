package com.kevinfreyap.shared_auth.domain.usecase

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_auth.domain.repository.IAuthenticationRepository
import javax.inject.Inject

class DeleteWithPassUseCase @Inject constructor(
    private val authenticationRepository: IAuthenticationRepository
) {
    suspend operator fun invoke(password: String): Resource<Unit> {
        return authenticationRepository.reAuthAndDeleteWithPassword(password)
    }
}