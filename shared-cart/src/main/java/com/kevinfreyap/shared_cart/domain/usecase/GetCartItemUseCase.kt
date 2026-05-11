package com.kevinfreyap.shared_cart.domain.usecase

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_cart.domain.model.Cart
import com.kevinfreyap.shared_cart.domain.repository.ICartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCartItemUseCase @Inject constructor(
    private val cartRepository: ICartRepository
) {
    operator fun invoke(): Flow<Resource<List<Cart>>> = cartRepository.getCartItems()
}