package com.kevinfreyap.shared_product.di

import com.kevinfreyap.shared_product.data.repository.ProductRepository
import com.kevinfreyap.shared_product.domain.repository.IProductRepository
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
    abstract fun provideProductRepository(productRepository: ProductRepository): IProductRepository
}