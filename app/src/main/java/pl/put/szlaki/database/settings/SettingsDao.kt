package pl.put.szlaki.database.settings

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {

    @Query("SELECT * FROM settings")
    fun getAllSettings(): Flow<List<SettingsEntity>>

    @Query("SELECT * FROM settings WHERE settingName = :settingEntity")
    fun getSetting(settingEntity: SettingsEntity): Flow<SettingsEntity>

    @Update
    suspend fun updateSetting(settingEntity: SettingsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settingEntity: SettingsEntity)
}
