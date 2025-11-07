package com.kevinfreyap.core.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.data.source.local.UserPreferences
import com.kevinfreyap.core.domain.model.auth.LoginRequest
import com.kevinfreyap.core.domain.model.auth.RegisterRequest
import com.kevinfreyap.core.domain.model.user.UserProfile
import com.kevinfreyap.core.domain.repository.IAuthenticationRepository
import com.kevinfreyap.core.utils.Constants.FIELD_EMAIL
import com.kevinfreyap.core.utils.Constants.FIELD_ID
import com.kevinfreyap.core.utils.Constants.USER_COLLECTION
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
    private val userPreferences: UserPreferences
): IAuthenticationRepository {

    private var cachedProfile: UserProfile? = null

    override fun register(registerRequest: RegisterRequest): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val response = firebaseAuth.createUserWithEmailAndPassword(
                registerRequest.email,
                registerRequest.password
            ).await()

            val user = response.user
            if (user != null) {
                saveUserInfo(user.uid, user.email)
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error("REGISTRATION FAILED"))
            }
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

        cachedProfile = null
    }

    override fun getUserProfile(): Flow<Resource<UserProfile?>> = flow{
        emit(Resource.Loading())

        if (cachedProfile != null) {
            emit(Resource.Success(cachedProfile))
            return@flow
        }

        val currentUserId = firebaseAuth.currentUser?.uid
        if (currentUserId == null) {
            emit(Resource.Error("ERROR_USER_NOT_FOUND"))
            return@flow
        }
        try {
            val documentSnapshot = firebaseFirestore.collection(USER_COLLECTION)
                .document(currentUserId)
                .get()
                .await()

            if (documentSnapshot.exists()) {
                val userProfile = documentSnapshot.toObject<UserProfile>()
                cachedProfile = userProfile
                emit(Resource.Success(userProfile))
            } else {
                emit(Resource.Error("ERROR_USER_NOT_FOUND"))
            }
        } catch (_: Exception) {
            emit(Resource.Error("ERROR_USER_NOT_FOUND"))
        }

    }

    override fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    private suspend fun saveUserInfo(userId: String, email: String?) {
        val userData = mapOf(
            FIELD_ID to userId,
            FIELD_EMAIL to email
        )
        firebaseFirestore.collection(USER_COLLECTION)
            .document(userId)
            .set(userData)
            .await()
    }
}