package pl.put.szlaki.ui.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

object NotificationChannelManager {
    fun initNotificationChannelManager(context: Context) {
        createMainNotificationChannel(context)
    }

    private fun createMainNotificationChannel(context: Context) {
        val channel =
            NotificationChannel(
                NotificationChannelsIds.TIMER.name,
                NotificationChannelsIds.TIMER.name,
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = "Notification channel for timers"
            }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }
}
