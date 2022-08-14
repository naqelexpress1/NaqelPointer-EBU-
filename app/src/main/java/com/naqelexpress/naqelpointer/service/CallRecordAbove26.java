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

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hasna on 12/20/18.
 */

public class CallRecordAbove26 extends JobIntentService {

    public static void start(Context context) {
        Intent starter = new Intent(context, CallRecordAbove26.class);
        CallRecordAbove26.enqueueWork(context, starter);
    }

    /**
     * Unique job ID for this service.
     */
    static final int JOB_ID = 1000;

    private static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, CallRecordAbove26.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull final Intent intent) {


        try {
            final DBConnections db = new DBConnections(getApplicationContext(), null);

            Cursor result = db.Fill("select * from CallLog Limit 1 ", getApplicationContext());
            // Toast.makeText(getApplicationContext(), "Count : " + String.valueOf(result.getCount()), Toast.LENGTH_LONG).show();
            if (result.getCount() > 0) {

                if (result.moveToFirst()) {
                    final com.naqelexpress.naqelpointer.DB.DBObjects.CallHistory callHistory =
                            new com.naqelexpress.naqelpointer.DB.DBObjects.CallHistory();

                    callHistory.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));

                    callHistory.Date = DateTime.parse(result.getString(result.getColumnIndex("Date")));
                    //callHistory.EmployID = Integer.parseInt(result.getString(result.getColumnIndex("EmployID")));

                    callHistory.Type = result.getString(result.getColumnIndex("Type"));
                    callHistory.MNO = result.getString(result.getColumnIndex("MNO"));
                    callHistory.Duration = result.getString(result.getColumnIndex("Duration"));
                    callHistory.WaybillNo = Integer.parseInt(result.getString(result.getColumnIndex("WayBillNo")));
                    callHistory.StartDate = DateTime.parse(result.getString(result.getColumnIndex("CallStartTime")));
                    callHistory.EndDate = DateTime.parse(result.getString(result.getColumnIndex("CallEndTime")));
                    callHistory.CNumber = result.getString(result.getColumnIndex("Number"));
                    callHistory.EmployID = result.getInt(result.getColumnIndex("EmpID"));

                    String jsonData = JsonSerializerDeserializer.serialize(callHistory, true);
                    jsonData = jsonData.replace("Date(-", "Date(");

                    //SaveCheckPoint(db, jsonData, callHistory.ID);

                    RequestQueue requestQueue = ApplicationController.getInstance().getmRequestQueue();
                    String URL = GlobalVar.GV().NaqelPointerAPILink + "SendCCallRecording";

                    final String finalJsonData = jsonData;
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                            URL, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                boolean IsSync = Boolean.parseBoolean(response.getString("IsSync"));
                                boolean HasError = Boolean.parseBoolean(response.getString("HasError"));
                                if (IsSync && !HasError) {
                                    db.deleteCallLogID(callHistory.ID, getApplicationContext());

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
