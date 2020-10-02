package com.tamanpelajar.aldy.difacademy.Notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

public class OreoNotification extends ContextWrapper {
    private static final String TAG = "ganteng";
    private static final String CHANNEL_ID = "tamanpelajar";
    private static final String CHANNEL_NAME = "payment";

    private NotificationManager notificationManager;

    public OreoNotification(Context context) {
        super(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        Log.d(TAG, "OreoNotification createChannel: jalan");
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(false);
        channel.enableVibration(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        Log.d(TAG, "OreoNotification getManager: jalan");
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return notificationManager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getOeroNotification(String title,
                                                    String body,
                                                    PendingIntent pendingIntent,
                                                    Uri soundUri,
                                                    String icon) {
        Log.d(TAG, "OreoNotification getOeroNotification: jalan");
        return new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(Integer.parseInt(icon))
                .setSound(soundUri)
                .setAutoCancel(true);
    }
}
