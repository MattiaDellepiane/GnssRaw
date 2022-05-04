package com.github.mattiadellepiane.gnssraw.services

import com.github.mattiadellepiane.gnssraw.R
import com.github.mattiadellepiane.gnssraw.data.SharedData
import android.content.Intent
import android.os.Build
import com.github.mattiadellepiane.gnssraw.MainActivity
import android.os.IBinder
import java.lang.UnsupportedOperationException
import android.app.*

class BackgroundMeasurementService : Service() {
    private var notificationManager: NotificationManager? = null

    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private val NOTIFICATION_ID = R.string.service_started
    var chan: NotificationChannel? = null
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.putExtra(EXTRA_NOTIFICATION, true)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chan = NotificationChannel(CHANNEL_ID, "Nome canale", NotificationManager.IMPORTANCE_DEFAULT)
            chan!!.description = "Descrizione canae"
            notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager!!.createNotificationChannel(chan!!)
        }
        val notification: Notification
        notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("GnssRaw")
                    .setContentText("Background GNSS")
                    .setSmallIcon(R.drawable.ic_notification_icon)
                    .setContentIntent(pendingIntent)
                    .build()
        } else {
            Notification()
        }
        startForeground(NOTIFICATION_ID, notification)
        SharedData.instance.startMeasurements()
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("Not yet implemented") //non serve, non devo chiamare metodi di questa classe dall'esterno
    }

    override fun onDestroy() {
        // Cancel the persistent notification.
        SharedData.instance.stopMeasurements()
        //notificationManager.cancel(NOTIFICATION_ID);
    }

    companion object {
        const val CHANNEL_ID = "com.github.mattiadellepiane.gnssraw.services.BackgroundMeasurementService"
        const val EXTRA_NOTIFICATION = "com.github.mattiadellepiane.gnssraw.services.BackgroundMeasurementService.NOTIFICATION"
    }
}