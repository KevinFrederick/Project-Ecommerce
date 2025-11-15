package com.kevinfreyap.core.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.data.paging.ProductRemoteMediator
import com.kevinfreyap.core.data.source.local.room.ProductDatabase
import com.kevinfreyap.core.data.source.remote.network.ApiService
import com.kevinfreyap.core.domain.model.product.Product
import com.kevinfreyap.core.domain.repository.IProductRepository
import com.kevinfreyap.core.utils.DataMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val database: ProductDatabase,
    private val apiService: ApiService,
    private val remoteMediator: ProductRemoteMediator
): IProductRepository {

    private val productDao = database.productDao()

    @OptIn(ExperimentalPagingApi::class)
    override fun getProducts(): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            remoteMediator = remoteMediator,
            pagingSourceFactory = {
                productDao.getProducts()
            }
        ).flow
            .map { pagingData ->
                pagingData.map { productEntity ->
                    DataMapper.mapEntityToDomain(productEntity)
                }
            }
            .flowOn(Dispatchers.IO)
    }

    override fun getProductById(productId: Int): Flow<Resource<Product?>> = flow {

        val dbFlow = productDao.getProductById(productId).map { entity ->
            entity?.let { DataMapper.mapEntityToDomain(it) }
        }
        emit(Resource.Loading(dbFlow.first()))

        try {
            val response = apiService.getProductById(productId)
            val productEntity = DataMapper.mapProductResponseToEntity(response)

            database.withTransaction {
                productDao.insertAllProducts(listOf(productEntity))
            }

            dbFlow.collect { data ->
                emit(Resource.Success(data))
            }

        }  catch (e: HttpException) {
            if (e.code() == 404 || e.code() == 400) {
                productDao.deleteProductById(productId)
                emit(Resource.Error("ERROR_PRODUCT_UNAVAILABLE"))
            } else {
                val staleData = dbFlow.first()
                emit(Resource.Error(e.message.toString(), staleData))
            }
        } catch (e: Exception) {
            val staleData = dbFlow.first()
            emit(Resource.Error(e.message ?: "Unknown Error", staleData))
        }
    }

    override suspend fun getProductByIdFromCache(productIds: List<Int>): List<Product> {
        return productDao.getProductByIds(productIds).map { productEntity ->
            DataMapper.mapEntityToDomain(productEntity)
        }
    }
}