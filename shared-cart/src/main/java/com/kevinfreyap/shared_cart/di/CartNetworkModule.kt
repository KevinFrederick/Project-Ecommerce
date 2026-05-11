package com.kevinfreyap.shared_cart.di

import com.kevinfreyap.core.BuildConfig
import com.kevinfreyap.shared_cart.data.source.remote.network.CartApiService
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
object CartNetworkModule {
    @Provides
    @Singleton
    fun provideCartApiService(client: OkHttpClient): CartApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(CartApiService::class.java)
    }
}