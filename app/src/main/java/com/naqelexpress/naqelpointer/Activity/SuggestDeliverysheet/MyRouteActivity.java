package com.naqelexpress.naqelpointer.Activity.SuggestDeliverysheet;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.SuggestMyRouteShipments;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.JSON.Request.BringMyRouteShipmentsRequest;
import com.naqelexpress.naqelpointer.R;

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

public class MyRouteActivity
        extends AppCompatActivity {
    private SwipeMenuListView mapListview;
    private RouteListAdapter adapter;
    Button btnStartTrip, btnCloseTrip;
    TextView txtStartTrip, txtCloseTrip;
    public static ArrayList<Location> places = new ArrayList<>();//96346
    protected boolean flag_thread = false;
    static int progressflag = 0;
    public static ArrayList<SuggestMyRouteShipments> myRouteShipmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.myroute);

        progressflag = 0;

        mapListview = (SwipeMenuListView) findViewById(R.id.myRouteListView);

        adapter = new RouteListAdapter(getApplicationContext(), myRouteShipmentList, "MyRouteActivity");
        mapListview.setAdapter(adapter);


        btnStartTrip = (Button) findViewById(R.id.btnStartTrip);
        btnCloseTrip = (Button) findViewById(R.id.btnCloseTrip);

        txtStartTrip = (TextView) findViewById(R.id.txtStartTrip);
        txtCloseTrip = (TextView) findViewById(R.id.txtCloseTrip);


        btnStartTrip.setVisibility(View.GONE);
        txtCloseTrip.setVisibility(View.GONE);
        txtStartTrip.setVisibility(View.GONE);
        btnCloseTrip.setVisibility(View.GONE);
        myRouteShipmentList.clear();

        BringMyRouteShipmentsRequest bringMyRouteShipmentsRequest = new BringMyRouteShipmentsRequest();
        String jsonData = JsonSerializerDeserializer.serialize(bringMyRouteShipmentsRequest, true);
        new BringMyRouteShipmentsList().execute(jsonData);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // MenuInflater inflater = getMenuInflater();
        //  inflater.inflate(R.menu.myroutemenu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    class Optimization {
        public String CurrentLocation;
        public String EmployID;
        public String FleetNo;
        public String Waybills;
    }

    double Latitude = 0, Longitude = 0;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.OptimizeShipments:
                return true;
            case R.id.deleteall:
                deleteConfirmRoute();
                return true;
            case R.id.mnuShowDeliverySheetOrder:

                return true;

            case R.id.mnuSyncData:
                return true;
            case R.id.groupmap:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private class BringMyRouteShipmentsList extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;
        String result = "";
        StringBuffer buffer;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MyRouteActivity.this, "Please wait.", "Downloading Shipments Details.", true);
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];

            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "BringSuggestMyRouteShipments");
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
                super.onPostExecute(String.valueOf(finalJson));

                try {
                    JSONObject jsonObject = new JSONObject(finalJson);

                    new SuggestMyRouteShipments(finalJson, String.valueOf(Latitude), String.valueOf(Longitude), getApplicationContext(),
                            getWindow().getDecorView().getRootView());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                adapter = new RouteListAdapter(getApplicationContext(), myRouteShipmentList, "MyRouteActivity");
                mapListview.setAdapter(adapter);


            } else
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.wentwrong), GlobalVar.AlertType.Error);
            if (progressDialog != null)
                progressDialog.dismiss();
        }
    }

    private void deleteConfirmRoute() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MyRouteActivity.this);
        builder1.setTitle("Info");
        builder1.setMessage("Do you want to delete all? ");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        GlobalVar.GV().myRouteShipmentList.clear();

                        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                        dbConnections.clearAllCourierDailyRoute(getApplicationContext());
                        new DeleteContact().execute("");
                        dbConnections.close();

                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private class DeleteContact extends AsyncTask<String, String, String> {


        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {

            try {
                DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                boolean loop = false;
                loop = GlobalVar.deleteContactRawID(dbConnections.ContactDetails(getApplicationContext()), getApplicationContext() , 0);
                int time = 1000;
                while (!loop)
                    Thread.sleep(time);


            } catch (InterruptedException e) {
                e.printStackTrace();

            } catch (Exception e) {
                e.printStackTrace();

            }
            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            finish();
        }


        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MyRouteActivity.this,
                    "Info",
                    "Your Request is being process,kindly please wait");
        }


        @Override
        protected void onProgressUpdate(String... text) {


        }
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
            myRouteShipmentList = savedInstanceState.getParcelableArrayList("kpi");
            Latitude = savedInstanceState.getDouble("Latitude");
            Longitude = savedInstanceState.getDouble("Longitude");
            places = (ArrayList<Location>) savedInstanceState.getSerializable("places");

            adapter = new RouteListAdapter(getApplicationContext(), myRouteShipmentList, "MyRouteActivity");
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
        outState.putParcelableArrayList("kpi", myRouteShipmentList);
        outState.putDouble("Latitude", Latitude);
        outState.putDouble("Longitude", Longitude);
        outState.putSerializable("places", places);
        super.onSaveInstanceState(outState);
    }

}