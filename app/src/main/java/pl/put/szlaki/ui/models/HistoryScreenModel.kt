package pl.put.szlaki.ui.models

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.put.szlaki.data.repository.SzlakiHistoryRepository
import pl.put.szlaki.domain.HistorizedSzlak
import javax.inject.Inject

class HistoryScreenModel
    @Inject
    constructor(
        private val szlakiHistoryRepository: SzlakiHistoryRepository,
    ) : ScreenModel {
        private val _historizedSzlaki =
            MutableStateFlow<DataStatus<List<HistorizedSzlak>>>(DataStatus.Loading)
        val historizedSzlaki = _historizedSzlaki.asStateFlow()

        init {
            loadHistory()
        }

        private fun loadHistory() {
            screenModelScope.launch {
                szlakiHistoryRepository.getSzlakHistory().collect {
                    _historizedSzlaki.value = DataStatus.Success(it)
                }
            }
        }
    }
