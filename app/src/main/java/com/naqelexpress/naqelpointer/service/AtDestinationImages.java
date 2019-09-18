package com.naqelexpress.naqelpointer.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AtDestinationImages extends Service {

    protected boolean flag_thread = false;

    protected String url, imageurl;
    protected DBConnections dbh = null;
    protected List<AsyncTask<String, Integer, String>> asyncTasks = new ArrayList<AsyncTask<String, Integer, String>>();
    protected int curid;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dbh = new DBConnections(getApplicationContext(), null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "com.naqelexpress.naqelpointer.service.signature";
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


        SharedPreferences sp = getSharedPreferences("naqelExpress",
                MODE_PRIVATE);


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

        // toast(getResources().getString(R.string.urdus));

        super.onDestroy();
    }

    String imagepath;
    //protected List<String> imagenames = new ArrayList<>();

    protected void updatefile() {

        try {

            Cursor cur = dbh.getSignData(getApplicationContext());


            if (cur.getCount() > 0) {

                if (cur.moveToFirst()) {
                    curid = cur.getInt(cur
                            .getColumnIndex(DBConnections.COLUMNID));
                    imagepath = cur.getString(cur
                            .getColumnIndex(DBConnections.COLUMNNAME_FILE));
                    //imagenames.add(cur.getString(cur
                    //		.getColumnIndex(DBHelper.COLUMNNAME_FILE)));
                }

                uploadimage();

            } else {

                flag_thread = false;
                this.stopSelf();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        } catch (Exception e) {
            flag_thread = false;
        }
    }

    private void uploadimage() {

        UploadFileToServer asynthread = new UploadFileToServer();
        StartAsyncTaskInParallel(asynthread, imagepath);
    }

    private void StartAsyncTaskInParallel(UploadFileToServer asynthread,
                                          String keys) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asynthread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, keys);
        else
            asynthread.execute(keys);

    }

    private class UploadFileToServer extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @SuppressWarnings("deprecation")
        protected String doInBackground(String... params) {

            boolean delete = false;
            try {
                //naqeldocuments
                delete = CloudStorage.uploadFile("naqeldocuments", params[0], getApplicationContext());

            } catch (Exception e2) {
                e2.printStackTrace();

            }

            File sourceFile = new File(params[0]);
            if (!sourceFile.exists()) {
                dbh.deleteSignData(curid, getApplicationContext());
            } else if (sourceFile.exists() && delete) {
                deletefile(params[0]);
                dbh.deleteSignData(curid, getApplicationContext());

            }


            return null;

        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            flag_thread = false;

        }
    }

    private void deletefile(String imagename) {
        try {
            File deletefile = new File(imagename);
            deletefile.delete();
        } catch (Exception e) {
            System.out.println(e);
        }

    }

//    private class uploadImages extends AsyncTask<String, Integer, String> {
//        StringBuffer buffer;
//        int position = 0;
//
//        @Override
//        protected void onPreExecute() {
//
//            super.onPreExecute();
//
//        }
//
//        protected String doInBackground(String... params) {
//            JSONObject jsonObject = null;
//            try {
//                jsonObject = new JSONObject();
//                jsonObject.put("ImageName", sendimages.get(0));
//                jsonObject.put("FileName", convertimage(0));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            String jsonData = jsonObject.toString();
//            HttpURLConnection httpURLConnection = null;
//            OutputStream dos = null;
//            InputStream ist = null;
//
//            try {
//
//                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "upload");
//                httpURLConnection = (HttpURLConnection) url.openConnection();
//
//                httpURLConnection.setRequestMethod("POST");
//                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//                httpURLConnection.setDoInput(true);
//                httpURLConnection.setDoOutput(true);
//                httpURLConnection.connect();
//
//                dos = httpURLConnection.getOutputStream();
//                httpURLConnection.getOutputStream();
//                dos.write(jsonData.getBytes());
//
//                ist = httpURLConnection.getInputStream();
//                String line;
//                BufferedReader reader = new BufferedReader(new InputStreamReader(ist));
//                buffer = new StringBuffer();
//
//                while ((line = reader.readLine()) != null) {
//                    buffer.append(line);
//                }
//                return String.valueOf(buffer);
//            } catch (Exception ignored) {
//            } finally {
//                try {
//                    if (ist != null)
//                        ist.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    if (dos != null)
//                        dos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                if (httpURLConnection != null)
//                    httpURLConnection.disconnect();
//            }
//            return null;
////
//
//
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute("");
//            if (result != null) {
//
//                try {
//                    boolean delete = false;
//
//                    if (result.contains("Created Successfully")) {
//
//                        delete = true;
//                    }
//
//                    File sourceFile = new File(GlobalVar.naqelvehicleimagepath + "/" + sendimages.get(0));
//
//                    if (sourceFile.exists() && delete) {
//                        deletefile(position);
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                System.out.println(result);
//            }
//
//
//        }
//    }

}
