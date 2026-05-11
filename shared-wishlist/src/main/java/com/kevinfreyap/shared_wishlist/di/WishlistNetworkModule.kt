package com.kevinfreyap.shared_wishlist.di

import com.kevinfreyap.core.BuildConfig
import com.kevinfreyap.shared_wishlist.data.source.remote.network.WishlistApiService
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
object WishlistNetworkModule {
    @Provides
    @Singleton
    fun provideApiService(client: OkHttpClient): WishlistApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(WishlistApiService::class.java)
    }
}