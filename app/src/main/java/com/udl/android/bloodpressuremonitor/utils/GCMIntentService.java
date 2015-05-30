package com.udl.android.bloodpressuremonitor.utils;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.udl.android.bloodpressuremonitor.BPMActivityController;
import com.udl.android.bloodpressuremonitor.R;

/**
 * Created by adrian on 28/3/15.
 */
public class GCMIntentService extends IntentService {

    public GCMIntentService(){

        super("GCMIntentService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {

            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString(), extras);
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " +
                        extras.toString(), extras);
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                sendNotification(getResources().getString(R.string.healthstatus),extras);
            }
        }

        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg, Bundle extras) {
        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        String message = extras.getString("message")+"";
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, BPMActivityController.class).setAction("com.new.push")
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP).putExtra(
                        "message",message), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentTitle("BPM Notification")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(Constants.NOTIFICATION, mBuilder.build());
    }
}
