package com.kevinfreyap.core.data.source.remote.network

import com.kevinfreyap.core.data.source.remote.response.CategoryResponse
import com.kevinfreyap.core.data.source.remote.response.ProductsResponseItem
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("products")
    suspend fun getProducts(
        @Query("limit") limit: Int = 0,
        @Query("offset") offset: Int = 0
    ): List<ProductsResponseItem>

    @GET("products/{id}")
    suspend fun getProductById(
        @Path("id") productId: Int
    ): ProductsResponseItem

    @GET("categories")
    suspend fun getCategories(): List<CategoryResponse>
}