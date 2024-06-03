package pl.put.szlaki.ui.components.screen.historyScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.put.szlaki.domain.HistorizedSzlak
import pl.put.szlaki.util.TimeUtils

@Composable
fun SzlakHistoryFragment(szlak: HistorizedSzlak) {
    val szlakCompleated = szlak.compleatedPercentage == 1.0

    val colors =
        if (szlakCompleated) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        } else {
            MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
        }

    ElevatedCard(
        modifier =
            Modifier
                .padding(20.dp, 20.dp, 0.dp, 20.dp).height(170.dp)
                .clip(RoundedCornerShape(15)),
        colors =
            CardDefaults.cardColors(
                containerColor = colors,
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 10.dp,
            ),
    ) {
        Column(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = szlak.name,
                fontSize = 22.sp,
                overflow = TextOverflow.Clip,
                maxLines = 1,
            )
            Text(
                text = "${szlak.selectedSzlakDificulty} ⋅ ${
                    TimeUtils.formatTime(
                        szlak
                            .travelTime,
                    )
                }",
            )
            if (szlakCompleated) {
                Text(text = "${"%.2f".format(szlak.szlakLength)} km")
            } else {
                ProgressBarWithDataIndicator(
                    szlak.szlakLength * szlak.compleatedPercentage,
                    szlak.szlakLength,
                )
            }
            if (szlakCompleated) {
                Text(text = "Szlak ukończony!")
            } else {
                Text(text = "Szlak nieukończony")
            }
        }
    }
}

@Composable
private fun ProgressBarWithDataIndicator(
    start: Double,
    end: Double,
    modifier: Modifier = Modifier,
) {
    Column(
        Modifier
            .padding(10.dp)
            .fillMaxSize(),
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
                text = "${"%.2f".format(start)} km",
            )
            Text(
                fontSize = 16.sp,
                text = "${"%.2f".format(end)} km",
            )
        }
        LinearProgressIndicator(
            progress = { (start / end).toFloat() },
            trackColor = MaterialTheme.colorScheme.primaryContainer,
            strokeCap = StrokeCap.Round,
            modifier =
                Modifier
                    .height(7.dp)
                    .fillMaxWidth(),
        )
    }
}
