package com.kevinfreyap.shared_voucher.data.source.local.room

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.MapColumn
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kevinfreyap.shared_voucher.data.source.local.entity.VoucherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VoucherDao {
    @Query("SELECT * FROM voucher ORDER BY isUsed ASC, (expiryDate < :now) ASC, expiryDate ASC")
    fun getAllVouchers(now: Long): Flow<List<VoucherEntity>>

    @Query("SELECT expiryDate FROM voucher WHERE isNew = 1 AND isUsed = 0")
    fun getNewVoucherDates(): Flow<List<Long>>

    // Used to check existence during sync
    @Query("SELECT id FROM voucher")
    suspend fun getAllIds(): List<String>

    @Query("SELECT id, isNew FROM voucher")
    suspend fun getIdsAndNewStatus(): Map<
            @MapColumn(columnName = "id") String,
            @MapColumn(columnName = "isNew") Boolean
        >

    @Query("DELETE FROM voucher WHERE type = 'PUBLIC' AND id NOT IN (:validIds)")
    suspend fun deleteMissingPublicVouchers(validIds: Set<String>)

    @Query("SELECT id, isNew, isUsed FROM voucher")
    suspend fun getLocalState(): List<LocalVoucherState>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vouchers: List<VoucherEntity>)

    @Query("UPDATE voucher SET isNew = 0")
    suspend fun markAllAsSeen()

    @Query("DELETE FROM voucher")
    suspend fun clearAll()
}

data class LocalVoucherState(
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "isNew") val isNew: Boolean,
    @ColumnInfo(name = "isUsed") val isUsed: Boolean
)