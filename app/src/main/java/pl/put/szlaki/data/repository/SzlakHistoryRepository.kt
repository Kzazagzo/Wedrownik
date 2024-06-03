package pl.put.szlaki.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pl.put.szlaki.database.AppDatabase
import pl.put.szlaki.database.szlakiHistory.SzlakHistoryEntity
import pl.put.szlaki.domain.HistorizedSzlak
import javax.inject.Inject

class SzlakiHistoryRepository
    @Inject
    constructor(
        private val appDatabase: AppDatabase,
    ) {
        suspend fun saveSzlakToHistory(szlakHistoryEntity: SzlakHistoryEntity) = appDatabase.szlakHistoryDao.insert(szlakHistoryEntity)

        fun getSzlakHistory(): Flow<List<HistorizedSzlak>> {
            return appDatabase.szlakHistoryDao.getAll().map {
                it.map {
                    it.asDomainModel()
                }
            }
        }
    }
