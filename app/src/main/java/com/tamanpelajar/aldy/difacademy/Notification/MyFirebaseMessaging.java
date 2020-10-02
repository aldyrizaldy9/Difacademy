package com.tamanpelajar.aldy.difacademy.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpNotifActivity;
import com.tamanpelajar.aldy.difacademy.CommonMethod;

import static com.tamanpelajar.aldy.difacademy.ActivityCommon.LoginActivity.USERID_PREFS;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    private static final String TAG = "ganteng";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "MyFirebaseMessaging onMessageReceived: jalan");
        String sented = remoteMessage.getData().get("sented");
        String user = remoteMessage.getData().get("user");

        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        String currentUser = preferences.getString(USERID_PREFS, "");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null && sented.equals(firebaseUser.getUid())) {
            if (!currentUser.equals(user)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sendOreoNotification(remoteMessage);
                } else {
                    sendNotification(remoteMessage);
                }
            }
        }
    }

    private void sendOreoNotification(RemoteMessage remoteMessage) {
        Log.d(TAG, "MyFirebaseMessaging sendOreoNotification: jalan");
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        double j = Double.valueOf(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, OpNotifActivity.class);
        intent.putExtra(CommonMethod.intentFromNotification, "ya");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) j, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoNotification oreoNotification = new OreoNotification(this);
        Notification.Builder builder = oreoNotification.getOeroNotification(title, body, pendingIntent, defaultSound, icon);

        double i = 0;
        if (j > 0) {
            i = j;
        }

        oreoNotification.getManager().notify((int) i, builder.build());
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        Log.d(TAG, "MyFirebaseMessaging sendNotification: jalan");
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, OpNotifActivity.class);
        intent.putExtra(CommonMethod.intentFromNotification, "ya");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent)
                .setContentText(body);
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        int i = 0;
        if (j > 0) {
            i = j;
        }

        nm.notify(i, builder.build());
    }
}
