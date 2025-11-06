package com.kevinfreyap.core.di

import com.kevinfreyap.core.domain.usecase.auth.AuthInteractor
import com.kevinfreyap.core.domain.usecase.auth.AuthUseCase
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
    abstract fun provideAuthUseCase(authInteractor: AuthInteractor): AuthUseCase
}