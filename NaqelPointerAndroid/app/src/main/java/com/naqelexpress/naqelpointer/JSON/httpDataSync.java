package com.naqelexpress.naqelpointer.JSON;

/**
 * Created by sofan on 05/03/2018.
 */

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.Classes.AsyncResponse;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class httpDataSync
{

    InputStream ByGetMethod(String ServerURL)
    {
        InputStream DataInputStream = null;
        try {

            URL url = new URL(ServerURL);
            HttpURLConnection cc = (HttpURLConnection)
                    url.openConnection();
            //set timeout for reading InputStream
            cc.setReadTimeout(5000);
            // set timeout for connection
            cc.setConnectTimeout(5000);
            //set HTTP method to GET
            cc.setRequestMethod("GET");
            //set it to true as we are connecting for input
            cc.setDoInput(true);

            //reading HTTP response code
            int response = cc.getResponseCode();

            //if response code is 200 / OK then read Inputstream
            if (response == HttpURLConnection.HTTP_OK) {
                DataInputStream = cc.getInputStream();
            }

        }
        catch (Exception e)
        {
            //Log.e(LOG_TAG, "Error in GetData", e);
        }
        return DataInputStream;

    }

    InputStream ByPostMethod(String ServerURL)
    {
        InputStream DataInputStream = null;
        try {

            //Post parameters
            String PostParam = "first_name=android&amp;last_name=pala";

            //Preparing
            URL url = new URL(ServerURL);

            HttpURLConnection cc = (HttpURLConnection)
                    url.openConnection();
            //set timeout for reading InputStream
            cc.setReadTimeout(5000);
            // set timeout for connection
            cc.setConnectTimeout(5000);
            //set HTTP method to POST
            cc.setRequestMethod("POST");
            //set it to true as we are connecting for input
            cc.setDoInput(true);
            //opens the communication link
            cc.connect();

            //Writing data (bytes) to the data output stream
            DataOutputStream dos = new DataOutputStream(cc.getOutputStream());
            dos.writeBytes(PostParam);
            //flushes data output stream.
            dos.flush();
            dos.close();

            //Getting HTTP response code
            int response = cc.getResponseCode();

            //if response code is 200 / OK then read Inputstream
            //HttpURLConnection.HTTP_OK is equal to 200
            if(response == HttpURLConnection.HTTP_OK)
            {
                DataInputStream = cc.getInputStream();
            }

        }
        catch (Exception e)
        {
            //Log.e(LOG_TAG, "Error in GetData", e);
        }
        return DataInputStream;

    }

    String ConvertStreamToString(InputStream stream)
    {

        InputStreamReader isr = new InputStreamReader(stream);
        BufferedReader reader = new BufferedReader(isr);
        StringBuilder response = new StringBuilder();

        String line = null;
        try {

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

        }
        catch (IOException e)
        {
            //Log.e(LOG_TAG, "Error in ConvertStreamToString", e);
        }
        catch (Exception e)
        {
            //Log.e(LOG_TAG, "Error in ConvertStreamToString", e);
        }
        finally
        {

            try {
                stream.close();

            } catch (IOException e)
            {
                //Log.e(LOG_TAG, "Error in ConvertStreamToString", e);

            } catch (Exception e)
            {
                //Log.e(LOG_TAG, "Error in ConvertStreamToString", e);
            }
        }
        return response.toString();


    }

    public void DisplayMessage(String a)
    {
        //TxtResult = (TextView) findViewById(R.id.response);
        //TxtResult.setText(a);
    }

    public class MakeNetworkCall extends AsyncTask<String, Void, String>
    {
        public AsyncResponse delegate = null;
        public MakeNetworkCall(AsyncResponse delegate)
        {
            this.delegate = delegate;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            DisplayMessage("Please Wait ...");
        }

        @Override
        protected String doInBackground(String... arg)
        {
            InputStream is = null;
            String URL = arg[0];
            String res = "";

            if (arg[1].equals("Post"))
            {
                is = ByPostMethod(URL);
            }
            else
            {
                is = ByGetMethod(URL);
            }

            if (is != null)
            {
                res = ConvertStreamToString(is);
            }
            else
            {
                res = "Something went wrong";
            }

            return res;
        }

        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);



            delegate.processFinish(result);
            //DisplayMessage(result);
        }
    }
}

