package com.naqelexpress.naqelpointer.service;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

/**
 * Created by Hasna on 12/20/18.
 */

public class UpdateLocation extends JobIntentService {

    public static void start(Context context) {
        Intent starter = new Intent(context, UpdateLocation.class);
        UpdateLocation.enqueueWork(context, starter);
    }

    /**
     * Unique job ID for this service.
     */
    static final int JOB_ID = 1001;

    private static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, UpdateLocation.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull final Intent intent) {


        try {

           /* final DBConnections db = new DBConnections(getApplicationContext(), null);

            Cursor result = db.Fill("select * from CurrentLocation Limit 1 ", getApplicationContext());
            // Toast.makeText(getApplicationContext(), "Count : " + String.valueOf(result.getCount()), Toast.LENGTH_LONG).show();
            if (result.getCount() > 0) {

                if (result.moveToFirst()) {
                    final int id = result.getInt(result.getColumnIndex("ID"));
                    String jsonData = result.getString(result.getColumnIndex("Json"));


                    RequestQueue requestQueue = ApplicationController.getInstance().getmRequestQueue();
                    String URL = GlobalVar.GV().NaqelPointerAPILink + "UpdateLocation";

                    final String finalJsonData = jsonData;
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                            URL, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                boolean IsSync = Boolean.parseBoolean(response.getString("IsSync"));
                                boolean HasError = Boolean.parseBoolean(response.getString("HasError"));
                                if (IsSync && !HasError) {
                                    db.deleteLocation(id, getApplicationContext());

                                }
                                enqueueWork(getApplicationContext(), intent);
                                if (db != null)
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
                            if (db != null)
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
            if (result != null)
                result.close();
            if (db != null)
                db.close();*/
        } catch (Exception e) {
            enqueueWork(getApplicationContext(), intent);
        }


    }


    @Override
    public boolean onStopCurrentWork() {
        return super.onStopCurrentWork();
    }

}
