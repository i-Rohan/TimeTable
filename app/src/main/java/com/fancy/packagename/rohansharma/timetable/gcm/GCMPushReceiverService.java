package com.fancy.packagename.rohansharma.timetable.gcm;

import android.app.Notification;
import android.database.sqlite.SQLiteDatabase;
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
            String stream = jsonObject.getString("stream");
            String date = jsonObject.getString("date");
            String time = jsonObject.getString("time");
            String subject = jsonObject.getString("subject");
            sendNotification(title, stream, date, time, subject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendNotification(String title, String stream, String date, String time,
                                  String subject) {
        SQLiteDatabase sqLiteDatabase = openOrCreateDatabase("notifications", MODE_PRIVATE, null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS Notifications(" +
                "N_ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "TITLE VARCHAR(100)," +
                "STREAM VARCHAR(10)," +
                "DATE VARCHAR(50)," +
                "TIME VARCHAR(50)," +
                "SUBJECT VARCHAR(100));");

        sqLiteDatabase.execSQL("INSERT INTO Notifications VALUES('null'," +
                '\'' + title + "'," +
                '\'' + stream + "'," +
                '\'' + date + "'," +
                '\'' + time + "'," +
                '\'' + subject + "');");

        PugNotification.with(getApplicationContext())
                .load()
                .title(title)
                .bigTextStyle(stream + '\n' + date + '\n' + time + '\n' + subject, "BMU Time Table")
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