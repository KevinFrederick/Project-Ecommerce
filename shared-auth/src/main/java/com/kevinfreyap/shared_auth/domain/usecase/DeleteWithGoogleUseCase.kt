package com.kevinfreyap.shared_auth.domain.usecase

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_auth.domain.repository.IAuthenticationRepository
import javax.inject.Inject

class DeleteWithGoogleUseCase @Inject constructor(
    private val authenticationRepository: IAuthenticationRepository
) {
    suspend operator fun invoke(idToken: String): Resource<Unit> {
        return authenticationRepository.reAuthAndDeleteWithGoogle(idToken)
    }
}