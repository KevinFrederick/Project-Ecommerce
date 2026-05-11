package com.kevinfreyap.auth.domain.usecase

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_auth.domain.repository.IAuthenticationRepository
import com.kevinfreyap.shared_events.AppEvent
import com.kevinfreyap.shared_events.AppEventBus
import com.kevinfreyap.shared_user.domain.model.UserProfile
import com.kevinfreyap.shared_user.domain.repository.IUserRepository
import javax.inject.Inject

class LoginWithGoogleUseCase @Inject constructor(
    private val authenticationRepository: IAuthenticationRepository,
    private val userRepository: IUserRepository
) {
    suspend operator fun invoke(idToken: String): Resource<Boolean> {
        if (idToken.isBlank()) {
            return Resource.Error("ERROR_GOOGLE_SIGN_IN_FAILED")
        }
        val authResult = authenticationRepository.loginWithGoogle(idToken)
        if (authResult is Resource.Error) return Resource.Error(authResult.message ?: "Login Failed")

        val sessionData = authResult.data!!

        val fallbackProfile = UserProfile(
            uid = sessionData.uid,
            email = sessionData.email,
            displayName = sessionData.displayName,
            photoUrl = sessionData.photoUrl,
            address = null,
            isGoogleAccount = sessionData.isGoogleAccount
        )
        userRepository.saveLocalProfile(fallbackProfile)

        userRepository.syncProfileFromRemote(sessionData.uid)

        AppEventBus.emit(AppEvent.UserLoggedIn)
        return Resource.Success(true)
    }
}