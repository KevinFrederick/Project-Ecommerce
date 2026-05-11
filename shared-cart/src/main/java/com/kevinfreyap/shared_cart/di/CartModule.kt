package com.kevinfreyap.shared_cart.di

import com.kevinfreyap.shared_cart.data.repository.CartRepository
import com.kevinfreyap.shared_cart.domain.repository.ICartRepository
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
    abstract fun provideCartRepository(cartRepository: CartRepository): ICartRepository
}