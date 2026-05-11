package com.kevinfreyap.shared_cart.domain.usecase

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_cart.domain.repository.ICartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemoveCartItemUseCase @Inject constructor(
    private val cartRepository: ICartRepository
) {
    operator fun invoke(productId: String): Flow<Resource<Boolean>> {
        return cartRepository.removeItemFromCart(productId)
    }
}