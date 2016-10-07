package com.tnine.ourcesu.gcm;

import android.app.Notification;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.fancy.packagename.rohansharma.timetable.R;
import com.fancy.packagename.rohansharma.timetable.activity.MainActivity;
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
            String endDatetime = jsonObject.getString("end_datetime");

            Log.d("GCM", "received");

            sendNotification(title, stream, date, time, subject, endDatetime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendNotification(String title, String stream, String date, String time,
                                  String subject, String endDatetime) {
        SQLiteDatabase sqLiteDatabase = openOrCreateDatabase("notifications", MODE_PRIVATE, null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS Notifications(" +
                "N_ID LONG," +
                "TITLE VARCHAR(100)," +
                "STREAM VARCHAR(10)," +
                "DATE VARCHAR(50)," +
                "TIME VARCHAR(50)," +
                "SUBJECT VARCHAR(100)," +
                "END_DATETIME VARCHAR(50));");

        sqLiteDatabase.execSQL("INSERT INTO Notifications " +
                "(N_ID,TITLE," +
                "STREAM," +
                "DATE," +
                "TIME," +
                "SUBJECT," +
                "END_DATETIME) " +
                "VALUES(" +
                System.currentTimeMillis() + ',' +
                '\'' + title + "'," +
                '\'' + stream + "'," +
                '\'' + date + "'," +
                '\'' + time + "'," +
                '\'' + subject + "'," +
                '\'' + endDatetime + "');");

        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("gcm", false))
            PugNotification.with(getApplicationContext())
                    .load()
                    .title(title)
                    .bigTextStyle(stream + '\n' + date + '\n' + time + '\n' + subject, "BMU Time Table")
                    .smallIcon(R.drawable.notification_icon)
                    .largeIcon(R.mipmap.ic_launcher)
                    .flags(Notification.DEFAULT_ALL)
                    .click(MainActivity.class)
                    .color(R.color.colorAccent)
                    .autoCancel(true)
                    .simple()
                    .build();
    }
}