package com.naqelexpress.naqelpointer.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.MultiDeliveryDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.MultiDeliveryWaybillDetail;
import com.naqelexpress.naqelpointer.GlobalVar;

import org.joda.time.DateTime;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MultiDelivery extends Service {

    protected boolean flag_thread = false;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
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


    protected void updatefile() {

        try {
            DBConnections db = new DBConnections(getApplicationContext(), null);

            Cursor result = db.Fill("select * from MultiDelivery where IsSync = 0 Limit 1 ", getApplicationContext());


            if (result.getCount() > 0) {

                if (result.moveToFirst()) {

                    final com.naqelexpress.naqelpointer.DB.DBObjects.MultiDelivery multiDeliveryRequest = new com.naqelexpress.naqelpointer.DB.DBObjects.MultiDelivery();
                    multiDeliveryRequest.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                    multiDeliveryRequest.ReceiverName = result.getString(result.getColumnIndex("ReceiverName"));
                    multiDeliveryRequest.PiecesCount = Integer.parseInt(result.getString(result.getColumnIndex("PiecesCount")));
                    multiDeliveryRequest.TimeIn = DateTime.parse(result.getString(result.getColumnIndex("TimeIn")));
                    multiDeliveryRequest.TimeOut = DateTime.parse(result.getString(result.getColumnIndex("TimeOut")));
                    multiDeliveryRequest.UserID = Integer.parseInt(result.getString(result.getColumnIndex("UserID")));
                    multiDeliveryRequest.IsSync = Boolean.parseBoolean(result.getString(result.getColumnIndex("IsSync")));
                    multiDeliveryRequest.StationID = Integer.parseInt(result.getString(result.getColumnIndex("StationID")));
                    multiDeliveryRequest.WaybillsCount = Integer.parseInt(result.getString(result.getColumnIndex("WaybillsCount")));
                    multiDeliveryRequest.Latitude = result.getString(result.getColumnIndex("Latitude"));
                    multiDeliveryRequest.Longitude = result.getString(result.getColumnIndex("Longitude"));
                    multiDeliveryRequest.ReceivedAmt = Double.parseDouble(result.getString(result.getColumnIndex("ReceivedAmt")));
                    multiDeliveryRequest.ReceiptNo = result.getString(result.getColumnIndex("ReceiptNo"));
                    multiDeliveryRequest.StopPointsID = Integer.parseInt(result.getString(result.getColumnIndex("StopPointsID")));
                    multiDeliveryRequest.al = result.getInt(result.getColumnIndex("AL"));
                    try {
                        FirebaseApp.initializeApp(this);
                        String token = FirebaseInstanceId.getInstance().getToken();
                        multiDeliveryRequest.DeviceToken = token;
                    } catch (Exception e) {
                        multiDeliveryRequest.DeviceToken = "";
                    }

                    Cursor resultDetail = db.Fill("select * from MultiDeliveryWaybillDetail where MultiDeliveryID = "
                            + multiDeliveryRequest.ID, getApplicationContext());

                    if (resultDetail.getCount() > 0) {
                        resultDetail.moveToFirst();
                        int index = 0;
                        resultDetail.moveToFirst();
                        do {
                            multiDeliveryRequest.multiDeliveryWaybillDetails.add(index,
                                    new MultiDeliveryWaybillDetail(resultDetail.getString(resultDetail.getColumnIndex("WaybillNo")),
                                            multiDeliveryRequest.ID));
                            index++;
                        }
                        while (resultDetail.moveToNext());
                    }

                    resultDetail = db.Fill("select * from MultiDeliveryDetail where MultiDeliveryID = " + multiDeliveryRequest.ID, getApplicationContext());

                    if (resultDetail.getCount() > 0) {
                        resultDetail.moveToFirst();
                        int index = 0;
                        resultDetail.moveToFirst();
                        do {
                            multiDeliveryRequest.multiDeliveryDetails.add(index,
                                    new MultiDeliveryDetail(resultDetail.getString(resultDetail.getColumnIndex("BarCode")),
                                            multiDeliveryRequest.ID));
                            index++;
                        }
                        while (resultDetail.moveToNext());
                    }


                    String jsonData = JsonSerializerDeserializer.serialize(multiDeliveryRequest, true);
                    jsonData = jsonData.replace("Date(-", "Date(");
                    SaveMultiDelivery(db, jsonData, multiDeliveryRequest.ID);
                }


            } else {

                flag_thread = false;
                this.stopSelf();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        } catch (Exception e) {
            flag_thread = false;
        }
    }


    public void SaveMultiDelivery(final DBConnections db, final String input, final int id) {


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = GlobalVar.GV().NaqelPointerAPILink + "MultiDelivery";


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    if (response.contains("Created")) {
                        db.updateMultiDeliveryID(id, getApplicationContext());
                        //db.deleteMultiDeliveryID(id, getApplicationContext());
                        // db.deleteMultiDeliveryWayBill(id, getApplicationContext());
                        // db.deleteMultiDeliveryBarcode(id, getApplicationContext());
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
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
        requestQueue.getCache().remove(URL);

    }


}
