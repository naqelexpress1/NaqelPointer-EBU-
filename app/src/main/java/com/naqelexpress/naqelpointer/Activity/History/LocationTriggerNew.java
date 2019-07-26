package com.naqelexpress.naqelpointer.Activity.History;

/**
 * Created by Hasna on 12/19/18.
 */

import android.Manifest;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.service.UpdateLocation;

import org.json.JSONException;
import org.json.JSONObject;

public class LocationTriggerNew extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {

        StringBuilder sb = new StringBuilder();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            if (ActivityCompat.checkSelfPermission(LocationTriggerNew.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {

        return false;
    }
}