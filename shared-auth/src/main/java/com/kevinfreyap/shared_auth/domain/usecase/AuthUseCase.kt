package com.kevinfreyap.shared_auth.domain.usecase

import com.kevinfreyap.shared_auth.domain.model.AuthRequest
import com.kevinfreyap.core.data.Resource
import kotlinx.coroutines.flow.Flow

interface AuthUseCase {
    fun loginWithGoogle(idToken: String): Flow<Resource<Boolean>>
    fun register(request: AuthRequest): Flow<Resource<Boolean>>
    fun login(request: AuthRequest): Flow<Resource<Boolean>>
    fun isUserLoggedIn(): Boolean
    fun sendPasswordResetEmail(email: String): Flow<Resource<Unit>>
    suspend fun updatePassword(currentPass: String, newPass: String, confirmPass: String): Resource<Unit>
    suspend fun syncUserData()
    suspend fun logout()
    suspend fun reAuthAndDeleteWithPassword(password: String): Resource<Unit>
    suspend fun reAuthAndDeleteWithGoogle(idToken: String): Resource<Unit>
}