package com.kevinfreyap.core.di

import com.kevinfreyap.core.data.repository.AuthenticationRepository
import com.kevinfreyap.core.data.repository.CartRepository
import com.kevinfreyap.core.data.repository.ProductRepository
import com.kevinfreyap.core.domain.repository.IAuthenticationRepository
import com.kevinfreyap.core.domain.repository.ICartRepository
import com.kevinfreyap.core.domain.repository.IProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun provideProductRepository(productRepository: ProductRepository): IProductRepository

    @Binds
    @Singleton
    abstract fun provideAuthRepository(authenticationRepository: AuthenticationRepository): IAuthenticationRepository

    @Binds
    @Singleton
    abstract fun provideCartRepository(cartRepository: CartRepository): ICartRepository
}