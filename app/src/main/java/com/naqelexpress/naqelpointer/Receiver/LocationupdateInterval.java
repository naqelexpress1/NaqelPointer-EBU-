package com.naqelexpress.naqelpointer.Receiver;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.service.LocationupdateResetAlarm;
import com.naqelexpress.naqelpointer.service.UpdateLocation;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import static android.os.Build.VERSION.SDK_INT;

/**
 * Created by Hasna on 12/31/18.
 */

public class LocationupdateInterval extends BroadcastReceiver {
    public static String ACTION_ALARM_RECEIVER = "com.naqelexpress.naqelpointer.LocationAlaram";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            Location location = GlobalVar.getLastKnownLocation(context);
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

            DBConnections dbConnections = new DBConnections(context, null);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("EmployID", dbConnections.getEmpId(context));
                jsonObject.put("Latitude", Latitude);
                jsonObject.put("Longitude", Longitude);


//                dbConnections.InsertLocation(jsonObject.toString(), context);
                dbConnections.UpdateLocation(jsonObject.toString(), context);
                dbConnections.close();

                UpdateLocation.start(context);


            } catch (JSONException e) {
                e.printStackTrace();
            }


            LocationupdateResetAlarm.enqueueWork(context, intent);

//            try {
//                new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Ismail/readwrite/").mkdir();
//                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Ismail/readwrite/data.txt");
//                if (!file.exists()) {
//                    file.createNewFile();
//                }
//                FileOutputStream fileOutputStream = new FileOutputStream(file, true);
//                fileOutputStream.write((DateTime.now().toString() + System.getProperty("line.separator")).getBytes());
//
//            } catch (FileNotFoundException ex) {
//                System.out.println(ex);
//            } catch (IOException ex) {
//                System.out.println(ex);
//            }
        } else
            LocationupdateResetAlarm.enqueueWork(context, intent);
    }

    public static void cancelAlarm(Context context) {
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        /* cancel any pending alarm */
        alarm.cancel(getPendingIntent(context));
    }

    public static void setAlarm(boolean force, final Context context) {

        cancelAlarm(context);

        long delay = (1000 * 60 * 3);
        long when = DateTime.now().getMillis();
        if (!force) {
            when += delay;
        }
        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        /* fire the broadcast */
        if (SDK_INT >= Build.VERSION_CODES.M) {
            alarms.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, when, getPendingIntent(context));
        } else if (Build.VERSION_CODES.KITKAT <= SDK_INT && SDK_INT < Build.VERSION_CODES.M) {
            alarms.setExact(AlarmManager.RTC_WAKEUP, when, getPendingIntent(context));
        } else if (SDK_INT < Build.VERSION_CODES.KITKAT) {
            alarms.set(AlarmManager.RTC_WAKEUP, when, getPendingIntent(context));
        }

    }

    private static PendingIntent getPendingIntent(Context context) {
        Intent alarmIntent = new Intent(context, LocationupdateInterval.class);
        alarmIntent.setAction(ACTION_ALARM_RECEIVER);

        return PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}