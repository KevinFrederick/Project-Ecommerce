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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.IOException
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
    }

    override fun getProductById(productId: Int): Flow<Resource<Product?>> = flow {
        emit(Resource.Loading())

        val dbFlow = productDao.getProductById(productId).map { productEntity ->
            productEntity?.let { DataMapper.mapEntityToDomain(it) }
        }

        try {
            val response = apiService.getProductById(productId)

            val productEntity = DataMapper.mapProductResponseToEntity(response)
            database.withTransaction {
                productDao.insertAllProducts(listOf(productEntity))
            }

            emitAll(dbFlow.map { Resource.Success(it) })
        } catch (_: IOException) {
            emitAll(dbFlow.map { staleData ->
                Resource.Error("ERROR_NO_CONNECTION", staleData)
            })
        } catch (e: HttpException) {
            if (e.code() == 404) {
                productDao.deleteProductById(productId)
                emit(Resource.Error("ERROR_PRODUCT_UNAVAILABLE"))
            } else {
                emitAll(dbFlow.map { staleData ->
                    Resource.Error(e.message(), staleData)
                })
            }
        } catch (e: Exception) {
            emitAll(dbFlow.map { staleData ->
                Resource.Error(e.message.toString(), staleData)
            })
        }
    }

    override fun getProductByIdFromCache(productId: Int): Flow<Product?> {
        return productDao.getProductById(productId).map { productEntity ->
            productEntity?.let { DataMapper.mapEntityToDomain(it) }
        }
    }
}