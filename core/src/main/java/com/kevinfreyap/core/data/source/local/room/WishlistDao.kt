package com.kevinfreyap.core.data.source.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kevinfreyap.core.data.source.local.entity.WishlistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WishlistDao {
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlist(watchlist: WishlistEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllWatchlist(list: List<WishlistEntity>)

    @Query("SELECT * FROM watchlist ORDER BY dateAdded DESC")
    fun getAllWatchlist(): Flow<List<WishlistEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist WHERE productId = :id LIMIT 1)")
    fun isProductInWatchlist(id: String): Flow<Boolean>

    @Query("UPDATE watchlist SET isAvailable = :available WHERE productId = :id")
    suspend fun updateAvailability(id: String, available: Boolean)

    @Query("UPDATE watchlist SET isNotified = :isNotified WHERE productId = :id")
    suspend fun updateNotificationStatus(id: String, isNotified: Boolean)

    @Query("SELECT * FROM watchlist")
    suspend fun getAllSync(): List<WishlistEntity>

    @Query("DELETE FROM watchlist WHERE productId = :id")
    suspend fun deleteWatchlistById(id: String)

    @Query("DELETE FROM watchlist")
    suspend fun clearWatchlist()
}