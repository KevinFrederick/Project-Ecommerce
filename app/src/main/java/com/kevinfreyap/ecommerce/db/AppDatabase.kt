package com.kevinfreyap.ecommerce.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kevinfreyap.shared_product.data.converter.ProductConverters
import com.kevinfreyap.shared_product.data.source.local.entity.CategoryEntity
import com.kevinfreyap.shared_product.data.source.local.room.CategoryDao
import com.kevinfreyap.shared_cart.data.source.local.entity.CartEntity
import com.kevinfreyap.shared_cart.data.source.local.room.CartDao
import com.kevinfreyap.shared_product.data.source.local.entity.ProductEntity
import com.kevinfreyap.shared_product.data.source.local.entity.RemoteKeyEntity
import com.kevinfreyap.shared_product.data.source.local.room.ProductDao
import com.kevinfreyap.shared_transaction.data.converter.OrderTypeConverters
import com.kevinfreyap.shared_transaction.data.converter.PaymentMethodConverters
import com.kevinfreyap.shared_transaction.data.converter.TransactionTypeConverters
import com.kevinfreyap.shared_transaction.data.source.local.entity.TransactionEntity
import com.kevinfreyap.shared_transaction.data.source.local.room.TransactionDao
import com.kevinfreyap.shared_voucher.data.source.local.entity.VoucherEntity
import com.kevinfreyap.shared_voucher.data.source.local.room.VoucherDao
import com.kevinfreyap.shared_wishlist.data.source.local.entity.WishlistEntity
import com.kevinfreyap.shared_wishlist.data.source.local.room.WishlistDao

@Database(
    entities = [
        ProductEntity::class,
        CartEntity::class,
        RemoteKeyEntity::class,
        TransactionEntity::class,
        WishlistEntity::class,
        CategoryEntity::class,
        VoucherEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    ProductConverters::class,
    TransactionTypeConverters::class,
    PaymentMethodConverters::class,
    OrderTypeConverters::class
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun transactionDao(): TransactionDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun categoryDao(): CategoryDao
    abstract fun voucherDao(): VoucherDao
}