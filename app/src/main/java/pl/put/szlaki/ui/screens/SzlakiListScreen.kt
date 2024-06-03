package pl.put.szlaki.ui.screens

import android.os.Parcelable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import pl.put.szlaki.R
import pl.put.szlaki.ui.components.screen.szlakiListScreen.CategorySzlakLazyList
import pl.put.szlaki.ui.components.screen.szlakiListScreen.SzlakItemsTopBar
import pl.put.szlaki.ui.models.CategoryScreenModel
import pl.put.szlaki.ui.models.DataStatus
import pl.put.szlaki.ui.models.SzlakiScreenModel

@Parcelize
object SzlakiListScreen : Tab, Parcelable {
    private fun readResolve(): Any = SzlakiListScreen

    override val options: TabOptions
        @Composable
        get() {
            val title = "Szlaki"
            val icon = painterResource(id = R.drawable.baseline_map_24)

            return remember {
                TabOptions(
                    index = 1u,
                    title = title,
                    icon = icon,
                )
            }
        }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<SzlakiScreenModel>()
        val categoriesState by screenModel.szlakiInCategories.collectAsStateWithLifecycle()

        when (categoriesState) {
            is DataStatus.Error -> DataStatus.Error((categoriesState as DataStatus.Error).msg)
            DataStatus.Loading -> DataStatus.Loading
            is DataStatus.Success -> {
                val category = (categoriesState as DataStatus.Success)

                val scope = rememberCoroutineScope()
                val pagerState = rememberPagerState(pageCount = { category.data.size })
                val selectedTabIndex = remember { derivedStateOf { pagerState.currentPage } }
                Scaffold(
                    modifier =
                        Modifier.fillMaxSize().windowInsetsPadding(
                            WindowInsets.navigationBars.only(WindowInsetsSides.Start + WindowInsetsSides.End),
                        ),
                    topBar = {
                        Column(Modifier.padding(top = 16.dp)) {
                            SzlakItemsTopBar(screenModel)
                            Spacer(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .height(16.dp),
                            )
                            TabRow(
                                selectedTabIndex = selectedTabIndex.value,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                category.data.forEachIndexed { index, categoryTab ->
                                    // NIE Z VOYAGERA, Z MATERIALA
                                    Tab(
                                        selected = selectedTabIndex.value == index,
                                        onClick = {
                                            scope.launch {
                                                pagerState.animateScrollToPage(index)
                                            }
                                        },
                                        text = { Text(categoryTab.category) },
                                    )
                                }
                            }
                        }
                    },
                    bottomBar = {},
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(top = it.calculateTopPadding()),
                    ) { page ->
                        CategorySzlakLazyList(
                            category.data[page].szlaki,
                            getScreenModel<SzlakiScreenModel>(),
                            getScreenModel<CategoryScreenModel>(),
                        )
                    }
                }
            }
        }
    }
}
