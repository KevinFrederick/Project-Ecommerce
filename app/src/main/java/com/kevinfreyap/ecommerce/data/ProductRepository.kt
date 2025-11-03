package com.kevinfreyap.ecommerce.data

import com.kevinfreyap.ecommerce.data.source.remote.RemoteDataSource
import com.kevinfreyap.ecommerce.data.source.remote.network.ApiResponse
import com.kevinfreyap.ecommerce.domain.model.Product
import com.kevinfreyap.ecommerce.domain.repository.IProductRepository
import com.kevinfreyap.ecommerce.utils.DataMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
): IProductRepository {
    override fun getProducts(): Flow<Resource<List<Product>>> = flow {
        emit(Resource.Loading())

        remoteDataSource.getProducts().collect { productsResponse ->
            when(productsResponse) {
                is ApiResponse.Success -> {
                    val data = DataMapper.mapProductsResponseToDomain(productsResponse.data)
                    emit(Resource.Success(data))
                }
                is ApiResponse.Error -> {
                    emit(Resource.Error(productsResponse.errorMessage))
                }
                is ApiResponse.Empty -> {
                    emit(Resource.Success(emptyList()))
                }
            }
        }
    }.catch { e ->
        emit(Resource.Error(e.message ?: "Unknown Error"))
    }
}