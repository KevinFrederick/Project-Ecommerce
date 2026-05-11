package com.kevinfreyap.shared_wishlist.di

import com.kevinfreyap.shared_wishlist.data.repository.WishlistRepository
import com.kevinfreyap.shared_wishlist.domain.repository.IWishlistRepository
import com.kevinfreyap.shared_wishlist.domain.usecase.WishlistInteractor
import com.kevinfreyap.shared_wishlist.domain.usecase.WishlistUseCase
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

    @Binds
    @Singleton
    abstract fun provideWishlistRepository(wishlistRepository: WishlistRepository): IWishlistRepository
}