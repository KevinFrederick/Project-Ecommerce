package com.kevinfreyap.core.domain.usecase.product

import com.kevinfreyap.core.data.repository.ProductRepository
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.domain.model.product.Product
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductInteractor @Inject constructor(private val productRepository: ProductRepository): ProductUseCase {
    override fun getProducts(): Flow<Resource<List<Product>>> = productRepository.getProducts()
}