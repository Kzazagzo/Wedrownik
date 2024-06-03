package pl.put.szlaki.ui.components.screen.szlakiListScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.put.szlaki.R
import pl.put.szlaki.domain.Szlak
import pl.put.szlaki.ui.components.screen.szlakiItemScreen.TravelSpeedConverter
import pl.put.szlaki.util.TimeUtils

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SzlakListItem(
    szlak: Szlak,
    isSelected: Boolean,
    onItemClick: (Szlak) -> Unit,
    onLongItemCLick: (Szlak) -> Unit,
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface

    ElevatedCard(
        modifier =
            Modifier
                .height(250.dp)
                .padding(20.dp, 20.dp, 0.dp, 20.dp)
                .clip(RoundedCornerShape(15, 0, 0, 15))
                .combinedClickable(
                    onClick = { onItemClick(szlak) },
                    onLongClick = { onLongItemCLick(szlak) },
                ),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary,
            ).copy(backgroundColor),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 10.dp,
            ),
    ) {
        Row(
            modifier =
                Modifier
                    .padding(start = 20.dp)
                    .padding(7.dp, 0.dp, 0.dp),
        ) {
            Scaffold(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .weight(1.2f),
                topBar = {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = szlak.name,
                            fontSize = 22.sp,
                            overflow = TextOverflow.Clip,
                            maxLines = 1,
                        )
                    }
                },
                bottomBar = {
                    Row(
                        Modifier.padding(10.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row {
                            Icon(
                                tint = MaterialTheme.colorScheme.primary,
                                painter = painterResource(R.drawable.baseline_outlined_flag_24),
                                contentDescription = "points",
                            )
                            Text(text = "${szlak.punkty.size}")
                        }
                        Row {
                            Text(
                                text =
                                    TimeUtils.formatTime(
                                        szlak.punkty.sumOf {
                                            TravelSpeedConverter.calculateTimeAssignSpeed(
                                                szlak
                                                    .selectedSzlakDificulty,
                                                it.roadToNextPoint,
                                            )
                                        },
                                    ),
                            )
                            Icon(
                                tint = MaterialTheme.colorScheme.primary,
                                painter = painterResource(R.drawable.baseline_access_time_24),
                                contentDescription = "points",
                            )
                        }
                    }
                },
            ) {
                ProgressBarWithDataIndicator(Modifier.padding(it), szlak)
            }
            Box(modifier = Modifier.weight(1f)) {
                szlak.bounds?.let {
                    OsmMapPreViewComponent(
                        modifier =
                            Modifier.border(3.dp, color = MaterialTheme.colorScheme.outline),
                        szlak = szlak,
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgressBarWithDataIndicator(
    modifier: Modifier,
    szlak: Szlak,
) {
    Column(
        Modifier
            .padding(10.dp).fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                fontSize = 16.sp,
                text = "${"%.2f".format(szlak.calculateRoadEta())} km",
            )
            Text(
                fontSize = 16.sp,
                text = "${"%.2f".format(szlak.szlakLength)} km",
            )
        }
        LinearProgressIndicator(
            progress = { szlak.calculateProgress() },
            trackColor = MaterialTheme.colorScheme.primaryContainer,
            strokeCap = StrokeCap.Round,
            modifier =
                Modifier
                    .height(7.dp)
                    .fillMaxWidth(),
        )
    }
}
