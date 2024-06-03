package pl.put.szlaki.data.repository

import androidx.annotation.WorkerThread
import pl.put.szlaki.database.AppDatabase
import pl.put.szlaki.domain.Punkt
import pl.put.szlaki.domain.Szlak
import javax.inject.Inject

class SzlakiRepository
    @Inject
    constructor(
        private val appDatabase: AppDatabase,
    ) {
        @WorkerThread
        private suspend fun updateSzlak(szlak: Szlak) {
            appDatabase.szlakiTurystyczneDao.updateSzlak(szlak.asDatabaseModel())
        }

        @WorkerThread
        suspend fun deleteSzlakByName(szlak: Szlak) {
            appDatabase.szlakiTurystyczneDao.deleteSzlakByName(szlak.name)
        }

        @WorkerThread
        suspend fun updateSzlakStopWatch(
            szlakName: String,
            time: Long,
        ) {
            appDatabase.szlakiTurystyczneDao.updateSzlakStopWatch(szlakName, time)
        }

        @WorkerThread
        suspend fun insertSzlakAndPunkty(
            szlak: Szlak,
            punkty: List<Punkt>,
        ) {
            appDatabase.szlakiTurystyczneDao.insertSzlakAndPunkty(
                szlak.asDatabaseModel(),
                punkty.map { it.asDatabaseModel() },
            )
        }

        @WorkerThread
        suspend fun updateSzlakAndPunkty(
            szlak: Szlak,
            punkty: List<Punkt>,
        ) {
            appDatabase.szlakiTurystyczneDao.updateSzlakAndPunkty(
                szlak.asDatabaseModel(),
                punkty.map { it.asDatabaseModel() },
            )
        }
    }
