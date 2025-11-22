package com.kevinfreyap.core.data.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.kevinfreyap.core.utils.ProductQuery
import com.kevinfreyap.core.data.Resource
import com.kevinfreyap.core.data.paging.ProductRemoteMediator
import com.kevinfreyap.core.data.source.local.entity.ProductEntity
import com.kevinfreyap.core.data.source.local.room.CategoryDao
import com.kevinfreyap.core.data.source.local.room.ProductDatabase
import com.kevinfreyap.core.data.source.remote.network.ApiService
import com.kevinfreyap.core.domain.model.filter.SearchFilter
import com.kevinfreyap.core.domain.model.product.Product
import com.kevinfreyap.core.domain.model.product.ProductCategory
import com.kevinfreyap.core.domain.repository.IProductRepository
import com.kevinfreyap.core.utils.DataMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
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
    private val categoryDao: CategoryDao,
    private val apiService: ApiService,
    private val productQuery: ProductQuery,
    private val remoteMediator: ProductRemoteMediator
): IProductRepository {

    private val productDao = database.productDao()

    @OptIn(ExperimentalPagingApi::class)
    override fun getProducts(query: String, filter: SearchFilter): Flow<PagingData<Product>> {
        val sqLiteQuery = productQuery.searchFilterQuery(query, filter)

        return Pager(
            config = PagingConfig(
                pageSize = 60,
                initialLoadSize = 60,
                enablePlaceholders = false
            ),
            remoteMediator = remoteMediator,
            pagingSourceFactory = {
                productDao.getProducts(sqLiteQuery)
            }
        ).flow
            .map { pagingData ->
                pagingData.map { productEntity ->
                    DataMapper.mapEntityToDomain(productEntity)
                }
            }
            .flowOn(Dispatchers.IO)
    }

    override fun getProductById(productId: String): Flow<Resource<Product?>> = flow {

        val dbFlow = productDao.getProductById(productId).map { entity ->
            entity?.let { DataMapper.mapEntityToDomain(it) }
        }
        emit(Resource.Loading(dbFlow.first()))

        try {
            val response = apiService.getProductById(productId.toInt())
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

    override suspend fun getProductByIdFromCache(productIds: List<String>): Flow<Resource<List<Product>>> {
        return productDao.getProductByIds(productIds)
            .map <List<ProductEntity>, Resource<List<Product>>> { productEntity ->
                val domainList = productEntity.map { DataMapper.mapEntityToDomain(it) }
                Resource.Success(domainList)
            }
            .catch { e ->
                emit(Resource.Error(e.message ?: "Failed to fetch products"))
            }
            .flowOn(Dispatchers.IO)
    }

    override fun getCategories(): Flow<Resource<List<ProductCategory>>> = flow {
        emit(Resource.Loading())

        categoryDao.getAllCategories().collect { entities ->
            val categoryDomain = entities.map { DataMapper.mapCategoryEntityToDomain(it) }
            emit(Resource.Success(categoryDomain))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun refreshCategories() {
        try {
            val remoteCategory = apiService.getCategories()
            val entities = remoteCategory.map {
                DataMapper.mapCategoryResponseToEntity(it)
            }
            categoryDao.replaceAll(entities)
        } catch (e: Exception) {
            Log.e("ProductRepository", "Failed to refresh categories: ${e.message}")
        }
    }
}