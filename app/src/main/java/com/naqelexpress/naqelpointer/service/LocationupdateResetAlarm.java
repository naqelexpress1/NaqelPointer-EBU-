package com.naqelexpress.naqelpointer.service;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

import com.naqelexpress.naqelpointer.Receiver.LocationupdateInterval;

/**
 * Created by Hasna on 1/1/19.
 */

public class LocationupdateResetAlarm extends JobIntentService {
    /* Give the Job a Unique Id */
    private static final int JOB_ID = 1003;

    public static void enqueueWork(Context ctx, Intent intent) {
        enqueueWork(ctx, LocationupdateResetAlarm.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        /* your code here */
        /* reset the alarm */
        LocationupdateInterval.setAlarm(false, getApplicationContext());
        stopSelf();
    }
}
