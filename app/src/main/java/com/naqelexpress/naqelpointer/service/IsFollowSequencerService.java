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
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
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
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.Models.IsFollowSequncerModel;
import com.naqelexpress.naqelpointer.R;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IsFollowSequencerService extends Service {

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
        String NOTIFICATION_CHANNEL_ID = "com.naqelexpress.naqelpointer.service.IsFollowSequencerService";
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
                    handler.postDelayed(this, 600000); //600000
                } catch (Exception e) {
                    flag_thread = false;
                    handler.postDelayed(this, 600000); //600000
                    Log.e("Dashboard thread", e.toString());
                }

            }
        }, 600000);

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

            Cursor result = db.Fill("select * from isFollowGoogle where IsSync = 0 Limit 20 ", getApplicationContext());

            List<IsFollowSequncerModel> isFollowSequncerModelList = new ArrayList<>();
            String IDs = "0";

            if (result.getCount() > 0) {

                result.moveToFirst();
                do {
                    IsFollowSequncerModel isFollowSequncerModel = new IsFollowSequncerModel();
                    isFollowSequncerModel.setID(result.getInt(result.getColumnIndex("ID")));
                    isFollowSequncerModel.setWaybillNo(result.getInt(result.getColumnIndex("WaybillNo")));
                    isFollowSequncerModel.setIsFollow(result.getInt(result.getColumnIndex("IsFollow")));
                    isFollowSequncerModel.setConsLatitude(result.getString(result.getColumnIndex("ConsLatitude")));
                    isFollowSequncerModel.setConsLongitude(result.getString(result.getColumnIndex("ConsLongitude")));
                    isFollowSequncerModel.setCourierLatitude(result.getString(result.getColumnIndex("CourierLatitude")));
                    isFollowSequncerModel.setCourierLongitude(result.getString(result.getColumnIndex("CourierLongitude")));
                    isFollowSequncerModel.setFollowTime(result.getString(result.getColumnIndex("FollowTime")));
                    isFollowSequncerModel.setDeliverysheetID(result.getString(result.getColumnIndex("DeliverysheetID")));
                    isFollowSequncerModel.setEmployeeID(result.getInt(result.getColumnIndex("EmployeeID")));
                    isFollowSequncerModelList.add(isFollowSequncerModel);
                    IDs = IDs + "," + String.valueOf(result.getInt(result.getColumnIndex("ID")));
                } while (result.moveToNext());


                String jsonData = JsonSerializerDeserializer.serialize(isFollowSequncerModelList, true);
                //jsonData = jsonData.replace("Date(-", "Date(");
                InsertIsfollowGoogle(db, jsonData, IDs);


            } else {

                flag_thread = false;
                this.stopSelf();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        } catch (Exception e) {
            flag_thread = false;
        }
    }


    public void InsertIsfollowGoogle(final DBConnections db, final String input, final String ids) {


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = GlobalVar.GV().NaqelPointerAPILink + "InsertIsfollowGoogle"; //CheckPoint


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
//                    if (response.equals("0"))
//                        flag_thread = false;
//                    else {
                    db.Update_IsFollowGoogle(response.replace("\"", ""));

                    flag_thread = false;
//                    }
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
