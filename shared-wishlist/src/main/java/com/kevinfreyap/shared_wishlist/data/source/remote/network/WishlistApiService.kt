package com.kevinfreyap.shared_wishlist.data.source.remote.network

import com.kevinfreyap.shared_wishlist.data.source.remote.response.ProductsResponseItem
import retrofit2.http.GET
import retrofit2.http.Path

interface WishlistApiService {
    @GET("products/{id}")
    suspend fun getProductById(
        @Path("id") productId: Int
    ): ProductsResponseItem
}