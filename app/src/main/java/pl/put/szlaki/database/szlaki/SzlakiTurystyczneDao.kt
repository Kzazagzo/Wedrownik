package pl.put.szlaki.database.szlaki

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import pl.put.szlaki.database.category.CategoryEntity
import pl.put.szlaki.database.category.SzlakCategoryJoin

@Dao
interface SzlakiTurystyczneDao {
    @Query("SELECT * FROM szlaki")
    fun getSzlaki(): Flow<List<SzlakEntity>>

    @Query("SELECT name FROM szlaki")
    fun getSzlakNames(): Flow<List<String>>

    @Update
    suspend fun updateSzlak(szlak: SzlakEntity)

    @Query("DELETE FROM szlaki WHERE name = :szlakName")
    suspend fun deleteSzlakByName(szlakName: String)

    @Query("SELECT * FROM szlaki WHERE name = :szlakName")
    suspend fun getSzlakByName(szlakName: String): SzlakEntity

    @Query("SELECT * FROM punkty WHERE szlakName = :szlakName")
    suspend fun getPunktyFromSzlakByName(szlakName: String): List<PunktEntity>

    @Query("SELECT * FROM punkty WHERE szlakName = :szlakName AND latitude = :latitude AND longitude = :longitude LIMIT 1")
    suspend fun findPunkt(
        szlakName: String,
        latitude: Double,
        longitude: Double,
    ): PunktEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSzlak(szlak: SzlakEntity): Long

    @Query(
        "SELECT * FROM szlaki s WHERE NOT " +
            "EXISTS (SELECT * FROM szlak_category_join scj WHERE " +
            "scj.szlakName = s.name)",
    )
    fun getSzlakiWithoutCategory(): Flow<List<SzlakEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPunkty(punkty: List<PunktEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategoryIfNotExists(category: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSzlakCategoryJoin(join: SzlakCategoryJoin)

    @Query("UPDATE szlaki SET stopWatchTime = :time WHERE name = :szlakName")
    suspend fun updateSzlakStopWatch(
        szlakName: String,
        time: Long,
    )

    @Query("UPDATE punkty SET visited = :visited WHERE pointNumber = :pointNumber")
    suspend fun updateVisited(
        pointNumber: Long,
        visited: Boolean,
    )

    @Transaction
    @Insert
    suspend fun insertSzlakAndPunkty(
        szlak: SzlakEntity,
        punkty: List<PunktEntity>,
    ) {
        insertSzlak(szlak)
        insertPunkty(punkty)
    }

    @Transaction
    @Insert
    suspend fun updateSzlakAndPunkty(
        szlak: SzlakEntity,
        punkty: List<PunktEntity>,
    ) {
        updateSzlak(szlak)
        punkty.forEach {
            val num = findPunkt(szlak.name, it.latitude, it.longitude)
            updateVisited(num!!.pointNumber, it.visited)
        }
    }
}
