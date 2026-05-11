package com.kevinfreyap.shared_user.di

import com.kevinfreyap.core.domain.event.IAuthEvenListener
import com.kevinfreyap.shared_user.data.event.UserAuthListener
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class UserEventModule {
    @Binds
    @IntoSet
    abstract fun bindUserAuthListener(
        impl: UserAuthListener
    ): IAuthEvenListener
}