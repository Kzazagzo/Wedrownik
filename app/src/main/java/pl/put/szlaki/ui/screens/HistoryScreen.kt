package pl.put.szlaki.ui.screens

import android.content.res.Configuration
import android.os.Parcelable
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import kotlinx.parcelize.Parcelize
import pl.put.szlaki.R
import pl.put.szlaki.domain.HistorizedSzlak
import pl.put.szlaki.ui.components.common.ErrorFragment
import pl.put.szlaki.ui.components.screen.historyScreen.SzlakHistoryFragment
import pl.put.szlaki.ui.models.DataStatus
import pl.put.szlaki.ui.models.HistoryScreenModel

@Parcelize
object HistoryScreen : Tab, Parcelable {
    private fun readResolve(): Any = HistoryScreen

    override val options: TabOptions
        @Composable
        get() {
            val title = "Historia"
            val icon = painterResource(id = R.drawable.baseline_history_24)

            return remember {
                TabOptions(
                    index = 2u,
                    title = title,
                    icon = icon,
                )
            }
        }

    @Composable
    override fun Content() {
        val screenModel = getScreenModel<HistoryScreenModel>()
        val historyContent by screenModel.historizedSzlaki.collectAsState()

        when (historyContent) {
            is DataStatus.Error -> ErrorFragment()
            DataStatus.Loading -> CircularProgressIndicator()
            is DataStatus.Success -> {
                val configuration = LocalConfiguration.current
                val columnsNumber =
                    if (configuration.orientation ==
                        Configuration
                            .ORIENTATION_LANDSCAPE
                    ) {
                        3
                    } else {
                        1
                    }

                LazyVerticalGrid(columns = GridCells.Fixed(columnsNumber)) {
                    items((historyContent as DataStatus.Success<List<HistorizedSzlak>>).data) {
                        SzlakHistoryFragment(it)
                    }
                }
            }
        }
    }
}
