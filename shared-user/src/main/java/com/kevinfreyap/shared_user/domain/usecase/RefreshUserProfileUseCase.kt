package com.kevinfreyap.shared_user.domain.usecase

import com.kevinfreyap.shared_user.domain.repository.IUserRepository
import javax.inject.Inject

class RefreshUserProfileUseCase @Inject constructor(
    private val userRepository: IUserRepository
) {
    suspend operator fun invoke() = userRepository.refreshUserProfile()
}