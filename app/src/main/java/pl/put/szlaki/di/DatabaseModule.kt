package pl.put.szlaki.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.put.szlaki.database.AppDatabase
import pl.put.szlaki.database.category.CategoryDao
import pl.put.szlaki.database.szlaki.SzlakiTurystyczneDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "Szlaki",
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideSzlakiDao(appDatabase: AppDatabase): SzlakiTurystyczneDao {
        return appDatabase.szlakiTurystyczneDao
    }

    @Provides
    fun provideSettingsDao(appDatabase: AppDatabase): CategoryDao {
        return appDatabase.categoryDao
    }
}
