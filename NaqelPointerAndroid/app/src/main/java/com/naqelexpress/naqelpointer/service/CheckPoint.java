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
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.CheckPointBarCodeDetails;
import com.naqelexpress.naqelpointer.DB.DBObjects.CheckPointWaybillDetails;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import org.joda.time.DateTime;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class CheckPoint extends Service {

    protected boolean flag_thread = false;

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
        String NOTIFICATION_CHANNEL_ID = "com.naqelexpress.naqelpointer.service.CheckPoint";
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
                    if (!flag_thread) {
                        flag_thread = true;

                        updatefile();

                    }
                    handler.postDelayed(this, 20000);
                } catch (Exception e) {
                    flag_thread = false;
                    handler.postDelayed(this, 20000);
                    Log.e("Dashboard thread", e.toString());
                }

            }
        }, 20000);

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


    protected void updatefile() {

        try {
            DBConnections db = new DBConnections(getApplicationContext(), null);

            Cursor result = db.Fill("select * from CheckPoint where IsSync = 0 Limit 1 ", getApplicationContext());

            if (result.getCount() > 0) {

                if (result.moveToFirst()) {
                    final com.naqelexpress.naqelpointer.DB.DBObjects.CheckPoint checkPoint = new com.naqelexpress.naqelpointer.DB.DBObjects.CheckPoint();
                    checkPoint.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                    checkPoint.CheckPointTypeID = Integer.parseInt(result.getString(result.getColumnIndex("CheckPointTypeID")));
                    checkPoint.Date = DateTime.parse(result.getString(result.getColumnIndex("Date")));
                    checkPoint.EmployID = Integer.parseInt(result.getString(result.getColumnIndex("EmployID")));
                    checkPoint.IsSync = Boolean.parseBoolean(result.getString(result.getColumnIndex("IsSync")));
                    checkPoint.Latitude = result.getString(result.getColumnIndex("Latitude"));
                    checkPoint.Longitude = result.getString(result.getColumnIndex("Longitude"));
                    checkPoint.CheckPointTypeDetailID = Integer.parseInt(result.getString(result.getColumnIndex("CheckPointTypeDetailID")));
                    checkPoint.CheckPointTypeDDetailID = Integer.parseInt(result.getString(result.getColumnIndex("CheckPointTypeDDetailID")));

                    Cursor resultDetail = db.Fill("select * from CheckPointWaybillDetails where CheckPointID = " + checkPoint.ID, getApplicationContext());
                    if (resultDetail.getCount() > 0) {
                        resultDetail.moveToFirst();
                        int index = 0;
                        resultDetail.moveToFirst();
                        do {
                            checkPoint.CheckPointWaybillDetails.add(index,
                                    new CheckPointWaybillDetails(resultDetail.getString(resultDetail.getColumnIndex("WaybillNo")),
                                            checkPoint.ID));
                            index++;
                        }
                        while (resultDetail.moveToNext());
                    }

                    resultDetail = db.Fill("select * from CheckPointBarCodeDetails where CheckPointID = " + checkPoint.ID, getApplicationContext());
                    if (resultDetail.getCount() > 0) {
                        resultDetail.moveToFirst();
                        int index = 0;
                        resultDetail.moveToFirst();
                        do {
                            checkPoint.CheckPointBarCodeDetails.add(index, new CheckPointBarCodeDetails(resultDetail.getString(resultDetail.getColumnIndex("BarCode")), checkPoint.ID));
                            index++;
                        }
                        while (resultDetail.moveToNext());
                    }


                    String jsonData = JsonSerializerDeserializer.serialize(checkPoint, true);
                    jsonData = jsonData.replace("Date(-", "Date(");
                    SaveCheckPoint(db, jsonData, checkPoint.ID);
                }


            } else {

                this.stopSelf();
            }
        } catch (Exception e) {
            flag_thread = false;
        }
    }


    public void SaveCheckPoint(final DBConnections db, final String input, final int id) {


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = GlobalVar.GV().NaqelPointerAPILink + "CheckPoint";


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    if (response.contains("Created")) {
                        db.deleteCheckPointID(id, getApplicationContext());
                        db.deleteCheckPointWayBill(id, getApplicationContext());
                        db.deleteCheckPointBarcode(id, getApplicationContext());
                        flag_thread = false;


                    } else
                        flag_thread = false;
                    db.close();
                } catch (Exception e) {
                    flag_thread = false;
                    if (db != null)
                        db.close();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //ArrayList<String> value = GlobalVar.VolleyError(error);
                flag_thread = false;
                db.close();
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

//            @Override
//            protected Response<String> parseNetworkResponse(NetworkResponse response) {
//                String responseString = "";
//                if (response != null) {
//
//                    responseString = String.valueOf(response.statusCode);
//
//                }
//                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
//            }
        };

        stringRequest.setShouldCache(false);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                120000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
        requestQueue.getCache().remove(URL);

    }


}
