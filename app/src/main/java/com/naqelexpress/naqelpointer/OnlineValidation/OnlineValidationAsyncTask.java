package com.naqelexpress.naqelpointer.OnlineValidation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.Activity.Login.LoginActivity;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.NCLBulk.NclShipmentActivity;
import com.naqelexpress.naqelpointer.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class OnlineValidationAsyncTask extends AsyncTask<String, Void, String> {

    //todo check warning
    private AsyncTaskCompleteListener<String> callback;
    private Context context;
    private Activity activity;
    private StringBuffer buffer;
    private ProgressDialog progressDialog = null;
    private String DomainURL = "";
    private String errorMessage;
    private boolean hasError;
    private int processType;
    int  count = 0;

    private final static String TAG = "OnlineValidationAsyncTask";

    public OnlineValidationAsyncTask(Context context, Activity activity,AsyncTaskCompleteListener<String> callback) {
        this.context = context;
        this.activity = activity;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
      try {

         progressDialog = ProgressDialog.show(activity, "Loading", "Upload online validation file , Please wait...", true);
          new CountDownTimer(300000, 30000) { // counter time is 5 min , update text every min

              public void onTick(long millisUntilFinished) {
                  count++;
                  if (count == 1){
                      progressDialog.setMessage("Uploading online validation file , Please wait...");
                  }else if (count == 2){
                      progressDialog.setMessage("Processing your request .. Kindly wait.");
                  } else if (count == 3) {
                      progressDialog.setMessage("This might take some time .. Kindly wait");
                  } else if (count == 4) {
                      progressDialog.setMessage("Uploading online validation file , Please wait...");
                  }

              }


              public void onFinish() {

              }

          }.start();

          DomainURL = GlobalVar.NaqelAPIUAT;

          super.onPreExecute();
      } catch (Exception e) {
          Log.d("test" , TAG + "" + e.toString());
      }
    }

    @Override
    protected String doInBackground(String... strings) {
        HttpURLConnection httpURLConnection = null;
        InputStream ist = null;

        try {

            processType = Integer.parseInt(strings[0]);
            URL url = null;

            url = new URL(DomainURL);



            if (processType == GlobalVar.NclAndArrival)
                url = new URL(DomainURL + "GetOnlineValidationData");

            if (processType == GlobalVar.DsAndInventory)
                url = new URL(DomainURL + "GetOnlineValidationData_v2");

            if (processType == GlobalVar.DsValidation)
                url = new URL(DomainURL + "GetOnlineValidationData");


            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(300000); //240000 4 min
            httpURLConnection.setConnectTimeout(300000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();


            ist = httpURLConnection.getInputStream();
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(ist));
            buffer = new StringBuffer();

            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            return String.valueOf(buffer);
        } catch (Exception ignored) {
            Log.d("test" , TAG + "" + ignored.toString());
            hasError = true;
            errorMessage = ignored.toString();
        } finally {
            try {
                if (ist != null)
                    ist.close();
            } catch (IOException e) {
                Log.d("test" , TAG + "" + e.toString());
                hasError = true;
                errorMessage = e.toString();
                e.printStackTrace();
            }

            if (httpURLConnection != null)
                httpURLConnection.disconnect();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("test" , "on post");

        try {
            super.onPostExecute("");
            if (result != null) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (!jsonObject.getBoolean("HasError")) {
                        JSONArray onlineValidationData = jsonObject.getJSONArray("OnLineValidationData");

                        DBConnections dbConnections = new DBConnections(context, null);

                        boolean isInserted = false;
                        if (onlineValidationData.length() > 0) {
                                isInserted = dbConnections.insertOnLineValidation(onlineValidationData,processType,context);
                        }

                        if (!isInserted) {
                            hasError = true;
                            errorMessage = "File couldn't be saved locally";
                        }

                        dbConnections.close();
                    } else {
                        hasError = true;
                        errorMessage =  jsonObject.getString("ErrorMessage");
                    }
                } catch (JSONException e) {
                    hasError = true;
                    errorMessage =  e.toString();
                    Log.d("test" , TAG + "" + e.toString());
                    e.printStackTrace();
                }
            } else {
                hasError = true;
                errorMessage =  "Something went wrong , Please try again.";
            }
            callback.onTaskComplete(hasError,errorMessage);
        } catch (Exception e ) {
            Log.d("test" , TAG + "" + e.toString());
        }
        progressDialog.dismiss();
        count = 0;
    }
}
