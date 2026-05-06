package com.kevinfreyap.shared_auth.domain.repository

import com.kevinfreyap.shared_auth.domain.model.AuthRequest
import com.kevinfreyap.core.data.Resource
import kotlinx.coroutines.flow.Flow

interface IAuthenticationRepository {
    // Flow<Resource<...>> is for operations that need to report state (Loading, Success, Error)
    // suspend fun is for operations that just do a job (and are slow).
    fun loginWithGoogle(idToken: String): Flow<Resource<Boolean>>
    fun register(registerRequest: AuthRequest): Flow<Resource<Boolean>>
    fun login(loginRequest: AuthRequest): Flow<Resource<Boolean>>
    fun isUserLoggedIn(): Boolean
    fun sendPasswordResetEmail(email: String): Flow<Resource<Unit>>
    suspend fun changePassword(currentPass: String, newPass: String): Resource<Unit>
    suspend fun logout()
    suspend fun reAuthAndDeleteWithPassword(password: String): Resource<Unit>
    suspend fun reAuthAndDeleteWithGoogle(idToken: String): Resource<Unit>
}