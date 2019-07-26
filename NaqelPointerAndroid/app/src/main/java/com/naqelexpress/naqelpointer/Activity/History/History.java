package com.naqelexpress.naqelpointer.Activity.History;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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


        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
//        Cursor result = dbConnections.Fill("select * from MultiDelivery");
//
//        Cursor result1 = dbConnections.Fill("select * from MultiDeliveryWaybillDetail ");
//
//        Cursor result2 = dbConnections.Fill("select * from MultiDeliveryDetail ");

        // GetBookingList();


        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.report, android.R.layout.simple_spinner_item);
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
                startAllService();


            }
        });
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

    private void startAllService() {
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
        if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.NotDelivery.class)) {
            startService(
                    new Intent(History.this,
                            com.naqelexpress.naqelpointer.service.NotDelivery.class));
        }
        if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.OnDelivery.class)) {
            startService(
                    new Intent(History.this,
                            com.naqelexpress.naqelpointer.service.OnDelivery.class));
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

}
