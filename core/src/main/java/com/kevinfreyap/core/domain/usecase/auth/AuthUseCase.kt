package com.kevinfreyap.core.domain.usecase.auth

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.auth.LoginRequest
import com.kevinfreyap.core.domain.model.auth.RegisterRequest
import kotlinx.coroutines.flow.Flow

interface AuthUseCase {
    fun loginWithGoogle(idToken: String): Flow<Resource<Boolean>>
    fun register(request: RegisterRequest): Flow<Resource<Boolean>>
    fun login(request: LoginRequest): Flow<Resource<Boolean>>
    fun isUserLoggedIn(): Boolean
    fun sendPasswordResetEmail(email: String): Flow<Resource<Unit>>
    suspend fun updatePassword(currentPass: String, newPass: String, confirmPass: String): Resource<Unit>
    suspend fun syncUserData()
    suspend fun logout()
}