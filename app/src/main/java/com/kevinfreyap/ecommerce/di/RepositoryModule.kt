package com.kevinfreyap.ecommerce.di

import com.kevinfreyap.ecommerce.data.ProductRepository
import com.kevinfreyap.ecommerce.domain.repository.IProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused")
@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun provideRepository(productRepository: ProductRepository): IProductRepository
}