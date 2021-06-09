package com.naqelexpress.naqelpointer.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Base64;
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
import com.android.volley.toolbox.Volley;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

//import com.naqelexpress.naqelpointer.JSON.Request.OnDeliveryRequest;

public class PlannedRoute_MyRouteComp extends Service {

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
        String NOTIFICATION_CHANNEL_ID = "com.naqelexpress.naqelpointer.service.MyRouteComp";
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
            @RequiresApi(api = Build.VERSION_CODES.O)
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


    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void updatefile() {

        try {
            DBConnections db = new DBConnections(getApplicationContext(), null);
            //db.DeleteAllSuggestLocation(getApplicationContext());
            Cursor result = db.Fill("select * from MyRouteCompliance where IsSync = 0 Limit 1 ", getApplicationContext());

            JSONObject header = new JSONObject();
            JSONObject myroutecomp = new JSONObject();
            JSONArray comp = new JSONArray();
            String MRCDate = "";
            int EmpID;
            String MRCID = "0", SLID = "0", PLID = "0";
            if (result.getCount() > 0) {

                result.moveToFirst();
                do {
                    MRCID = String.valueOf(result.getInt(result.getColumnIndex("ID")));
                    EmpID = result.getInt(result.getColumnIndex("EmpID"));
                    MRCDate = result.getString(result.getColumnIndex("Date"));
                    myroutecomp.put("Compliance", result.getInt(result.getColumnIndex("Compliance")));
                    myroutecomp.put("Date", result.getString(result.getColumnIndex("IsDate")));
                    myroutecomp.put("EmpID", EmpID);
                    myroutecomp.put("UserID", result.getInt(result.getColumnIndex("UserID")));
                    myroutecomp.put("DeliverysheetID", db.GetDeliverysheet(getApplicationContext()));
                    try {
                        myroutecomp.put("DeviceModel", GlobalVar.GV().getDeviceName());

                    } catch (Exception e) {
                        myroutecomp.put("DeviceModel", "No Model");
                    }
                    try {
                        myroutecomp.put("Softwareversion", android.os.Build.VERSION.RELEASE);

                    } catch (Exception e) {
                        myroutecomp.put("Softwareversion", "");
                    }
                    try {
                        myroutecomp.put("Sdkversion", android.os.Build.VERSION.SDK_INT);

                    } catch (Exception e) {
                        myroutecomp.put("Sdkversion", 0);
                    }

                    if (android.os.Build.VERSION.SDK_INT >= 29) {
                        boolean background = ActivityCompat.checkSelfPermission(this,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
                        myroutecomp.put("BGLocation", background);
                    } else
                        myroutecomp.put("BGLocation", true);

                    try {

                        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                            myroutecomp.put("DeviceID", telephonyManager.getDeviceId().toString());
                        }
                    } catch (Exception e) {
                        try {
                            myroutecomp.put("DeviceID", Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID).toString());
                        } catch (Exception ee) {

                        }
                    }
                    comp.put(myroutecomp);
                } while (result.moveToNext());

                header.put("Compliance", myroutecomp);


                result = db.Fill("select * from SuggestLocations where Date = '" + MRCDate + "' " +
                        "and EmpID = " + EmpID + " and  IsSync = 0  Limit 1 ", getApplicationContext());

                JSONArray sl_array = new JSONArray();
                if (result != null && result.getCount() > 0) {
                    result.moveToFirst();
                    do {
                        SLID = SLID + "," + String.valueOf(result.getInt(result.getColumnIndex("ID")));
                        String data = result.getString(result.getColumnIndex("StringData"));
                        byte[] b = data.getBytes();
                        JSONObject sl_obj = new JSONObject();
                        sl_obj.put("ID", result.getInt(result.getColumnIndex("ID")));
                        //sl_obj.put("SLByte", b.toString());
                        String encodedString = android.util.Base64.encodeToString(b, Base64.DEFAULT);
                        //String encodedString = java.util.Base64.getEncoder().encodeToString(b);
                        sl_obj.put("SLByte", encodedString);
                        sl_array.put(sl_obj);
//                            String split[] = data.split("@");
//                            for (int i = 0; i < split.length; i++) {
//                                JSONObject sl_obj = new JSONObject();
//                                String temp[] = split[i].split("_");
//                                sl_obj.put("Lat", Double.parseDouble(temp[1]));
//                                sl_obj.put("Lng", Double.parseDouble(temp[2]));
//                                if (temp[0].equals("Stratingplace"))
//                                    sl_obj.put("WaybillNo", 0);
//                                else
//                                    sl_obj.put("WaybillNo", Integer.parseInt(temp[0]));
//                                sl_obj.put("SeqNo", Float.parseFloat(temp[temp.length - 2]));
//                                sl_obj.put("DLSeqNo", Float.parseFloat(temp[temp.length - 1]));
//                                sl_array.put(sl_obj);
//
//                            }


                    }
                    while (result.moveToNext());

                    header.put("SuggestLocation", sl_array);
                }

                result = db.Fill("select * from plannedLocation where Date = '" + MRCDate + "' " +
                        "and EmpID = " + EmpID + " and  IsSync = 0 ", getApplicationContext());

                JSONArray pl_array = new JSONArray();
                if (result != null && result.getCount() > 0) {
                    result.moveToFirst();
                    do {
                        PLID = PLID + "," + String.valueOf(result.getInt(result.getColumnIndex("ID")));
                        String data = result.getString(result.getColumnIndex("StringData"));
                        byte[] b = data.getBytes();
                        JSONObject sl_obj = new JSONObject();
                        sl_obj.put("ID", result.getInt(result.getColumnIndex("ID")));
                        sl_obj.put("position", result.getInt(result.getColumnIndex("position")));
                       // String encodedString = java.util.Base64.getEncoder().encodeToString(b);
                        String encodedString = android.util.Base64.encodeToString(b, Base64.DEFAULT);
                        sl_obj.put("PLByte", encodedString);
                        // sl_obj.put("Date", encodedString);
                        // sl_obj.put("PLByte", encodedString);
                        pl_array.put(sl_obj);
//                            for (int i = 0; i < split.length; i++) {
//                                JSONObject sl_obj = new JSONObject();
//                                String temp[] = split[i].split("_");
//                                sl_obj.put("Lat", Double.parseDouble(temp[1]));
//                                sl_obj.put("Lng", Double.parseDouble(temp[2]));
//                                if (temp[0].equals("Stratingplace"))
//                                    sl_obj.put("WaybillNo", 0);
//                                else
//                                    sl_obj.put("WaybillNo", Integer.parseInt(temp[0]));
//                                sl_obj.put("SeqNo", Float.parseFloat(temp[temp.length - 2]));
//                                sl_obj.put("DLSeqNo", Float.parseFloat(temp[temp.length - 1]));
//                                pl_array.put(sl_obj);
//
//                            }


                    }
                    while (result.moveToNext());

                    header.put("PlannedLocation", pl_array);
                    System.out.println(header.toString());


                }

                SaveMyRouteCompliance(db, header.toString(), MRCID, SLID, PLID);
            } else {
                flag_thread = false;
                this.stopSelf();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        } catch (
                Exception e) {
            flag_thread = false;
        }

    }


    public void SaveMyRouteCompliance(final DBConnections db, final String input, final String MRCID, final String SLID, final String PLID) {


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String DomainURL = GlobalVar.GV().GetDomainURLforService(getApplicationContext(), "Delivery");
        //String URL = GlobalVar.GV().NaqelPointerAPILink + "PartialDelivery";
        String URL = DomainURL + "MyRouteComplaince";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    boolean IsSync = Boolean.parseBoolean(response.getString("IsSync"));
                    boolean HasError = Boolean.parseBoolean(response.getString("HasError"));
                    String updateDeliver = response.getString("ErrorMessage");
                    if (IsSync && !HasError) {
                        db.updateMyRouteCompliance(MRCID, SLID, PLID, getApplicationContext());
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
        jsonObjectRequest.setShouldCache(false);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                GlobalVar.GV().loadbalance_Contimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjectRequest);
        requestQueue.getCache().remove(URL);

    }


}
