package com.kevinfreyap.shared_wishlist.domain.usecase

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_wishlist.domain.model.ProductCategory
import com.kevinfreyap.shared_wishlist.domain.model.WishlistProduct
import com.kevinfreyap.shared_wishlist.domain.repository.IWishlistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddToWishlistUseCase @Inject constructor(
    private val wishlistRepository: IWishlistRepository
) {
    operator fun invoke(
        id: String,
        title: String,
        price: Int,
        categoryId: String,
        categoryName: String,
        categoryImage: String,
        images: List<String>
    ): Flow<Resource<Unit>> {
        val wishlistProduct = WishlistProduct(
            id = id,
            title = title,
            price = price,
            category = ProductCategory(
                id = categoryId,
                name = categoryName,
                image = categoryImage
            ),
            images = images,
            creationAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
        )
        return wishlistRepository.addToWishlist(wishlistProduct)
    }
}