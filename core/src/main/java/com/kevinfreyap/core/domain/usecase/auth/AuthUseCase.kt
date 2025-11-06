package com.kevinfreyap.core.domain.usecase.auth

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.auth.LoginRequest
import com.kevinfreyap.core.domain.model.auth.RegisterRequest
import com.kevinfreyap.core.domain.model.auth.UserProfile
import kotlinx.coroutines.flow.Flow

interface AuthUseCase {
    fun register(request: RegisterRequest): Flow<Resource<Boolean>>
    fun login(request: LoginRequest): Flow<Resource<Boolean>>
    suspend fun logout()
    fun getUserProfile(): UserProfile?
    fun isUserLoggedIn(): Boolean
}