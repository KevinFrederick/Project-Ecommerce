package com.kevinfreyap.shared_product.domain.usecase

import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.shared_product.domain.model.Product
import com.kevinfreyap.shared_product.domain.repository.IProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductByIdUseCase @Inject constructor(
    private val productRepository: IProductRepository
) {
    operator fun invoke(productId: String): Flow<Resource<Product?>> = productRepository.getProductById(productId)
}