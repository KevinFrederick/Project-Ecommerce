package com.kevinfreyap.shared_user.domain.repository

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.notification.NotificationPreferences
import com.kevinfreyap.shared_user.domain.model.UserAddress
import com.kevinfreyap.shared_user.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface IUserRepository {
    fun getUserProfile(): Flow<Resource<UserProfile>>
    fun updateUserName(newName: String): Flow<Resource<Unit>>
    fun updateAddress(newAddress: UserAddress): Flow<Resource<Unit>>
    fun getTheme(): Flow<Int>
    fun getNotificationSettings(): Flow<NotificationPreferences>
    suspend fun syncProfileFromRemote(uid: String): Resource<Unit>
    suspend fun saveLocalProfile(profile: UserProfile)
    suspend fun refreshUserProfile()
    suspend fun clearLocalUser()
    suspend fun saveTheme(mode: Int)
    suspend fun updateNotificationSetting(isSystem: Boolean, isEnabled: Boolean)
}