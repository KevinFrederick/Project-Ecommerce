package com.kevinfreyap.shared_cart.domain.usecase

import com.kevinfreyap.shared_cart.domain.repository.ICartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCartItemCountUseCase @Inject constructor(
    private val cartRepository: ICartRepository
) {
    operator fun invoke(): Flow<Int> = cartRepository.getCartItemCount()
}