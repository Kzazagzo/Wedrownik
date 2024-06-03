package pl.put.szlaki.ui.components.screen.szlakiItemScreen

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import pl.put.szlaki.R
import pl.put.szlaki.ui.components.common.NamedDividerFragment
import pl.put.szlaki.ui.models.SzlakiScreenModel
import pl.put.szlaki.util.TimeUtils
import java.util.Locale

enum class TravelSpeed {
    SLOW,
    NORMAL,
    FAST,
}

object TravelSpeedConverter {
    fun calculateTimeAssignSpeed(
        selectedSpeed: String,
        roadToNextPoint: Double?,
    ): Long {
        return roadToNextPoint?.let {
            when (fromString(selectedSpeed)) {
                TravelSpeed.SLOW -> it / 0.5
                TravelSpeed.NORMAL -> it / 1
                TravelSpeed.FAST -> it / 1.5
            }.times(1000).toLong()
        } ?: 0L
    }

    fun calculateTimeInSecondsToString(
        selectedSpeed: String,
        roadToNextPoint: Double?,
    ): String {
        return roadToNextPoint?.let {
            TimeUtils.formatTime(
                calculateTimeAssignSpeed(selectedSpeed, roadToNextPoint),
            )
        } ?: ""
    }

    @JvmStatic
    fun fromString(value: String): TravelSpeed {
        return when (value) {
            "Slow" -> TravelSpeed.SLOW
            "Normal" -> TravelSpeed.NORMAL
            "Fast" -> TravelSpeed.FAST
            else -> throw IllegalArgumentException("Nieprawidłowa wartość dla TravelSpeed: $value")
        }
    }

    @JvmStatic
    fun toString(travelSpeed: TravelSpeed): String {
        return when (travelSpeed) {
            TravelSpeed.SLOW -> "Slow"
            TravelSpeed.NORMAL -> "Normal"
            TravelSpeed.FAST -> "Fast"
        }
    }
}

@Composable
fun TravelSpeedOptionFragment(
    modifier: Modifier = Modifier,
    screenModel: SzlakiScreenModel,
    selectedSpeed: TravelSpeed = TravelSpeed.NORMAL,
    onSpeedSelected: (TravelSpeed) -> Unit,
) {
    NamedDividerFragment(text = "Wybrana prędkość podróży")
    LazyRow(
        horizontalArrangement = Arrangement.Center,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(16.dp),
    ) {
        items(TravelSpeed.entries) { speed ->
            SpeedButton(
                text =
                    speed.name.lowercase()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                isSelected = speed == selectedSpeed,
                onClick = { onSpeedSelected(speed) },
                icon =
                    when (speed) {
                        TravelSpeed.SLOW -> painterResource(R.drawable.outline_assist_walker_24)
                        TravelSpeed.NORMAL -> painterResource(R.drawable.baseline_hiking_24)
                        TravelSpeed.FAST -> painterResource(R.drawable.rounded_directions_run_24)
                    },
            )
        }
    }
}

@Composable
private fun SpeedButton(
    modifier: Modifier = Modifier,
    text: String,
    icon: Painter,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val backgroundColor by animateColorAsState(
        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
    )
    val contentColor by animateColorAsState(
        if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
    )
    val borderColor by animateColorAsState(
        if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurface.copy(
                alpha = 0.3f,
            )
        },
    )

    Surface(
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor),
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { onClick() },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.padding(16.dp),
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                color = contentColor,
            )
        }
    }
}
