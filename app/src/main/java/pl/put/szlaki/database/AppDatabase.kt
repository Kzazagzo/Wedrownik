package pl.put.szlaki.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pl.put.szlaki.database.category.CategoryDao
import pl.put.szlaki.database.category.CategoryEntity
import pl.put.szlaki.database.category.SzlakCategoryJoin
import pl.put.szlaki.database.settings.SettingsConverter
import pl.put.szlaki.database.settings.SettingsDao
import pl.put.szlaki.database.settings.SettingsEntity
import pl.put.szlaki.database.szlaki.PunktEntity
import pl.put.szlaki.database.szlaki.SzlakEntity
import pl.put.szlaki.database.szlaki.SzlakiTurystyczneDao
import pl.put.szlaki.database.szlakiHistory.SzlakHistoryDao
import pl.put.szlaki.database.szlakiHistory.SzlakHistoryEntity

@Database(
    entities = [
        SzlakEntity::class, PunktEntity::class, CategoryEntity::class,
        SzlakCategoryJoin::class, SettingsEntity::class, SzlakHistoryEntity::class,
    ],
    version = 692137,
    exportSchema = false,
)
@TypeConverters(SettingsConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val szlakiTurystyczneDao: SzlakiTurystyczneDao
    abstract val categoryDao: CategoryDao
    abstract val settingsDao: SettingsDao
    abstract val szlakHistoryDao: SzlakHistoryDao
}
