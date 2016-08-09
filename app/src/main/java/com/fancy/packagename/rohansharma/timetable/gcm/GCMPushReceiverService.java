package com.fancy.packagename.rohansharma.timetable.gcm;

import android.app.Notification;
import android.os.Bundle;

import com.fancy.packagename.rohansharma.timetable.R;
import com.fancy.packagename.rohansharma.timetable.activity.TimeTableActivity;
import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.goncalves.pugnotification.notification.PugNotification;

public class GCMPushReceiverService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");

        try {
            JSONObject jsonObject = new JSONObject(message);
            String title = jsonObject.getString("title");
            String msg = jsonObject.getString("stream") + '\n' + jsonObject.getString("date") +
                    '\n' + jsonObject.getString("time") + '\n' + jsonObject.getString("subject");
            sendNotification(title, msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendNotification(String title, String message) {
        PugNotification.with(getApplicationContext())
                .load()
                .title(title)
                .bigTextStyle(message, "BMU Time Table")
                .smallIcon(R.drawable.notification_icon)
                .largeIcon(R.mipmap.ic_launcher)
                .flags(Notification.DEFAULT_ALL)
                .click(TimeTableActivity.class)
                .color(R.color.colorAccent)
                .autoCancel(true)
                .simple()
                .build();
    }
}