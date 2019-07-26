package com.naqelexpress.naqelpointer.Activity.SuggestDeliverysheet;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.naqelexpress.naqelpointer.Activity.MyRoute.RouteListAdapter;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.MyRouteShipments;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import org.joda.time.DateTime;

import java.util.ArrayList;

public class ListViewRoute extends Fragment {
    View rootView;

    RouteListAdapter adapter;
    private SwipeMenuListView mapListview;
    ArrayList<MyRouteShipments> myRouteShipmentList;
    sendMapData sendMapData;

    protected void displayReceivedData() {
        LoadMyRouteShipments();
    }

    interface sendMapData {
        void sendmapData();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        {
            if (rootView == null) {
                rootView = inflater.inflate(R.layout.myroute, container, false);


                mapListview = (SwipeMenuListView) rootView.findViewById(R.id.myRouteListView);
                myRouteShipmentList = new ArrayList<>();

                Button btnStartTrip = (Button) rootView.findViewById(R.id.btnStartTrip);
                TextView txtStartTrip = (TextView) rootView.findViewById(R.id.txtStartTrip);
                Button btnCloseTrip = (Button) rootView.findViewById(R.id.btnCloseTrip);
                TextView txtCloseTrip = (TextView) rootView.findViewById(R.id.txtCloseTrip);
                btnStartTrip.setVisibility(View.GONE);
                txtStartTrip.setVisibility(View.GONE);
                btnCloseTrip.setVisibility(View.GONE);
                txtCloseTrip.setVisibility(View.GONE);

                if (DeliverySheet.call == 1)
                    displayReceivedData();

            }

            return rootView;
        }
    }


    private void initviews() {
        adapter = new RouteListAdapter(getActivity().getApplicationContext(), myRouteShipmentList, "CourierKpi");
        mapListview.setAdapter(adapter);

    }

    public void LoadMyRouteShipments() {

        DBConnections dbConnections = new DBConnections(getContext(), null);

        int position = 1;

        Cursor result = dbConnections.Fill("select * from MyRouteShipments Where DDate = '" +
                GlobalVar.getDate() + "' and EmpID = " + GlobalVar.GV().EmployID, getContext());
        if (result.getCount() > 0) {

            dbConnections.InsertOFD(result.getCount(), GlobalVar.getDate(), getContext());

            myRouteShipmentList = new ArrayList<>();

            result.moveToFirst();
            do {
                MyRouteShipments myRouteShipments = new MyRouteShipments();
                myRouteShipments.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                myRouteShipments.BillingType = result.getString(result.getColumnIndex("BillingType"));
                myRouteShipments.OrderNo = Integer.parseInt(result.getString(result.getColumnIndex("OrderNo")));
                myRouteShipments.ItemNo = result.getString(result.getColumnIndex("ItemNo"));

                myRouteShipments.TypeID = Integer.parseInt(result.getString(result.getColumnIndex("TypeID")));
                myRouteShipments.CODAmount = Double.parseDouble(result.getString(result.getColumnIndex("CODAmount")));
                myRouteShipments.DeliverySheetID = Integer.parseInt(result.getString(result.getColumnIndex("DeliverySheetID")));
                myRouteShipments.Date = DateTime.parse(result.getString(result.getColumnIndex("Date")));
                myRouteShipments.ExpectedTime = DateTime.parse(result.getString(result.getColumnIndex("ExpectedTime")));

                myRouteShipments.Latitude = result.getString(result.getColumnIndex("Latitude"));
                myRouteShipments.Longitude = result.getString(result.getColumnIndex("Longitude"));

                myRouteShipments.ClientID = Integer.parseInt(result.getString(result.getColumnIndex("ClientID")));
                myRouteShipments.ClientName = result.getString(result.getColumnIndex("ClientName"));
                myRouteShipments.ClientFName = result.getString(result.getColumnIndex("ClientFName"));
                myRouteShipments.ClientAddressPhoneNumber = result.getString(result.getColumnIndex("ClientAddressPhoneNumber"));
                myRouteShipments.ClientAddressFirstAddress = result.getString(result.getColumnIndex("ClientAddressFirstAddress"));
                myRouteShipments.ClientAddressSecondAddress = result.getString(result.getColumnIndex("ClientAddressSecondAddress"));
                myRouteShipments.ClientContactName = result.getString(result.getColumnIndex("ClientContactName"));
                myRouteShipments.ClientContactFName = result.getString(result.getColumnIndex("ClientContactFName"));
                myRouteShipments.ClientContactPhoneNumber = result.getString(result.getColumnIndex("ClientContactPhoneNumber"));
                myRouteShipments.ClientContactMobileNo = result.getString(result.getColumnIndex("ClientContactMobileNo"));
                myRouteShipments.ConsigneeName = result.getString(result.getColumnIndex("ConsigneeName"));
                myRouteShipments.ConsigneeFName = result.getString(result.getColumnIndex("ConsigneeFName"));
                myRouteShipments.ConsigneePhoneNumber = result.getString(result.getColumnIndex("ConsigneePhoneNumber"));
                myRouteShipments.ConsigneeFirstAddress = result.getString(result.getColumnIndex("ConsigneeFirstAddress"));
                myRouteShipments.ConsigneeSecondAddress = result.getString(result.getColumnIndex("ConsigneeSecondAddress"));
                myRouteShipments.ConsigneeNear = result.getString(result.getColumnIndex("ConsigneeNear"));
                myRouteShipments.ConsigneeMobile = result.getString(result.getColumnIndex("ConsigneeMobile"));
                myRouteShipments.Origin = result.getString(result.getColumnIndex("Origin"));
                myRouteShipments.Destination = result.getString(result.getColumnIndex("Destination"));
                myRouteShipments.PODNeeded = Boolean.parseBoolean(result.getString(result.getColumnIndex("PODNeeded")));
                myRouteShipments.PODDetail = result.getString(result.getColumnIndex("PODDetail"));
                myRouteShipments.PODTypeCode = result.getString(result.getColumnIndex("PODTypeCode"));
                myRouteShipments.PODTypeName = result.getString(result.getColumnIndex("PODTypeName"));
                myRouteShipments.IsDelivered = Boolean.parseBoolean(result.getString(result.getColumnIndex("IsDelivered")));
                myRouteShipments.NotDelivered = Boolean.parseBoolean(result.getString(result.getColumnIndex("NotDelivered")));
                myRouteShipments.CourierDailyRouteID = Integer.parseInt(result.getString(result.getColumnIndex("CourierDailyRouteID")));
                myRouteShipments.OptimzeSerialNo = Integer.parseInt(result.getString(result.getColumnIndex("OptimzeSerialNo")));
                myRouteShipments.HasComplaint = result.getInt(result.getColumnIndex("HasComplaint")) > 0;
                myRouteShipments.HasDeliveryRequest = Boolean.parseBoolean(result.getString(result.getColumnIndex("CourierDailyRouteID")));

                myRouteShipmentList.add(myRouteShipments);

                position += 1;
            }
            while (result.moveToNext());

            try {
                sendMapData = (sendMapData) getActivity();
            } catch (Exception e) {

            }
        }
        initviews();
        dbConnections.close();
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {

        }
    }

}