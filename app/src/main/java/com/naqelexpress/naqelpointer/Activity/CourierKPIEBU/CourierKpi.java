package com.naqelexpress.naqelpointer.Activity.CourierKPIEBU;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class CourierKpi
        extends AppCompatActivity {

    private SwipeMenuListView mapListview;
    private RouteListAdapter adapter;
    Button btnStartTrip, btnCloseTrip;
    TextView txtStartTrip, txtCloseTrip;

    static int progressflag = 0;
    ArrayList<HashMap<String, String>> kpi = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.myroute);

        progressflag = 0;

        mapListview = (SwipeMenuListView) findViewById(R.id.myRouteListView);

        adapter = new RouteListAdapter(getApplicationContext(), kpi);
        mapListview.setAdapter(adapter);


        btnStartTrip = (Button) findViewById(R.id.btnStartTrip);
        btnCloseTrip = (Button) findViewById(R.id.btnCloseTrip);

        txtStartTrip = (TextView) findViewById(R.id.txtStartTrip);
        txtCloseTrip = (TextView) findViewById(R.id.txtCloseTrip);


        btnStartTrip.setVisibility(View.GONE);
        txtCloseTrip.setVisibility(View.GONE);
        txtStartTrip.setVisibility(View.GONE);
        btnCloseTrip.setVisibility(View.GONE);

        kpi.clear();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("StationID", GlobalVar.GV().StationID);

            new BringMyRouteShipmentsList().execute(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // MenuInflater inflater = getMenuInflater();
        //  inflater.inflate(R.menu.myroutemenu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    private class BringMyRouteShipmentsList extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;
        String result = "";
        StringBuffer buffer;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(CourierKpi.this, "Please wait.", "Downloading KPI Details.", true);
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];

            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "BringCourierKPIEBU");
                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                // httpURLConnection.setConnectTimeout(6000);
                // httpURLConnection.setReadTimeout(6000);
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
                super.onPostExecute(String.valueOf(finalJson));

                try {
                    JSONObject jsonObject = new JSONObject(finalJson);
                    if (!jsonObject.getBoolean("HasError")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("KPI");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            HashMap<String, String> temp = new HashMap<>();
                            temp.put("Name", obj.getString("Name"));
                            temp.put("EmployID", String.valueOf(obj.getInt("EmployID")));
                            temp.put("DeliverySheetCount", String.valueOf(obj.getInt("DeliverySheetCount")));
                            temp.put("DeliveryCount", String.valueOf(obj.getInt("DeliveryCount")));
                            temp.put("PickupCount", String.valueOf(obj.getInt("PickupCount")));
                            kpi.add(temp);


                        }
                    } else
                        ErrorAlert("Server not respond / Something went wrong , try again..  ");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                adapter.notifyDataSetChanged();

            } else
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.wentwrong), GlobalVar.AlertType.Error);

            if (progressDialog != null)
                progressDialog.dismiss();
        }
    }

    private void ErrorAlert(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(CourierKpi.this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Error");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("StationID", GlobalVar.GV().StationID);

                            new BringMyRouteShipmentsList().execute(jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            GlobalVar.GV().EmployID = savedInstanceState.getInt("EmployID");
            GlobalVar.GV().UserID = savedInstanceState.getInt("UserID");
            GlobalVar.GV().StationID = savedInstanceState.getInt("StationID");
            GlobalVar.GV().EmployMobileNo = savedInstanceState.getString("EmployMobileNo");
            GlobalVar.GV().EmployName = savedInstanceState.getString("EmployName");
            GlobalVar.GV().EmployStation = savedInstanceState.getString("EmployStation");
            kpi = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("kpi");

            adapter = new RouteListAdapter(getApplicationContext(), kpi);
            mapListview.setAdapter(adapter);

            btnCloseTrip.setVisibility(View.GONE);
            txtCloseTrip.setVisibility(View.GONE);

        }

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("EmployID", GlobalVar.GV().EmployID);
        outState.putInt("UserID", GlobalVar.GV().UserID);
        outState.putInt("StationID", GlobalVar.GV().StationID);
        outState.putString("EmployMobileNo", GlobalVar.GV().EmployMobileNo);
        outState.putString("EmployName", GlobalVar.GV().EmployName);
        outState.putString("EmployStation", GlobalVar.GV().EmployStation);
        outState.putSerializable("kpi", kpi);

        super.onSaveInstanceState(outState);
    }

}