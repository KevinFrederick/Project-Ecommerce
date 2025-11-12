package com.kevinfreyap.core.di

import com.kevinfreyap.core.domain.usecase.cart.CartInteractor
import com.kevinfreyap.core.domain.usecase.cart.CartUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class CartModule {
    @Binds
    @Singleton
    abstract fun provideCartUseCase(cartInteractor: CartInteractor): CartUseCase
}