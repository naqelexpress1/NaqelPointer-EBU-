package com.naqelexpress.naqelpointer.Activity.GoogleApiFusedLocation;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.service.SendNotificationtoConsignee;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hasna on 1/19/19.
 */

public class LocationService extends Service {

    private GoogleLocationService googleLocationService;
    protected boolean flag_thread = false;
    double Latitude = 24.552710;
    double Longitude = 46.864072;
    protected int i = 0;
    protected int locationSize = 0;

    ArrayList<LatLng> location = new ArrayList<>();
    ArrayList<String> phnos = new ArrayList<>();
    DateTime timein;

    @Override
    public void onCreate() {
        super.onCreate();
        //start the handler for getting locations
        //create component
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(10, new Notification());

        updateLocation(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // new DownloadJSON().execute();
                try {
                    // if (!flag_thread) {
                    //      flag_thread = true;

                    //updateLocation(getApplicationContext());
                    System.out.println("test");

                    //     }
                    handler.postDelayed(this, 60000);
                } catch (Exception e) {
                    flag_thread = false;
                    handler.postDelayed(this, 60000);
                    Log.e("Dashboard thread", e.toString());
                }

            }
        }, 60000);
        return Service.START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "com.naqelexpress.naqelpointer.service.GLFAPI";
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
                .setOngoing(true)
                .setAutoCancel(false)
                .build();
        startForeground(11, notification);
    }


    //get current location os user
    private void updateLocation(Context context) {


        googleLocationService = new GoogleLocationService(context, new LocationUpdateListener() {
            @Override
            public void canReceiveLocationUpdates() {
            }

            @Override
            public void cannotReceiveLocationUpdates() {
            }

            //update location to our servers for tracking purpose

            @Override
            public void updateLocation(Location location) {
                if (location != null) {

                    if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                                    != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                    DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                    int lastlogin = GlobalVar.getlastlogin(getApplicationContext());
                    String devision = "";
                    Cursor result = dbConnections.Fill("select * from UserME where StatusID <> 3 and EmployID = " +
                            lastlogin, getApplicationContext());

                    if (result != null && result.getCount() > 0) {
                        result.moveToFirst();
                        devision = result.getString(result.getColumnIndex("Division"));

                    }

                    if (!devision.equals("Courier"))
                        return;

                    String empid = dbConnections.getDeliverysheetEmpID(getApplicationContext());
                    String split[] = empid.split(",");
                    //                    dbConnections.

                    try {

                        JSONObject jsonObject = new JSONObject();
                        try {


                            jsonObject.put("latitude", location.getLatitude());
                            jsonObject.put("longitude", location.getLongitude());
                            jsonObject.put("Channel", "CBU-" + String.valueOf(lastlogin));
                            //jsonObject.put("WaybillNo", dbConnections.GetLastDeliveredWaybill(getApplicationContext()));GetLastActionWaybill

                            String wd = dbConnections.GetLastActionWaybill(getApplicationContext());
                            if (!wd.equals("0")) {
                                jsonObject.put("WaybillNo", wd.split("_")[0]);
                                jsonObject.put("DsID", wd.split("_")[1]);
                            } else
                                jsonObject.put("WaybillNo", wd);

                            jsonObject.put("EmpID", empid);
                            jsonObject.put("Division", devision);
                            jsonObject.put("Date", GlobalVar.GV().getCurrentDateTimeSS());

                            dbConnections.close();
                            result.close();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (requestQueue != null)
                            requestQueue.cancelAll("old");
                        if (split.length > 1 && split[1].equals("1"))
                            //shareLiveLocation(jsonObject.toString()); //if you need  Live just uncomment
                            saveLiveLocation(jsonObject.toString());
                        if (split[0].equals("19127"))
                            saveLiveLocation(jsonObject.toString());
                        //shareLiveLocation(jsonObject.toString()); //if you need  Live just uncomment

                        //sendNotificationtoConsignee(location.getLatitude(), location.getLongitude()); //Comment because of Data usage

                        flag_thread = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void updateLocationName(String localityName, Location location) {
                //if (flag_thread)
                //    flag_thread = false;
                // googleLocationService.stopLocationUpdates();
            }
        });
        googleLocationService.startUpdates();
    }


    IBinder mBinder = new LocalBinder();


    public class LocalBinder extends Binder {
        public LocationService getServerInstance() {
            return LocationService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    //stop location updates on stopping the service
    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (googleLocationService != null) {
//            googleLocationService.stopLocationUpdates();
//        }
//
//
//        flag_thread = false;
    }

    RequestQueue requestQueue;


    public void saveLiveLocation(final String input) {


        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        dbConnections.InsertLocationintoMongo(getApplicationContext(), input);
        dbConnections.close();


        if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.LocationIntoMongo.class)) {
            startService(new Intent(getBaseContext(),
                    com.naqelexpress.naqelpointer.service.LocationIntoMongo.class));
        }

    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    public void shareLiveLocation(final String input) {

        try {


            if (requestQueue == null)
                requestQueue = Volley.newRequestQueue(this);
            String URL = GlobalVar.GV().NaqelPointerLivetracking + "HelloWorld";


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    URL, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println(response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error.toString().contains("NoConnectionError"))
                    // shareLiveLocation(input);
                    {
                        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                        dbConnections.InsertLocationintoMongo(getApplicationContext(), input);
                        dbConnections.close();
                    }
                    System.out.println(error);

                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() {
                    try {
                        return input == null ? null : input.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", input, "utf-8");
                        return null;
                    }
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json; charset=utf-8");
                    return params;
                }

                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
//                if (response.statusCode != 200)
//                    shareLiveLocation(input);
                    System.out.println(String.valueOf(response.statusCode));
                    return super.parseNetworkResponse(response);
                }
            };

            jsonObjectRequest.setShouldCache(false);
            jsonObjectRequest.setTag("old");
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    20000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
            requestQueue.getCache().remove(URL);

        } catch (Exception e) {

        }
    }


    public void sendNotificationtoConsignee(double foundLatitude, double foundLongitude) {
        location.clear();
        phnos.clear();

        try {
            DBConnections db = new DBConnections(getApplicationContext(), null);
            Cursor cursor = db.Fill("select * from MyRouteShipments where EmpID = " + db.getEmpId(getApplicationContext()) +
                            " and Latitude !='' and Longitude !='' and IsDelivered <> 1 and DDate = '" + GlobalVar.getDate() + "'" +
                            " and Notification = 0"
                    , getApplicationContext());

            if (cursor.getCount() != locationSize)
                resetValue();

            if (cursor.getCount() > 0) {
                locationSize = cursor.getCount();
                cursor.moveToFirst();
                do {

                    String latitude = cursor.getString(cursor.getColumnIndex("Latitude"));
                    String longitude = cursor.getString(cursor.getColumnIndex("Longitude"));
                    String mno = cursor.getString(cursor.getColumnIndex("ConsigneeMobile"));
                    String pho = cursor.getString(cursor.getColumnIndex("ConsigneePhoneNumber"));
                    String waybillno = cursor.getString(cursor.getColumnIndex("ItemNo"));

                    if (latitude.length() > 0) {
                        LatLng latLong = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                        location.add(latLong);
                        phnos.add(mno + "," + pho + "," + waybillno);
                    }
                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();

            if (!flag_thread) {
                Latitude = location.get(i).latitude;
                Longitude = location.get(i).longitude;
            }


            if (GlobalVar.distanceLocations(Latitude, Longitude, foundLatitude, foundLongitude, getApplicationContext())) {
                if (!flag_thread) {
//                            Toast.makeText(getApplicationContext(), "true", Toast.LENGTH_SHORT).show();
                    flag_thread = true;
                    DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                    String split[] = phnos.get(i).split(",");
                    dbConnections.AddConsigneeNotify(phnos.get(i).toString(), split[split.length - 1],
                            DateTime.now().toString(), getApplicationContext());
                    timein = DateTime.now();
                    dbConnections.close();
                } else {
                    boolean result = Minutes.minutesBetween(new DateTime(timein), new DateTime())
                            .isGreaterThan(Minutes.minutes(2));
                    if (result) {

                        i = i + 1;

                        if (i == location.size())
                            i = 0;
                        flag_thread = false;

                        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                        String split[] = phnos.get(i).split(",");
                        dbConnections.AddConsigneeNotify(phnos.get(i).toString(), split[split.length - 1],
                                DateTime.now().toString(), getApplicationContext());
                        dbConnections.close();

                        SendNotificationtoConsignee.start(getApplicationContext());


                    }
                }
            } else {
                if (flag_thread) {


                    i = i + 1;

                    if (i == location.size())
                        i = 0;
                    flag_thread = false;
//
                    DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                    String split[] = phnos.get(i).split(",");
                    dbConnections.AddConsigneeNotify(phnos.get(i).toString(), split[split.length - 1],
                            DateTime.now().toString(), getApplicationContext());
                    dbConnections.close();

                    SendNotificationtoConsignee.start(getApplicationContext());


                } else {
                    i = i + 1;
                    if (i == location.size())
                        i = 0;
                }
            }


        } catch (Exception e) {
            flag_thread = false;
            Log.e("Dashboard thread", e.toString());
        }
    }

    private void resetValue() {

        flag_thread = false;
        i = 0;
    }
}