package pl.put.szlaki.ui.services

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableLongStateOf
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.put.szlaki.R
import pl.put.szlaki.domain.Szlak
import pl.put.szlaki.location.DefaultLocationClient
import pl.put.szlaki.location.LocationClient
import pl.put.szlaki.util.TimeUtils

class SzlakNotificationService : LifecycleService() {
    private lateinit var locationClient: LocationClient
    private val szlakiMap = mutableMapOf<String, SzlakData>()

    private val notificationManager by lazy { NotificationManagerCompat.from(this) }
    private val binder = LocalBinder()

    data class SzlakData(
        val szlak: Szlak,
        var stoperTime: MutableLongState,
        var isRunning: Boolean,
        var job: Job,
        var notification: NotificationCompat.Builder,
    )

    inner class LocalBinder : Binder() {
        fun getService() = this@SzlakNotificationService
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        locationClient =
            DefaultLocationClient(
                fusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient(
                        applicationContext,
                    ),
                application = application,
            )
    }

    enum class Actions {
        START,
        STOP,
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        super.onStartCommand(intent, flags, startId)
        when (intent?.action) {
            Actions.START.name -> {
                val szlakId = intent.getStringExtra("szlakId")!!
                val szlak =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra("szlak", Szlak::class.java)!!
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getParcelableExtra("szlak")!!
                    }

                startService(szlakId, szlak)
            }
            Actions.STOP.name -> {
                val szlakId = intent.getStringExtra("szlakId")!!
                stopService(szlakId)
            }
        }
        return START_NOT_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun startStoperLoop(szlakId: String) {
        val szlakData = szlakiMap[szlakId]!!
        szlakData.job =
            CoroutineScope(Dispatchers.Main).launch {
                szlakData.isRunning = true
                while (szlakData.isRunning) {
                    delay(1000)
                    szlakData.stoperTime.longValue += 1
                    szlakData.notification.setContentText(
                        "Podróżujesz przez ${TimeUtils.formatTime(szlakData.stoperTime.longValue)}",
                    )
                    notificationManager.notify(szlakId.hashCode(), szlakData.notification.build())
                }
            }
    }

    fun getStopwatch(szlakId: String): SzlakData? {
        return szlakiMap[szlakId]
    }

    private fun startService(
        szlakId: String,
        szlak: Szlak,
    ) {
        val stoperTime = mutableLongStateOf(szlak.stopWatchTime)
        val notification = createMainNotification(szlakId, szlak, stoperTime)

        val szlakData =
            SzlakData(
                szlak = szlak,
                stoperTime = stoperTime,
                isRunning = false,
                job = Job(),
                notification = notification,
            )

        szlakiMap[szlakId] = szlakData
        locationClient.startLocationUpdates()
        startStoperLoop(szlakId)
        startForeground(szlakId.hashCode(), notification.build())
    }

    private fun stopService(szlakId: String) {
        val szlakData = szlakiMap[szlakId] ?: return
        szlakData.isRunning = false
        szlakData.job.cancel()
        notificationManager.cancel(szlakId.hashCode())
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        szlakiMap.remove(szlakId)
    }

    @SuppressLint("MissingPermission")
    private fun createMainNotification(
        szlakId: String,
        szlak: Szlak,
        stoperTime: MutableState<Long>,
    ): NotificationCompat.Builder {
        val intentAction =
            Intent(this, ActionReceiver::class.java).apply {
                action = "ACTION_PERFORM"
            }
        val pendingIntentAction = PendingIntent.getBroadcast(this, 0, intentAction, PendingIntent.FLAG_UPDATE_CURRENT)

        val action =
            NotificationCompat.Action.Builder(
                null,
                "Wykonaj akcję",
                pendingIntentAction,
            ).build()

        return NotificationCompat.Builder(this, NotificationChannelsIds.TIMER.name)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentTitle(szlak.name)
            .setContentText(
                "Podróżujesz przez ${TimeUtils.formatTime(stoperTime.value)}",
            )
            .setGroup("TIMER_GROUP")
            .setGroupSummary(true)
            .setOnlyAlertOnce(true)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.baseline_arrow_upward_24,
                ),
            )
            .setContentIntent(pendingIntentAction)
            .extend(NotificationCompat.WearableExtender().addAction(action))
            .addAction(action)
    }
}

class ActionReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context?,
        intent: Intent?,
    ) {
        when (intent?.action) {
            "ACTION_PERFORM" -> {
                Toast.makeText(context, "Action performed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
