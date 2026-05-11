package com.kevinfreyap.shared_auth.di

import com.kevinfreyap.shared_auth.data.repository.AuthenticationRepository
import com.kevinfreyap.shared_auth.domain.repository.IAuthenticationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {
    @Binds
    @Singleton
    abstract fun provideAuthRepository(authenticationRepository: AuthenticationRepository): IAuthenticationRepository
}