package com.fancy.packagename.rohansharma.timetable.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.fancy.packagename.rohansharma.timetable.R;
import com.fancy.packagename.rohansharma.timetable.activity.TimeTableActivity;
import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by RohansyN on 27-07-2016.
 */
public class GCMPushReciever extends GcmListenerService {
    public static String message;

    //This method will be called on every new message received
    @Override
    public void onMessageReceived(String from, Bundle data) {
        //Getting the message from the bundle
        message = data.getString("message");


        //Displaying a notification with the message
        sendNotification(message);
    }

    //This method is generating a notification and displaying the notification
    private void sendNotification(String message) {
        Intent intent = new Intent(this,TimeTableActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int requestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(message)
                .setContentTitle("DU & U")
                .setSound(sound)
                .setVibrate(new long[]{1000, 1000, 1000})
                .setLights(Color.RED, 3000, 3000)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, noBuilder.build()); //0 = ID of notification
    }
}