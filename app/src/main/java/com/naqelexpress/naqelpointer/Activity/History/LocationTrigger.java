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
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.service.UpdateLocation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class LocationTrigger extends JobService {

    static JobInfo JOB_INFO;
    static long delytimemills = 5000;

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            JobInfo.Builder builder = new JobInfo.Builder(4003,
                    new ComponentName("com.naqelexpress.naqelpointer", LocationTrigger.class.getName()));
            builder.setMinimumLatency(delytimemills);
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            builder.setPersisted(true);
            JOB_INFO = builder.build();
        } else {
            JobInfo.Builder builder = new JobInfo.Builder(4003,
                    new ComponentName("com.naqelexpress.naqelpointer", LocationTrigger.class.getName()));
            builder.setPeriodic(delytimemills);
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            builder.setPersisted(true);
            JOB_INFO = builder.build();
        }

    }

    final Handler mHandler = new Handler();
    final Runnable mWorker = new Runnable() {
        @Override
        public void run() {
            scheduleJob(LocationTrigger.this);
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
            if (jobs.get(i).getId() == 4003) {
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
        js.cancel(4003);
    }

    @Override
    public boolean onStartJob(JobParameters params) {

        mRunningParams = params;

        StringBuilder sb = new StringBuilder();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            if (ActivityCompat.checkSelfPermission(LocationTrigger.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                mHandler.postDelayed(mWorker, delytimemills);
                return true;
            }

            Location location = GlobalVar.getLastKnownLocation(getApplicationContext());
            String Latitude = "";
            String Longitude = "";
            if (location != null) {
                try {
                    Latitude = String.valueOf(location.getLatitude());
                    Longitude = String.valueOf(location.getLongitude());
                } catch (Exception e) {
                    System.out.println(e);
                }
            }

            DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("EmployID", dbConnections.getEmpId(getApplicationContext()));
                jsonObject.put("Latitude", Latitude);
                jsonObject.put("Longitude", Longitude);


                dbConnections.InsertLocation(jsonObject.toString(), getApplicationContext());
                dbConnections.close();
                UpdateLocation.start(getApplicationContext());

                sb.append("Latitude " + Latitude);
                sb.append("Longitude " + Longitude);
                Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        mHandler.postDelayed(mWorker, delytimemills);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        mHandler.removeCallbacks(mWorker);
        return false;
    }
}