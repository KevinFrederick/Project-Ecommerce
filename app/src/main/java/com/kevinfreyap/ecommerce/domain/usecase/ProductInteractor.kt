package com.kevinfreyap.ecommerce.domain.usecase

import com.kevinfreyap.ecommerce.data.ProductRepository
import com.kevinfreyap.ecommerce.data.Resource
import com.kevinfreyap.ecommerce.domain.model.Product
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductInteractor @Inject constructor(private val productRepository: ProductRepository): ProductUseCase {
    override fun getProducts(): Flow<Resource<List<Product>>> = productRepository.getProducts()
}