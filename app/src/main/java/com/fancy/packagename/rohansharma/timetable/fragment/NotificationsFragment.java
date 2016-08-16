package com.fancy.packagename.rohansharma.timetable.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.fancy.packagename.rohansharma.timetable.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.fancy.packagename.rohansharma.timetable.commons.AppCommons.FIRST_LAUNCH;

public class NotificationsFragment extends Fragment {
    ListView listView;
    RelativeLayout rl;

    public NotificationsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notifications, container, false);

        listView = (ListView) v.findViewById(R.id.listView);
        rl = (RelativeLayout) v.findViewById(R.id.rl);

        File file = new File("/data/data/" + getActivity().getPackageName() + "/databases/notifications");
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(FIRST_LAUNCH,
                Context.MODE_PRIVATE);
        if (file.exists() && sharedPreferences.getBoolean(FIRST_LAUNCH, true))
            file.delete();

        SharedPreferences.Editor editor = getActivity().getSharedPreferences(FIRST_LAUNCH,
                Context.MODE_PRIVATE).edit();
        editor.putBoolean(FIRST_LAUNCH, false);
        editor.apply();

        file = new File("/data/data/" + getActivity().getPackageName() + "/databases/notifications");
        if (file.exists()) {
            SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openDatabase("/data/data/" +
                            getActivity().getPackageName() + "/databases/notifications", null,
                    SQLiteDatabase.OPEN_READWRITE);

            Date time = Calendar.getInstance().getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            String currentDate = sdf.format(time);
//            Log.d("DATETIME", currentDate);
            sqLiteDatabase.execSQL("DELETE FROM Notifications WHERE END_DATETIME<'" + currentDate + "';");

            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM Notifications ORDER BY N_ID DESC"
                    , null);
            AbstractList<String> list = new ArrayList<>();
            if (cursor.moveToFirst()) {
                cursor.moveToFirst();
                String temp = cursor.getString(1) + '\n' + cursor.getString(2) + '\n' +
                        cursor.getString(3) + '\n' + cursor.getString(4) + '\n' +
                        cursor.getString(5);
                list.add(temp);
                while (cursor.moveToNext()) {
                    temp = cursor.getString(1) + '\n' + cursor.getString(2) + '\n' +
                            cursor.getString(3) + '\n' + cursor.getString(4) + '\n' +
                            cursor.getString(5);
                    list.add(temp);
                }
            } else {
                listView.setVisibility(View.GONE);
                rl.setVisibility(View.VISIBLE);
            }
            cursor.close();
            sqLiteDatabase.close();
            String values[] = list.toArray(new String[list.size()]);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_list_item_2, android.R.id.text2, values);
            listView.setAdapter(adapter);
        } else {
            listView.setVisibility(View.GONE);
            rl.setVisibility(View.VISIBLE);
        }

        return v;
    }

}
