package pl.put.szlaki.database.szlakiHistory

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SzlakHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(szlakHistoryEntity: SzlakHistoryEntity): Long

    @Update
    suspend fun update(szlakHistoryEntity: SzlakHistoryEntity)

    @Delete
    suspend fun delete(szlakHistoryEntity: SzlakHistoryEntity)

    @Query("SELECT * FROM szlak_history")
    fun getAll(): Flow<List<SzlakHistoryEntity>>

    @Query("SELECT * FROM szlak_history WHERE id = :id")
    suspend fun getById(id: Long): SzlakHistoryEntity?
}
