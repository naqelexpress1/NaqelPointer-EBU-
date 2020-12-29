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
import com.naqelexpress.naqelpointer.DB.DBObjects.CheckPointBarCodeDetails;
import com.naqelexpress.naqelpointer.DB.DBObjects.CheckPointWaybillDetails;
import com.naqelexpress.naqelpointer.DB.DBObjects.MyRouteShipments;
import com.naqelexpress.naqelpointer.DB.DBObjects.NotDelivered;
import com.naqelexpress.naqelpointer.DB.DBObjects.NotDeliveredDetail;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.JSON.Request.OnDeliveryDetailRequest;
import com.naqelexpress.naqelpointer.JSON.Request.OnDeliveryRequest;
import com.naqelexpress.naqelpointer.JSON.Request.PickUpDetailRequest;
import com.naqelexpress.naqelpointer.JSON.Request.PickUpRequest;
import com.naqelexpress.naqelpointer.R;

import org.joda.time.DateTime;
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
import java.text.ParseException;
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
                if (GlobalVar.ValidateAutomacticDate(getApplicationContext())) {
                    manualsyncbtn.setEnabled(false);
                    manualsyncbtn.setClickable(false);

                    stopallservice();
                    startAllService();
                    insertManual();
                } else
                    GlobalVar.RedirectSettings(History.this);

            }
        });


        //DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        //dbConnections.DeleteAllSyncData(getApplicationContext());
    }

    private void stopallservice() {

        stopService(
                new Intent(History.this,
                        com.naqelexpress.naqelpointer.service.NclServiceBulk.class));

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
//        if (GetDivision()) {
//
//            stopService(
//                    new Intent(History.this,
//                            com.naqelexpress.naqelpointer.service.Deliverysheet.class));
//
//        }
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

        stopService(
                new Intent(History.this,
                        com.naqelexpress.naqelpointer.service.CheckPoint.class));
        stopService(
                new Intent(History.this,
                        com.naqelexpress.naqelpointer.service.NotDelivery.class));

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

            } else if (parent.getItemAtPosition(pos).toString().equals("MultiDelivery")) {

                ManualFunction = "MultiDelivery";
                //insertManual();
                mydeliverylist = new ArrayList<>();
                myrouteadapter = new RouteListAdapter(getApplicationContext(), mydeliverylist, "History");
                mapListview.setAdapter(myrouteadapter);
                GetMultiDeliveryList();

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
            } else if (parent.getItemAtPosition(pos).toString().equals("CheckPoint")) {

                ManualFunction = "CheckPoint";
                //insertManual();
                mydeliverylist = new ArrayList<>();
                myrouteadapter = new RouteListAdapter(getApplicationContext(), mydeliverylist, "History");
                mapListview.setAdapter(myrouteadapter);
                GetCheckPointData();

            }
            if (parent.getItemAtPosition(pos).toString().equals("Consolidation")) {

                ManualFunction = "Consolidation";
                //insertManual();
                mydeliverylist = new ArrayList<>();
                myrouteadapter = new RouteListAdapter(getApplicationContext(), mydeliverylist, "History");
                mapListview.setAdapter(myrouteadapter);
                GetConsolidationList();

            }
            if (parent.getItemAtPosition(pos).toString().equals("Waybill Measurement")) {

                ManualFunction = "Waybill Measurement";
                //insertManual();
                mydeliverylist = new ArrayList<>();
                myrouteadapter = new RouteListAdapter(getApplicationContext(), mydeliverylist, "History");
                mapListview.setAdapter(myrouteadapter);
                GetWaybillMeasurementList();

            }


        }

        public void onNothingSelected(AdapterView parent) {
            // Do nothing.
        }
    }

    private void GetBookingList() {
        // myBookingList.clear();
        // myBookingList.addAll(GlobalVar.getPickupSyncData(getApplicationContext()));
        myBookingList.addAll(GlobalVar.getPickupHistory(getApplicationContext()));

        mapListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getPickupPieces(myBookingList.get(position).ID);


            }
        });
        if (myBookingList.size() > 0) {
            adapter.notifyDataSetChanged();
            nodata.setVisibility(View.GONE);
        } else
            nodata.setVisibility(View.VISIBLE);


    }

    private void GetConsolidationList() {

        mydeliverylist.addAll(GlobalVar.getNCLNotSyncData(getApplicationContext()));
        if (mydeliverylist.size() > 0) {
            myrouteadapter.notifyDataSetChanged();
            nodata.setVisibility(View.GONE);
        } else
            nodata.setVisibility(View.VISIBLE);


    }

    private void GetWaybillMeasurementList() {

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        mydeliverylist.addAll(dbConnections.getWaybillMeasurementListHistory(getApplicationContext()));
        if (mydeliverylist.size() > 0) {
            myrouteadapter.notifyDataSetChanged();
            nodata.setVisibility(View.GONE);
        } else
            nodata.setVisibility(View.VISIBLE);
        dbConnections.close();

    }

    private ArrayList<String> getPickupPieces(int WaybillNo) {

        ArrayList<String> piececodes = new ArrayList<>();

        DBConnections db = new DBConnections(getApplicationContext(), null);
        Cursor result = db.Fill("select * from PickUpAuto where WaybillNo = " + WaybillNo, getApplicationContext());
        result.moveToFirst();
        String pieces[];

        if (result.getString(result.getColumnIndex("JsonData")) != null) {
            pieces = result.getString(result.getColumnIndex("JsonData")).split(",");
            for (String piece : pieces)
                piececodes.add(piece);

        }
        result.close();
        db.close();

        if (piececodes.size() > 0)
            LoadPieces(piececodes);
        else
            GlobalVar.ShowDialog(History.this, "Info", "No Pieces under this " + String.valueOf(WaybillNo), true);
        return piececodes;
    }

    private void GetCheckPointData() {
        // myBookingList.clear();
        // mydeliverylist.addAll(GlobalVar.getDeliverySyncData(getApplicationContext()));getDeliveryHistory
        mydeliverylist.addAll(GlobalVar.getCheckpointData(getApplicationContext()));

        if (mydeliverylist.size() > 0) {
            myrouteadapter.notifyDataSetChanged();
            nodata.setVisibility(View.GONE);
        } else
            nodata.setVisibility(View.VISIBLE);

        mapListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getOnDeliveryPieces(mydeliverylist.get(position).ID, mydeliverylist.get(position).ItemNo);


            }
        });
    }

    private void GetOnDeliveryList() {
        // myBookingList.clear();
        // mydeliverylist.addAll(GlobalVar.getDeliverySyncData(getApplicationContext()));getDeliveryHistory
        mydeliverylist.addAll(GlobalVar.getDeliveryHistory(getApplicationContext()));
        if (mydeliverylist.size() > 0) {
            myrouteadapter.notifyDataSetChanged();
            nodata.setVisibility(View.GONE);
        } else
            nodata.setVisibility(View.VISIBLE);

        mapListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getOnDeliveryPieces(mydeliverylist.get(position).ID, mydeliverylist.get(position).ItemNo);


            }
        });
    }

    private void GetMultiDeliveryList() {

        mydeliverylist.addAll(GlobalVar.getMultiDeliveryHistory(getApplicationContext()));
        if (mydeliverylist.size() > 0) {
            myrouteadapter.notifyDataSetChanged();
            nodata.setVisibility(View.GONE);
        } else
            nodata.setVisibility(View.VISIBLE);

        mapListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getMultiDeliveryPieces(mydeliverylist.get(position).ID);


            }
        });
    }

    private ArrayList<String> getOnDeliveryPieces(int deliveryID, String WaybillNo) {

        ArrayList<String> piececodes = new ArrayList<>();

        DBConnections db = new DBConnections(getApplicationContext(), null);

        Cursor resultDetail = db.Fill("select * from OnDelivery where ID =  " + deliveryID, getApplicationContext());
        if (resultDetail.getCount() > 0) {
            resultDetail.moveToFirst();
            String split[] = resultDetail.getString(resultDetail.getColumnIndex("Barcode")).split("\\,");
            for (String piece : split)
                piececodes.add(piece);
        }

        resultDetail.close();
        db.close();
        if (piececodes.size() > 0)
            LoadPieces(piececodes);
        else
            GlobalVar.ShowDialog(History.this, "Info", "No Pieces under this " + WaybillNo, true);
        return piececodes;
    }

    private ArrayList<String> getMultiDeliveryPieces(int ID) {

        ArrayList<String> piececodes = new ArrayList<>();

        DBConnections db = new DBConnections(getApplicationContext(), null);

        Cursor resultDetail = db.Fill("select * from MultiDeliveryWaybillDetail where MultiDeliveryID =  " + ID
                , getApplicationContext());
        resultDetail.moveToFirst();
        if (resultDetail.getCount() > 0) {
            piececodes.add("WaybillNos");
            resultDetail.moveToFirst();
            do {
                piececodes.add(resultDetail.getString(resultDetail.getColumnIndex("WaybillNo")));
            }
            while (resultDetail.moveToNext());


        }

        resultDetail = db.Fill("select * from MultiDeliveryDetail where MultiDeliveryID =  " + ID
                , getApplicationContext());
        resultDetail.moveToFirst();
        if (resultDetail.getCount() > 0) {
            piececodes.add("PieceCodes");
            resultDetail.moveToFirst();
            do {
                piececodes.add(resultDetail.getString(resultDetail.getColumnIndex("BarCode")));
            }
            while (resultDetail.moveToNext());


        }

        resultDetail.close();
        db.close();
        if (piececodes.size() > 0)
            LoadPieces(piececodes);
        else
            GlobalVar.ShowDialog(History.this, "Info", "No Pieces under this ", true);
        return piececodes;
    }

    private void GetNotDeliveryList() {
        //mydeliverylist.addAll(GlobalVar.getNotDeliverySyncData(getApplicationContext()));
        mydeliverylist.addAll(GlobalVar.getNotDeliveryData(getApplicationContext()));
        if (mydeliverylist.size() > 0) {
            myrouteadapter.notifyDataSetChanged();
            nodata.setVisibility(View.GONE);
        } else
            nodata.setVisibility(View.VISIBLE);

        mapListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getNotDeliveryPieces(mydeliverylist.get(position).ID, mydeliverylist.get(position).ItemNo);


            }
        });


    }

    private ArrayList<String> getNotDeliveryPieces(int deliveryID, String WaybillNo) {

        ArrayList<String> piececodes = new ArrayList<>();

        DBConnections db = new DBConnections(getApplicationContext(), null);

        Cursor resultDetail = db.Fill("select * from NotDelivered where ID =  " + deliveryID, getApplicationContext());

        //if (resultDetail.getCount() > 0) {
        //   resultDetail.moveToFirst();
        //   do {
        if (resultDetail.getCount() > 0) {
            resultDetail.moveToFirst();
            String split[] = resultDetail.getString(resultDetail.getColumnIndex("Barcode")).split("\\,");
            for (String piece : split)
                piececodes.add(piece);
        }
        //   }
        //    while (resultDetail.moveToNext());
        // }

        resultDetail.close();
        db.close();
        if (piececodes.size() > 0)
            LoadPieces(piececodes);
        else
            GlobalVar.ShowDialog(History.this, "Info", "No Pieces under this " + WaybillNo, true);
        return piececodes;
    }

    private void GetDeliverySheet() {
        try {
            if (!GetDivision())
                mydeliverylist.addAll(GlobalVar.getDeliverySheetforEBU(getApplicationContext()));
            else
                mydeliverylist.addAll(GlobalVar.getDeliverySheet(getApplicationContext()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
        mydeliverylist.addAll(GlobalVar.getNightStockHistory(getApplicationContext()));
        if (mydeliverylist.size() > 0) {
            myrouteadapter.notifyDataSetChanged();
            nodata.setVisibility(View.GONE);
        } else
            nodata.setVisibility(View.VISIBLE);


        mapListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GetNightStockPieces(mydeliverylist.get(position).ID);


            }
        });

    }

    private ArrayList<String> GetNightStockPieces(int ID) {

        ArrayList<String> piececodes = new ArrayList<>();

        DBConnections db = new DBConnections(getApplicationContext(), null);

        Cursor resultDetail = db.Fill("select * from NightStockDetail where NightStockID =  " + ID
                , getApplicationContext());
        resultDetail.moveToFirst();
        if (resultDetail.getCount() > 0) {
            piececodes.add("PieceCodes");
            resultDetail.moveToFirst();
            do {
                piececodes.add(resultDetail.getString(resultDetail.getColumnIndex("BarCode")));
            }
            while (resultDetail.moveToNext());


        }

        resultDetail.close();
        db.close();
        if (piececodes.size() > 0)
            LoadPieces(piececodes);
        else
            GlobalVar.ShowDialog(History.this, "Info", "No Pieces under this ", true);
        return piececodes;
    }

    private void startAllService() {

        stopService(
                new Intent(History.this,
                        com.naqelexpress.naqelpointer.service.NclServiceBulk.class));

        if (!GetDivision()) {
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
        if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.OnLoading.class)) {
            startService(
                    new Intent(History.this,
                            com.naqelexpress.naqelpointer.service.OnLoading.class));
        }


        if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.PickUp.class)) {
            startService(
                    new Intent(History.this,
                            com.naqelexpress.naqelpointer.service.PickUp.class));
        }

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
        String DomainURL = "";
        String isInternetAvailable = "";

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
            DomainURL = GlobalVar.GV().GetDomainURL(getApplicationContext());

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
            Cursor loop = db.Fill("select * from PickUpAuto where IsSync = 0 order by timein", getApplicationContext());
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

                String piececodes[];
                int index = 0;
                if (loop.getString(loop.getColumnIndex("JsonData")) != null) {
                    piececodes = loop.getString(loop.getColumnIndex("JsonData")).split(",");
                    for (String piece : piececodes) {
                        pickUpRequest.PickUpDetailRequestList.add(index, new PickUpDetailRequest
                                (piece));
                        index++;
                    }
                } else {
                    if (resultDetail.getCount() > 0) {
                        index = 0;
                        resultDetail.moveToFirst();
                        do {
                            pickUpRequest.PickUpDetailRequestList.add(index, new PickUpDetailRequest
                                    (resultDetail.getString(resultDetail.getColumnIndex("BarCode"))));
                            index++;
                        }
                        while (resultDetail.moveToNext());
                    }
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
                    httpURLConnection.setReadTimeout(GlobalVar.GV().loadbalance_ConRedtimeout);
                    httpURLConnection.setConnectTimeout(GlobalVar.GV().loadbalance_Contimeout);
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
                    isInternetAvailable = e.toString();
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
                    db.updatePickupbyID(pickUpRequest.ID, getApplicationContext());
                    //db.deletePickupID(pickUpRequest.ID, getApplicationContext());
                    //db.deletePickupDetails(pickUpRequest.ID, getApplicationContext());
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
            if (finalJson != null) {
                try {

                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }

                    DBConnections db = new DBConnections(getApplicationContext(), null);
                    Cursor ts = db.Fill("select Count(1) As totalRecord  from PickUpAuto Where Issync = 0", getApplicationContext());
                    ts.moveToFirst();
                    int tls = 0;
                    try {
                        tls = ts.getInt(ts.getColumnIndex("totalRecord"));
                    } catch (Exception e) {
                        tls = 0;
                    }

                    if (tls > 0) {

                        startService(
                                new Intent(History.this,
                                        com.naqelexpress.naqelpointer.service.PickUp.class));

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
            /*else {
                if (isInternetAvailable.contains("No address associated with hostname")) {
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly check your internet", GlobalVar.AlertType.Error);
                } else {
                    GlobalVar.GV().triedTimes = GlobalVar.GV().triedTimes + 1;
                    if (GlobalVar.GV().triedTimes == GlobalVar.GV().triedTimesCondition) {
                        GlobalVar.GV().SwitchoverDomain(getApplicationContext(), DomainURL);
                    }

                }
            }*/
        }
    }

    private class SaveDeliverybyManual extends AsyncTask<String, Integer, String> {
        String returnresult = "";
        StringBuffer buffer;
        int moveddata = 0;
        String DomainURL = "";
        String isInternetAvailable = "";

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
            // DomainURL = GlobalVar.GV().GetDomainURL(getApplicationContext());
            DomainURL = GlobalVar.GV().NaqelPointerAPILink;

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
                onDeliveryRequest.Barcode = result.getString(result.getColumnIndex("Barcode"));

                try {
                    int index = 0;
                    String barcode[] = onDeliveryRequest.Barcode.split("\\,");
                    for (String piececode : barcode) {

                        Cursor wid = db.Fill("select * from BarCode where BarCode = '" + piececode + "'"
                                , getApplicationContext());

                        int WayBillID = 0;
                        if (wid.getCount() > 0) {
                            wid.moveToFirst();
                            WayBillID = wid.getInt(wid.getColumnIndex("WayBillID"));
                        }
                        wid.close();

                        onDeliveryRequest.OnDeliveryDetailRequestList.add(index,
                                new OnDeliveryDetailRequest(piececode, WayBillID));

                        index++;
                    }

                } catch (Exception e) {
                    System.out.println(e);
                }

                String jsonData = JsonSerializerDeserializer.serialize(onDeliveryRequest, true);
                jsonData = jsonData.replace("Date(-", "Date(");

                HttpURLConnection httpURLConnection = null;
                OutputStream dos = null;
                InputStream ist = null;

                try {
                    URL url = new URL(DomainURL + "SendOnDeliveryDataToServer");
                    httpURLConnection = (HttpURLConnection) url.openConnection();

                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setReadTimeout(GlobalVar.GV().loadbalance_ConRedtimeout);
                    httpURLConnection.setConnectTimeout(GlobalVar.GV().loadbalance_Contimeout);
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
                    isInternetAvailable = e.toString();
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
            if (finalJson != null) {
                try {

                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }

                    DBConnections db = new DBConnections(getApplicationContext(), null);
                    Cursor ts = db.Fill("select Count(1) As totalRecord  from OnDelivery Where Issync = 0", getApplicationContext());
                    ts.moveToFirst();
                    int tls = 0;
                    try {
                        tls = ts.getInt(ts.getColumnIndex("totalRecord"));
                    } catch (Exception e) {
                        tls = 0;
                    }

                    if (tls > 0) {

                        StartOnDeliveryService();

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
                }
            }
//            else {
//                if (isInternetAvailable.contains("No address associated with hostname")) {
//                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly check your internet", GlobalVar.AlertType.Error);
//                } else {
//                    GlobalVar.GV().triedTimes = GlobalVar.GV().triedTimes + 1;
//                    if (GlobalVar.GV().triedTimes == GlobalVar.GV().triedTimesCondition) {
//                        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
//                        // dbConnections.UpdateDomaintriedTimes(GlobalVar.GV().triedTimes, DomainURL, getApplicationContext());
//                        GlobalVar.GV().SwitchoverDomain(getApplicationContext(), DomainURL);
//                    }
//
//                }
//            }
        }
    }

    private class SaveDeliverybyManualCBU extends AsyncTask<String, Integer, String> {
        String returnresult = "";
        StringBuffer buffer;
        int moveddata = 0;
        String DomainURL = "";
        String isInternetAvailable = "";

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
            DomainURL = GlobalVar.GV().GetDomainURL(getApplicationContext());
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

            if (result.getCount() == 0)
                return "No Data";

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
                onDeliveryRequest.Barcode = result.getString(result.getColumnIndex("Barcode"));

                try {
                    int index = 0;
                    String barcode[] = onDeliveryRequest.Barcode.split("\\,");
                    for (String piececode : barcode) {

                        Cursor wid = db.Fill("select * from BarCode where BarCode = '" + piececode + "'"
                                , getApplicationContext());

                        int WayBillID = 0;
                        if (wid.getCount() > 0) {
                            wid.moveToFirst();
                            WayBillID = wid.getInt(wid.getColumnIndex("WayBillID"));
                        }
                        wid.close();

                        onDeliveryRequest.OnDeliveryDetailRequestList.add(index,
                                new OnDeliveryDetailRequest(piececode, WayBillID));

                        index++;
                    }

                } catch (Exception e) {
                    System.out.println(e);
                }

                String jsonData = JsonSerializerDeserializer.serialize(onDeliveryRequest, true);
                jsonData = jsonData.replace("Date(-", "Date(");

                HttpURLConnection httpURLConnection = null;
                OutputStream dos = null;
                InputStream ist = null;

                try {
                    URL url = new URL(DomainURL + "PartialDelivery");
                    httpURLConnection = (HttpURLConnection) url.openConnection();

                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    httpURLConnection.setReadTimeout(GlobalVar.GV().loadbalance_ConRedtimeout);
                    httpURLConnection.setConnectTimeout(GlobalVar.GV().loadbalance_Contimeout);
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
                    isInternetAvailable = e.toString();
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


                try {
                    JSONObject response = new JSONObject(returnresult);
                    boolean IsSync = Boolean.parseBoolean(response.getString("IsSync"));
                    boolean HasError = Boolean.parseBoolean(response.getString("HasError"));
                    String updateDeliver = response.getString("ErrorMessage");
                    if (IsSync && !HasError) {
                        isInternetAvailable = "Done";
                        moveddata = moveddata + 1;

                        db.updateOnDeliveryID(onDeliveryRequest.ID, getApplicationContext());

                        if (updateDeliver.equals("Complete")) {
                            db.UpdateMyRouteShipmentsIsDeliverd(getApplicationContext(), onDeliveryRequest.WaybillNo, onDeliveryRequest.ID);
                        } else {
                            db.UpdateMyRouteShipmentsIsPartialDelivered(getApplicationContext(), onDeliveryRequest.WaybillNo, onDeliveryRequest.ID);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {
                    uploaddatacount = uploaddatacount + 1;
                } catch (Exception e) {

                }
                publishProgress((int) ((uploaddatacount * 100) / totalsize));

            } while (result.moveToNext());

            result.close();
            db.close();
            return isInternetAvailable;
        }

        @Override
        protected void onPostExecute(String finalJson) {

            if (finalJson != null && finalJson.equals("No Data")) {
                ErrorAlert("No Data",
                        "All Delivered Data Synchronized Successfully,It Will take max 10-15min to reflect InfoTrack");
            }

            if (finalJson != null && finalJson.equals("Done") && !finalJson.equals("No Data")) {
                try {

                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }

                    DBConnections db = new DBConnections(getApplicationContext(), null);
                    Cursor ts = db.Fill("select Count(1) As totalRecord  from OnDelivery Where Issync = 0", getApplicationContext());
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
            } else {
                if (isInternetAvailable.contains("No address associated with hostname")) {
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly check your internet", GlobalVar.AlertType.Error);
                } else {
                    GlobalVar.GV().triedTimes = GlobalVar.GV().triedTimes + 1;
                    if (GlobalVar.GV().triedTimes == GlobalVar.GV().triedTimesCondition) {
                        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                        // dbConnections.UpdateDomaintriedTimes(GlobalVar.GV().triedTimes, DomainURL, getApplicationContext());
                        GlobalVar.GV().SwitchoverDomain(getApplicationContext(), DomainURL);
                    }

                }
            }
            manualsyncbtn.setEnabled(true);
            manualsyncbtn.setClickable(true);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
    }


    private void insertManual() {

        totalsize = 0;
        uploaddatacount = 0;


        try {
            DBConnections db = new DBConnections(getApplicationContext(), null);


            Cursor ts = null;
            if (ManualFunction.equals("Pickup")) {
                stopService(
                        new Intent(History.this,
                                com.naqelexpress.naqelpointer.service.PickUp.class));

                ts = db.Fill("select Count(1) As totalRecord  from PickUpAuto where issync = 0 ", getApplicationContext());
            } else if (ManualFunction.equals("OnDelivery")) {

                stopService(
                        new Intent(History.this,
                                com.naqelexpress.naqelpointer.service.PartialDelivery.class));
                stopService(
                        new Intent(History.this,
                                com.naqelexpress.naqelpointer.service.OnDelivery.class));

                ts = db.Fill("select Count(1) As totalRecord  from OnDelivery where issync = 0", getApplicationContext());
            } else if (ManualFunction.equals("Not Delivery")) {
                stopService(
                        new Intent(History.this,
                                com.naqelexpress.naqelpointer.service.NotDelivery.class));
                ts = db.Fill("select Count(1) As totalRecord  from NotDelivered Where IsSync = 0  ", getApplicationContext()); //where issync = 0
            } else if (ManualFunction.equals("CheckPoint")) {
                stopService(
                        new Intent(History.this,
                                com.naqelexpress.naqelpointer.service.CheckPoint.class));
                ts = db.Fill("select Count(1) As totalRecord  from CheckPoint Where IsSync = 0  ", getApplicationContext()); //where issync = 0
            } else if (ManualFunction.equals("Consolidation")) {
                stopService(
                        new Intent(History.this,
                                com.naqelexpress.naqelpointer.service.NclServiceBulk.class));

                ts = db.Fill("select Count(1) As totalRecord  from NCL where issync = 0 ", getApplicationContext());
            } else
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
                } else if (totalsize > 0 && ManualFunction.equals("OnDelivery")) {
                    if (GlobalVar.getDivision(getApplicationContext()).equals("Express"))
                        new SaveDeliverybyManual().execute(String.valueOf(totalsize));
                    else
                        new SaveDeliverybyManualCBU().execute(String.valueOf(totalsize));
                } else if (totalsize > 0 && ManualFunction.equals("Not Delivery"))
                    new SaveNotDeliverybyManual().execute(String.valueOf(totalsize));
                else if (totalsize > 0 && ManualFunction.equals("CheckPoint"))
                    new SyncCheckpointbyManual().execute(String.valueOf(totalsize));
                else {
                    manualsyncbtn.setEnabled(true);
                    manualsyncbtn.setClickable(true);
                    ErrorAlert("No Data",
                            "All " + ManualFunction + " Data Synchronized Successfully"
                    );
                }
            } else {
                manualsyncbtn.setEnabled(true);
                manualsyncbtn.setClickable(true);
                ErrorAlert("Info",
                        "Data is start to moving , kindly please wait"
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

    private class SaveNotDeliverybyManual extends AsyncTask<String, Integer, String> {
        String returnresult = "";
        StringBuffer buffer;
        int moveddata = 0;
        String DomainURL = "";
        String isInternetAvailable = "";

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
            DomainURL = GlobalVar.GV().GetDomainURL(getApplicationContext());
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

            JSONArray jsonArray = new JSONArray();
            JSONObject data = new JSONObject();

            int Limit = 30;
            double loopcount = Float.parseFloat(params[0]) / 30f;
            int loop = (int) Math.ceil(loopcount);
            int Offset = 0;

            for (int i = 0; i < loop; i++) {

                returnresult = "";
                buffer = new StringBuffer();
                buffer.setLength(0);

                Cursor result = db.Fill("select * from NotDelivered  Where IsSync = 0 Limit " + Limit + " oFFset " + Offset, getApplicationContext()); //where IsSync = 0
                result.moveToFirst();

                if (result.getCount() == 0)
                    return "No Data";
                try {

                    do {
                        NotDelivered notDeliveredRequest = new NotDelivered();
                        notDeliveredRequest.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                        notDeliveredRequest.WaybillNo = String.valueOf(result.getString(result.getColumnIndex("WaybillNo")));
                        notDeliveredRequest.TimeIn = DateTime.parse(result.getString(result.getColumnIndex("TimeIn")));
                        notDeliveredRequest.TimeOut = DateTime.parse(result.getString(result.getColumnIndex("TimeOut")));
                        notDeliveredRequest.UserID = Integer.parseInt(result.getString(result.getColumnIndex("UserID")));
                        notDeliveredRequest.StationID = Integer.parseInt(result.getString(result.getColumnIndex("StationID")));
                        notDeliveredRequest.PiecesCount = Integer.parseInt(result.getString(result.getColumnIndex("PiecesCount")));
                        notDeliveredRequest.Latitude = result.getString(result.getColumnIndex("Latitude"));
                        notDeliveredRequest.Longitude = result.getString(result.getColumnIndex("Longitude"));
                        notDeliveredRequest.DeliveryStatusID = Integer.parseInt(result.getString(result.getColumnIndex("DeliveryStatusID")));
                        notDeliveredRequest.DeliveryStatusReasonID = result.getInt(result.getColumnIndex("DeliveryStatusReasonID"));
                        notDeliveredRequest.Notes = result.getString(result.getColumnIndex("Notes"));
                        notDeliveredRequest.Barcode = result.getString(result.getColumnIndex("Barcode"));

                        try {
                            int index = 0;
                            String barcode[] = notDeliveredRequest.Barcode.split("\\,");
                            for (String piececode : barcode) {
                                notDeliveredRequest.NotDeliveredDetails.add(index,
                                        new NotDeliveredDetail(piececode, notDeliveredRequest.ID));
                                index++;
                            }

                        } catch (Exception e) {
                            System.out.println(e);
                        }
                        JSONObject jsonData = new JSONObject(JsonSerializerDeserializer.serialize(notDeliveredRequest, true));
                        jsonArray.put(jsonData);
                    } while (result.moveToNext());

                    data.put("NotDelivered", jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String jsonData = data.toString();
                jsonData = jsonData.replace("Date(-", "Date(");

                HttpURLConnection httpURLConnection = null;
                OutputStream dos = null;
                InputStream ist = null;

                try {
                    URL url = new URL(DomainURL + "SendNotDeliveredDataToServerBulk");
                    httpURLConnection = (HttpURLConnection) url.openConnection();

                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    httpURLConnection.setReadTimeout(GlobalVar.GV().loadbalance_ConRedtimeout);
                    httpURLConnection.setConnectTimeout(GlobalVar.GV().loadbalance_Contimeout);
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
                    isInternetAvailable = e.toString();
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

                try {
                    JSONObject jsonObject = new JSONObject(returnresult);
                    boolean IsSync = Boolean.parseBoolean(jsonObject.getString("IsSync"));
                    boolean HasError = Boolean.parseBoolean(jsonObject.getString("HasError"));
                    if (IsSync && !HasError) {
                        String rIDs[] = jsonObject.getString("ErrorMessage").split("\\,");
                        isInternetAvailable = "Done";
                        for (String id : rIDs) {

                            Cursor waybill_result = db.Fill("select * from NotDelivered where ID = " + id, getApplicationContext());

                            if (waybill_result.getCount() > 0) {
                                waybill_result.moveToFirst();
                                String waybillno = waybill_result.getString(waybill_result.getColumnIndex("WaybillNo"));
                                int dsID = waybill_result.getInt(waybill_result.getColumnIndex("DeliveryStatusID"));

                                db.updateNotDeliveryID(Integer.parseInt(id), getApplicationContext());

                                db.UpdateMyRouteShipmentsNotDeliverd(getApplicationContext(), waybillno);

                                if (dsID == 8)
                                    db.UpdateMyRouteShipmentsRefused(getApplicationContext(), waybillno);
                            }
                            waybill_result.close();
                        }

                        moveddata = moveddata + result.getCount();
                    }
                    uploaddatacount = uploaddatacount + result.getCount();

                    publishProgress((int) ((uploaddatacount * 100) / totalsize));
                    Offset = Offset + Limit;
                } catch (JSONException e) {
                    result.close();
                    uploaddatacount = uploaddatacount + result.getCount();
                }
                result.close();
            }


            db.close();
            return isInternetAvailable;
        }

        @Override
        protected void onPostExecute(String finalJson) {
            if (finalJson != null && finalJson.equals("No Data")) {
                ErrorAlert("No Data",
                        "All Not Delivered Data Synchronized Successfully,It Will take max 10-15min to reflect InfoTrack");
            }
            if (finalJson.equals("Done") && !finalJson.equals("No Data")) {
                try {

                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }

                    DBConnections db = new DBConnections(getApplicationContext(), null);
                    Cursor ts = db.Fill("select Count(1) As totalRecord  from NotDelivered Where IsSync = 0  ", getApplicationContext()); //where issync = 0
                    ts.moveToFirst();
                    int tls = 0;
                    try {
                        tls = ts.getInt(ts.getColumnIndex("totalRecord"));
                    } catch (Exception e) {
                        tls = 0;
                    }

                    if (tls > 0) {
                        // StartOnDeliveryService();
                        startService(
                                new Intent(History.this,
                                        com.naqelexpress.naqelpointer.service.NotDelivery.class));

                        ErrorAlert("Something went wrong",
                                "Pending Data :- " + String.valueOf(tls) + " Check your internet connection,and try again");

                    } else {
                        ErrorAlert("No Data",
                                "All Not Delivered Data Synchronized Successfully,It Will take max 10-15min to reflect InfoTrack");
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
            } else {
                if (isInternetAvailable.contains("No address associated with hostname") || isInternetAvailable.contains("ConnectException")) {
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly check your internet", GlobalVar.AlertType.Error);
                } else {
                    GlobalVar.GV().triedTimes = GlobalVar.GV().triedTimes + 1;
                    if (GlobalVar.GV().triedTimes == GlobalVar.GV().triedTimesCondition) {
                        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                        // dbConnections.UpdateDomaintriedTimes(GlobalVar.GV().triedTimes, DomainURL, getApplicationContext());
                        GlobalVar.GV().SwitchoverDomain(getApplicationContext(), DomainURL);
                    }

                }
            }
            manualsyncbtn.setEnabled(true);
            manualsyncbtn.setClickable(true);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        }

    }

    private class SyncCheckpointbyManual extends AsyncTask<String, Integer, String> {
        String returnresult = "";
        StringBuffer buffer;
        int moveddata = 0;
        String DomainURL = "";
        String isInternetAvailable = "";

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
            DomainURL = GlobalVar.GV().GetDomainURL(getApplicationContext());

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

            int Limit = 1;
            double loopcount = Float.parseFloat(params[0]) / 1f;
            int loop = (int) Math.ceil(loopcount);
            int Offset = 0;
            String jsonData = "";
            int ID = 0;

            for (int i = 0; i < loop; i++) {

                returnresult = "";
                buffer = new StringBuffer();
                buffer.setLength(0);

                Cursor result = db.Fill("select * from CheckPoint  Where IsSync = 0 Limit " + Limit, getApplicationContext()); //where IsSync = 0
                result.moveToFirst();


                final com.naqelexpress.naqelpointer.DB.DBObjects.CheckPoint checkPoint = new com.naqelexpress.naqelpointer.DB.DBObjects.CheckPoint();
                checkPoint.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                ID = checkPoint.ID;
                checkPoint.CheckPointTypeID = Integer.parseInt(result.getString(result.getColumnIndex("CheckPointTypeID")));
                checkPoint.Date = DateTime.parse(result.getString(result.getColumnIndex("Date")));
                checkPoint.EmployID = Integer.parseInt(result.getString(result.getColumnIndex("EmployID")));
                checkPoint.IsSync = Boolean.parseBoolean(result.getString(result.getColumnIndex("IsSync")));
                checkPoint.Latitude = result.getString(result.getColumnIndex("Latitude"));
                checkPoint.Longitude = result.getString(result.getColumnIndex("Longitude"));
                checkPoint.CheckPointTypeDetailID = Integer.parseInt(result.getString(result.getColumnIndex("CheckPointTypeDetailID")));
                checkPoint.CheckPointTypeDDetailID = Integer.parseInt(result.getString(result.getColumnIndex("CheckPointTypeDDetailID")));
                checkPoint.Reference = result.getString(result.getColumnIndex("Ref"));

                Cursor resultDetail = db.Fill("select * from CheckPointWaybillDetails where CheckPointID = " + checkPoint.ID, getApplicationContext());
                if (resultDetail.getCount() > 0) {
                    resultDetail.moveToFirst();
                    int index = 0;
                    resultDetail.moveToFirst();
                    do {
                        checkPoint.CheckPointWaybillDetails.add(index,
                                new CheckPointWaybillDetails(resultDetail.getString(resultDetail.getColumnIndex("WaybillNo")),
                                        checkPoint.ID));
                        index++;
                    }
                    while (resultDetail.moveToNext());
                }

                resultDetail = db.Fill("select * from CheckPointBarCodeDetails where CheckPointID = " + checkPoint.ID, getApplicationContext());
                if (resultDetail.getCount() > 0) {
                    resultDetail.moveToFirst();
                    int index = 0;
                    resultDetail.moveToFirst();
                    do {
                        checkPoint.CheckPointBarCodeDetails.add(index, new CheckPointBarCodeDetails(resultDetail.getString(resultDetail.getColumnIndex("BarCode")), checkPoint.ID));
                        index++;
                    }
                    while (resultDetail.moveToNext());
                }


                jsonData = JsonSerializerDeserializer.serialize(checkPoint, true);
                jsonData = jsonData.replace("Date(-", "Date(");
                //SaveCheckPoint(db, jsonData, checkPoint.ID);


                HttpURLConnection httpURLConnection = null;
                OutputStream dos = null;
                InputStream ist = null;

                try {
                    URL url = new URL(DomainURL + "CheckPointByPieceLevel");
                    httpURLConnection = (HttpURLConnection) url.openConnection();

                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    httpURLConnection.setReadTimeout(GlobalVar.GV().loadbalance_ConRedtimeout);
                    httpURLConnection.setConnectTimeout(GlobalVar.GV().loadbalance_Contimeout);
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
                        isInternetAvailable = e.toString();
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

                try {
                    //JSONObject jsonObject = new JSONObject(returnresult);

                    try {
                        if (returnresult.contains("Created")) {
                            db.deleteCheckPointID(ID, getApplicationContext());
                            db.deleteCheckPointWayBill(ID, getApplicationContext());
                            db.deleteCheckPointBarcode(ID, getApplicationContext());

                            moveddata = moveddata + result.getCount();
                        }
                        // db.close();
                    } catch (Exception e) {

//                            if (db != null)
//                                db.close();
                        e.printStackTrace();
                        uploaddatacount = uploaddatacount + result.getCount();
                    }


                    uploaddatacount = uploaddatacount + result.getCount();
                    publishProgress((int) ((uploaddatacount * 100) / totalsize));
                    Offset = Offset + Limit;
                } catch (Exception e) {
                    //result.close();
                    uploaddatacount = uploaddatacount + result.getCount();
                }

                if (result != null)
                    result.close();
            }

            if (db != null)
                db.close();
            return null;
        }

        @Override
        protected void onPostExecute(String finalJson) {
            if (finalJson != null) {
                try {

                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }

                    DBConnections db = new DBConnections(getApplicationContext(), null);
                    Cursor ts = db.Fill("select Count(1) As totalRecord  from CheckPoint Where IsSync = 0  ", getApplicationContext()); //where issync = 0
                    ts.moveToFirst();
                    int tls = 0;
                    try {
                        tls = ts.getInt(ts.getColumnIndex("totalRecord"));
                    } catch (Exception e) {
                        tls = 0;
                    }

                    if (tls > 0) {
                        // StartOnDeliveryService();
                        startService(
                                new Intent(History.this,
                                        com.naqelexpress.naqelpointer.service.CheckPoint.class));

                        ErrorAlert("Something went wrong",
                                "Pending Data :- " + String.valueOf(tls) + " Check your internet connection,and try again");

                    } else {
                        ErrorAlert("No Data",
                                "All Checkpoint Data Synchronized Successfully,It Will take max 10-15min to reflect InfoTrack");
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
            } else {
                if (isInternetAvailable.contains("No address associated with hostname")) {
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly check your internet", GlobalVar.AlertType.Error);
                } else {
                    GlobalVar.GV().triedTimes = GlobalVar.GV().triedTimes + 1;
                    if (GlobalVar.GV().triedTimes == GlobalVar.GV().triedTimesCondition) {
                        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                        // dbConnections.UpdateDomaintriedTimes(GlobalVar.GV().triedTimes, DomainURL, getApplicationContext());
                        GlobalVar.GV().SwitchoverDomain(getApplicationContext(), DomainURL);
                    }

                }
            }
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

    private void LoadPieces(ArrayList<String> pieces) {


        AlertDialog.Builder builderSingle = new AlertDialog.Builder(History.this);
        builderSingle.setIcon(R.drawable.naqellogowhite);
        builderSingle.setTitle("Your Scanned Pieces is");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(History.this,
                android.R.layout.simple_list_item_1);
        arrayAdapter.addAll(pieces);

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                String strName = arrayAdapter.getItem(which);
//                AlertDialog.Builder builderInner = new AlertDialog.Builder(History.this);
//                builderInner.setMessage(strName);
//                builderInner.setTitle("Your Scanned Pieces");
//                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//                builderInner.show();
            }
        });
        builderSingle.show();
    }
}
