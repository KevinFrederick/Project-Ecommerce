package com.kevinfreyap.shared_user.data.event

import com.kevinfreyap.core.domain.event.IAuthEvenListener
import com.kevinfreyap.shared_user.domain.repository.IUserRepository
import javax.inject.Inject

class UserAuthListener @Inject constructor(
    private val userRepository: IUserRepository
): IAuthEvenListener{
    override suspend fun onUserLoggedIn() {
        userRepository.refreshUserProfile()
    }

    override suspend fun onUserLoggedOut() {
        runCatching { userRepository.clearLocalUser() }
    }
}