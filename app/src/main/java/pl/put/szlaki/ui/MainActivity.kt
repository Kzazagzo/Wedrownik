package pl.put.szlaki.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.navigator.LocalNavigatorSaver
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.parcelableNavigatorSaver
import cafe.adriel.voyager.transitions.CrossfadeTransition
import dagger.hilt.android.AndroidEntryPoint
import org.jetbrains.annotations.VisibleForTesting
import pl.put.szlaki.ui.screens.LoadingScreen
import pl.put.szlaki.ui.services.NotificationChannelManager
import pl.put.szlaki.ui.theme.TaApaDoSzlakowTheme

@AndroidEntryPoint
@VisibleForTesting
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalVoyagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT))
        super.onCreate(savedInstanceState)

        setContent {
            NotificationChannelManager.initNotificationChannelManager(this)
            getActionBar()?.hide()

            TaApaDoSzlakowTheme {
                CompositionLocalProvider(
                    LocalNavigatorSaver provides parcelableNavigatorSaver(),
                ) {
                    Navigator(
                        screen = LoadingScreen,
                    ) {
                        CrossfadeTransition(navigator = it)
                    }
                }
            }
        }
    }
}
