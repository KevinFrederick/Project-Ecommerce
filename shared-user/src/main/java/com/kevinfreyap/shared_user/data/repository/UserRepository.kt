package com.kevinfreyap.shared_user.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.notification.NotificationPreferences
import com.kevinfreyap.core.utils.Constants
import com.kevinfreyap.core.utils.isGoogleAccount
import com.kevinfreyap.shared_user.data.source.local.UserPreference
import com.kevinfreyap.shared_user.domain.model.UserAddress
import com.kevinfreyap.shared_user.domain.model.UserProfile
import com.kevinfreyap.shared_user.domain.repository.IUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
    private val userPreferences: UserPreference
): IUserRepository {
    override fun getUserProfile(): Flow<Resource<UserProfile>> {
        return userPreferences.getUserProfile()
            .map { profile ->
                val user = firebaseAuth.currentUser
                if (profile.uid.isNotEmpty()) {
                    Resource.Success(profile)
                } else if (user != null) {
                    val isGoogle = user.isGoogleAccount()

                    Resource.Success(
                        UserProfile(
                            uid = firebaseAuth.currentUser?.uid ?: "",
                            email = firebaseAuth.currentUser?.email,
                            displayName = firebaseAuth.currentUser?.displayName,
                            photoUrl = firebaseAuth.currentUser?.photoUrl.toString(),
                            address = null,
                            isGoogleAccount = isGoogle
                        )
                    )
                } else {
                    Resource.Success(UserProfile())
                }
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

            firebaseFirestore.collection(Constants.USER_COLLECTION)
                .document(currentUser.uid)
                .update(Constants.FIELD_NAME, newName)
                .await()

            val currentCachedProfile = userPreferences.getUserProfile().first()
            val updatedProfile = currentCachedProfile.copy(displayName = newName)

            userPreferences.saveUserProfile(updatedProfile)

            emit(Resource.Success(Unit))
        } catch (e: Exception) {
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

            firebaseFirestore.collection(Constants.USER_COLLECTION)
                .document(currentUser.uid)
                .update(Constants.FIELD_ADDRESS, newAddress)
                .await()

            val currentProfile = userPreferences.getUserProfile().first()
            val updatedProfile = currentProfile.copy(address = newAddress)

            userPreferences.saveUserProfile(updatedProfile)

            emit(Resource.Success(Unit))
        } catch (e: java.lang.Exception) {
            emit(Resource.Error(e.message ?: "Failed to update address"))
        }
    }.flowOn(Dispatchers.IO)

    override fun getTheme(): Flow<Int> = userPreferences.themeMode

    override fun getNotificationSettings(): Flow<NotificationPreferences> {
        return userPreferences.getNotificationSettings()
    }

    override suspend fun syncProfileFromRemote(uid: String): Resource<Unit> {
        return try {
            val snapshot = firebaseFirestore.collection(Constants.USER_COLLECTION)
                .document(uid)
                .get()
                .await()

            val fullProfile = snapshot.toObject(UserProfile::class.java)

            if (fullProfile != null) {
                userPreferences.saveUserProfile(fullProfile)
            }
            Resource.Success(Unit)
        } catch (e: java.lang.Exception) {
            Log.e("AuthRepository", "Failed to sync full profile on login", e)
            Resource.Error("Login Failed")
        }
    }

    override suspend fun saveLocalProfile(profile: UserProfile) {
        userPreferences.saveUserProfile(profile)
    }

    override suspend fun refreshUserProfile() {
        val uid = firebaseAuth.currentUser?.uid ?: return

        try {
            val snapshot = firebaseFirestore.collection(Constants.USER_COLLECTION)
                .document(uid)
                .get()
                .await()

            val remoteProfile = snapshot.toObject(UserProfile::class.java)

            if (remoteProfile != null) {
                userPreferences.saveUserProfile(remoteProfile)
            }
        } catch (e: java.lang.Exception) {
            Log.e("UserRepo", "Background sync failed: ${e.message}")
        }
    }

    override suspend fun clearLocalUser() {
        userPreferences.clearLocalUserProfile()
    }

    override suspend fun saveTheme(mode: Int) {
        userPreferences.saveTheme(mode)
    }

    override suspend fun updateNotificationSetting(
        isSystem: Boolean,
        isEnabled: Boolean
    ) {
        userPreferences.saveNotificationSettings(isSystem = isSystem, isEnabled = isEnabled)

        val field = if (isSystem) "settings.notification.system" else "settings.notification.promotion"
        val uid = firebaseAuth.currentUser?.uid ?: return

        try {
            firebaseFirestore.collection(Constants.USER_COLLECTION)
                .document(uid)
                .update(field, isEnabled)
                .await()
        } catch (e: Exception) {
            Log.e("UserRepo", "Failed to sync notification setting", e)
        }
    }
}