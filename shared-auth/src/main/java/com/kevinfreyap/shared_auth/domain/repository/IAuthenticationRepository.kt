package com.kevinfreyap.shared_auth.domain.repository

import com.kevinfreyap.shared_auth.domain.model.AuthRequest
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_auth.domain.model.AuthSessionResult

interface IAuthenticationRepository {
    // Flow<Resource<...>> is for operations that need to report state (Loading, Success, Error)
    // suspend fun is for operations that just do a job (and are slow).
    suspend fun loginWithGoogle(idToken: String): Resource<AuthSessionResult>
    suspend fun register(registerRequest: AuthRequest): Resource<Boolean>
    suspend fun login(loginRequest: AuthRequest): Resource<AuthSessionResult>
    fun isUserLoggedIn(): Boolean
    suspend fun sendPasswordResetEmail(email: String): Resource<Unit>
    suspend fun changePassword(currentPass: String, newPass: String): Resource<Unit>
    suspend fun logout()
    suspend fun reAuthAndDeleteWithPassword(password: String): Resource<Unit>
    suspend fun reAuthAndDeleteWithGoogle(idToken: String): Resource<Unit>
}