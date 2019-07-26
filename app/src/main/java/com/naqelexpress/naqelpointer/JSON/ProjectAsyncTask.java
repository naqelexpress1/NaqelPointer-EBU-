package com.naqelexpress.naqelpointer.JSON;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.naqelexpress.naqelpointer.Classes.OnUpdateListener;
import com.naqelexpress.naqelpointer.GlobalVar;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by sofan on 07/03/2018.
 */

public class ProjectAsyncTask extends AsyncTask<String, Void, String>
{
    String GetUnicodeMessage(String Message) throws UnsupportedEncodingException
    {
        String msg = "";
        byte[] data = Message.getBytes("UTF-8");
        msg = Base64.encodeToString(data, Base64.DEFAULT);
        return msg;
    }

    String URL = GlobalVar.GV().NaqelPointerAPILink;
    String CustomURL = "";
    String Controller = "";
    String CallType = "";
    String JsonData = "";

    InputStream ByGetMethod(String ServerURL)
    {
        InputStream DataInputStream = null;
        try {

            URL url = new URL(ServerURL);
            HttpURLConnection cc = (HttpURLConnection)
                    url.openConnection();

            //set timeout for reading InputStream
            cc.setReadTimeout(60000);
            cc.setRequestProperty("Content-Type","application/json; charset=UTF-8");
            cc.setRequestProperty("AppID", String.valueOf(GlobalVar.GV().AppID));
            cc.setRequestProperty("UseToken","no");
            String userIDPassword = String.valueOf(GlobalVar.GV().UserID) +":"+ GlobalVar.GV().UserPassword;
            String Auth = "Basic "+GetUnicodeMessage(userIDPassword);
            cc.setRequestProperty("Authorization",Auth);

            // set timeout for connection
            cc.setConnectTimeout(60000);

            //set HTTP method to GET
            cc.setRequestMethod("GET");
            //set it to true as we are connecting for input
            cc.setDoInput(true);

            //reading HTTP response code
            int response = cc.getResponseCode();

            //if response code is 200 / OK then read Inputstream
            if (response == HttpURLConnection.HTTP_OK)
            {
                DataInputStream = cc.getInputStream();
            }

        } catch (Exception e) {
            //Log.e(LOG_TAG, "Error in GetData", e);
        }
        return DataInputStream;
    }

    InputStream ByPostMethod(String ServerURL) {
        InputStream DataInputStream = null;
        try {

            //Post parameters
            //String PostParam = "first_name=android&amp;last_name=pala";

            //Preparing
            URL url = new URL(ServerURL);

            HttpURLConnection cc = (HttpURLConnection)
                    url.openConnection();
            //set timeout for reading InputStream
            cc.setReadTimeout(60000);
            cc.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            if (CustomURL == "")
            {
                cc.setRequestProperty("AppID", String.valueOf(GlobalVar.GV().AppID));
                cc.setRequestProperty("UseToken", "no");
                String userIDPassword = String.valueOf(GlobalVar.GV().UserID) + ":" + GlobalVar.GV().UserPassword;
                String Auth = "Basic " + GetUnicodeMessage(userIDPassword);
                cc.setRequestProperty("Authorization", Auth);
            }
            // set timeout for connection
            cc.setConnectTimeout(60000);
            //set HTTP method to POST
            cc.setRequestMethod("POST");
            //set it to true as we are connecting for input
            cc.setDoInput(true);
            //cc.setDoOutput(true);
            //opens the communication link
            cc.connect();

            //Writing data (bytes) to the data output stream
            DataOutputStream dos = new DataOutputStream(cc.getOutputStream());
            dos.write(JsonData.getBytes());
            //flushes data output stream.
            dos.flush();
            dos.close();

            //Getting HTTP response code
            int response = cc.getResponseCode();

            //if response code is 200 / OK then read Inputstream
            //HttpURLConnection.HTTP_OK is equal to 200
            if (response == HttpURLConnection.HTTP_CREATED || response == HttpURLConnection.HTTP_OK)
            {
                DataInputStream = cc.getInputStream();
            }

        } catch (Exception e) {
            Log.e("Error", "Error in GetData", e);
        }
        return DataInputStream;

    }

    String ConvertStreamToString(InputStream stream) {

        InputStreamReader isr = new InputStreamReader(stream);
        BufferedReader reader = new BufferedReader(isr);
        StringBuilder response = new StringBuilder();

        String line = null;
        try {

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

        } catch (IOException e) {
            //Log.e(LOG_TAG, "Error in ConvertStreamToString", e);
        } catch (Exception e) {
            //Log.e(LOG_TAG, "Error in ConvertStreamToString", e);
        } finally {

            try {
                stream.close();

            } catch (IOException e) {
                //Log.e(LOG_TAG, "Error in ConvertStreamToString", e);

            } catch (Exception e) {
                //Log.e(LOG_TAG, "Error in ConvertStreamToString", e);
            }
        }
        return response.toString();
    }

    private OnUpdateListener listener;

    public ProjectAsyncTask(String controller, String callType)
    {
        Controller = controller;
        CallType = callType;
    }

   public ProjectAsyncTask(String controller, String callType,String jsonData)
   {
       Controller = controller;
       CallType = callType;
       JsonData = jsonData;
   }

    public ProjectAsyncTask(String controller, String callType, String jsonData, String url)
    {
        Controller = controller;
        CallType = callType;
        JsonData = jsonData;
        CustomURL = url;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        if (listener != null)
        {
            listener.onPreExecuteUpdate();
        }
    }

    @Override
    protected String doInBackground(String... arg)
    {
        InputStream is = null;
        String res = "";
//        if (!GlobalVar.GV().HasInternetAccess)
//            return res;

        if (CallType.equals("Post"))
            if (CustomURL != "")
                is = ByPostMethod(CustomURL + Controller);
            else
                is = ByPostMethod(URL + Controller);
        else
            is = ByGetMethod(URL + Controller);

        if (is != null)
            res = ConvertStreamToString(is);
        else
            res = "Something went wrong, please try again.";

        return res;
    }

    public void setUpdateListener(OnUpdateListener listener)
    {
        this.listener = listener;
    }

    protected void onPostExecute(String obj)
    {
        if (listener != null)
        {
            listener.onPostExecuteUpdate(obj);
        }
    }
}