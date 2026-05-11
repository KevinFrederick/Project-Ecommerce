package com.kevinfreyap.checkout.di

import com.kevinfreyap.checkout.data.repository.OrderRepository
import com.kevinfreyap.checkout.domain.repository.IOrderRepository
import com.kevinfreyap.checkout.domain.usecase.OrderInteractor
import com.kevinfreyap.checkout.domain.usecase.OrderUseCase
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

    @Binds
    @Singleton
    abstract fun provideOrderRepository(orderRepository: OrderRepository): IOrderRepository
}