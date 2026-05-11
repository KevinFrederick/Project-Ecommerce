package com.kevinfreyap.shared_product.data.source.local.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.kevinfreyap.shared_product.data.source.local.entity.RemoteKeyEntity
import com.kevinfreyap.shared_product.data.source.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProducts(products: List<ProductEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRemoteKeys(remoteKeys: List<RemoteKeyEntity>)

    @RawQuery(observedEntities = [ProductEntity::class])
    fun getProducts(query: SupportSQLiteQuery): PagingSource<Int, ProductEntity>

    @Query("SELECT * FROM product WHERE id IN (:ids)")
    fun getProductByIds(ids: List<String>): Flow<List<ProductEntity>>

    @Query("SELECT * FROM product WHERE id = :id")
    fun getProductById(id: String): Flow<ProductEntity?>

    @Query("DELETE FROM product WHERE id = :id")
    suspend fun deleteProductById(id: String)

    @Query("SELECT * FROM remote_keys WHERE productId = :id")
    suspend fun getRemoteKeysById(id: Int): RemoteKeyEntity?

    @Query("SELECT * FROM remote_keys ORDER BY productId DESC LIMIT 1")
    suspend fun getLastRemoteKey(): RemoteKeyEntity?

    @Query("DELETE FROM product")
    suspend fun clearAllProduct()

    @Query("DELETE FROM remote_keys")
    suspend fun clearRemoteKeys()
}