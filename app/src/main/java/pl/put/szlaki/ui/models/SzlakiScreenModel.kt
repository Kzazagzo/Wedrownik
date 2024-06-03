package pl.put.szlaki.ui.models

import android.location.Location
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import pl.put.szlaki.data.repository.CategoryRepository
import pl.put.szlaki.data.repository.SzlakiHistoryRepository
import pl.put.szlaki.data.repository.SzlakiRepository
import pl.put.szlaki.domain.Szlak
import pl.put.szlaki.ui.components.screen.szlakiListScreen.CategoryTab
import javax.inject.Inject

class SzlakiScreenModel
    @Inject
    constructor(
        private val szlakiRepository: SzlakiRepository,
        private val categoryRepository: CategoryRepository,
        private val szlakiHistoryRepository: SzlakiHistoryRepository,
    ) : ScreenModel {
        private var fullSzlakiInCategories: List<CategoryTab<Szlak>> = listOf()

        private val _szlakiInCategories =
            MutableStateFlow<DataStatus<List<CategoryTab<Szlak>>>>(DataStatus.Loading)
        val szlakiInCategories: StateFlow<DataStatus<List<CategoryTab<Szlak>>>> =
            _szlakiInCategories.asStateFlow()

        private val _szlakiNames = MutableStateFlow<Set<String>>(emptySet())
        val szlakiNames = _szlakiNames.asStateFlow()

        private val _currentLocation = MutableStateFlow<Location?>(null)
        val currentLocation = _currentLocation.asStateFlow()

        private val _searchText = MutableStateFlow("")
        val searchText: StateFlow<String> = _searchText.asStateFlow()

        init {
            loadSzlaki()
        }

        private fun filterCategories(text: String) {
            if (fullSzlakiInCategories.isNotEmpty()) {
                val filteredCategories =
                    fullSzlakiInCategories.map { categoryTab ->
                        val filteredSzlaki =
                            categoryTab.szlaki.filter { szlak ->
                                szlak.name.contains(text, ignoreCase = true)
                            }
                        categoryTab.copy(szlaki = filteredSzlaki)
                    }
                _szlakiInCategories.value = DataStatus.Success(filteredCategories)
            }
        }

        // Sounds quite drastic...
        fun sendToHistory(szlak: Szlak) {
            screenModelScope.launch {
                szlakiHistoryRepository.saveSzlakToHistory(szlak.toHistoryAsDatabaseModel())
            }
            szlak.resetSzlak()
        }

        private fun loadSzlaki() {
            screenModelScope.launch {
                categoryRepository.categoriesWithSzlaki.collect { categoryData ->
                    var index = 0
                    val newMap =
                        categoryData.map { category ->
                            val szlakiList =
                                try {
                                    category.elementsInCategory?.map {
                                        it
                                    } ?: emptyList()
                                } catch (e: Exception) {
                                    emptyList()
                                }
                            CategoryTab(category.categoryName, szlakiList, index++)
                        }
                    _szlakiInCategories.value = DataStatus.Success(newMap)
                    fullSzlakiInCategories = newMap
                }
            }
        }

        fun addSzlakWithEmbededPoints(szlak: Szlak) =
            screenModelScope.launch {
                szlakiRepository.insertSzlakAndPunkty(szlak, szlak.punkty)
            }

        private fun updateSzlakInDatabase(szlak: Szlak) =
            screenModelScope.launch {
                szlakiRepository.updateSzlakAndPunkty(szlak, szlak.punkty)
            }

        fun updateSzlakVisitedPoints(
            szlak: Szlak,
            pointsIdx: List<Int>,
            setTo: Boolean,
        ) {
            screenModelScope.launch {
                for (i in pointsIdx) {
                    szlak.punkty[i].visited = setTo
                }
                updateSzlakInDatabase(szlak)
            }
        }

        fun deleteSzlak(szlak: Szlak) =
            screenModelScope.launch {
                szlakiRepository.deleteSzlakByName(szlak)
            }

        fun updateSzlakStopWatch(szlak: Szlak) =
            screenModelScope.launch {
                szlakiRepository.updateSzlakStopWatch(szlak.name, szlak.stopWatchTime)
            }

        fun onSearchTextChange(text: String) {
            _searchText.value = text
            screenModelScope.launch {
                filterCategories(text)
            }
        }
    }

sealed class DataStatus<out T> {
    data object Loading : DataStatus<Nothing>()

    data class Success<T>(val data: T) : DataStatus<T>()

    data class Error(val msg: String?) : DataStatus<Nothing>()
}
