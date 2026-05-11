package com.kevinfreyap.shared_cart.domain.usecase

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_cart.domain.repository.ICartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateItemQuantityUseCase @Inject constructor(
    private val cartRepository: ICartRepository
) {
    operator fun invoke(
        productId: String,
        newQuantity: Int
    ): Flow<Resource<Boolean>> {
        return cartRepository.updateItemQuantity(productId, newQuantity)
    }
}