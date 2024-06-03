package pl.put.szlaki.ui.components.screen.szlakiItemScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import pl.put.szlaki.R
import pl.put.szlaki.domain.Punkt
import pl.put.szlaki.domain.Szlak
import pl.put.szlaki.ui.components.common.NamedDividerFragment

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PunktFragment(
    punkt: Punkt,
    selectedSpeed: String,
    index: Int,
    szlak: Szlak,
    isSelected: Boolean,
    onPress: (Punkt) -> Unit,
    onLongPress: (Punkt) -> Unit,
) {
    var formattedTime by remember(
        selectedSpeed,
        punkt.roadToNextPoint,
    ) {
        mutableStateOf(
            TravelSpeedConverter.calculateTimeInSecondsToString(
                selectedSpeed,
                punkt.roadToNextPoint,
            ),
        )
    }
    SideEffect {
        formattedTime =
            TravelSpeedConverter.calculateTimeInSecondsToString(
                selectedSpeed,
                punkt.roadToNextPoint,
            )
    }

    val backgroundColor =
        when {
            isSelected -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.4f)
            punkt.visited -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            else -> MaterialTheme.colorScheme.surface
        }

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp).combinedClickable(
                    onClick = { onPress(punkt) },
                    onLongClick = { onLongPress(punkt) },
                ),
        colors =
            CardDefaults.cardColors(
                containerColor = backgroundColor,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // z let kotlin dostaje zawału... ciekawe dlaczego
            if (punkt.roadToNextPoint != null) {
                Text(
                    "${
                        "%.2f m".format(
                            punkt.roadToNextPoint!! * 1000,
                        )
                    } ",
                )
                Text(
                    "-> $formattedTime",
                )
            } else {
                NamedDividerFragment(text = "Punkt Startowy")
            }
            if (index + 1 < szlak.punkty.size) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_arrow_upward_24),
                    contentDescription = "direction",
                    modifier =
                        Modifier.graphicsLayer {
                            rotationZ =
                                punkt.calculateBearing(szlak.punkty[index + 1])
                        },
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_outlined_flag_24),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "finish",
                )
            }
        }
    }
}
