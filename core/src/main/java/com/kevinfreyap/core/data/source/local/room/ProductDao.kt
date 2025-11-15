package com.kevinfreyap.core.data.source.local.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kevinfreyap.core.data.source.local.entity.ProductEntity
import com.kevinfreyap.core.data.source.local.entity.RemoteKeyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProducts(products: List<ProductEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRemoteKeys(remoteKeys: List<RemoteKeyEntity>)

    @Query("SELECT * FROM product")
    fun getProducts(): PagingSource<Int, ProductEntity>

    @Query("SELECT * FROM product WHERE id in (:ids)")
    fun getProductByIds(ids: List<Int>): List<ProductEntity>

    @Query("SELECT * FROM product WHERE id = :id")
    fun getProductById(id: Int): Flow<ProductEntity?>

    @Query("DELETE FROM product WHERE id = :id")
    suspend fun deleteProductById(id: Int)

    @Query("SELECT * FROM remote_keys WHERE productId = :id")
    suspend fun getRemoteKeysById(id: Int): RemoteKeyEntity?

    @Query("SELECT * FROM remote_keys ORDER BY productId DESC LIMIT 1")
    suspend fun getLastRemoteKey(): RemoteKeyEntity?

    @Query("DELETE FROM product")
    suspend fun clearAllProduct()

    @Query("DELETE FROM remote_keys")
    suspend fun clearRemoteKeys()
}