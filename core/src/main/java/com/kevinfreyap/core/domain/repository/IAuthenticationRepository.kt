package com.kevinfreyap.core.domain.repository

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.auth.LoginRequest
import com.kevinfreyap.core.domain.model.auth.RegisterRequest
import com.kevinfreyap.core.domain.model.user.UserProfile
import kotlinx.coroutines.flow.Flow

interface IAuthenticationRepository {
    fun register(registerRequest: RegisterRequest): Flow<Resource<Boolean>>
    fun login(loginRequest: LoginRequest): Flow<Resource<Boolean>>
    suspend fun logout()
    fun getUserProfile(): Flow<Resource<UserProfile?>>
    fun isUserLoggedIn(): Boolean
}