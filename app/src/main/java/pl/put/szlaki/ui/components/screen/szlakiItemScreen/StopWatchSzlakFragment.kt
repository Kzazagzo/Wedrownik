package pl.put.szlaki.ui.components.screen.szlakiItemScreen

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import pl.put.szlaki.R
import pl.put.szlaki.domain.Szlak
import pl.put.szlaki.ui.components.common.NamedDividerFragment
import pl.put.szlaki.ui.models.SzlakiScreenModel
import pl.put.szlaki.ui.services.SzlakNotificationService
import pl.put.szlaki.util.TimeUtils

class StopWatchSzlakFragment(
    private val modifier: Modifier = Modifier,
    private val szlakiScreenModel: SzlakiScreenModel,
    private val szlak: Szlak,
) {
    @Composable
    fun Content() {
        val context = LocalContext.current
        val stoperService = remember { mutableStateOf<SzlakNotificationService?>(null) }
        val binded = remember { mutableStateOf(false) }
        val isStoperRunning = remember { mutableStateOf(false) }
        val nasluch = remember { mutableStateOf(false) }

        val connection =
            remember {
                object : ServiceConnection {
                    override fun onServiceConnected(
                        name: ComponentName,
                        service: IBinder,
                    ) {
                        stoperService.value =
                            (service as SzlakNotificationService.LocalBinder).getService().also {
                                binded.value = true
                            }
                    }

                    override fun onServiceDisconnected(name: ComponentName?) {
                        binded.value = false
                    }
                }
            }

        LaunchedEffect(Unit) {
            bindToExistingService(context, connection)
        }

        LaunchedEffect(binded.value) {
            if (binded.value) {
                isStoperRunning.value = stoperService.value?.getStopwatch(szlak.name)?.isRunning
                    ?: false
            }
        }

        LaunchedEffect(stoperService.value) {
            if (stoperService.value?.getStopwatch(szlak.name)?.isRunning == null) {
                while (stoperService.value?.getStopwatch(szlak.name)?.isRunning == null) {
                    delay(100)
                }
            }
            isStoperRunning.value = true
        }

        NamedDividerFragment(text = "Stoper Szlaku")
        Row(
            modifier =
                modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            if (isStoperRunning.value) {
                IconButton(onClick = {
                    stopStopWatch(context, connection, stoperService.value)
                    isStoperRunning.value = false
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_pause_24),
                        contentDescription = "Pause",
                    )
                }
            } else {
                IconButton(onClick = {
                    startStopWatch(context, connection)
                    nasluch.value = true
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_play_arrow_24),
                        contentDescription = "Play",
                    )
                }
            }
            Row {
                Text(
                    text =
                        if (isStoperRunning.value) {
                            TimeUtils.formatTime(stoperService.value?.getStopwatch(szlak.name)?.stoperTime?.longValue!!)
                        } else {
                            TimeUtils.formatTime(szlak.stopWatchTime)
                        },
                )
                Text(
                    text = "/${ TimeUtils.formatTime(szlak.calculateETA())}",
                )
            }
        }
    }

    private fun syncStopWatch(stoperService: SzlakNotificationService?) {
        if (stoperService?.getStopwatch(szlak.name)?.stoperTime?.value != null) {
            szlak.stopWatchTime = stoperService.getStopwatch(szlak.name)?.stoperTime?.value!!
            szlakiScreenModel.updateSzlakStopWatch(szlak)
        }
    }

    private fun startStopWatch(
        context: Context,
        connection: ServiceConnection,
    ) {
        Intent(context, SzlakNotificationService::class.java).also {
            it.putExtra("szlakId", szlak.name)
            it.putExtra("szlak", szlak)
            it.action = SzlakNotificationService.Actions.START.toString()
            context.startForegroundService(it)
            bindToExistingService(context, connection)
        }
    }

    private fun stopStopWatch(
        context: Context,
        connection: ServiceConnection,
        stoperService: SzlakNotificationService?,
    ) {
        syncStopWatch(stoperService)
        Intent(context, SzlakNotificationService::class.java).also {
            it.putExtra("szlakId", szlak.name)
            it.action = SzlakNotificationService.Actions.STOP.toString()
            context.startForegroundService(it)
        }
        unbindService(context, connection)
    }

    private fun bindToExistingService(
        context: Context,
        connection: ServiceConnection,
    ) {
        Intent(context, SzlakNotificationService::class.java).also {
            context.bindService(it, connection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun unbindService(
        context: Context,
        connection: ServiceConnection,
    ) {
        context.unbindService(connection)
    }
}
