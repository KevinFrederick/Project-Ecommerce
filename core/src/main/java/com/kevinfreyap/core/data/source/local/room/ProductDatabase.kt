package com.kevinfreyap.core.data.source.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kevinfreyap.core.data.source.local.converters.PaymentMethodConverters
import com.kevinfreyap.core.data.source.local.converters.ProductConverters
import com.kevinfreyap.core.data.source.local.converters.TransactionTypeConverters
import com.kevinfreyap.core.data.source.local.entity.CartEntity
import com.kevinfreyap.core.data.source.local.entity.ProductEntity
import com.kevinfreyap.core.data.source.local.entity.RemoteKeyEntity
import com.kevinfreyap.core.data.source.local.entity.TransactionEntity
import com.kevinfreyap.core.data.source.local.entity.WishlistEntity

@Database(
    entities = [
        ProductEntity::class,
        CartEntity::class,
        RemoteKeyEntity::class,
        TransactionEntity::class,
        WishlistEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    ProductConverters::class,
    TransactionTypeConverters::class,
    PaymentMethodConverters::class
)
abstract class ProductDatabase: RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun transactionDao(): TransactionDao
    abstract fun wishlistDao(): WishlistDao
}