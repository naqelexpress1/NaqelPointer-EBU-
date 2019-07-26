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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AtOrigin extends Service {

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
        String NOTIFICATION_CHANNEL_ID = "com.naqelexpress.naqelpointer.service.ArrivedatDestination";
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
            Cursor result = db.Fill("select * from ArrivedatDestination Limit 1 ", getApplicationContext());


            if (result.getCount() > 0) {

                if (result.moveToFirst()) {
                    int id = result.getInt(result
                            .getColumnIndex("ID"));

                    String jsondata = result.getString(result
                            .getColumnIndex("Json"));
                    db.close();
                    //SaveAtOrigin(db, jsondata, id);
                    jsondata = jsondata.replace("Date(-", "Date(");
                    new SaveAtOrigin().execute(jsondata, String.valueOf(id));
                }


            } else {
                db.close();
                this.stopSelf();
            }
        } catch (Exception e) {
            flag_thread = false;
        }
    }


    private class SaveAtOrigin extends AsyncTask<String, Void, String> {
        String result = "";
        StringBuffer buffer;
        int id = 0;


        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            id = Integer.parseInt(params[1]);

            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "ArrivedatDestination"); //LoadtoDestination
                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();

                dos = httpURLConnection.getOutputStream();
                httpURLConnection.getOutputStream();
                dos.write(jsonData.getBytes());

                ist = httpURLConnection.getInputStream();
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(ist));
                buffer = new StringBuffer();

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                return String.valueOf(buffer);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ist != null)
                        ist.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (dos != null)
                        dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (httpURLConnection != null)
                    httpURLConnection.disconnect();
                result = String.valueOf(buffer);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String finalJson) {
            if (finalJson != null) {
                if (finalJson.contains("201 - Created Successfully")) {
                    DBConnections db = new DBConnections(getApplicationContext(), null);
                    db.deleteAtOrigin(id, getApplicationContext());
                    flag_thread = false;
                    db.close();
                }
                super.onPostExecute(String.valueOf(finalJson));

            }

            flag_thread = false;

        }
    }

//    public void SaveAtOrigin(final DBConnections db, final String input, final int id) {
//
//
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        String URL = GlobalVar.GV().NaqelPointerAPILink + "LoadtoDestination";
//
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
//                URL, null, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//
//                try {
//                    if (response.getInt("StatusID") == 201) {
//                        db.deleteAtOrigin(id);
//                        flag_thread = false;
//                        db.close();
//
//                    } else
//                        flag_thread = false;
//                } catch (JSONException e) {
//                    flag_thread = false;
//                    e.printStackTrace();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//                //ArrayList<String> value = GlobalVar.VolleyError(error);
//                flag_thread = false;
//            }
//        }) {
//            @Override
//            public String getBodyContentType() {
//                return "application/json; charset=utf-8";
//            }
//
//            @Override
//            public byte[] getBody() {
//                try {
//                    return input == null ? null : input.getBytes("utf-8");
//                } catch (UnsupportedEncodingException uee) {
//                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", input, "utf-8");
//                    return null;
//                }
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type", "application/json; charset=utf-8");
//                return params;
//            }
//
////            @Override
////            protected Response<String> parseNetworkResponse(NetworkResponse response) {
////                String responseString = "";
////                if (response != null) {
////
////                    responseString = String.valueOf(response.statusCode);
////
////                }
////                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
////            }
//        };
//        jsonObjectRequest.setShouldCache(false);
//        requestQueue.add(jsonObjectRequest);
//        requestQueue.getCache().remove(URL);
//
//    }


}
