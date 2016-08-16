package com.fancy.packagename.rohansharma.timetable.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fancy.packagename.rohansharma.timetable.R;
import com.fancy.packagename.rohansharma.timetable.activity.StreamChooserActivity;
import com.fancy.packagename.rohansharma.timetable.commons.AppCommons;
import com.kennyc.bottomsheet.BottomSheet;
import com.kennyc.bottomsheet.BottomSheetListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.fancy.packagename.rohansharma.timetable.commons.AppCommons.API_URL;
import static com.fancy.packagename.rohansharma.timetable.commons.AppCommons.DAYS;
import static com.fancy.packagename.rohansharma.timetable.commons.AppCommons.DAY_IDS;
import static com.fancy.packagename.rohansharma.timetable.commons.AppCommons.PERIOD_IDS;
import static com.fancy.packagename.rohansharma.timetable.commons.AppCommons.SIGNED_IN;
import static com.fancy.packagename.rohansharma.timetable.commons.AppCommons.SIGN_IN;
import static com.fancy.packagename.rohansharma.timetable.commons.AppCommons.STREAM;
import static com.fancy.packagename.rohansharma.timetable.commons.AppCommons.STREAMS;

public class TimeTableFragment extends Fragment {
    public static boolean screenshot = true;
    public static String fname = "screenshot.jpg";
    static View v;
    int id = 1;
    TableLayout tableLayout;
    SQLiteDatabase timeTable;
    ProgressBar progressBar;
    TextView updated;
    boolean timeTableExists;
    TextView stream;
    ListView listView;
    HorizontalScrollView sv;

    public TimeTableFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_time_table, container, false);

        tableLayout = (TableLayout) v.findViewById(R.id.main_table);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        updated = (TextView) v.findViewById(R.id.updated);
        stream = (TextView) v.findViewById(R.id.stream);
        sv = (HorizontalScrollView) v.findViewById(R.id.sv);

        if (stream != null) {
            stream.setText(STREAMS[AppCommons.stream]);
        }

        File file = new File("/data/data/" + '/' + getActivity().getPackageName() + "/databases/" +
                "timetable");
        if (file.exists()) {
            timeTableExists = true;
            progressBar.setVisibility(View.GONE);
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SIGN_IN,
                    Context.MODE_PRIVATE);
            AppCommons.stream = sharedPreferences.getInt("stream", 0);
            long temp = (System.currentTimeMillis() - sharedPreferences.getLong("updated", 0))
                    / (1000 * 3600 * 24);
            if (temp == 0)
                updated.setText("Last synced: Today");
            else if (temp == 1)
                updated.setText("Last synced: " + 1 + " day ago");
            else
                updated.setText("Last synced: " + temp + " days ago");

            TableRow trHead = new TableRow(getActivity());
            trHead.setId(id++);
            trHead.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            TextView dayHeader = new TextView(getActivity());
            dayHeader.setId(id++);
            dayHeader.setText("Day");
            dayHeader.setGravity(Gravity.CENTER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                dayHeader.setBackground(ResourcesCompat.getDrawable(getResources(),
                        R.drawable.border1, null));
                dayHeader.setTextColor(Color.WHITE);
            }
            dayHeader.setPadding(16, 16, 16, 16);
            trHead.addView(dayHeader);

            TextView p1Header = new TextView(getActivity());
            p1Header.setId(id++);
            p1Header.setText("09:30 AM - 10:30 AM");
            p1Header.setGravity(Gravity.CENTER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                p1Header.setBackground(ResourcesCompat.getDrawable(getResources(),
                        R.drawable.border1, null));
                p1Header.setTextColor(Color.WHITE);
            }
            p1Header.setPadding(16, 16, 16, 16);
            trHead.addView(p1Header);

            TextView p2Header = new TextView(getActivity());
            p2Header.setId(id++);
            p2Header.setText("10:30 AM - 11:30 AM");
            p2Header.setGravity(Gravity.CENTER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                p2Header.setBackground(ResourcesCompat.getDrawable(getResources(),
                        R.drawable.border1, null));
                p2Header.setTextColor(Color.WHITE);
            }
            p2Header.setPadding(16, 16, 16, 16);
            trHead.addView(p2Header);

            TextView p3Header = new TextView(getActivity());
            p3Header.setId(id++);
            p3Header.setText("11:30 AM - 12:30 PM");
            p3Header.setGravity(Gravity.CENTER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                p3Header.setBackground(ResourcesCompat.getDrawable(getResources(),
                        R.drawable.border1, null));
                p3Header.setTextColor(Color.WHITE);
            }
            p3Header.setPadding(16, 16, 16, 16);
            trHead.addView(p3Header);

            TextView p4Header = new TextView(getActivity());
            p4Header.setId(id++);
            p4Header.setText("12:30 PM - 01:30 AM");
            p4Header.setGravity(Gravity.CENTER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                p4Header.setBackground(ResourcesCompat.getDrawable(getResources(),
                        R.drawable.border1, null));
                p4Header.setTextColor(Color.WHITE);
            }
            p4Header.setPadding(16, 16, 16, 16);
            trHead.addView(p4Header);

            TextView p5Header = new TextView(getActivity());
            p5Header.setId(id++);
            p5Header.setText("01:30 PM - 02:30 PM");
            p5Header.setGravity(Gravity.CENTER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                p5Header.setBackground(ResourcesCompat.getDrawable(getResources(),
                        R.drawable.border1, null));
                p5Header.setTextColor(Color.WHITE);
            }
            p5Header.setPadding(16, 16, 16, 16);
            trHead.addView(p5Header);

            TextView p6Header = new TextView(getActivity());
            p6Header.setId(id++);
            p6Header.setText("02:30 PM - 03:30 PM");
            p6Header.setGravity(Gravity.CENTER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                p6Header.setBackground(ResourcesCompat.getDrawable(getResources(),
                        R.drawable.border1, null));
                p6Header.setTextColor(Color.WHITE);
            }
            p6Header.setPadding(16, 16, 16, 16);
            trHead.addView(p6Header);

            TextView p7Header = new TextView(getActivity());
            p7Header.setId(id++);
            p7Header.setText("03:30 PM - 04:30 PM");
            p7Header.setGravity(Gravity.CENTER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                p7Header.setBackground(ResourcesCompat.getDrawable(getResources(),
                        R.drawable.border1, null));
                p7Header.setTextColor(Color.WHITE);
            }
            p7Header.setPadding(16, 16, 16, 16);
            trHead.addView(p7Header);

            TextView p8Header = new TextView(getActivity());
            p8Header.setId(id++);
            p8Header.setText("04:30 PM - 05:30 PM");
            p8Header.setGravity(Gravity.CENTER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                p8Header.setBackground(ResourcesCompat.getDrawable(getResources(),
                        R.drawable.border1, null));
                p8Header.setTextColor(Color.WHITE);
            }
            p8Header.setPadding(16, 16, 16, 16);
            trHead.addView(p8Header);

            tableLayout.addView(trHead, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            for (int i = 0; i < 5; i++) {
                SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openDatabase("/data/data/" + '/' +
                                getActivity().getPackageName() + "/databases/timetable", null,
                        SQLiteDatabase.OPEN_READONLY);
                Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM TimeTable WHERE STREAM='" +
                        STREAMS[AppCommons.stream] + "' AND DAY=" + i, null);
                cursor.moveToFirst();

                TableRow tr = new TableRow(getActivity());
                tr.setId(id++);
                tr.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));

                TextView day = new TextView(getActivity());
                day.setId(id++);
                day.setGravity(Gravity.CENTER_HORIZONTAL);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    day.setBackground(ResourcesCompat.getDrawable(
                            getResources(), R.drawable.border1, null));
                }
                day.setTextColor(Color.WHITE);
                day.setText(DAYS[i]);
                day.setPadding(16, 16, 16, 16);
                tr.addView(day);

                for (int k = 2; k < 10; k++) {
                    TextView period = new TextView(getActivity());
                    period.setId(id++);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        period.setBackground(ResourcesCompat.getDrawable(
                                getResources(), R.drawable.border2, null));
                    }
                    period.setTextColor(Color.BLACK);
                    period.setText(cursor.getString(k));
                    period.setPadding(16, 16, 16, 16);
                    tr.addView(period);
                }
                cursor.close();
                sqLiteDatabase.close();
                tableLayout.addView(tr, new TableLayout.LayoutParams(ViewGroup.LayoutParams
                        .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }

            getTimeTableData();
        } else {
            timeTableExists = false;

            getTimeTableData();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                takeScreenshot();
            }
        }, 100);

        return v;
    }

    public void takeScreenshot() {
        Bitmap bitmap = loadBitmapFromView(); // Get the bitmap
        if (screenshot)
            saveTheBitmap(bitmap);               // Save it to the external storage device.
    }

    public void saveTheBitmap(Bitmap bmp) {
        File myDir = new File(Environment.getExternalStorageDirectory() + "/.timetable");
        if (!myDir.exists())
            myDir.mkdirs();
        File file = new File(myDir, fname);
//        Log.i("File name", String.valueOf(file));
//        if (file.exists())
//            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public Bitmap loadBitmapFromView() {
        Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(sv.getChildAt(0).getWidth(),
                    sv.getChildAt(0).getHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            sv.getChildAt(0).draw(canvas);
        } catch (RuntimeException ignored) {
            screenshot = false;
        }
        return bitmap;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (timeTableExists) {
            for (int i = 0; i < 8; i++) {
                TextView textView = (TextView) v.findViewById(PERIOD_IDS[i]);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    if (textView != null) {
                        textView.setBackground(ResourcesCompat.getDrawable(
                                getResources(), R.drawable.border1, null));
                    }
                }
            }

            for (int i = 0; i < 5; i++) {
                TextView textView = (TextView) v.findViewById(DAY_IDS[i]);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    if (textView != null) {
                        textView.setBackground(ResourcesCompat.getDrawable(
                                getResources(), R.drawable.border1, null));
                    }
                }
            }

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            String time = simpleDateFormat.format(calendar.getTime());

            int temp1 = 0;

            for (int i = 9; i <= 16; i++) {
                if (i == 9) {
                    if ((time.compareTo("09:30") >= 0) && (time.compareTo("10:30") <= 0)) {
                        TextView textView = (TextView) v.findViewById(PERIOD_IDS[0]);
                        temp1 = PERIOD_IDS[0];
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            if (textView != null) {
                                textView.setBackground(ResourcesCompat.getDrawable(
                                        getResources(), R.drawable.border3, null));
                            }
                            if (textView != null) {
                                textView.setTextColor(Color.WHITE);
                            }
                        }
                    }
                } else if ((time.compareTo(i + ":30") >= 0) && (time.compareTo((i + 1) + ":30") <= 0)) {
                    TextView textView = (TextView) v.findViewById(PERIOD_IDS[i - 9]);
                    temp1 = PERIOD_IDS[i - 9];
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        if (textView != null) {
                            textView.setBackground(ResourcesCompat.getDrawable(
                                    getResources(), R.drawable.border3, null));
                        }
                        if (textView != null) {
                            textView.setTextColor(Color.WHITE);
                        }
                    }
                }
            }

            Calendar instance = Calendar.getInstance();
            int day = instance.get(Calendar.DAY_OF_WEEK);
            int temp2 = 0;
            for (int i = 0; i < 5; i++) {
                if (day == (i + 2)) {
                    TextView textView = (TextView) v.findViewById(DAY_IDS[i]);
                    temp2 = DAY_IDS[i];
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        if (textView != null) {
                            textView.setBackground(ResourcesCompat.getDrawable(
                                    getResources(), R.drawable.border3, null));
                        }
                        if (textView != null) {
                            textView.setTextColor(Color.WHITE);
                        }
                    }
                }
            }

            if ((temp1 >= 3) && (temp1 <= 10) && (temp2 >= 12) && (temp2 <= 52)) {
                TextView textView = (TextView) v.findViewById((temp2 + temp1) - 2);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    if (textView != null) {
                        textView.setBackground(ResourcesCompat.getDrawable(
                                getResources(), R.drawable.border3, null));
                    }
                    if (textView != null) {
                        textView.setTextColor(Color.WHITE);
                    }
                }
            }
        }
    }

    void getTimeTableData() {
        String url = API_URL + "getTimeTable.php";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Log.d("timetable", ' ' + response);

                        timeTable = getActivity().openOrCreateDatabase("timetable",
                                Context.MODE_PRIVATE, null);
                        timeTable.execSQL("CREATE TABLE IF NOT EXISTS TimeTable(" +
                                "STREAM VARCHAR(10),DAY INT,P1 VARCHAR(50)," +
                                "P2 VARCHAR(50),P3 VARCHAR(50),P4 VARCHAR(50)," +
                                "P5 VARCHAR(50),P6 VARCHAR(50),P7 VARCHAR(50)," +
                                "P8 VARCHAR(50));");
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("result");
                            int length = jsonArray.length();
                            timeTable.execSQL("DELETE FROM TimeTable");
                            for (int i = 0; i < length; i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                timeTable.execSQL("INSERT INTO TimeTable VALUES('" +
                                        object.getString("stream") + "','" + object.getInt("day") + "','" +
                                        object.getString("p1") + "','" + object.getString("p2") + "','" +
                                        object.getString("p3") + "','" + object.getString("p4") + "','" +
                                        object.getString("p5") + "','" + object.getString("p6") + "','" +
                                        object.getString("p7") + "','" + object.getString("p8") + "');");
                            }
                            SharedPreferences.Editor editor = getActivity().getSharedPreferences(
                                    "SignIn", Context.MODE_PRIVATE).edit();
                            editor.putLong("updated", System.currentTimeMillis());
                            editor.apply();
                            updated.setText("Last synced: Today");
                            if (!timeTableExists) {
                                startActivity(getActivity().getIntent());
                                getActivity().finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Log.d("timetable", "error");

                        if (!timeTableExists) {
                            Toast.makeText(getActivity(), "Connection Problem! :(",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }) {
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }
}
