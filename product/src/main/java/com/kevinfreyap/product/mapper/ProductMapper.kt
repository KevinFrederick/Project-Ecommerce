package com.kevinfreyap.product.mapper

import com.kevinfreyap.product.model.ProductUi
import com.kevinfreyap.shared_product.domain.model.Product

fun Product.toUiModel(): ProductUi{
    return ProductUi(
        id = this.id,
        name = this.title,
        price = this.price,
        imageUrls = this.images
    )
}