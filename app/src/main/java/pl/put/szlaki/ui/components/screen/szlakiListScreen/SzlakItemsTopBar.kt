package pl.put.szlaki.ui.components.screen.szlakiListScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import pl.put.szlaki.ui.components.common.InputField
import pl.put.szlaki.ui.models.SzlakiScreenModel

@Composable
fun SzlakItemsTopBar(screenModel: SzlakiScreenModel) {
    val searchText by screenModel.searchText.collectAsState()

    val addDialogOpened = remember { mutableStateOf(false) }
    val addSzlakFab = remember { AddSzlakFab(screenModel = screenModel, openAlertDialog = addDialogOpened) }

    if (addDialogOpened.value) {
        addSzlakFab.AddDialog()
    }

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        InputField(
            modifier = Modifier.weight(0.6f),
            text = searchText,
            onTextChange = screenModel::onSearchTextChange,
        )
        addSzlakFab.AddSzlakFab()
    }
}
