package com.fancy.packagename.rohansharma.timetable.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fancy.packagename.rohansharma.timetable.R;
import com.fancy.packagename.rohansharma.timetable.fragment.NotificationsFragment;
import com.fancy.packagename.rohansharma.timetable.fragment.TimeTableFragment;
import com.fancy.packagename.rohansharma.timetable.gcm.GCMRegistrationIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static cn.pedant.SweetAlert.SweetAlertDialog.WARNING_TYPE;
import static com.fancy.packagename.rohansharma.timetable.commons.AppCommons.API_URL;
import static com.fancy.packagename.rohansharma.timetable.commons.AppCommons.DOWNLOAD_URL;
import static com.fancy.packagename.rohansharma.timetable.commons.AppCommons.FIRST_LAUNCH;
import static com.fancy.packagename.rohansharma.timetable.commons.AppCommons.SIGNED_IN;
import static com.fancy.packagename.rohansharma.timetable.commons.AppCommons.SIGN_IN;
import static com.fancy.packagename.rohansharma.timetable.commons.AppCommons.STREAM;

public class MainActivity extends AppCompatActivity {
    static String link;
    boolean doubleBackToExitPressedOnce;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager());

        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        SharedPreferences sharedPreferences = getSharedPreferences("v0.55-alpha", MODE_PRIVATE);
        if (sharedPreferences.getBoolean(FIRST_LAUNCH, true)) {
            File file = new File("/data/data/" + getPackageName() + "/databases/timetable");
            if (file.exists())
                file.delete();
        }

        SharedPreferences.Editor editor = getSharedPreferences("v0.55-alpha", MODE_PRIVATE).edit();
        editor.putBoolean(FIRST_LAUNCH, false);
        editor.apply();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    int verCode = pInfo.versionCode;
                    checkForUpdate(verCode);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }, 2000);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)) {
                    final String token = intent.getStringExtra("token");

                    Log.d("GCM Token", token);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            putGCMToken(token);
                        }
                    }, 1000);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            putAnalytics(token);
                        }
                    }, 1500);
                } else if (intent.getAction().equals(
                        GCMRegistrationIntentService.REGISTRATION_ERROR)) {
                    Toast.makeText(getApplicationContext(), "GCM registration error!",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_LONG)
                            .show();
                }
            }
        };

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(
                getApplicationContext());

        if (ConnectionResult.SUCCESS == resultCode) {
            Intent intent = new Intent(this, GCMRegistrationIntentService.class);
            startService(intent);
        } else {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                Toast.makeText(getApplicationContext(), "Google Play Service is not " +
                        "install/enabled in this device!", Toast.LENGTH_LONG).show();
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());
            } else {
                Toast.makeText(getApplicationContext(), "This device does not support for Google " +
                        "Play Service!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
    }


    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_stream:
                SharedPreferences.Editor editor = getSharedPreferences(SIGN_IN, MODE_PRIVATE).edit();
                editor.remove(SIGNED_IN);
                editor.remove(STREAM);
                editor.apply();
                startActivity(new Intent(this, StreamChooserActivity.class));
                finish();
                return true;
            case R.id.action_feedback:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"irohansharma95@gmail.com"});
                try {
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(getApplicationInfo().labelRes) +
                            ' ' + getPackageManager().getPackageInfo(getPackageName(), 0)
                            .versionName + " Feedback");
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                intent.putExtra(Intent.EXTRA_TEXT, "Model: " + Build.MODEL + '\n' + "Android API: "
                        + Build.VERSION.SDK_INT + "\n\n");
                try {
                    startActivity(Intent.createChooser(intent, "Send mail..."));
                } catch (ActivityNotFoundException ignored) {
                    Toast.makeText(this, "There are no email clients installed.",
                            Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_share_app:
                intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, DOWNLOAD_URL);
                startActivity(Intent.createChooser(intent, "Share Via..."));
                return true;
            case R.id.actioon_share_timetable:
                if (TimeTableFragment.screenshot) {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/jpeg");
                    String imagePath = Environment.getExternalStorageDirectory() + "/.timetable/" +
                            TimeTableFragment.fname;
//                    Log.d("image path", imagePath);
                    File imageFileToShare = new File(imagePath);
                    Uri uri = Uri.fromFile(imageFileToShare);
                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(share, "Share Via..."));
                } else
                    Toast.makeText(this, "Cannot share on this device! :(", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String getUsername() {
        AccountManager manager = AccountManager.get(this);
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<>();

        for (Account account : accounts)
            possibleEmails.add(account.name);

        if (!possibleEmails.isEmpty() && (possibleEmails.get(0) != null)) {
            String email = possibleEmails.get(0);

            return email.isEmpty() ? "null" : email;
        }
        return null;
    }

    protected void putGCMToken(final String token) {
        String url = API_URL + "putGCMToken.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("putGcm", ' ' + response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("putGcm", "error");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", token);
                params.put("username", getUsername());
                Log.d("username", getUsername());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    void checkForUpdate(final int vCode) {
        String url = API_URL + "checkForUpdate.php";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("update", response);

//                        String name;

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("result");
                            JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                            int code = jsonObject1.getInt("vCode");
//                            name = c.getString("vName");
                            String desc = jsonObject1.getString("desc");
                            link = jsonObject1.getString("link");
                            if (code > vCode) {
                                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(
                                        MainActivity.this, WARNING_TYPE);
                                sweetAlertDialog.setTitleText("App Update Available!");
                                sweetAlertDialog.setContentText(desc);
                                sweetAlertDialog.setConfirmText("Download");
                                sweetAlertDialog.setCancelText("Cancel");
                                sweetAlertDialog.setCancelable(true);
                                sweetAlertDialog.setCanceledOnTouchOutside(false);
                                sweetAlertDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismissWithAnimation();
                                    }
                                });
                                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                                Uri.parse(link));
                                        startActivity(browserIntent);
                                    }
                                });
                                sweetAlertDialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("update", "error");
                    }
                }) {
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    protected void putAnalytics(final String token) {
        String url = API_URL + "putAnalytics.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("putAnalytics", ' ' + response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("putAnalytics", "error");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", token);
                params.put("username", getUsername());
                params.put("time", String.valueOf(System.currentTimeMillis()));
                SharedPreferences sharedPreferences = getSharedPreferences(SIGN_IN, MODE_PRIVATE);
                params.put("stream", String.valueOf(sharedPreferences.getInt("stream", 0)));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new TimeTableFragment();
                case 1:
                    return new NotificationsFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Time Table";
                case 1:
                    return "Notifications";
            }
            return null;
        }
    }
}