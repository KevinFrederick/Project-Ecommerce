package com.kevinfreyap.ecommerce.db

import android.content.Context
import androidx.room.Room
import androidx.room.withTransaction
import com.kevinfreyap.core.data.source.local.DbProductRunner
import com.kevinfreyap.shared_cart.data.source.local.room.CartDao
import com.kevinfreyap.shared_product.data.source.local.room.CategoryDao
import com.kevinfreyap.shared_product.data.source.local.room.ProductDao
import com.kevinfreyap.shared_transaction.data.source.local.room.TransactionDao
import com.kevinfreyap.shared_voucher.data.source.local.room.VoucherDao
import com.kevinfreyap.shared_wishlist.data.source.local.room.WishlistDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, "product.db"
        ).build()
    }

    @Provides
    fun provideProductDao(database: AppDatabase): ProductDao = database.productDao()

    @Provides
    fun provideCartDao(database: AppDatabase): CartDao = database.cartDao()

    @Provides
    fun provideTransactionDao(database: AppDatabase): TransactionDao = database.transactionDao()

    @Provides
    fun provideWishlistDao(database: AppDatabase): WishlistDao = database.wishlistDao()

    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao = database.categoryDao()

    @Provides
    fun provideVoucherDao(database: AppDatabase): VoucherDao = database.voucherDao()

    @Provides
    fun provideProductRunner(database: AppDatabase): DbProductRunner {
        return object : DbProductRunner {
            override suspend fun <T> invoke(block: suspend () -> T): T {
                return database.withTransaction { block() }
            }
        }
    }
}