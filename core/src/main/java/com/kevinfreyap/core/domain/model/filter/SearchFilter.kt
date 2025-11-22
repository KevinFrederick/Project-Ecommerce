package com.kevinfreyap.core.domain.model.filter

data class SearchFilter(
    val minPrice: Int? = null,
    val maxPrice: Int? = null,
    val category: String? = null
)