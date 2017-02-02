package com.github.ymegane.android.twitter.wear;

import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;

import com.github.ymegane.android.dlog.DLog;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        DLog.d("From: " + remoteMessage.getFrom());
        DLog.d("Notification Message Body: " + remoteMessage.getNotification().getBody());

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setContentTitle(notification.getTitle());
        builder.setContentText(notification.getBody());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setShowWhen(true);

        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());
    }
}
