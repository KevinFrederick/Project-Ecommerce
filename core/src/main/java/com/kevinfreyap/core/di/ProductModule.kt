package com.kevinfreyap.core.di

import com.kevinfreyap.core.domain.usecase.product.ProductInteractor
import com.kevinfreyap.core.domain.usecase.product.ProductUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class ProductModule {
    @Binds
    @Singleton
    abstract fun provideProductUseCase(productInteractor: ProductInteractor): ProductUseCase
}