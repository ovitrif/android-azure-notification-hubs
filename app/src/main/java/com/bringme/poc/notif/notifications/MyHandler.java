package com.bringme.poc.notif.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.bringme.poc.notif.MainActivity;
import com.bringme.poc.notif.R;
import com.microsoft.windowsazure.notifications.NotificationsHandler;

public class MyHandler extends NotificationsHandler {
    private static final int NOTIFICATION_ID = 1;
    private Context context;

    @Override
    public void onReceive(Context context, Bundle bundle) {
        this.context = context;
        String message = bundle.getString("message");
        sendNotification(message);
        if (MainActivity.isVisible) {
            MainActivity.mainActivity.ToastNotify(message);
        }
    }

    private void sendNotification(String msg) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getString(R.string.notification_title))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(msg))
                .setSound(defaultSoundUri)
                .setContentText(msg);

        notifBuilder.setContentIntent(contentIntent);

        notificationManager.notify(NOTIFICATION_ID, notifBuilder.build());
    }

}
