package com.naqelexpress.naqelpointer.service;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;
import android.widget.Toast;

import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;

import org.joda.time.DateTime;

/**
 * Created by Hasna on 12/20/18.
 */

public class RadiusValidation200 extends JobIntentService {

    public static void start(Context context) {
        Intent starter = new Intent(context, RadiusValidation200.class);
        RadiusValidation200.enqueueWork(context, starter);
    }

    /**
     * Unique job ID for this service.
     */
    static final int JOB_ID = 1006;
    protected boolean flag_thread = false;
    double Latitude = 24.5536336;
    double Longitude = 46.8623006;


    private static void enqueueWork(Context context, Intent intent) {

        enqueueWork(context, RadiusValidation200.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull final Intent intent) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // new DownloadJSON().execute();
                try {

                    if (GlobalVar.find200Radios(0, 0, 10) && !flag_thread) {
                        Toast.makeText(getApplicationContext(), "true", Toast.LENGTH_SHORT).show();
                        flag_thread = true;
                        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                        dbConnections.InsertALInOut(String.valueOf(Latitude), String.valueOf(Longitude), DateTime.now().toString(),
                                "", getApplicationContext());
                        dbConnections.close();
                    } else {
                        if (flag_thread) {
                            flag_thread = false;
                            DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                            dbConnections.InsertALInOut(String.valueOf(Latitude), String.valueOf(Longitude), "",
                                    DateTime.now().toString(), getApplicationContext());
                            dbConnections.close();
                            handler.removeCallbacksAndMessages(null);
                            stopSelf();
                            android.os.Process.killProcess(android.os.Process.myPid());

                        }
                    }


                    handler.postDelayed(this, 20000);
                } catch (Exception e) {
                    flag_thread = false;
                    handler.postDelayed(this, 20000);
                    Log.e("Dashboard thread", e.toString());
                }

            }
        }, 20000);


    }


    @Override
    public boolean onStopCurrentWork() {
        return super.onStopCurrentWork();
    }

}
