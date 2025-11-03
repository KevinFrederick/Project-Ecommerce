package com.kevinfreyap.ecommerce.data.source.remote

import android.util.Log
import com.kevinfreyap.ecommerce.data.source.remote.network.ApiResponse
import com.kevinfreyap.ecommerce.data.source.remote.network.ApiService
import com.kevinfreyap.ecommerce.data.source.remote.response.ProductsResponseItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val apiService: ApiService) {

    fun getProducts(): Flow<ApiResponse<List<ProductsResponseItem>>> {
        return flow {
            try {
                val products = apiService.getProducts()

                if (products.isNotEmpty()){
                    emit(ApiResponse.Success(products))
                } else {
                    emit(ApiResponse.Empty)
                }
            } catch (e: Exception){
                emit(ApiResponse.Error(e.message.toString()))
                Log.e("RemoteDataSource", "getProducts : ${e.message.toString()}")
            }
        }
    }
}