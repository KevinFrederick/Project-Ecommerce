package com.kevinfreyap.core.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.data.source.local.UserPreferences
import com.kevinfreyap.core.domain.model.auth.LoginRequest
import com.kevinfreyap.core.domain.model.auth.RegisterRequest
import com.kevinfreyap.core.domain.model.user.UserAddress
import com.kevinfreyap.core.domain.model.user.UserProfile
import com.kevinfreyap.core.domain.repository.IAuthenticationRepository
import com.kevinfreyap.core.utils.Constants.FIELD_ADDRESS
import com.kevinfreyap.core.utils.Constants.FIELD_EMAIL
import com.kevinfreyap.core.utils.Constants.FIELD_ID
import com.kevinfreyap.core.utils.Constants.FIELD_NAME
import com.kevinfreyap.core.utils.Constants.USER_COLLECTION
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
    private val userPreferences: UserPreferences
): IAuthenticationRepository {
    override fun loginWithGoogle(idToken: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)

            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val user = authResult.user

            if (user != null){
                val token = user.getIdToken(true).await().token ?: ""
                userPreferences.saveAuthToken(token)

                val profile = UserProfile(
                    uid = user.uid,
                    email = user.email,
                    displayName = user.displayName,
                    photoUrl = user.photoUrl?.toString(),
                    address = null
                )
                userPreferences.saveUserProfile(profile)

                try {
                    val snapshot = firebaseFirestore.collection(USER_COLLECTION)
                        .document(user.uid)
                        .get()
                        .await()

                    val fullProfile = snapshot.toObject(UserProfile::class.java)
                    if (fullProfile != null) {
                        userPreferences.saveUserProfile(fullProfile)
                    }
                } catch (e: Exception) {
                    Log.e("AuthRepository", "Firestore sync failed", e)
                }

                emit(Resource.Success(true))
            } else {
                emit(Resource.Error("ERROR_GOOGLE_SIGN_IN_FAILED"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Login Failed"))
        }
    }.flowOn(Dispatchers.IO)

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
                emit(Resource.Error("REGISTRATION_FAILED"))
            }
        } catch (_: IOException) {
            emit(Resource.Error("ERROR_NO_CONNECTION"))
        } catch (e: FirebaseAuthException) {
            emit(Resource.Error(e.message ?: "REGISTRATION_FAILED"))
        } catch (e: Exception) {
            if (e.message?.contains("network", ignoreCase = true) == true) {
                emit(Resource.Error("ERROR_NO_CONNECTION"))
            }else {
                emit(Resource.Error(e.message ?: "REGISTRATION_FAILED"))
            }
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

                val profile = UserProfile(
                    uid = user.uid,
                    email = user.email,
                    displayName = user.displayName,
                    photoUrl = user.photoUrl.toString(),
                    address = null
                )
                userPreferences.saveUserProfile(profile)

                try {
                    val snapshot = firebaseFirestore.collection(USER_COLLECTION)
                        .document(user.uid)
                        .get()
                        .await()

                    val fullProfile = snapshot.toObject(UserProfile::class.java)

                    if (fullProfile != null) {
                        userPreferences.saveUserProfile(fullProfile)
                    }
                } catch (e: Exception) {
                    Log.e("AuthRepository", "Failed to sync full profile on login", e)
                }

                emit(Resource.Success(true))
            } else {
                emit(Resource.Error("UNKNOWN_ERROR"))
            }
        } catch (_: IOException) {
            emit(Resource.Error("ERROR_NO_CONNECTION"))
        } catch (e: Exception) {
            when(e) {
                is FirebaseAuthInvalidUserException -> {
                    emit(Resource.Error("ERROR_EMAIL_NOT_REGISTERED"))
                }
                is FirebaseAuthInvalidCredentialsException -> {
                    emit(Resource.Error("ERROR_WRONG_PASSWORD"))
                }
                is FirebaseAuthException -> {
                    if (e.errorCode == "ERROR_NETWORK_REQUEST_FAILED") {
                        emit(Resource.Error("ERROR_NO_CONNECTION"))
                    } else {
                        emit(Resource.Error(e.message ?: "Login Failed"))
                    }
                }
                else -> {
                    if (e.message?.contains("network", ignoreCase = true) == true) {
                        emit(Resource.Error("ERROR_NO_CONNECTION"))
                    }else {
                        emit(Resource.Error(e.message ?: "Login Failed"))
                    }
                }
            }
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun logout() {
        firebaseAuth.signOut()
        userPreferences.clearSession()
    }

    override fun getUserProfile(): Flow<Resource<UserProfile>> {
        return userPreferences.getUserProfile()
            .map { profile ->
                if (profile.uid.isNotEmpty()) {
                    Resource.Success(profile)
                } else if (firebaseAuth.currentUser != null) {
                    Resource.Success(
                        UserProfile(
                            uid = firebaseAuth.currentUser?.uid ?: "",
                            email = firebaseAuth.currentUser?.email,
                            displayName = firebaseAuth.currentUser?.displayName,
                            photoUrl = firebaseAuth.currentUser?.photoUrl.toString(),
                            address = null
                        )
                    )
                } else {
                    Resource.Success(UserProfile())
                }
            }
    }

    override suspend fun refreshUserProfile() {
        val uid = firebaseAuth.currentUser?.uid ?: return

        try {
            val snapshot = firebaseFirestore.collection(USER_COLLECTION)
                .document(uid)
                .get()
                .await()

            val remoteProfile = snapshot.toObject(UserProfile::class.java)

            if (remoteProfile != null) {
                userPreferences.saveUserProfile(remoteProfile)
            }
        } catch (e: Exception) {
            Log.e("UserRepo", "Background sync failed: ${e.message}")
        }
    }

    override fun updateUserName(newName: String): Flow<Resource<Unit>> = flow {
        try {
            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                emit(Resource.Error("ERROR_USER_NOT_FOUND"))
                return@flow
            }

            val profileUpdates = userProfileChangeRequest {
                displayName = newName
            }
            currentUser.updateProfile(profileUpdates).await()

            firebaseFirestore.collection(USER_COLLECTION)
                .document(currentUser.uid)
                .update(FIELD_NAME, newName)
                .await()

            val currentCachedProfile = userPreferences.getUserProfile().first()
            val updatedProfile = currentCachedProfile.copy(displayName = newName)

            userPreferences.saveUserProfile(updatedProfile)

            emit(Resource.Success(Unit))
        } catch (e: kotlin.Exception) {
            emit(Resource.Error(e.message ?: "Failed to update name"))
        }
    }.flowOn(Dispatchers.IO)

    override fun updateAddress(newAddress: UserAddress): Flow<Resource<Unit>> = flow {
        try {
            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                emit(Resource.Error("ERROR_USER_NOT_FOUND"))
                return@flow
            }

            firebaseFirestore.collection(USER_COLLECTION)
                .document(currentUser.uid)
                .update(FIELD_ADDRESS, newAddress)
                .await()

            val currentProfile = userPreferences.getUserProfile().first()
            val updatedProfile = currentProfile.copy(address = newAddress)

            userPreferences.saveUserProfile(updatedProfile)

            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to update address"))
        }
    }.flowOn(Dispatchers.IO)

    override fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override fun sendPasswordResetEmail(email: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            firebaseAuth.sendPasswordResetEmail(email).await()

            emit(Resource.Success(Unit))
        } catch (_: Exception) {
            emit(Resource.Error("ERROR_FAILED_RESET_PASSWORD"))
        }
    }.flowOn(Dispatchers.IO)

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