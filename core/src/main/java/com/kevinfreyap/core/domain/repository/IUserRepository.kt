package com.kevinfreyap.core.domain.repository

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.user.UserAddress
import com.kevinfreyap.core.domain.model.user.UserProfile
import kotlinx.coroutines.flow.Flow

interface IUserRepository {
    fun getUserProfile(): Flow<Resource<UserProfile>>
    fun updateUserName(newName: String): Flow<Resource<Unit>>
    fun updateAddress(newAddress: UserAddress): Flow<Resource<Unit>>
    fun getTheme(): Flow<Int>
    suspend fun refreshUserProfile()
    suspend fun saveTheme(mode: Int)
}