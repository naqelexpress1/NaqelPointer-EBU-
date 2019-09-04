package com.naqelexpress.naqelpointer.Activity.History;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.naqelexpress.naqelpointer.Activity.Booking.Booking;
import com.naqelexpress.naqelpointer.Activity.Booking.BookingListAdapter;
import com.naqelexpress.naqelpointer.Activity.MyRoute.RouteListAdapter;
import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.MyRouteShipments;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.JSON.Request.OnDeliveryDetailRequest;
import com.naqelexpress.naqelpointer.JSON.Request.OnDeliveryRequest;
import com.naqelexpress.naqelpointer.JSON.Request.PickUpDetailRequest;
import com.naqelexpress.naqelpointer.JSON.Request.PickUpRequest;
import com.naqelexpress.naqelpointer.R;

import org.joda.time.DateTime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Hasna on 8/7/18.
 */

public class History extends Activity {
    public ArrayList<Booking> myBookingList;
    private SwipeMenuListView mapListview;
    BookingListAdapter adapter;
    private TextView nodata;
    private RouteListAdapter myrouteadapter;
    private ArrayList<MyRouteShipments> mydeliverylist;
    Button manualsyncbtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);

        mapListview = (SwipeMenuListView) findViewById(R.id.myBookingListView);


        nodata = (TextView) findViewById(R.id.nodata);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter;
        if (!GetDivision())
            adapter = ArrayAdapter.createFromResource(
                    this, R.array.reportEBU, android.R.layout.simple_spinner_item);
        else
            adapter = ArrayAdapter.createFromResource(
                    this, R.array.reportCBU, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());

        manualsyncbtn = (Button) findViewById(R.id.sync);
        manualsyncbtn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                //SyncManulMessage();
                manualsyncbtn.setEnabled(false);
                manualsyncbtn.setClickable(false);

                stopallservice();
                startAllService();
                insertManual();


            }
        });
    }

    private void stopallservice() {


        stopService(
                new Intent(History.this,
                        com.naqelexpress.naqelpointer.service.ArrivedatDest.class));


        stopService(
                new Intent(History.this,
                        com.naqelexpress.naqelpointer.service.AtOrigin.class));


        stopService(
                new Intent(History.this,
                        com.naqelexpress.naqelpointer.service.CheckPoint.class));

        if (!GetDivision()) {

            stopService(
                    new Intent(History.this,
                            com.naqelexpress.naqelpointer.service.Deliverysheet.class));

        } else {

            stopService(
                    new Intent(History.this,
                            com.naqelexpress.naqelpointer.service.DeliverysheetbyPiece.class));

        }
        if (GetDivision()) {

            stopService(
                    new Intent(History.this,
                            com.naqelexpress.naqelpointer.service.Deliverysheet.class));

        }
        if (!GetDivision()) {

            stopService(
                    new Intent(History.this,
                            com.naqelexpress.naqelpointer.service.MultiDelivery.class));

        }
        if (!GetDivision()) {

            stopService(
                    new Intent(History.this,
                            com.naqelexpress.naqelpointer.service.NightStock.class));
        }


        stopService(
                new Intent(History.this,
                        com.naqelexpress.naqelpointer.service.NotDelivery.class));

        if (!GetDivision()) {

            stopService(
                    new Intent(History.this,
                            com.naqelexpress.naqelpointer.service.OnDelivery.class));

        } else {

            stopService(
                    new Intent(History.this,
                            com.naqelexpress.naqelpointer.service.PartialDelivery.class));

        }

        stopService(
                new Intent(History.this,
                        com.naqelexpress.naqelpointer.service.OnLoading.class));


        stopService(
                new Intent(History.this,
                        com.naqelexpress.naqelpointer.service.PickUp.class));

        if (GetDivision()) {

            stopService(
                    new Intent(History.this,
                            com.naqelexpress.naqelpointer.service.TerminalHandling.class));
            stopService(
                    new Intent(History.this,
                            com.naqelexpress.naqelpointer.service.TerminalHandlingBulk.class));
        }

        if (!GetDivision()) {

            stopService(
                    new Intent(History.this,
                            com.naqelexpress.naqelpointer.service.VehicleManifest.class));
        }

        stopService(
                new Intent(History.this,
                        com.naqelexpress.naqelpointer.service.WayBillMeasurement.class));

        stopService(
                new Intent(History.this,
                        com.naqelexpress.naqelpointer.service.signature.class));


    }

    String ManualFunction = "";

    public class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent,
                                   View view, int pos, long id) {
            if (parent.getItemAtPosition(pos).toString().equals("Pickup")) {

                ManualFunction = "Pickup";
                //insertManual();
                myBookingList = new ArrayList<>();
                adapter = new BookingListAdapter(History.this, myBookingList, "History");
                mapListview.setAdapter(adapter);
                GetBookingList();
            } else if (parent.getItemAtPosition(pos).toString().equals("OnDelivery")) {

                ManualFunction = "OnDelivery";
                //insertManual();
                mydeliverylist = new ArrayList<>();
                myrouteadapter = new RouteListAdapter(getApplicationContext(), mydeliverylist, "History");
                mapListview.setAdapter(myrouteadapter);
                GetOnDeliveryList();
            } else if (parent.getItemAtPosition(pos).toString().equals("Not Delivery")) {
                ManualFunction = "Not Delivery";
                mydeliverylist = new ArrayList<>();
                myrouteadapter = new RouteListAdapter(getApplicationContext(), mydeliverylist, "History");
                mapListview.setAdapter(myrouteadapter);
                GetNotDeliveryList();
            } else if (parent.getItemAtPosition(pos).toString().equals("Delivery Sheet")) {
                ManualFunction = "Delivery Sheet";
                mydeliverylist = new ArrayList<>();
                myrouteadapter = new RouteListAdapter(getApplicationContext(), mydeliverylist, "History");
                mapListview.setAdapter(myrouteadapter);
                GetDeliverySheet();
            } else if (parent.getItemAtPosition(pos).toString().equals("At Origin")) {
                ManualFunction = "At Origin";
                mydeliverylist = new ArrayList<>();
                myrouteadapter = new RouteListAdapter(getApplicationContext(), mydeliverylist, "History");
                mapListview.setAdapter(myrouteadapter);
                GetAtOrigin();
            } else if (parent.getItemAtPosition(pos).toString().equals("Loat to Dest")) {
                ManualFunction = "Loat to Dest";
                mydeliverylist = new ArrayList<>();
                myrouteadapter = new RouteListAdapter(getApplicationContext(), mydeliverylist, "History");
                mapListview.setAdapter(myrouteadapter);
                GetLoadtoDest();
            } else if (parent.getItemAtPosition(pos).toString().equals("Arrived at Dest")) {
                ManualFunction = "Arrived at Dest";
                mydeliverylist = new ArrayList<>();
                myrouteadapter = new RouteListAdapter(getApplicationContext(), mydeliverylist, "History");
                mapListview.setAdapter(myrouteadapter);
                GetArrivedatDest();
            } else if (parent.getItemAtPosition(pos).toString().equals("Night Stock")) {
                ManualFunction = "Night Stock";
                mydeliverylist = new ArrayList<>();
                myrouteadapter = new RouteListAdapter(getApplicationContext(), mydeliverylist, "History");
                mapListview.setAdapter(myrouteadapter);
                GetNightStock();
            }


        }

        public void onNothingSelected(AdapterView parent) {
            // Do nothing.
        }
    }

    private void GetBookingList() {
        // myBookingList.clear();
        myBookingList.addAll(GlobalVar.getPickupSyncData(getApplicationContext()));
        if (myBookingList.size() > 0) {
            adapter.notifyDataSetChanged();
            nodata.setVisibility(View.GONE);
        } else
            nodata.setVisibility(View.VISIBLE);


    }

    private void GetOnDeliveryList() {
        // myBookingList.clear();
        mydeliverylist.addAll(GlobalVar.getDeliverySyncData(getApplicationContext()));
        if (mydeliverylist.size() > 0) {
            myrouteadapter.notifyDataSetChanged();
            nodata.setVisibility(View.GONE);
        } else
            nodata.setVisibility(View.VISIBLE);


    }

    private void GetNotDeliveryList() {
        mydeliverylist.addAll(GlobalVar.getNotDeliverySyncData(getApplicationContext()));
        if (mydeliverylist.size() > 0) {
            myrouteadapter.notifyDataSetChanged();
            nodata.setVisibility(View.GONE);
        } else
            nodata.setVisibility(View.VISIBLE);


    }

    private void GetDeliverySheet() {
        mydeliverylist.addAll(GlobalVar.getDeliverySheet(getApplicationContext()));
        if (mydeliverylist.size() > 0) {
            myrouteadapter.notifyDataSetChanged();
            nodata.setVisibility(View.GONE);
        } else
            nodata.setVisibility(View.VISIBLE);


    }

    private void GetArrivedatDest() {
        mydeliverylist.addAll(GlobalVar.getArrivedatDest(getApplicationContext()));
        if (mydeliverylist.size() > 0) {
            myrouteadapter.notifyDataSetChanged();
            nodata.setVisibility(View.GONE);
        } else
            nodata.setVisibility(View.VISIBLE);
    }

    private void GetLoadtoDest() {
        mydeliverylist.addAll(GlobalVar.getLoadtoDest(getApplicationContext()));
        if (mydeliverylist.size() > 0) {
            myrouteadapter.notifyDataSetChanged();
            nodata.setVisibility(View.GONE);
        } else
            nodata.setVisibility(View.VISIBLE);


    }

    private void GetAtOrigin() {
        mydeliverylist.addAll(GlobalVar.getAtOrigin(getApplicationContext()));
        if (mydeliverylist.size() > 0) {
            myrouteadapter.notifyDataSetChanged();
            nodata.setVisibility(View.GONE);
        } else
            nodata.setVisibility(View.VISIBLE);


    }

    private void GetNightStock() {
        mydeliverylist.addAll(GlobalVar.getNightStock(getApplicationContext()));
        if (mydeliverylist.size() > 0) {
            myrouteadapter.notifyDataSetChanged();
            nodata.setVisibility(View.GONE);
        } else
            nodata.setVisibility(View.VISIBLE);
    }

    private void startAllService() {

        if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.ArrivedatDest.class)) {

            startService(
                    new Intent(History.this,
                            com.naqelexpress.naqelpointer.service.ArrivedatDest.class));
        }

        if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.AtOrigin.class)) {

            startService(
                    new Intent(History.this,
                            com.naqelexpress.naqelpointer.service.AtOrigin.class));
        }
        if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.CheckPoint.class)) {
            startService(
                    new Intent(History.this,
                            com.naqelexpress.naqelpointer.service.CheckPoint.class));
        }
        if (!GetDivision()) {
            if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.Deliverysheet.class)) {
                startService(
                        new Intent(History.this,
                                com.naqelexpress.naqelpointer.service.Deliverysheet.class));
            }
        } else {
            if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.DeliverysheetbyPiece.class)) {
                startService(
                        new Intent(History.this,
                                com.naqelexpress.naqelpointer.service.DeliverysheetbyPiece.class));
            }
        }
        if (GetDivision()) {
            if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.Discrepancy.class)) {
                startService(
                        new Intent(History.this,
                                com.naqelexpress.naqelpointer.service.Discrepancy.class));
            }
        }
        if (!GetDivision()) {
            if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.MultiDelivery.class)) {
                startService(
                        new Intent(History.this,
                                com.naqelexpress.naqelpointer.service.MultiDelivery.class));
            }
        }
        if (!GetDivision()) {
            if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.NightStock.class)) {
                startService(
                        new Intent(History.this,
                                com.naqelexpress.naqelpointer.service.NightStock.class));
            }
        }
        if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.NotDelivery.class)) {
            startService(
                    new Intent(History.this,
                            com.naqelexpress.naqelpointer.service.NotDelivery.class));
        }

        if (!GetDivision()) {
            if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.OnDelivery.class)) {
//                startService(
//                        new Intent(History.this,
//                                com.naqelexpress.naqelpointer.service.OnDelivery.class));
            }
        } else {
            if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.PartialDelivery.class)) {
                startService(
                        new Intent(History.this,
                                com.naqelexpress.naqelpointer.service.PartialDelivery.class));
            }
        }
        if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.OnLoading.class)) {
            startService(
                    new Intent(History.this,
                            com.naqelexpress.naqelpointer.service.OnLoading.class));
        }


//            if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.PickUp.class)) {
//                startService(
//                        new Intent(History.this,
//                                com.naqelexpress.naqelpointer.service.PickUp.class));
//            }

        if (GetDivision()) {
            if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.TerminalHandlingBulk.class)) {
                startService(
                        new Intent(History.this,
                                com.naqelexpress.naqelpointer.service.TerminalHandlingBulk.class));
            }
        }
        if (GetDivision()) {
            if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.TerminalHandling.class)) {
                startService(
                        new Intent(History.this,
                                com.naqelexpress.naqelpointer.service.TerminalHandling.class));
            }
        }

        if (!GetDivision()) {
            if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.VehicleManifest.class)) {
                startService(
                        new Intent(History.this,
                                com.naqelexpress.naqelpointer.service.VehicleManifest.class));
            }
        }
        if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.WayBillMeasurement.class)) {
            startService(
                    new Intent(History.this,
                            com.naqelexpress.naqelpointer.service.WayBillMeasurement.class));
        }
        if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.signature.class)) {
            startService(
                    new Intent(History.this,
                            com.naqelexpress.naqelpointer.service.signature.class));
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getApplication()
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private boolean GetDivision() {
        String division = GlobalVar.GV().getDivisionID(getApplicationContext(), GlobalVar.GV().EmployID);

        if (GlobalVar.GV().EmployID == 19127)
            return false;
        if (division.equals("Express"))
            return false;
        else
            return true;

    }

    private void SyncManulMessage() {
        AlertDialog alertDialog = new AlertDialog.Builder(History.this).create();
        alertDialog.setTitle("Info");
        alertDialog.setMessage("Kindly please wait your process is going on...");
        alertDialog.setCancelable(false);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //finish();
                    }
                });
        alertDialog.show();
    }

    ProgressDialog progressDialog;
    int totalsize = 0, uploaddatacount = 0;

    private class SavePickupbyManual extends AsyncTask<String, Integer, String> {
        String result = "";
        StringBuffer buffer;
        int moveddata = 0;

        @Override
        protected void onPreExecute() {

            uploaddatacount = 0;
            moveddata = 0;
            if (progressDialog == null) {

                progressDialog = new ProgressDialog(History.this);
                progressDialog.setTitle("Request is being process,please wait...");
                progressDialog.setMessage("Remaining " + String.valueOf(totalsize) + " / " + String.valueOf(totalsize));
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setMax(100);
                progressDialog.setCancelable(false);
                progressDialog.setProgress(1);
                progressDialog.show();

            }

            super.onPreExecute();

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values[0]);
            progressDialog.setMessage("Remaining  " + String.valueOf(totalsize - moveddata) + " / " + String.valueOf(totalsize));
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected String doInBackground(String... params) {
            totalsize = Integer.parseInt(params[0]);

            DBConnections db = new DBConnections(getApplicationContext(), null);
            Cursor loop = db.Fill("select * from PickUpAuto where IsSync = 0", getApplicationContext());
            loop.moveToFirst();
            do {
                result = "";
                buffer = new StringBuffer();
                buffer.setLength(0);

                PickUpRequest pickUpRequest = new PickUpRequest();
                pickUpRequest.ID = Integer.parseInt(loop.getString(loop.getColumnIndex("ID")));
                pickUpRequest.WaybillNo = loop.getString(loop.getColumnIndex("WaybillNo"));
                pickUpRequest.ClientID = Integer.parseInt(loop.getString(loop.getColumnIndex("ClientID")));
                pickUpRequest.FromStationID = Integer.parseInt(loop.getString(loop.getColumnIndex("FromStationID")));
                pickUpRequest.ToStationID = Integer.parseInt(loop.getString(loop.getColumnIndex("ToStationID")));
                pickUpRequest.PiecesCount = Integer.parseInt(loop.getString(loop.getColumnIndex("PieceCount")));
                pickUpRequest.Weight = Double.parseDouble(loop.getString(loop.getColumnIndex("Weight")));
                pickUpRequest.TimeIn = DateTime.parse(loop.getString(loop.getColumnIndex("TimeIn")));
                pickUpRequest.TimeOut = DateTime.parse(loop.getString(loop.getColumnIndex("TimeOut")));
                pickUpRequest.UserMEID = Integer.parseInt(loop.getString(loop.getColumnIndex("UserID")));
                pickUpRequest.StationID = Integer.parseInt(loop.getString(loop.getColumnIndex("StationID")));
                pickUpRequest.RefNo = loop.getString(loop.getColumnIndex("RefNo"));
                pickUpRequest.Latitude = loop.getString(loop.getColumnIndex("Latitude"));
                pickUpRequest.Longitude = loop.getString(loop.getColumnIndex("Longitude"));
                pickUpRequest.CurrentVersion = loop.getString(loop.getColumnIndex("CurrentVersion"));
                pickUpRequest.LoadTypeID = loop.getInt(loop.getColumnIndex("LoadTypeID"));
                pickUpRequest.al = loop.getInt(loop.getColumnIndex("AL"));
                pickUpRequest.TruckID = loop.getInt(loop.getColumnIndex("TruckID"));
                pickUpRequest.DeviceToken = "";

                Cursor resultDetail = db.Fill("select * from PickUpDetailAuto where PickUpID = " +
                        pickUpRequest.ID, getApplicationContext());

                if (resultDetail.getCount() > 0) {
                    int index = 0;
                    resultDetail.moveToFirst();
                    do {
                        pickUpRequest.PickUpDetailRequestList.add(index, new PickUpDetailRequest
                                (resultDetail.getString(resultDetail.getColumnIndex("BarCode"))));
                        index++;
                    }
                    while (resultDetail.moveToNext());
                }

                String jsonData = JsonSerializerDeserializer.serialize(pickUpRequest, true);
                jsonData = jsonData.replace("Date(-", "Date(");

                HttpURLConnection httpURLConnection = null;
                OutputStream dos = null;
                InputStream ist = null;

                try {
                    URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "SendPickUpDataToServer");
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


                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    result = String.valueOf(buffer);

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

                if (result.contains("Created")) {
                    moveddata = moveddata + 1;
                    db.deletePickupID(pickUpRequest.ID, getApplicationContext());
                    db.deletePickupDetails(pickUpRequest.ID, getApplicationContext());
                }
                try {
                    uploaddatacount = uploaddatacount + 1;
                } catch (Exception e) {

                }
                publishProgress((int) ((uploaddatacount * 100) / totalsize));

            } while (loop.moveToNext());

            loop.close();
            db.close();
            return null;
        }

        @Override
        protected void onPostExecute(String finalJson) {
            try {

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }

                DBConnections db = new DBConnections(getApplicationContext(), null);
                Cursor ts = db.Fill("select Count(1) As totalRecord  from PickUpAuto", getApplicationContext());
                ts.moveToFirst();
                int tls = 0;
                try {
                    tls = ts.getInt(ts.getColumnIndex("totalRecord"));
                } catch (Exception e) {
                    tls = 0;
                }

                if (tls > 0) {
                    ErrorAlert("Something went wrong",
                            "Pending Data :- " + String.valueOf(tls) + " Check your internet connection,and try again"
                    );

//                    startService(
//                            new Intent(History.this,
//                                    com.naqelexpress.naqelpointer.service.PickUp.class));

                } else {
                    ErrorAlert("No Data",
                            "All Pickup Data Synchronized Successfully,It Will take max 10-15min to reflect InfoTrack");
                }
                ts.close();
                db.close();
                manualsyncbtn.setEnabled(true);
                manualsyncbtn.setClickable(true);

                super.onPostExecute(String.valueOf(finalJson));


            } catch (Exception e) {
                System.out.println(e);
                //  insertManual();
            }
        }
    }

    private class SaveDeliverybyManual extends AsyncTask<String, Integer, String> {
        String returnresult = "";
        StringBuffer buffer;
        int moveddata = 0;

        @Override
        protected void onPreExecute() {

            uploaddatacount = 0;
            moveddata = 0;
            if (progressDialog == null) {

                progressDialog = new ProgressDialog(History.this);
                progressDialog.setTitle("Request is being process,please wait...");
                progressDialog.setMessage("Remaining " + String.valueOf(totalsize) + " / " + String.valueOf(totalsize));
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setMax(100);
                progressDialog.setCancelable(false);
                progressDialog.setProgress(1);
                progressDialog.show();

            }

            super.onPreExecute();

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values[0]);
            progressDialog.setMessage("Remaining  " + String.valueOf(totalsize - moveddata) + " / " + String.valueOf(totalsize));
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected String doInBackground(String... params) {
            totalsize = Integer.parseInt(params[0]);

            DBConnections db = new DBConnections(getApplicationContext(), null);
            Cursor result = db.Fill("select * from OnDelivery where IsSync = 0", getApplicationContext());
            result.moveToFirst();
            do {
                returnresult = "";
                buffer = new StringBuffer();
                buffer.setLength(0);


                OnDeliveryRequest onDeliveryRequest = new OnDeliveryRequest();
                onDeliveryRequest.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                onDeliveryRequest.WaybillNo = result.getString(result.getColumnIndex("WaybillNo"));
                onDeliveryRequest.ReceiverName = result.getString(result.getColumnIndex("ReceiverName"));

                onDeliveryRequest.PiecesCount = Integer.parseInt(result.getString(result.getColumnIndex("PiecesCount")));
                onDeliveryRequest.TimeIn = DateTime.parse(result.getString(result.getColumnIndex("TimeIn")));
                onDeliveryRequest.TimeOut = DateTime.parse(result.getString(result.getColumnIndex("TimeOut")));
                onDeliveryRequest.EmployID = Integer.parseInt(result.getString(result.getColumnIndex("EmployID")));
                onDeliveryRequest.StationID = Integer.parseInt(result.getString(result.getColumnIndex("StationID")));
                onDeliveryRequest.IsPartial = Boolean.parseBoolean(result.getString(result.getColumnIndex("IsPartial")));
                onDeliveryRequest.Latitude = result.getString(result.getColumnIndex("Latitude"));
                onDeliveryRequest.Longitude = result.getString(result.getColumnIndex("Longitude"));
                onDeliveryRequest.ReceivedAmt = Double.parseDouble(result.getString(result.getColumnIndex("TotalReceivedAmount")));
                //onDeliveryRequest.ReceiptNo = result.getString(result.getColumnIndex("ReceiptNo"));
                //onDeliveryRequest.StopPointsID = Integer.parseInt(result.getString(result.getColumnIndex("StopPointsID")));
                onDeliveryRequest.POSAmount = Double.parseDouble(result.getString(result.getColumnIndex("POSAmount")));
                onDeliveryRequest.CashAmount = Double.parseDouble(result.getString(result.getColumnIndex("CashAmount")));
                onDeliveryRequest.al = result.getInt(result.getColumnIndex("AL"));
                onDeliveryRequest.DeviceToken = "";

                Cursor resultDetail = db.Fill("select * from OnDeliveryDetail where DeliveryID = " + onDeliveryRequest.ID, getApplicationContext());

                if (resultDetail.getCount() > 0) {
                    resultDetail.moveToFirst();
                    int index = 0;
                    resultDetail.moveToFirst();
                    do {
                        onDeliveryRequest.OnDeliveryDetailRequestList.add(index,
                                new OnDeliveryDetailRequest(resultDetail.getString(resultDetail.getColumnIndex("BarCode")), 0));
                        index++;
                    }
                    while (resultDetail.moveToNext());


                }
                String jsonData = JsonSerializerDeserializer.serialize(onDeliveryRequest, true);
                jsonData = jsonData.replace("Date(-", "Date(");

                HttpURLConnection httpURLConnection = null;
                OutputStream dos = null;
                InputStream ist = null;

                try {
                    URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "SendOnDeliveryDataToServer");
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


                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    returnresult = String.valueOf(buffer);

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
                    returnresult = String.valueOf(buffer);
                }


                if (returnresult.contains("Created")) {
                    moveddata = moveddata + 1;
                    db.deleteonDeliveryID(onDeliveryRequest.ID, getApplicationContext());
                    db.deleteDeliveyDetails(onDeliveryRequest.ID, getApplicationContext());
                    db.UpdateMyRouteShipmentsIsDeliverd(getApplicationContext(), onDeliveryRequest.WaybillNo, onDeliveryRequest.ID);
                }
                try {
                    uploaddatacount = uploaddatacount + 1;
                } catch (Exception e) {

                }
                publishProgress((int) ((uploaddatacount * 100) / totalsize));

            } while (result.moveToNext());

            result.close();
            db.close();
            return null;
        }

        @Override
        protected void onPostExecute(String finalJson) {
            try {

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }

                DBConnections db = new DBConnections(getApplicationContext(), null);
                Cursor ts = db.Fill("select Count(1) As totalRecord  from OnDelivery", getApplicationContext());
                ts.moveToFirst();
                int tls = 0;
                try {
                    tls = ts.getInt(ts.getColumnIndex("totalRecord"));
                } catch (Exception e) {
                    tls = 0;
                }

                if (tls > 0) {
                    // StartOnDeliveryService();
                    ErrorAlert("Something went wrong",
                            "Pending Data :- " + String.valueOf(tls) + " Check your internet connection,and try again");

                } else {
                    ErrorAlert("No Data",
                            "All Delivered Data Synchronized Successfully,It Will take max 10-15min to reflect InfoTrack");
                }
                ts.close();
                db.close();
                manualsyncbtn.setEnabled(true);
                manualsyncbtn.setClickable(true);
                super.onPostExecute(String.valueOf(finalJson));


            } catch (Exception e) {
                System.out.println(e);
                //  insertManual();
            }
        }
    }


    private void insertManual() {

        totalsize = 0;
        uploaddatacount = 0;


        try {
            DBConnections db = new DBConnections(getApplicationContext(), null);


            Cursor ts = null;
            if (ManualFunction.equals("Pickup"))
                ts = db.Fill("select Count(1) As totalRecord  from PickUpAuto ", getApplicationContext());
            else if (ManualFunction.equals("OnDelivery"))
                ts = db.Fill("select Count(1) As totalRecord  from OnDelivery ", getApplicationContext());
            else
                return;

            if (ts != null) {
                ts.moveToFirst();
                try {
                    totalsize = ts.getInt(ts.getColumnIndex("totalRecord"));
                } catch (Exception e) {
                    totalsize = 0;
                }


                if (totalsize > 0 && ManualFunction.equals("Pickup")) {
                    new SavePickupbyManual().execute(String.valueOf(totalsize));
                } else if (totalsize > 0 && ManualFunction.equals("OnDelivery"))
                    new SaveDeliverybyManual().execute(String.valueOf(totalsize));
                else {
                    manualsyncbtn.setEnabled(true);
                    manualsyncbtn.setClickable(true);
                    ErrorAlert("No Data",
                            "All Data Synchronized Successfully"
                    );
                }
            } else {
                manualsyncbtn.setEnabled(true);
                manualsyncbtn.setClickable(true);
                ErrorAlert("Error",
                        "Something went wrong,Kindly contact concern person"
                );
            }
            ts.close();
            db.close();

        } catch (Exception e) {
            manualsyncbtn.setEnabled(true);
            manualsyncbtn.setClickable(true);
            System.out.println(e);
        }

    }

    private void ErrorAlert(String title, final String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(History.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Continue",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (message.contains("Synchronized Successfully"))
                            finish();

                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Exit",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        alertDialog.show();
    }

    private void StartOnDeliveryService() {
        if (!GetDivision()) {
            if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.OnDelivery.class)) {
                startService(
                        new Intent(History.this,
                                com.naqelexpress.naqelpointer.service.OnDelivery.class));
            }
        } else {
            if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.PartialDelivery.class)) {
                startService(
                        new Intent(History.this,
                                com.naqelexpress.naqelpointer.service.PartialDelivery.class));
            }
        }

    }

}
