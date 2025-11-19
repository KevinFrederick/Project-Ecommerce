package com.kevinfreyap.core.domain.usecase.wishlist

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.product.Product
import com.kevinfreyap.core.domain.repository.IProductRepository
import com.kevinfreyap.core.domain.repository.IWishlistRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WishlistInteractor @Inject constructor(
    private val wishlistRepository: IWishlistRepository,
    private val productRepository: IProductRepository
): WishlistUseCase {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getWishlist(): Flow<Resource<List<Product>>> {
        return wishlistRepository.getWishlist()
            .flatMapLatest { resource ->
                when(resource) {
                    is Resource.Loading -> {
                        flowOf(Resource.Loading())
                    }
                    is Resource.Success -> {
                        val wishlistItems = resource.data

                        val ids = wishlistItems.map { it.productId }

                        productRepository.getProductByIdFromCache(ids).map { productResource ->
                            if (productResource is Resource.Success ) {
                                val products = productResource.data

                                val dateMap = wishlistItems.associate { it.productId to it.dateAdded }

                                val sortedProducts = products.sortedByDescending { product ->
                                    dateMap[product.id] ?: 0L
                                }

                                Resource.Success(sortedProducts)
                            } else {
                                productResource
                            }
                        }
                    }
                    is Resource.Error -> {
                        flowOf(Resource.Error(resource.message ?: "Error loading wishlist"))
                    }
                }
            }
    }

    override fun observeIsProductInWishlist(productId: String): Flow<Boolean> {
        return wishlistRepository.observeIsProductInWishlist(productId)
    }

    override suspend fun addToWishlist(productId: String) {
        wishlistRepository.addToWishlist(productId)
    }

    override suspend fun removeFromWishlist(productId: String) {
        wishlistRepository.removeFromWishlist(productId)
    }

    override suspend fun syncWishlistOnLogin() {
        wishlistRepository.syncWishlistOnLogin()
    }

    override suspend fun clearWishlist() {
        wishlistRepository.clearWishlist()
    }
}