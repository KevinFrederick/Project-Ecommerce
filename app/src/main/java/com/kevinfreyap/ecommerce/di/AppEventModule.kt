package com.kevinfreyap.ecommerce.di

import com.kevinfreyap.core.domain.event.IAuthEvenListener
import com.kevinfreyap.ecommerce.domain.event.NotificationAuthListener
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class AppEventModule {
    @Binds
    @IntoSet
    abstract fun bindNotificationAuthListener(
        impl: NotificationAuthListener
    ): IAuthEvenListener
}