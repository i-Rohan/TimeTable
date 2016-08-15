package com.fancy.packagename.rohansharma.timetable.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.fancy.packagename.rohansharma.timetable.R;

public class SettingsActivity extends AppCompatActivity {
    Switch switch1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);

        switch1 = (Switch) findViewById(R.id.switch1);

        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("gcm", false))
            switch1.setChecked(true);

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor =
                        getSharedPreferences("Settings", MODE_PRIVATE).edit();
                editor.putBoolean("gcm", b);
                editor.apply();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }
}
