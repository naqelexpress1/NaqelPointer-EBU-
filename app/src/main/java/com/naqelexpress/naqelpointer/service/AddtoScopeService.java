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
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.UpdateData;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.Models.AddtoScopeModels;
import com.naqelexpress.naqelpointer.R;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddtoScopeService extends Service {

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
        String NOTIFICATION_CHANNEL_ID = "com.naqelexpress.naqelpointer.service.AddtoScope";
        String channelName = "Naqel Service";
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
                    handler.postDelayed(this, 50000);
                } catch (Exception e) {
                    flag_thread = false;
                    handler.postDelayed(this, 50000);
                    Log.e("Dashboard thread", e.toString());
                }

            }
        }, 50000);

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
        DBConnections db = new DBConnections(getApplicationContext(), null);

        try {


            Cursor result = db.Fill("select * from AddtoScope where IsSync = 0 Limit 50  ", getApplicationContext());

            if (result.getCount() > 0) {
                List<AddtoScopeModels> addtoScopeModelsList = new ArrayList<>();

                result.moveToFirst();

                do {
                    AddtoScopeModels addtoScopeModels = new AddtoScopeModels();
                    addtoScopeModels.setUserID(result.getInt(result.getColumnIndex("UserID")));
                    addtoScopeModels.setTimeIn(DateTime.parse(result.getString(result.getColumnIndex("TimeIn"))));
                    addtoScopeModels.setPIDNCL(result.getString(result.getColumnIndex("PIDNCL")));
                    addtoScopeModels.setLongitude(result.getString(result.getColumnIndex("Longitude")));
                    addtoScopeModels.setLatitude(result.getString(result.getColumnIndex("Latitude")));
                    addtoScopeModels.setID(result.getInt(result.getColumnIndex("ID")));
                    addtoScopeModelsList.add(addtoScopeModels);
                } while (result.moveToNext());


                String jsonData = JsonSerializerDeserializer.serialize(addtoScopeModelsList, true);

                //jsonData = jsonData.replace("Date(-", "Date(");
                SaveAddtoScopeData(jsonData);


            } else {

                flag_thread = false;
                this.stopSelf();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        } catch (Exception e) {
            flag_thread = false;
        }

        if (db != null)
            db.close();
    }


    public void SaveAddtoScopeData(final String input) {


        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String URL = GlobalVar.GV().NaqelPointerAPILink + "insertAddtoScopeData";


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
//                    boolean IsSync = Boolean.parseBoolean(response.getString("IsSync"));
                    boolean HasError = Boolean.parseBoolean(response.getString("HasError"));
                    if (!HasError) {
                        UpdateData updateData = new UpdateData();

                        updateData.UpdateAddtoScope(response.getString("ErrorMessage"), getApplicationContext());

                        flag_thread = false;


                    } else
                        flag_thread = false;


                } catch (JSONException e) {
                    flag_thread = false;
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //ArrayList<String> value = GlobalVar.VolleyError(error);
                flag_thread = false;

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
