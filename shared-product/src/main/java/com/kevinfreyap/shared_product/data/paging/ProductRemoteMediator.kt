package com.kevinfreyap.shared_product.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.kevinfreyap.core.data.source.local.DbProductRunner
import com.kevinfreyap.shared_product.data.mapper.DataMapper
import com.kevinfreyap.shared_product.data.source.local.entity.ProductEntity
import com.kevinfreyap.shared_product.data.source.local.entity.RemoteKeyEntity
import com.kevinfreyap.shared_product.data.source.local.room.ProductDao
import com.kevinfreyap.shared_product.data.source.remote.network.ProductApiService
import javax.inject.Inject
import kotlin.collections.map

@OptIn(ExperimentalPagingApi::class)
class ProductRemoteMediator @Inject constructor(
    private val productApiService: ProductApiService,
    private val productDao: ProductDao,
    private val dbRunner: DbProductRunner
): RemoteMediator<Int, ProductEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ProductEntity>
    ): MediatorResult {
        try {
            // Determine the offset (page) to load
            val loadKey = when(loadType) {
                // REFRESH: User is swiping to refresh. Load from the beginning.
                LoadType.REFRESH -> 0
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                // APPEND: User is scrolling down. Load the next page.
                LoadType.APPEND -> {
                    // 1. Check if we can find the last item
                    val lastItem = state.lastItemOrNull()
                    if (lastItem == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    // 2. Check if we can find the key for that item
                    val remoteKey = productDao.getRemoteKeysById(lastItem.id)
                    if (remoteKey == null) {
                        return MediatorResult.Success(endOfPaginationReached = false)
                    }

                    // 3. Check the next offset
                    val nextKey = remoteKey.nextOffset
                    if (nextKey == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    nextKey
                }
            }

            // Call API with calculated offset
            val response = productApiService.getProducts(
                limit = state.config.pageSize,
                offset = loadKey
            )

            val endOfPaginationReached = response.isEmpty() || response.size < state.config.pageSize

            // Map API response to Room Entity
            val productEntities = DataMapper.mapProductsResponseToEntity(response)

            // Calculate new keys
            val newOffset = if (endOfPaginationReached) {
                null
            } else {
                loadKey + response.size
            }
            val keyEntities = response.map {
                RemoteKeyEntity(productId = it.id, nextOffset = newOffset)
            }

            // Save data to Room in a single transaction
            dbRunner {
                if (loadType == LoadType.REFRESH) {
                    productDao.clearAllProduct()
                    productDao.clearRemoteKeys()
                }
                productDao.insertAllProducts(productEntities)
                productDao.insertAllRemoteKeys(keyEntities)
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }
}