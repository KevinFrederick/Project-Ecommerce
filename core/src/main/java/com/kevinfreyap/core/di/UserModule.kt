package com.kevinfreyap.core.di

import com.kevinfreyap.core.domain.usecase.user.UserInteractor
import com.kevinfreyap.core.domain.usecase.user.UserUseCase
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
    abstract fun provideUserUseCase(userInteractor: UserInteractor): UserUseCase
}