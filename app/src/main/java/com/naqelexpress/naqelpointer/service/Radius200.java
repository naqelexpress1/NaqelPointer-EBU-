package com.naqelexpress.naqelpointer.service;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

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

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hasna on 12/20/18.
 */

public class Radius200 extends JobIntentService {

    public static void start(Context context) {
        Intent starter = new Intent(context, Radius200.class);
        Radius200.enqueueWork(context, starter);
    }

    /**
     * Unique job ID for this service.
     */
    static final int JOB_ID = 1006;

    private static void enqueueWork(Context context, Intent intent) {

        enqueueWork(context, Radius200.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull final Intent intent) {


        try {
            final DBConnections db = new DBConnections(getApplicationContext(), null);

            Cursor result = db.Fill("select * from Radius200 Limit 1 ", getApplicationContext());
            // Toast.makeText(getApplicationContext(), "Count : " + String.valueOf(result.getCount()), Toast.LENGTH_LONG).show();
            if (result.getCount() > 0) {

                if (result.moveToFirst()) {
                    OnDeliveryRequest onDeliveryRequest = new OnDeliveryRequest();
                    final int id = result.getInt(result.getColumnIndex("ID"));
                    onDeliveryRequest.Latitude= result.getString(result.getColumnIndex("Lat"));
                    onDeliveryRequest.Longitude = result.getString(result.getColumnIndex("Long"));
                    onDeliveryRequest.TimeIn = DateTime.parse(result.getString(result.getColumnIndex("Timein")));
                    onDeliveryRequest.TimeOut = DateTime.parse(result.getString(result.getColumnIndex("Timeout")));
                    onDeliveryRequest.EmployID = db.getEmpId(getApplicationContext());
//                    JSONObject jsonData = new JSONObject();
//                    jsonData.put("Latitude", lat);
//                    jsonData.put("Longitude", lon);
//                    jsonData.put("TimeIn", onDeliveryRequest.TimeIn);
//                    jsonData.put("TimeOut", timeout);
//                    jsonData.put("EmployID", db.getEmpId(getApplicationContext()));


                    RequestQueue requestQueue = ApplicationController.getInstance().getmRequestQueue();
                    String URL = GlobalVar.GV().NaqelPointerAPILink + "InsertChallengesForDriver";

                    //final String finalJsonData = jsonData.toString().replace("Date(-", "Date(");
                    String jsonData = JsonSerializerDeserializer.serialize(onDeliveryRequest, true);
                    final String finalJsonData = jsonData.replace("Date(-", "Date(");

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                            URL, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                boolean IsSync = Boolean.parseBoolean(response.getString("IsSync"));
                                boolean HasError = Boolean.parseBoolean(response.getString("HasError"));
                                if (IsSync && !HasError) {
                                    db.deleteRadios200(id, getApplicationContext());

                                }
                                enqueueWork(getApplicationContext(), intent);

                                db.close();
                            } catch (JSONException e) {
                                enqueueWork(getApplicationContext(), intent);
                                if (db != null)
                                    db.close();
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            enqueueWork(getApplicationContext(), intent);
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
                                return finalJsonData == null ? null : finalJsonData.getBytes("utf-8");
                            } catch (UnsupportedEncodingException uee) {
                                VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", finalJsonData, "utf-8");
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
        } catch (Exception e) {
            enqueueWork(getApplicationContext(), intent);
        }


    }


    @Override
    public boolean onStopCurrentWork() {
        return super.onStopCurrentWork();
    }

}
