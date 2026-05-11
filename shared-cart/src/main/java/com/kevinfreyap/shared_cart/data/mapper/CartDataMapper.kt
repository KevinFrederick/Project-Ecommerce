package com.kevinfreyap.shared_cart.data.mapper

import com.kevinfreyap.shared_cart.data.source.local.entity.CartEntity
import com.kevinfreyap.core.utils.DateHelper
import com.kevinfreyap.shared_cart.data.source.remote.response.CartProductResponseItem
import com.kevinfreyap.shared_cart.domain.model.CartProduct

object CartDataMapper {
    fun mapCartEntityToDomain(entity: CartEntity): CartProduct {
        return CartProduct(
            id = entity.productId,
            title = entity.name,
            price = entity.price,
            images = listOf(entity.imageUrl),
            creationAt = 0L,
            updatedAt = 0L
        )
    }

    fun mapProductResponseToDomain(response: CartProductResponseItem): CartProduct {
        return CartProduct(
            id = response.id.toString(),
            title = response.title,
            price = response.price,
            images = response.images,
            creationAt = DateHelper.parseIsoStringToLong(response.creationAt),
            updatedAt = DateHelper.parseIsoStringToLong(response.updatedAt)
        )
    }
}