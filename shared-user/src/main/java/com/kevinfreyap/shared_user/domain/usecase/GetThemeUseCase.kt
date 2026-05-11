package com.kevinfreyap.shared_user.domain.usecase

import com.kevinfreyap.shared_user.domain.repository.IUserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetThemeUseCase @Inject constructor(
    private val userRepository: IUserRepository
) {
    operator fun invoke(): Flow<Int> = userRepository.getTheme()
}