package com.kevinfreyap.shared_user.di

import com.kevinfreyap.shared_user.data.repository.UserRepository
import com.kevinfreyap.shared_user.domain.repository.IUserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class UserModule {

    @Binds
    @Singleton
    abstract fun provideUserRepository(userRepository: UserRepository): IUserRepository
}