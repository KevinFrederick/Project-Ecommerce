package com.kevinfreyap.core.di

import android.content.Context
import androidx.room.Room
import com.kevinfreyap.core.data.paging.ProductRemoteMediator
import com.kevinfreyap.core.data.source.local.room.CartDao
import com.kevinfreyap.core.data.source.local.room.CategoryDao
import com.kevinfreyap.core.data.source.local.room.ProductDao
import com.kevinfreyap.core.data.source.local.room.ProductDatabase
import com.kevinfreyap.core.data.source.local.room.TransactionDao
import com.kevinfreyap.core.data.source.local.room.WishlistDao
import com.kevinfreyap.core.data.source.remote.network.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ProductDatabase {
        return Room.databaseBuilder(
            context,
            ProductDatabase::class.java, "product.db"
        ).build()
    }

    @Provides
    fun provideProductDao(database: ProductDatabase): ProductDao = database.productDao()

    @Provides
    fun provideCartDao(database: ProductDatabase): CartDao = database.cartDao()

    @Provides
    fun provideTransactionDao(database: ProductDatabase): TransactionDao = database.transactionDao()

    @Provides
    fun provideWishlistDao(database: ProductDatabase): WishlistDao = database.wishlistDao()

    @Provides
    fun provideCategoryDao(database: ProductDatabase): CategoryDao = database.categoryDao()

    @Provides
    @Singleton
    fun provideRemoteMediator(
        apiService: ApiService,
        database: ProductDatabase
    ): ProductRemoteMediator {
        return ProductRemoteMediator(apiService, database)
    }
}