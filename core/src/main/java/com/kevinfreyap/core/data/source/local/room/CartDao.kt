package com.kevinfreyap.core.data.source.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kevinfreyap.core.data.source.local.entity.CartEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_table ORDER BY isAvailable DESC, dateAdded DESC")
    fun getAllCartItems(): Flow<List<CartEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cartItem: CartEntity)

    // Bulk insert for syncing
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cartItems: List<CartEntity>)

    @Update
    suspend fun update(cartEntity: CartEntity)

    @Query("DELETE FROM cart_table WHERE productId = :id")
    suspend fun deleteItem(id: String)

    @Query("DELETE FROM cart_table")
    suspend fun clearCart()

    @Query("UPDATE cart_table SET quantity = :newQuantity WHERE productId = :id")
    suspend fun updateQuantity(id: String, newQuantity: Int)

    @Query("SELECT COUNT(*) FROM cart_table WHERE isAvailable = 1")
    fun getCartItemCount(): Flow<Int>

    // Helper to get current quantity (needed for error rollback)
    @Query("SELECT quantity FROM cart_table WHERE productId = :id")
    suspend fun getQuantity(id: String): Int?

    @Query("SELECT * FROM cart_table WHERE productId = :id")
    suspend fun getCartItemById(id: String): CartEntity?

    // The "Soft Delete" - Mark as grayed out
    @Query("UPDATE cart_table SET isAvailable = 0 WHERE productId = :id")
    suspend fun markAsUnavailable(id: String)

    // Reset availability (in case item comes back in stock)
    @Query("UPDATE cart_table SET isAvailable = 1 WHERE productId = :id")
    suspend fun markAsAvailable(id: String)
}