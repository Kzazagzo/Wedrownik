package pl.put.szlaki.ui.components.screen.szlakiListScreen

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import pl.put.szlaki.R
import pl.put.szlaki.data.prasing.XmlSzlak
import pl.put.szlaki.ui.models.SzlakiScreenModel
import pl.put.szlaki.util.parseFile

class AddSzlakFab(
    val screenModel: SzlakiScreenModel,
    private val openAlertDialog: MutableState<Boolean>,
) {
    private lateinit var parsedXmlFile: XmlSzlak

    @SuppressLint("NotConstructor")
    @Composable
    fun AddSzlakFab() {
        ExtendedFloatingActionButton(
            modifier = Modifier.clip(RoundedCornerShape(20.dp)),
            onClick = {
                openAlertDialog.value = true
            },
        ) {
            Icon(
                painterResource(id = R.drawable.baseline_map_24),
                "Dodaj szlak",
            )
            Text(text = "      Dodaj szlak")
        }
    }

    @Composable
    fun AddDialog() {
        val context = LocalContext.current

        val selectedFiles = remember { mutableStateListOf<Uri?>(null) }
        val loadDataError = remember { mutableStateOf(false) }
        val nameWindowShowed = remember { mutableStateOf(false) }
        val selectedName = remember { mutableStateOf("") }

        val illegalNames = screenModel.szlakiNames.collectAsState()

        val fileLauncher =
            rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetMultipleContents(),
            ) { uris ->
                selectedFiles.addAll(uris)
                if (uris.isNotEmpty()) {
                    val inputStream = context.contentResolver.openInputStream(uris.first())
                    inputStream?.bufferedReader().use { reader ->
                        parsedXmlFile = parseFile<XmlSzlak>(reader?.readText())!!
                        parsedXmlFile.run {
                            if (this.trk?.name == null ||
                                illegalNames.value.contains(
                                    this.trk.name,
                                )
                            ) {
                                nameWindowShowed.value = true
                            } else {
                                val (szlak) =
                                    this.asDomainModelToSzlak() to
                                        this.asDomainModelToSzlak()
                                screenModel.addSzlakWithEmbededPoints(szlak)
                            }
                        }
                    }
                }
            }

        if (nameWindowShowed.value) {
            AlertDialog(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_warning_amber_24),
                        "watning",
                    )
                },
                onDismissRequest = { nameWindowShowed.value = false },
                title = { Text("Szlak nie posiada własnej nazwy") },
                text = {
                    Column {
                        Text(
                            text =
                                "Szlak nie posiada nazwy, bądź szlak o takiej nazwie już jest zapisany na urządzeniu \n" +
                                    "Musisz wprowadzić unikalną nazwę dla nowego szlaku lub anulować import.",
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        TextField(
                            value = selectedName.value,
                            onValueChange = { selectedName.value = it },
                            isError = illegalNames.value.contains(selectedName.value),
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (!illegalNames.value.contains(selectedName.value)) {
                            parsedXmlFile.let { parsedFile ->
                                val szlak =
                                    parsedFile.asDomainModelToSzlak(
                                        selectedName.value,
                                    )
                                screenModel.addSzlakWithEmbededPoints(szlak)
                            }
                        }
                    }) {
                        Text("OK")
                    }
                },
            )
        }

        AlertDialog(
            icon = {
                Icon(
                    painter = painterResource(R.drawable.baseline_add_24),
                    'a'.toString(),
                )
            },
            title = { Text(text = "Dodaj nowy szlak") },
            text = {
                Text(
                    text =
                        "Aby dodać nowy szlak do lokalnej bazy danych prześlij plik .gpx z danymi szlaku.\n" +
                            "Plik ten można dostać z każdej dobrej strony do tworzenia szlaków turystycznych.",
                )
            },
            onDismissRequest = { openAlertDialog.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        try {
                            fileLauncher.launch("*/*")
                        } catch (e: Exception) {
                            loadDataError.value = true
                        }
                    },
                ) {
                    Text("Dodaj szlak")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openAlertDialog.value = false
                    },
                ) {
                    Text("Anuluj")
                }
            },
        )
    }
}
