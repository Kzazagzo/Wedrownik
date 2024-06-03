package pl.put.szlaki.ui.screens

import android.Manifest
import android.os.Parcelable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.parcelize.Parcelize
import pl.put.szlaki.R
import pl.put.szlaki.domain.Szlak
import pl.put.szlaki.ui.components.screen.szlakiItemScreen.ChartSzlakFragment
import pl.put.szlaki.ui.components.screen.szlakiItemScreen.OsmMapViewComponent
import pl.put.szlaki.ui.components.screen.szlakiItemScreen.PunktFragment
import pl.put.szlaki.ui.components.screen.szlakiItemScreen.StopWatchSzlakFragment
import pl.put.szlaki.ui.components.screen.szlakiItemScreen.TravelSpeedConverter
import pl.put.szlaki.ui.components.screen.szlakiItemScreen.TravelSpeedOptionFragment
import pl.put.szlaki.ui.models.SzlakiScreenModel

@Parcelize
data class SzlakItemScreen(val szlak: Szlak) : Screen, Parcelable {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
    @Composable
    override fun Content() {
        val scaffoldState = rememberBottomSheetScaffoldState()
        val screenModel = getScreenModel<SzlakiScreenModel>()
        val selectedPoints = remember { mutableStateListOf<Int>() }
        val showFinishDialog = remember { mutableStateOf(false) }

        if (showFinishDialog.value) {
            AlertDialog(
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.baseline_add_24),
                        'a'.toString(),
                    )
                },
                title = { Text(text = "Zakończ szlak") },
                text = {
                    Text(
                        text =
                            "Czy chcesz zakończyć swoją podróż? Ta akcja nie może być cofnięta?",
                    )
                },
                onDismissRequest = { showFinishDialog.value = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            screenModel.sendToHistory(szlak)
                            screenModel.updateSzlakStopWatch(szlak)
                            screenModel.updateSzlakVisitedPoints(
                                szlak,
                                (0..<szlak.punkty.size)
                                    .toList(),
                                false,
                            )
                            showFinishDialog.value = false
                        },
                    ) {
                        Text("Zakończ podróż")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showFinishDialog.value = false
                        },
                    ) {
                        Text("Anuluj")
                    }
                },
            )
        }

        var wihajsterRekompozycyjny by remember { mutableStateOf(false) }

        Box(
            Modifier.fillMaxSize(),
        ) {
            BottomSheetScaffold(
                sheetMaxWidth = Dp.Unspecified,
                sheetContent = {
                    Scaffold(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        bottomBar = {
                            AnimatedVisibility(
                                visible = selectedPoints.isNotEmpty(),
                                enter =
                                    slideInVertically(
                                        initialOffsetY = { it },
                                        animationSpec = tween(durationMillis = 300),
                                    ),
                                exit =
                                    slideOutVertically(
                                        targetOffsetY = { it },
                                        animationSpec = tween(durationMillis = 300),
                                    ),
                            ) {
                                if (selectedPoints.isNotEmpty()) {
                                    BottomBar(
                                        onVisitedClick = {
                                            screenModel.updateSzlakVisitedPoints(
                                                szlak,
                                                selectedPoints,
                                                true,
                                            )
                                            selectedPoints.clear()
                                            wihajsterRekompozycyjny = !wihajsterRekompozycyjny
                                        },
                                        onUnVisitedClick = {
                                            screenModel.updateSzlakVisitedPoints(
                                                szlak,
                                                selectedPoints,
                                                false,
                                            )
                                            selectedPoints.clear()
                                            wihajsterRekompozycyjny = !wihajsterRekompozycyjny
                                        },
                                        onRangeSelectClick = {
                                            screenModel.updateSzlakVisitedPoints(
                                                szlak,
                                                (0..selectedPoints.max()).toList(),
                                                true,
                                            )
                                            selectedPoints.clear()
                                            wihajsterRekompozycyjny = !wihajsterRekompozycyjny
                                        },
                                    )
                                }
                            }
                        },
                    ) {
                        var rowHeight by remember { mutableStateOf(0.dp) }
                        var selectedSpeed by remember { mutableStateOf(szlak.selectedSzlakDificulty) }
                        var clickingMode by remember { mutableStateOf(false) }

                        LazyColumn(
                            Modifier
                                .fillMaxSize()
                                .padding(it),
                        ) {
                            item {
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .onSizeChanged { rowHeight = it.height.dp },
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Column {
                                        Text(
                                            szlak.name,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = 32.sp,
                                        )
                                        Text(
                                            "${"%.2f".format(szlak.szlakLength)} km",
                                            color = MaterialTheme.colorScheme.secondary,
                                            fontSize = 24.sp,
                                        )
                                    }
                                    IconButton(onClick = { showFinishDialog.value = true }) {
                                        Icon(
                                            painter =
                                                painterResource(
                                                    id =
                                                        R.drawable
                                                            .baseline_not_started_24,
                                                ),
                                            contentDescription = "Navigate",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier =
                                                Modifier
                                                    .size(rowHeight)
                                                    .scale(1f),
                                        )
                                    }
                                }
                            }

                            item {
                                TravelSpeedOptionFragment(
                                    selectedSpeed = TravelSpeedConverter.fromString(selectedSpeed),
                                    onSpeedSelected = {
                                        selectedSpeed =
                                            TravelSpeedConverter
                                                .toString(it)
                                    },
                                    screenModel = screenModel,
                                )
                                StopWatchSzlakFragment(
                                    szlakiScreenModel = screenModel,
                                    szlak = szlak,
                                ).Content()
                                ChartSzlakFragment(szlak = szlak)
                            }
                            itemsIndexed(szlak.punkty) { index, punkt ->
                                val isSelected = selectedPoints.contains(index)
                                PunktFragment(
                                    punkt,
                                    selectedSpeed,
                                    index,
                                    szlak,
                                    isSelected,
                                    onPress = {
                                        if (clickingMode) {
                                            if (isSelected) {
                                                selectedPoints.remove(index)
                                            } else {
                                                selectedPoints.add(index)
                                            }
                                        }
                                        clickingMode = !selectedPoints.isEmpty()
                                    },
                                    onLongPress = {
                                        if (isSelected) {
                                            selectedPoints.remove(index)
                                        } else {
                                            selectedPoints.add(index)
                                        }
                                        clickingMode = !selectedPoints.isEmpty()
                                    },
                                )
                            }
                        }
                    }
                },
                scaffoldState = scaffoldState,
                sheetPeekHeight = LocalConfiguration.current.screenHeightDp.dp * 0.2f,
            ) { _ ->
                val locationPermissions =
                    rememberMultiplePermissionsState(
                        permissions =
                            listOf(
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                            ),
                    )

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {},
                            actions = {
                                IconButton(
                                    onClick = {
                                        locationPermissions.launchMultiplePermissionRequest()
                                    },
                                ) {
                                    Icon(
                                        modifier = Modifier.padding(end = 16.dp),
                                        painter = painterResource(id = R.drawable.baseline_navigation_24),
                                        contentDescription = "Navigation",
                                        tint =
                                            MaterialTheme
                                                .colorScheme.primary,
                                    )
                                }
                            },
                        )
                    },
                ) {
                    OsmMapViewComponent(
                        Modifier
                            .padding(it),
                        screenModel,
                        szlak,
                        wihajsterRekompozycyjny,
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomBar(
    onVisitedClick: () -> Unit,
    onUnVisitedClick: () -> Unit,
    onRangeSelectClick: () -> Unit,
) {
    Surface(
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth(),
        contentColor = Color.Unspecified,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 30.dp),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Box(
                modifier =
                    Modifier
                        .shadow(8.dp, RoundedCornerShape(50), clip = false)
                        .background(
                            MaterialTheme.colorScheme.secondaryContainer,
                            RoundedCornerShape(50),
                        )
                        .border(
                            2.dp,
                            MaterialTheme.colorScheme.outline,
                            RoundedCornerShape(50),
                        )
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .clip(RoundedCornerShape(50))
                        .wrapContentWidth(),
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onVisitedClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_check_24),
                            contentDescription = "category",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                    IconButton(onClick = onUnVisitedClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.sharp_close_24),
                            contentDescription = "delete",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                    IconButton(onClick = onRangeSelectClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_playlist_add_check_24),
                            contentDescription = "delete",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                }
            }
        }
    }
}
