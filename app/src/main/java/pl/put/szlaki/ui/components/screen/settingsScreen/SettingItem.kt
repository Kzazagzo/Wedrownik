package pl.put.szlaki.ui.components.screen.settingsScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import pl.put.szlaki.domain.Setting

@Composable
fun SettingItemStandard(setting: Setting<*, *>) {
    var value by remember { mutableStateOf(setting.settingValue) }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = setting.settingName)
        when (setting.typeDetails) {
            is Int -> {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = value.toString(),
                    onValueChange = { value = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }
            is String -> {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = value as String,
                    onValueChange = { value = it },
                )
            }
            is Boolean -> {
                Switch(checked = value as Boolean, onCheckedChange = { value = it })
            }
        }
    }
}
