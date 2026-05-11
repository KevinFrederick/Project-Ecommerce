package com.kevinfreyap.shared_cart.di

import com.kevinfreyap.core.domain.event.IAuthEvenListener
import com.kevinfreyap.shared_cart.domain.event.CartAuthListener
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class CartEventModule {
    @Binds
    @IntoSet // Tells Hilt: "Add this to the giant set of Auth Listeners!"
    abstract fun bindCartAuthListener(
        impl: CartAuthListener
    ): IAuthEvenListener
}