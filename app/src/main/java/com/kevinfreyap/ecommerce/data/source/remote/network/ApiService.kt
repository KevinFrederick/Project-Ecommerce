package com.kevinfreyap.ecommerce.data.source.remote.network

import com.kevinfreyap.ecommerce.data.source.remote.response.ProductsResponseItem
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("products")
    suspend fun getProducts(
        @Query("limit") limit: Int = 0,
        @Query("offset") offset: Int = 0
    ): List<ProductsResponseItem>
}