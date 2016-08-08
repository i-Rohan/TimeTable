package com.fancy.packagename.rohansharma.timetable.gcm;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by RohansyN on 27-07-2016.
 */
public class GCMTokenRefreshService extends InstanceIDListenerService {

    //If the token is changed registering the device again
    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, GCMRegistrationService.class);
        startService(intent);
    }



}
