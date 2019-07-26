package com.naqelexpress.naqelpointer.Activity.History;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.naqelexpress.naqelpointer.Activity.AtOriginusingLocalDB.AtOrigin;
import com.naqelexpress.naqelpointer.Activity.Booking.Booking;
import com.naqelexpress.naqelpointer.Activity.Booking.BookingListAdapter;
import com.naqelexpress.naqelpointer.Activity.MyRoute.RouteListAdapter;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.MyRouteShipments;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);

        mapListview = (SwipeMenuListView) findViewById(R.id.myBookingListView);


        nodata = (TextView) findViewById(R.id.nodata);


        // DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
//        Cursor result = dbConnections.Fill("select * from MultiDelivery");
//
//        Cursor result1 = dbConnections.Fill("select * from MultiDeliveryWaybillDetail ");
//
//        Cursor result2 = dbConnections.Fill("select * from MultiDeliveryDetail ");

        // GetBookingList();


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

        Button myButton = (Button) findViewById(R.id.sync);
        myButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

//                Spinner sp = (Spinner) findViewById(R.id.spinner);
//                String spinnerString = null;
//                spinnerString = sp.getSelectedItem().toString();
//                int nPos = sp.getSelectedItemPosition();
                stopallservice();
                startAllService();


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
        }
        if (GetDivision()) {

            stopService(
                    new Intent(History.this,
                            com.naqelexpress.naqelpointer.service.TerminalHandling.class));
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

    public class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent,
                                   View view, int pos, long id) {
            if (parent.getItemAtPosition(pos).toString().equals("Pickup")) {
                myBookingList = new ArrayList<>();
                adapter = new BookingListAdapter(History.this, myBookingList, "History");
                mapListview.setAdapter(adapter);
                GetBookingList();
            } else if (parent.getItemAtPosition(pos).toString().equals("OnDelivery")) {

                mydeliverylist = new ArrayList<>();
                myrouteadapter = new RouteListAdapter(getApplicationContext(), mydeliverylist, "History");
                mapListview.setAdapter(myrouteadapter);
                GetOnDeliveryList();
            } else if (parent.getItemAtPosition(pos).toString().equals("Not Delivery")) {

                mydeliverylist = new ArrayList<>();
                myrouteadapter = new RouteListAdapter(getApplicationContext(), mydeliverylist, "History");
                mapListview.setAdapter(myrouteadapter);
                GetNotDeliveryList();
            } else if (parent.getItemAtPosition(pos).toString().equals("Delivery Sheet")) {

                mydeliverylist = new ArrayList<>();
                myrouteadapter = new RouteListAdapter(getApplicationContext(), mydeliverylist, "History");
                mapListview.setAdapter(myrouteadapter);
                GetDeliverySheet();
            } else if (parent.getItemAtPosition(pos).toString().equals("At Origin")) {
                mydeliverylist = new ArrayList<>();
                myrouteadapter = new RouteListAdapter(getApplicationContext(), mydeliverylist, "History");
                mapListview.setAdapter(myrouteadapter);
                GetAtOrigin();
            } else if (parent.getItemAtPosition(pos).toString().equals("Loat to Dest")) {
                mydeliverylist = new ArrayList<>();
                myrouteadapter = new RouteListAdapter(getApplicationContext(), mydeliverylist, "History");
                mapListview.setAdapter(myrouteadapter);
                GetLoadtoDest();
            } else if (parent.getItemAtPosition(pos).toString().equals("Arrived at Dest")) {

                mydeliverylist = new ArrayList<>();
                myrouteadapter = new RouteListAdapter(getApplicationContext(), mydeliverylist, "History");
                mapListview.setAdapter(myrouteadapter);
                GetArrivedatDest();
            } else if (parent.getItemAtPosition(pos).toString().equals("Night Stock")) {

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
                                com.naqelexpress.naqelpointer.service.Deliverysheet.class));
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
            if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.TerminalHandling.class)) {
                startService(
                        new Intent(History.this,
                                com.naqelexpress.naqelpointer.service.TerminalHandling.class));
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

    private void ErrorAlert(String Waybill, String BarCode, int Count) {
        AlertDialog alertDialog = new AlertDialog.Builder(History.this).create();
        alertDialog.setTitle("Info");
        alertDialog.setMessage("Kindly please wait your process is going on");
        alertDialog.setCancelable(false);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        alertDialog.show();
    }

}
