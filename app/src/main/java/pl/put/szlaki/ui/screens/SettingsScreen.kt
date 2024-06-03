package pl.put.szlaki.ui.screens

import android.os.Parcelable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import kotlinx.parcelize.Parcelize
import pl.put.szlaki.R
import pl.put.szlaki.ui.components.screen.settingsScreen.CategorySetting
import pl.put.szlaki.ui.components.screen.settingsScreen.SettingItemStandard
import pl.put.szlaki.ui.models.CategoryScreenModel
import pl.put.szlaki.ui.models.DataStatus
import pl.put.szlaki.ui.models.SettingsScreenModel

@Parcelize
object SettingsScreen : Tab, Parcelable {
    private fun readResolve(): Any = SettingsScreen

    override val options: TabOptions
        @Composable
        get() {
            val title = "Ustawienia"
            val icon = painterResource(id = R.drawable.baseline_settings_24)

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon,
                )
            }
        }

    @Composable
    override fun Content() {
        val screenModel = getScreenModel<SettingsScreenModel>()
        val categoryScreenModel = getScreenModel<CategoryScreenModel>()

        val settingsData by screenModel.settings.collectAsState()
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) {
            when (settingsData) {
                is DataStatus.Error -> DataStatus.Error((settingsData as DataStatus.Error).msg)
                DataStatus.Loading -> DataStatus.Loading
                is DataStatus.Success -> {
                    CategorySetting(screenModel = categoryScreenModel).Facade()
                    val settings = (settingsData as DataStatus.Success).data
                    LazyColumn(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(it),
                    ) {
                        items(settings) {
                            SettingItemStandard(it)
                        }
                    }
                }
            }
        }
    }
}
