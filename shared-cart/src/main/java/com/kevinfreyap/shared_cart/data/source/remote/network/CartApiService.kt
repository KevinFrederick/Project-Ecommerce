package com.kevinfreyap.shared_cart.data.source.remote.network

import com.kevinfreyap.shared_cart.data.source.remote.response.CartProductResponseItem
import retrofit2.http.GET
import retrofit2.http.Path

interface CartApiService {
    @GET("products/{id}")
    suspend fun getProductById(
        @Path("id") productId: Int
    ): CartProductResponseItem
}