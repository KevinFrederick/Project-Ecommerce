package com.kevinfreyap.shared_cart.domain.usecase

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_cart.domain.model.CartProduct
import com.kevinfreyap.shared_cart.domain.repository.ICartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddToCartUseCase @Inject constructor(
    private val cartRepository: ICartRepository
) {
    operator fun invoke(
        productId: String,
        productTitle: String,
        productPrice: Int,
        productImages: List<String>,
        quantity: Int
    ): Flow<Resource<Boolean>> {
        val cartProduct = CartProduct(
            id = productId,
            title = productTitle,
            price = productPrice,
            images = productImages,
            creationAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        return cartRepository.addToCart(
            cartProduct,
            quantity
        )
    }
}