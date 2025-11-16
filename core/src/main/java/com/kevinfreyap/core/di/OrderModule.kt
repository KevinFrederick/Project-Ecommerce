package com.kevinfreyap.core.di

import com.kevinfreyap.core.domain.usecase.order.OrderInteractor
import com.kevinfreyap.core.domain.usecase.order.OrderUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class OrderModule {
    @Binds
    @Singleton
    abstract fun provideOrderUseCase(orderInteractor: OrderInteractor): OrderUseCase
}