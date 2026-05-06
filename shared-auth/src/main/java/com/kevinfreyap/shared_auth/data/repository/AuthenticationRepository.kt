package com.kevinfreyap.shared_auth.data.repository

import android.util.Log
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.data.source.local.UserPreferences
import com.kevinfreyap.core.domain.model.user.UserProfile
import com.kevinfreyap.core.utils.Constants
import com.kevinfreyap.core.utils.isGoogleAccount
import com.kevinfreyap.shared_auth.domain.model.AuthRequest
import com.kevinfreyap.shared_auth.domain.repository.IAuthenticationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import java.io.IOException
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

            if (user != null) {
                val token = user.getIdToken(true).await().token ?: ""
                userPreferences.saveAuthToken(token)

                val isGoogleAccount = user.isGoogleAccount()

                val profile = UserProfile(
                    uid = user.uid,
                    email = user.email,
                    displayName = user.displayName,
                    photoUrl = user.photoUrl?.toString(),
                    address = null,
                    isGoogleAccount = isGoogleAccount
                )
                userPreferences.saveUserProfile(profile)

                try {
                    val snapshot = firebaseFirestore.collection(Constants.USER_COLLECTION)
                        .document(user.uid)
                        .get()
                        .await()

                    val fullProfile = snapshot.toObject(UserProfile::class.java)
                    if (fullProfile != null) {
                        userPreferences.saveUserProfile(fullProfile)
                    }
                } catch (e: java.lang.Exception) {
                    Log.e("AuthRepository", "Firestore sync failed", e)
                }

                emit(Resource.Success(true))
            } else {
                emit(Resource.Error("ERROR_GOOGLE_SIGN_IN_FAILED"))
            }
        } catch (e: java.lang.Exception) {
            emit(Resource.Error(e.message ?: "Login Failed"))
        }
    }.flowOn(Dispatchers.IO)

    override fun register(registerRequest: AuthRequest): Flow<Resource<Boolean>> = flow {
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
        } catch (e: java.lang.Exception) {
            if (e.message?.contains("network", ignoreCase = true) == true) {
                emit(Resource.Error("ERROR_NO_CONNECTION"))
            } else {
                emit(Resource.Error(e.message ?: "REGISTRATION_FAILED"))
            }
        }
    }

    override fun login(loginRequest: AuthRequest): Flow<Resource<Boolean>> = flow {
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

                val isGoogleAccount = user.isGoogleAccount()

                val profile = UserProfile(
                    uid = user.uid,
                    email = user.email,
                    displayName = user.displayName,
                    photoUrl = user.photoUrl.toString(),
                    address = null,
                    isGoogleAccount = isGoogleAccount
                )
                userPreferences.saveUserProfile(profile)

                try {
                    val snapshot = firebaseFirestore.collection(Constants.USER_COLLECTION)
                        .document(user.uid)
                        .get()
                        .await()

                    val fullProfile = snapshot.toObject(UserProfile::class.java)

                    if (fullProfile != null) {
                        userPreferences.saveUserProfile(fullProfile)
                    }
                } catch (e: java.lang.Exception) {
                    Log.e("AuthRepository", "Failed to sync full profile on login", e)
                }

                emit(Resource.Success(true))
            } else {
                emit(Resource.Error("UNKNOWN_ERROR"))
            }
        } catch (_: IOException) {
            emit(Resource.Error("ERROR_NO_CONNECTION"))
        } catch (e: java.lang.Exception) {
            when (e) {
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
                    } else {
                        emit(Resource.Error(e.message ?: "Login Failed"))
                    }
                }
            }
        }
    }.flowOn(Dispatchers.IO)

    override fun sendPasswordResetEmail(email: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            firebaseAuth.sendPasswordResetEmail(email).await()

            emit(Resource.Success(Unit))
        } catch (_: java.lang.Exception) {
            emit(Resource.Error("ERROR_FAILED_RESET_PASSWORD"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun changePassword(
        currentPass: String,
        newPass: String
    ): Resource<Unit> {
        return try {
            val user = firebaseAuth.currentUser ?: return Resource.Error("ERROR_USER_NOT_FOUND")
            val email = user.email ?: return Resource.Error("ERROR_EMAIL_NOT_FOUND")

            val credential = EmailAuthProvider.getCredential(email, currentPass)

            user.reauthenticate(credential).await()

            user.updatePassword(newPass).await()

            Resource.Success(Unit)
        } catch (_: java.lang.Exception) {
            Resource.Error("ERROR_WRONG_PASSWORD")
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
        userPreferences.clearSession()
    }

    override suspend fun reAuthAndDeleteWithPassword(password: String): Resource<Unit> {
        return try {
            val user = firebaseAuth.currentUser ?: return Resource.Error("ERROR_USER_NOT_FOUND")
            val email = user.email ?: return Resource.Error("ERROR_EMAIL_NOT_FOUND")

            val credential = EmailAuthProvider.getCredential(email, password)

            performDelete(user, credential)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete")
        }
    }

    override suspend fun reAuthAndDeleteWithGoogle(idToken: String): Resource<Unit> {
        return try {
            val user = firebaseAuth.currentUser ?: return Resource.Error("ERROR_USER_NOT_FOUND")

            val credential = GoogleAuthProvider.getCredential(idToken, null)

            performDelete(user, credential)
        } catch (e: java.lang.Exception) {
            Resource.Error(e.message ?: "Failed to delete")
        }
    }

    override fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    private suspend fun saveUserInfo(userId: String, email: String?) {
        val userData = mapOf(
            Constants.FIELD_ID to userId,
            Constants.FIELD_EMAIL to email
        )
        firebaseFirestore.collection(Constants.USER_COLLECTION)
            .document(userId)
            .set(userData)
            .await()
    }

    private suspend fun performDelete(user: FirebaseUser, credential: AuthCredential): Resource<Unit> {
        val uid = user.uid

        user.reauthenticate(credential).await()

        firebaseFirestore.collection(Constants.USER_COLLECTION)
            .document(uid)
            .delete()
            .await()

        user.delete().await()
        return Resource.Success(Unit)
    }
}