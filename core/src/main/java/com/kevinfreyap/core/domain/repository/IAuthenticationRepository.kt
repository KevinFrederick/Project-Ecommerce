package com.kevinfreyap.core.domain.repository

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.auth.LoginRequest
import com.kevinfreyap.core.domain.model.auth.RegisterRequest
import com.kevinfreyap.core.domain.model.user.UserAddress
import com.kevinfreyap.core.domain.model.user.UserProfile
import kotlinx.coroutines.flow.Flow

interface IAuthenticationRepository {
    // Flow<Resource<...>> is for operations that need to report state (Loading, Success, Error)
    // suspend fun is for operations that just do a job (and are slow).
    fun loginWithGoogle(idToken: String): Flow<Resource<Boolean>>
    fun register(registerRequest: RegisterRequest): Flow<Resource<Boolean>>
    fun login(loginRequest: LoginRequest): Flow<Resource<Boolean>>
    suspend fun logout()
    fun getUserProfile(): Flow<Resource<UserProfile>>
    suspend fun refreshUserProfile()
    fun updateUserName(newName: String): Flow<Resource<Unit>>
    fun updateAddress(newAddress: UserAddress): Flow<Resource<Unit>>
    fun isUserLoggedIn(): Boolean
    fun sendPasswordResetEmail(email: String): Flow<Resource<Unit>>
}