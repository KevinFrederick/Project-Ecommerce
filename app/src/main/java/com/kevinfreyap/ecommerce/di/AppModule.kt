package com.kevinfreyap.ecommerce.di

import com.kevinfreyap.ecommerce.domain.usecase.ProductInteractor
import com.kevinfreyap.ecommerce.domain.usecase.ProductUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds
    @Singleton
    abstract fun provideProductUseCase(productInteractor: ProductInteractor): ProductUseCase
}