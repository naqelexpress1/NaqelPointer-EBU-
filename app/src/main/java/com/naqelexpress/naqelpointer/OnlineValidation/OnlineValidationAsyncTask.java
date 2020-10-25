package com.naqelexpress.naqelpointer.OnlineValidation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.naqelexpress.naqelpointer.Activity.Login.LoginActivity;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;

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

    //todo riyam check warning
    private AsyncTaskCompleteListener<String> callback;
    private Context context;
    private Activity activity;
    private StringBuffer buffer;
    private ProgressDialog progressDialog = null;
    private String DomainURL = "";
    private String errorMessage;
    private boolean hasError;
    private int processType;

    public OnlineValidationAsyncTask(Context context, Activity activity,AsyncTaskCompleteListener<String> callback) {
        this.context = context;
        this.activity = activity;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
      try {
          //todo riyam change to live + cause a crash
          Log.d("test" , "On pre");
          progressDialog = ProgressDialog.show(activity, "Loading", "Upload online validation file , Please wait...", true);
          DomainURL = "http://172.19.20.70:45455//api/pointer/";
          super.onPreExecute();
      } catch (Exception e) {
          Log.d("test" , "On pre exception " + e.toString());
      }
    }

    @Override
    protected String doInBackground(String... strings) {

        HttpURLConnection httpURLConnection = null;
        InputStream ist = null;

        try {

            processType = Integer.parseInt(strings[0]);
            URL url = null;

            if (processType == GlobalVar.NclAndArrival)
                url = new URL(DomainURL + "GetOvNclArrivalPieces");

            if (processType == GlobalVar.DsAndInventory)
                url = new URL(DomainURL + "GetOvDsInventoryPieces");

            if (processType == GlobalVar.DsValidation)
                url = new URL(DomainURL + "GetOvDsValidationPieces");


            //TODO Riyam test process type e.g 6 (other)

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(300000); //240000
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
            hasError = true;
            errorMessage = ignored.toString();
        } finally {
            try {
                if (ist != null)
                    ist.close();
            } catch (IOException e) {
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

        try {
            Log.d("test" , "On post");
            //todo riyam try server down - no internet
            super.onPostExecute("");
            if (result != null) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (!jsonObject.getBoolean("HasError")) {
                        JSONArray filteredPiecesList = jsonObject.getJSONArray("ViewFilteredPieces");

                        DBConnections dbConnections = new DBConnections(context, null);

                        //todo riyam check if indersted successfully let inser returns boolean
                        //TODO if inserted insert in in file details
                        // if any error pass it to has error with error message
                        boolean isInserted = false;
                        if (filteredPiecesList.length() > 0)
                            isInserted = dbConnections.insertOnLineValidation(filteredPiecesList,processType,context);

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
                    e.printStackTrace();
                }
            } else {
                Log.d("test" , "Post result is null");
            }
            callback.onTaskComplete(hasError,errorMessage);
        } catch (Exception e ) {
            Log.d("test" , "Post");
        }
        progressDialog.dismiss();
    }
}
