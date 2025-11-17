package com.kevinfreyap.core.di

import android.content.Context
import android.net.ConnectivityManager
import com.kevinfreyap.core.domain.usecase.product.ProductInteractor
import com.kevinfreyap.core.domain.usecase.product.ProductUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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