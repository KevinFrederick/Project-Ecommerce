package com.kevinfreyap.core.domain.usecase.user

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.notification.NotificationPreferences
import com.kevinfreyap.core.domain.model.user.UserAddress
import com.kevinfreyap.core.domain.model.user.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserUseCase {
    fun getUserProfile(): Flow<Resource<UserProfile>>
    fun updateUserName(newName: String): Flow<Resource<Unit>>
    fun updateAddress(newAddress: UserAddress): Flow<Resource<Unit>>
    fun getTheme(): Flow<Int>
    fun getNotificationSettings(): Flow<NotificationPreferences>
    suspend fun refreshUserProfile()
    suspend fun saveTheme(mode: Int)
    suspend fun updateNotificationSetting(isSystem: Boolean, isEnabled: Boolean)
}