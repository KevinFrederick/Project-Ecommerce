package com.kevinfreyap.shared_product.domain.model

data class SearchFilter(
    val minPrice: Int? = null,
    val maxPrice: Int? = null,
    val category: String? = null
)