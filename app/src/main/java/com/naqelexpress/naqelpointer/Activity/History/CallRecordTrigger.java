package com.naqelexpress.naqelpointer.Activity.History;

/**
 * Created by Hasna on 12/19/18.
 */

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.service.CallRecordAbove26;

import org.joda.time.DateTime;

import java.util.List;

public class CallRecordTrigger extends JobService {

    static final Uri CallLog_URI = CallLog.Calls.CONTENT_URI;
    static JobInfo JOB_INFO;

    static {
        JobInfo.Builder builder = new JobInfo.Builder(4002,
                new ComponentName("com.naqelexpress.naqelpointer", CallRecordTrigger.class.getName()));
        // Look for specific changes to images in the provider.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.addTriggerContentUri(new JobInfo.TriggerContentUri(
                    CallLog.Calls.CONTENT_URI_WITH_VOICEMAIL,// MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    JobInfo.TriggerContentUri.FLAG_NOTIFY_FOR_DESCENDANTS));
            builder.addTriggerContentUri(new JobInfo.TriggerContentUri(CallLog_URI, 0));
            JOB_INFO = builder.build();
        }

    }

    final Handler mHandler = new Handler();
    final Runnable mWorker = new Runnable() {
        @Override
        public void run() {
            scheduleJob(CallRecordTrigger.this);
            jobFinished(mRunningParams, false);
        }
    };

    JobParameters mRunningParams;

    // Schedule this job, replace any existing one.
    public static void scheduleJob(Context context) {
        JobScheduler js = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            js = context.getSystemService(JobScheduler.class);
        }
        js.schedule(JOB_INFO);
        Log.i("CallRecord", "JOB SCHEDULED!");
    }

    // Check whether this job is currently scheduled.
    public static boolean isScheduled(Context context) {
        JobScheduler js = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            js = context.getSystemService(JobScheduler.class);
        }
        List<JobInfo> jobs = js.getAllPendingJobs();
        if (jobs == null) {
            return false;
        }
        for (int i = 0; i < jobs.size(); i++) {
            if (jobs.get(i).getId() == 4002) {
                return true;
            }
        }
        return false;
    }

    // Cancel this job, if currently scheduled.
    public static void cancelJob(Context context) {
        JobScheduler js = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            js = context.getSystemService(JobScheduler.class);
        }
        js.cancel(4002);
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i("CallRecord", "JOB STARTED!");
        mRunningParams = params;

//        Toast.makeText(this, "Call", Toast.LENGTH_LONG).show();

        // Instead of real work, we are going to build a string to show to the user.
        //  StringBuilder sb = new StringBuilder();

        // Did we trigger due to a content change?

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // new DownloadJSON().execute();
                try {

                } catch (Exception e) {

//                            handler.postDelayed(this, 20000);
                    Log.e("Dashboard thread", e.toString());
                }

            }
        }, 5000);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (params.getTriggeredContentAuthorities() != null) {
                //    boolean rescanNeeded = false;

                if (params.getTriggeredContentUris() != null) {

                    if (ActivityCompat.checkSelfPermission(CallRecordTrigger.this, Manifest.permission.READ_CALL_LOG)
                            != PackageManager.PERMISSION_GRANTED) {
                        mHandler.postDelayed(mWorker, 10 * 1000);
                        return true;
                    }

                    Cursor managedCursor = getContentResolver().query(CallLog_URI, null,
                            null, null, android.provider.CallLog.Calls.DATE + " DESC limit 1;"); //android.provider.CallLog.Calls.DATE + " DESC limit 1;"


                    int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
                    int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
                    int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
                    int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

                    managedCursor.moveToFirst();

                    // while (managedCursor.moveToNext()) {
                    String phNumber = managedCursor.getString(number);
                    String callType = managedCursor.getString(type);
                    String callDate = managedCursor.getString(date);
                    String callDayTime = new DateTime(Long.valueOf(callDate)).toString();
                    String callDuration = managedCursor.getString(duration);
                    DateTime endtime = DateTime.now();
                    String dir = null;
                    int dircode = Integer.parseInt(callType);
                    switch (dircode) {
                        case CallLog.Calls.OUTGOING_TYPE:
                            endtime = DateTime.now().minusSeconds(10);
                            dir = "OUTGOING";
                            break;

                        case CallLog.Calls.INCOMING_TYPE:
                            endtime = DateTime.now().plusSeconds(Integer.parseInt(callDuration));
                            dir = "INCOMING";
                            break;

                        case CallLog.Calls.MISSED_TYPE:
                            dir = "MISSED";
                            break;
                        case CallLog.Calls.REJECTED_TYPE:
                            dir = "REJECT";
                            break;
                        case CallLog.Calls.BLOCKED_TYPE:
                            dir = "BLOCK";
                            break;

                    }
                    String couriernumber = "";
                    try {
                        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                        couriernumber = tm.getLine1Number();
                        if (couriernumber == null || couriernumber.length() == 0) {
                            couriernumber = tm.getSimSerialNumber();
                            if (couriernumber == null || couriernumber.length() == 0) {
                                couriernumber = tm.getDeviceId();
                            }
                        }
                    } catch (Exception e) {

                    }
//                    sb.append("C No - " + couriernumber);
//                    sb.append("type " + dir);
//                    sb.appendd("Call No - " + phNumber);
//                    Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();

                    String CustNo =
                            (phNumber.length() >= 9) ? phNumber.substring(phNumber.length() - 9) : "Not";
                    DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                    String WayBillNo = dbConnections.getWaybillByMobileNo(CustNo, getApplicationContext());
                    dbConnections.InsertCallLog(dir, phNumber, callDuration, WayBillNo, callDayTime,
                            callDayTime.toString(), endtime.toString(), couriernumber, getApplicationContext());

                    dbConnections.close();
//                    System.out.println(sb);
                    CallRecordAbove26.start(getApplicationContext());

                }
            }
        }
//        Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();

        // We will emulate taking some time to do this work, so we can see batching happen.
        mHandler.postDelayed(mWorker, 10 * 1000);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        mHandler.removeCallbacks(mWorker);
        return false;
    }
}