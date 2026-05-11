package com.kevinfreyap.shared_product.data.source.remote.network

import com.kevinfreyap.shared_product.data.source.remote.response.CategoryResponse
import com.kevinfreyap.shared_product.data.source.remote.response.ProductsResponseItem
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApiService {
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