package com.kevinfreyap.shared_user.domain.usecase

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_user.domain.model.UserProfile
import com.kevinfreyap.shared_user.domain.repository.IUserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val userRepository: IUserRepository
) {
    operator fun invoke(): Flow<Resource<UserProfile>> = userRepository.getUserProfile()
}