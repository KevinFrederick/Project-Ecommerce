package com.kevinfreyap.core.di

import com.kevinfreyap.core.domain.usecase.wishlist.WishlistInteractor
import com.kevinfreyap.core.domain.usecase.wishlist.WishlistUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class WishlistModule {
    @Binds
    @Singleton
    abstract fun provideWishlistUseCase(wishlistInteractor: WishlistInteractor): WishlistUseCase
}