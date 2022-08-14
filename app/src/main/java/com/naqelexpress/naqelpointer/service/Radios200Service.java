package com.naqelexpress.naqelpointer.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.maps.model.LatLng;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import org.joda.time.DateTime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Radios200Service extends Service {

    protected boolean flag_thread = false;
    double Latitude = 24.552710;
    double Longitude = 46.864072;
    protected int i = 0;

    ArrayList<LatLng> location = new ArrayList<>();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "com.naqelexpress.naqelpointer.service.Radios200";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // new DownloadJSON().execute();
                try {
                    DBConnections db = new DBConnections(getApplicationContext(), null);
                    Cursor cursor = db.Fill("select * from MyRouteShipments where EmpID = " + db.getEmpId(getApplicationContext()), getApplicationContext());

                    if (cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        do {

                            String latitude = cursor.getString(cursor.getColumnIndex("Latitude"));
                            String longitude = cursor.getString(cursor.getColumnIndex("Longitude"));
                            if (latitude.length() > 0) {
                                LatLng latLong = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                                location.add(latLong);
                            }
                        } while (cursor.moveToNext());
                    }
//                    LatLng latLong1 = new LatLng(24.552710, 46.864072);
//                    LatLng latLong2 = new LatLng(24.553198, 46.874951);
//                    LatLng latLong3 = new LatLng(24.547226, 46.871410);
//                    location.add(0, latLong1);
//                    location.add(1, latLong2);
//                    location.add(2, latLong3);

                    double foundLatitude = 0.0, foundLongitude = 0.0;
                    Location loc = GlobalVar.getLastKnownLocation(getApplicationContext());
                    if (loc != null) {
                        foundLatitude = loc.getLatitude();
                        foundLongitude = loc.getLongitude();
                    }


//                    GlobalVar.distFrom(Latitude, Longitude, foundLatitude, foundLongitude, getApplicationContext());
//                    GlobalVar.distanceLocations(Latitude, Longitude, foundLatitude, foundLongitude, getApplicationContext());
                    if (!flag_thread) {
                        Latitude = location.get(i).latitude;
                        Longitude = location.get(i).longitude;
                    }


                    if (GlobalVar.distanceLocations(Latitude, Longitude, foundLatitude, foundLongitude, getApplicationContext())) {
                        if (!flag_thread) {
//                            Toast.makeText(getApplicationContext(), "true", Toast.LENGTH_SHORT).show();
                            flag_thread = true;
                            DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                            dbConnections.InsertALInOut(String.valueOf(Latitude), String.valueOf(Longitude), DateTime.now().toString(),
                                    DateTime.now().toString(), getApplicationContext());
                            dbConnections.close();
                        }
                    } else {
                        if (flag_thread) {

                            try {
                                new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Ismail/Location/").mkdir();
                                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Ismail/Location/data.txt");
                                if (!file.exists()) {
                                    file.createNewFile();
                                }
                                FileOutputStream fileOutputStream = new FileOutputStream(file, true);
                                fileOutputStream.write((DateTime.now().toString() + System.getProperty("line.separator")).getBytes());

                            } catch (FileNotFoundException ex) {
                                System.out.println(ex);
                            } catch (IOException ex) {
                                System.out.println(ex);
                            }
                            i = i + 1;

                            if (i == location.size())
                                i = 0;
                            flag_thread = false;
                            DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                            dbConnections.InsertALInOut(String.valueOf(Latitude), String.valueOf(Longitude), "",
                                    DateTime.now().toString(), getApplicationContext());
                            dbConnections.close();

                            startService(
                                    new Intent(getApplicationContext(),
                                            com.naqelexpress.naqelpointer.service.Radios200InsertService.class));
                            // android.os.Process.killProcess(android.os.Process.myPid());

                        } else {
                            i = i + 1;
                            if (i == location.size())
                                i = 0;
                        }
                    }


                    handler.postDelayed(this, 10000);
                } catch (Exception e) {
                    flag_thread = false;
                    handler.postDelayed(this, 10000);
                    Log.e("Dashboard thread", e.toString());
                }

            }
        }, 10000);


        return START_STICKY;
    }

    @SuppressLint("HandlerLeak")
    private Handler toast = new Handler() {
        public void handleMessage(Message msg) {

            Toast.makeText(getApplicationContext(), "service called",
                    Toast.LENGTH_SHORT).show();

        }
    };

    @Override
    public void onDestroy() {

        super.onDestroy();
    }


}
