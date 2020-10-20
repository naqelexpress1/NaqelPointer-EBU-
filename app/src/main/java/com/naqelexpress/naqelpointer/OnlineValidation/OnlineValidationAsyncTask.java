package com.naqelexpress.naqelpointer.OnlineValidation;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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
    private StringBuffer buffer;
    private String DomainURL = "";
    private String errorMessage;
    private boolean hasError;
    private int processType;

    public OnlineValidationAsyncTask(Context context, AsyncTaskCompleteListener<String> callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        //todo riyam change to live + cause a crash
        DomainURL = "http://172.19.20.70:45455//api/pointer/";
        super.onPreExecute();
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

            //TODO RIyam test process type e.g 6

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
            //todo riyam try server down - no internert
            super.onPostExecute("");
            if (result != null) {

                try {
                    JSONObject jsonObject = new JSONObject(result);

                    if (!jsonObject.getBoolean("HasError")) {
                        JSONArray filteredPiecesList = jsonObject.getJSONArray("ViewFilteredPieces");

                        DBConnections dbConnections = new DBConnections(context, null);

                        //todo check if indersted successfully
                        if (filteredPiecesList.length() > 0)
                            dbConnections.insertOnLineValidation(filteredPiecesList,processType,context);

                        hasError = false;
                        dbConnections.close();
                    } else {
                        hasError = true;
                        errorMessage =  jsonObject.getString("ErrorMessage");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            callback.onTaskComplete(hasError,errorMessage);
        } catch (Exception e ) {
            Log.d("test" , "pre " + e.toString());
        }
    }

}
