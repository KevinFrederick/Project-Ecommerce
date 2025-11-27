package com.kevinfreyap.core.domain.repository

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.auth.LoginRequest
import com.kevinfreyap.core.domain.model.auth.RegisterRequest
import kotlinx.coroutines.flow.Flow

interface IAuthenticationRepository {
    // Flow<Resource<...>> is for operations that need to report state (Loading, Success, Error)
    // suspend fun is for operations that just do a job (and are slow).
    fun loginWithGoogle(idToken: String): Flow<Resource<Boolean>>
    fun register(registerRequest: RegisterRequest): Flow<Resource<Boolean>>
    fun login(loginRequest: LoginRequest): Flow<Resource<Boolean>>
    fun isUserLoggedIn(): Boolean
    fun sendPasswordResetEmail(email: String): Flow<Resource<Unit>>
    suspend fun changePassword(currentPass: String, newPass: String): Resource<Unit>
    suspend fun logout()
    suspend fun reAuthAndDeleteWithPassword(password: String): Resource<Unit>
    suspend fun reAuthAndDeleteWithGoogle(idToken: String): Resource<Unit>
}
