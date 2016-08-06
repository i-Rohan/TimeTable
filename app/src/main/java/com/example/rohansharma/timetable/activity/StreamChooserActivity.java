package com.example.rohansharma.timetable.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.rohansharma.timetable.R;

import java.util.ArrayList;
import java.util.Collections;

import static com.example.rohansharma.timetable.commons.AppCommons.SIGNED_IN;
import static com.example.rohansharma.timetable.commons.AppCommons.SIGN_IN;
import static com.example.rohansharma.timetable.commons.AppCommons.STREAM;
import static com.example.rohansharma.timetable.commons.AppCommons.STREAMS;
import static com.example.rohansharma.timetable.commons.AppCommons.stream;

public class StreamChooserActivity extends AppCompatActivity {
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_chooser);

        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);

        setTitle("Select Stream");

        spinner = (Spinner) findViewById(R.id.spinner);

        SharedPreferences sharedPreferences = getSharedPreferences(SIGN_IN, Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean(SIGNED_IN, false)) {
            stream = sharedPreferences.getInt(STREAM, 0);
            startActivity(new Intent(this, TimeTableActivity.class));
            finish();
            return;
        }

        ArrayList<String> list = new ArrayList<>();
        Collections.addAll(list, STREAMS);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
    }

    public void onClickContinue(View v) {
        SharedPreferences.Editor editor = getSharedPreferences(SIGN_IN, Context.MODE_PRIVATE).edit();
        editor.putBoolean(SIGNED_IN, true);
        editor.putInt(STREAM, spinner.getSelectedItemPosition());
        editor.apply();

        startActivity(new Intent(this, TimeTableActivity.class));
        finish();
    }
}
