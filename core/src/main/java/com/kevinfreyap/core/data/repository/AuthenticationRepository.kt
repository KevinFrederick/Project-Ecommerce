package com.kevinfreyap.core.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.data.source.local.UserPreferences
import com.kevinfreyap.core.domain.model.auth.LoginRequest
import com.kevinfreyap.core.domain.model.auth.RegisterRequest
import com.kevinfreyap.core.domain.model.auth.UserProfile
import com.kevinfreyap.core.domain.repository.IAuthenticationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userPreferences: UserPreferences
): IAuthenticationRepository {
    override fun register(registerRequest: RegisterRequest): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            firebaseAuth.createUserWithEmailAndPassword(
                registerRequest.email,
                registerRequest.password
            ).await()

            emit(Resource.Success(true))
        } catch (_: Exception) {
            emit(Resource.Error("REGISTRATION FAILED"))
        }
    }

    override fun login(loginRequest: LoginRequest): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(
                loginRequest.email,
                loginRequest.password
            ).await()

            val user = authResult.user
            if (user != null) {
                val token = user.getIdToken(true).await().token ?: ""
                userPreferences.saveAuthToken(token)
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error("UNKNOWN_ERROR"))
            }
        } catch (e: Exception) {
            when(e) {
                is FirebaseAuthInvalidUserException -> {
                    emit(Resource.Error("ERROR_USER_NOT_FOUND"))
                }
                is FirebaseAuthInvalidCredentialsException -> {
                    emit(Resource.Error("ERROR_WRONG_PASSWORD"))
                }
                else -> {
                    emit(Resource.Error("Login Failed"))
                }
            }
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
        userPreferences.clearAuthToken()
    }

    override fun getUserProfile(): UserProfile? {
        val currentUser = firebaseAuth.currentUser
        return currentUser?.let {
            UserProfile(
                uid = it.uid,
                email = it.email,
                displayName = it.displayName
            )
        }
    }

    override fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }
}