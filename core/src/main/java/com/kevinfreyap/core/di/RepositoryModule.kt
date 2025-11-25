package com.kevinfreyap.core.di

import com.kevinfreyap.core.data.repository.AuthenticationRepository
import com.kevinfreyap.core.data.repository.CartRepository
import com.kevinfreyap.core.data.repository.OrderRepository
import com.kevinfreyap.core.data.repository.ProductRepository
import com.kevinfreyap.core.data.repository.TransactionRepository
import com.kevinfreyap.core.data.repository.VoucherRepository
import com.kevinfreyap.core.data.repository.WishlistRepository
import com.kevinfreyap.core.domain.repository.IAuthenticationRepository
import com.kevinfreyap.core.domain.repository.ICartRepository
import com.kevinfreyap.core.domain.repository.IOrderRepository
import com.kevinfreyap.core.domain.repository.IProductRepository
import com.kevinfreyap.core.domain.repository.ITransactionRepository
import com.kevinfreyap.core.domain.repository.IVoucherRepository
import com.kevinfreyap.core.domain.repository.IWishlistRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun provideProductRepository(productRepository: ProductRepository): IProductRepository

    @Binds
    @Singleton
    abstract fun provideAuthRepository(authenticationRepository: AuthenticationRepository): IAuthenticationRepository

    @Binds
    @Singleton
    abstract fun provideCartRepository(cartRepository: CartRepository): ICartRepository

    @Binds
    @Singleton
    abstract fun provideOrderRepository(orderRepository: OrderRepository): IOrderRepository

    @Binds
    @Singleton
    abstract fun provideTransactionRepository(transactionRepository: TransactionRepository): ITransactionRepository

    @Binds
    @Singleton
    abstract fun provideWishlistRepository(wishlistRepository: WishlistRepository): IWishlistRepository

    @Binds
    @Singleton
    abstract fun provideVoucherRepository(voucherRepository: VoucherRepository): IVoucherRepository
}