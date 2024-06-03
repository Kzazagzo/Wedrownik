package pl.put.szlaki.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pl.put.szlaki.database.AppDatabase
import pl.put.szlaki.database.settings.asDomainModel
import pl.put.szlaki.domain.Setting
import javax.inject.Inject

class SettingsRepository
    @Inject
    constructor(
        private val appDatabase: AppDatabase,
    ) {
        val settings: Flow<List<Setting<*, *>>> =
            appDatabase.settingsDao.getAllSettings().map {
                it.map { it.asDomainModel() }
            }
    }
