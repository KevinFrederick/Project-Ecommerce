package com.kevinfreyap.shared_wishlist.di

import com.kevinfreyap.core.domain.event.IAuthEvenListener
import com.kevinfreyap.shared_wishlist.data.event.WishlistAuthListener
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class WishlistEventModule {
    @Binds
    @IntoSet
    abstract fun bindWishlistAuthListener(
        impl: WishlistAuthListener
    ): IAuthEvenListener
}