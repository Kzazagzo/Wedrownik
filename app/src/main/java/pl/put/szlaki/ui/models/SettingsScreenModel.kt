package pl.put.szlaki.ui.models

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.put.szlaki.data.repository.SettingsRepository
import pl.put.szlaki.domain.Setting
import javax.inject.Inject

class SettingsScreenModel
    @Inject
    constructor(
        private val settingsRepository: SettingsRepository,
    ) : ScreenModel {
        private val _settings = MutableStateFlow<DataStatus<List<Setting<*, *>>>>(DataStatus.Loading)
        val settings = _settings.asStateFlow()

        init {
            loadSettings()
        }

        private fun loadSettings() {
            screenModelScope.launch {
                settingsRepository.settings.collect {
                    try {
                        _settings.value = DataStatus.Success(it)
                    } catch (e: Exception) {
                        _settings.value = DataStatus.Error(e.message)
                    }
                }
            }
        }
    }
