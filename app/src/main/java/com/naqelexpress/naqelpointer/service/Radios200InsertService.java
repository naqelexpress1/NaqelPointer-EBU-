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
import android.os.PowerManager;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.naqelexpress.naqelpointer.ApplicationController;
import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.JSON.Request.OnDeliveryRequest;
import com.naqelexpress.naqelpointer.R;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Radios200InsertService extends Service {

    protected boolean flag_thread = false;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    PowerManager.WakeLock wakeLock;

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
        String NOTIFICATION_CHANNEL_ID = "com.naqelexpress.naqelpointer.service.Radios200InsertService";
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
                PowerManager mgr = (PowerManager) getSystemService(Context.POWER_SERVICE);
                wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
                wakeLock.acquire();
                try {
                    if (!flag_thread) {
                        flag_thread = true;
                        //Toast.makeText(getApplicationContext(), "Service Called", Toast.LENGTH_LONG).show();
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
            final DBConnections db = new DBConnections(getApplicationContext(), null);

            Cursor result = db.Fill("select * from Radius200 Limit 1 ", getApplicationContext());
            // Toast.makeText(getApplicationContext(), "Count : " + String.valueOf(result.getCount()), Toast.LENGTH_LONG).show();
            if (result.getCount() > 0) {

                if (result.moveToFirst()) {
                    OnDeliveryRequest onDeliveryRequest = new OnDeliveryRequest();
                    final int id = result.getInt(result.getColumnIndex("ID"));
                    onDeliveryRequest.Latitude = result.getString(result.getColumnIndex("Lat"));
                    onDeliveryRequest.Longitude = result.getString(result.getColumnIndex("Long"));
                    if (result.getString(result.getColumnIndex("Timein")).length() > 0)
                        onDeliveryRequest.TimeIn = DateTime.parse(result.getString(result.getColumnIndex("Timein")));
                    else
                        onDeliveryRequest.TimeIn = DateTime.now();
                    if (result.getString(result.getColumnIndex("Timeout")).length() > 0)
                        onDeliveryRequest.TimeOut = DateTime.parse(result.getString(result.getColumnIndex("Timeout")));
                    else
                        onDeliveryRequest.TimeOut = DateTime.now();
                    onDeliveryRequest.EmployID = db.getEmpId(getApplicationContext());
                    long d1 = onDeliveryRequest.TimeOut.getMillis() - onDeliveryRequest.TimeIn.getMillis();
                    long diffInMin = TimeUnit.MILLISECONDS.toMinutes(d1);
                    onDeliveryRequest.ST = String.valueOf(diffInMin);

                    String jsonData = JsonSerializerDeserializer.serialize(onDeliveryRequest, true);
                    final String finalJsonData = jsonData.replace("Date(-", "Date(");
                    SaveCheckPoint(db, finalJsonData, id);
                }


            } else {

                this.stopSelf();
                wakeLock.release();
            }
        } catch (Exception e) {
            flag_thread = false;
        }
    }


    public void SaveCheckPoint(final DBConnections db, final String input, final int id) {


//        RequestQueue requestQueue = Volley.newRequestQueue(this);
        RequestQueue requestQueue = ApplicationController.getInstance().getmRequestQueue();
        String URL = GlobalVar.GV().NaqelPointerAPILink + "InsertChallengesForDriver";


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    boolean IsSync = Boolean.parseBoolean(response.getString("IsSync"));
                    boolean HasError = Boolean.parseBoolean(response.getString("HasError"));
                    //Toast.makeText(getApplicationContext(), "Result : " + response.getString("IsSync"), Toast.LENGTH_LONG).show();
                    if (IsSync && !HasError) {
                        db.deleteRadios200(id, getApplicationContext());
                        flag_thread = false;


                    } else
                        flag_thread = false;
                    db.close();
                } catch (JSONException e) {
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

        };

        jsonObjectRequest.setShouldCache(false);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                120000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjectRequest);
        requestQueue.getCache().remove(URL);

    }


}
