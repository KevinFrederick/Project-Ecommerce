package com.kevinfreyap.core.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.kevinfreyap.core.data.paging.ProductRemoteMediator
import com.kevinfreyap.core.data.source.local.room.ProductDatabase
import com.kevinfreyap.core.domain.model.product.Product
import com.kevinfreyap.core.domain.repository.IProductRepository
import com.kevinfreyap.core.utils.DataMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val database: ProductDatabase,
    private val remoteMediator: ProductRemoteMediator
): IProductRepository {
    @OptIn(ExperimentalPagingApi::class)
    override fun getProducts(): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            remoteMediator = remoteMediator,
            pagingSourceFactory = {
                database.productDao().getProducts()
            }
        ).flow
            .map { pagingData ->
                pagingData.map { productEntity ->
                    DataMapper.mapEntityToDomain(productEntity)
                }
            }
    }
}