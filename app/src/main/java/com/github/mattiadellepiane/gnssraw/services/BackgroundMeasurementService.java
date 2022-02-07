package com.github.mattiadellepiane.gnssraw.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.github.mattiadellepiane.gnssraw.MainActivity;
import com.github.mattiadellepiane.gnssraw.R;
import com.github.mattiadellepiane.gnssraw.data.SharedData;

public class BackgroundMeasurementService extends Service {
    private NotificationManager notificationManager;

    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private int NOTIFICATION_ID = R.string.service_started;
    public static final String CHANNEL_ID = "com.github.mattiadellepiane.gnssraw.services.BackgroundMeasurementService";
    public static final String EXTRA_NOTIFICATION = "com.github.mattiadellepiane.gnssraw.services.BackgroundMeasurementService.NOTIFICATION";
    NotificationChannel chan;

    public BackgroundMeasurementService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra(EXTRA_NOTIFICATION, true);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            chan = new NotificationChannel(CHANNEL_ID, "Nome canale", NotificationManager.IMPORTANCE_DEFAULT);
            chan.setDescription("Descrizione canae");
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(chan);
        }

        Notification notification;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("GnssRaw")
                    .setContentText("Background GNSS")
                    .setSmallIcon(R.drawable.ic_notification_icon)
                    .setContentIntent(pendingIntent)
                    .build();
        }
        else{
            notification = new Notification();
        }

        startForeground(NOTIFICATION_ID, notification);

        SharedData.getInstance().startMeasurements();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented"); //non serve, non devo chiamare metodi di questa classe dall'esterno
    }


    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        SharedData.getInstance().stopMeasurements();
        //notificationManager.cancel(NOTIFICATION_ID);
    }
}