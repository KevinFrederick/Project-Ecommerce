package com.kevinfreyap.shared_product.domain.usecase

import com.kevinfreyap.shared_product.domain.repository.IProductRepository
import javax.inject.Inject

class RefreshCategoriesUseCase @Inject constructor(
    private val productRepository: IProductRepository
) {
    suspend operator fun invoke() {
        productRepository.refreshCategories()
    }
}