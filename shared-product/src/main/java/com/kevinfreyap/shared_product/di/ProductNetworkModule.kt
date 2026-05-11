package com.kevinfreyap.shared_product.di

import com.kevinfreyap.core.BuildConfig
import com.kevinfreyap.shared_product.data.source.remote.network.ProductApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProductNetworkModule {
    @Provides
    @Singleton
    fun provideApiService(client: OkHttpClient): ProductApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(ProductApiService::class.java)
    }
}