package pl.put.szlaki.ui.components.screen.settingsScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

data class SettingOptions(
    val title: String,
    val description: String,
    val icon: Painter? = null,
)

abstract class SettingItem(
    private val modifier: Modifier = Modifier,
) : Screen {
    private var showContent by mutableStateOf(false)

    abstract val options: SettingOptions
        @Composable get

    @Composable
    fun Facade() {
        if (showContent) {
            LocalNavigator.currentOrThrow.parent!!.push(this)
        } else {
            SettingPreview()
        }
    }

    @Composable
    private fun SettingPreview() {
        val settingOptions = options
        Row(
            modifier =
                modifier.fillMaxWidth()
                    .clickable { showContent = true }
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            settingOptions.icon?.let {
                Icon(
                    painter = it,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp),
                )
            }
            Column {
                Text(text = settingOptions.title)
                Text(text = settingOptions.description)
            }
        }
    }
}
