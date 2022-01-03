package com.naqelexpress.naqelpointer.Activity.PaperLessDSSummary;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.SelectData;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.Models.CourierNotesModels;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.service.CourierNotesService;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Hasna on 11/11/18.
 */

public class RouteHistory extends AppCompatActivity implements RouteHistoryAdapter.RouteAdapterListener {

    RecyclerView dsitems;
    RouteHistoryAdapter adapter;
    List<HashMap<String, String>> hashMapList;
    TextView statusheader;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.routehistory);

        dsitems = (RecyclerView) findViewById(R.id.dsitems);

        hashMapList = new ArrayList<>();
        statusheader = (TextView) findViewById(R.id.statusheader);
        Button ds = (Button) findViewById(R.id.ds);
        Button as = (Button) findViewById(R.id.as);
        Button nas = (Button) findViewById(R.id.nas);

        HashMap<String, String> hashMap = SelectData.getPaperlessDSHeaderInfo(getApplicationContext());
        if (hashMap.size() > 0) {
            ds.setText("Delivered Shipments - " + hashMap.get("DeliveredCount"));
            as.setText("Attempted Shipments - " + hashMap.get("AttemptedCount"));
            nas.setText("Not attempted Shipments - " + hashMap.get("NotAttemptCount"));
        }
    }

    public void DeliveredShipments(View view) {
        clearList();
        hashMapList.addAll(GlobalVar.getAllDeliveredWaybills(getApplicationContext()));
        setAdapter(0);
    }

    public void AttemptedShipments(View view) {
        atemptedshp();
    }

    private void atemptedshp() {
        clearList();
        hashMapList.addAll(GlobalVar.getAllNotDeliveredWaybills(getApplicationContext()));
        setAdapter(1);
    }

    public void NotAttemptedShipments(View view) {

        if (!GlobalVar.GV().isSeqComplete(getApplicationContext()))
            GlobalVar.ShowDialog(RouteHistory.this, "Info", "Please finish your Sequenced delivery", true);
        else {
            clearList();
            hashMapList.addAll(GlobalVar.getAllNotAttemptedWaybills(getApplicationContext()));
            setAdapter(2);
        }


    }

    private void clearList() {
        hashMapList.clear();
    }

    private void setAdapter(int status) {
        if (hashMapList.size() > 0) {
            adapter = new RouteHistoryAdapter(getApplicationContext(), hashMapList,
                    "CourierKpi", this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            dsitems.setLayoutManager(mLayoutManager);
            dsitems.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            dsitems.setVisibility(View.VISIBLE);
        } else if (status == 2) {
            GlobalVar.ShowDialog(RouteHistory.this, "Info", "successfully attempted all", true);
        }

        setHeader(status);

    }

    private void setHeader(int status) {
        if (status == 0)
            statusheader.setText("Delivered Shipments");
        else if (status == 1)
            statusheader.setText("Attempted Shipments");
        else if (status == 2)
            statusheader.setText("Not Attempted Shipments");
    }

    @Override
    public void onItemSelected(HashMap<String, String> hashMap, int position) {
        if (statusheader.getText().toString().equals("Attempted Shipments"))
            if (GlobalVar.GV().isSeqComplete(getApplicationContext()))
                RedirectWaybillPlanActivity(hashMap.get("WaybillNo"));
            else
                GlobalVar.ShowDialog(RouteHistory.this, "Info", "Please finish your Sequenced delivery", true);

//            updateNotes(hashMap);

    }

    private void updateNotes(final HashMap<String, String> hashMap) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(RouteHistory.this);
        alert.setMessage("Please enter Notes for " + hashMap.get("WaybillNo"));
        alert.setTitle("Info");

        alert.setView(edittext);

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String notes = edittext.getText().toString();
                insertCourierNotes(notes, hashMap.get("WaybillNo"));
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }


    private void insertCourierNotes(String notes, String WaybillNo) {
        if (notes.length() > 1) {
            DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
            CourierNotesModels courierNotesModels = new CourierNotesModels();
            courierNotesModels.setWaybillNo(Integer.parseInt(WaybillNo));
            courierNotesModels.setDeliverySheetID(dbConnections.GetDeliverysheetIDbyWNo(getApplicationContext(),
                    Integer.parseInt(WaybillNo)));
            courierNotesModels.setTimeIn(new DateTime().toString());
            courierNotesModels.setUserID(GlobalVar.GV().EmployID);
            courierNotesModels.setNotes(notes);
            boolean isnotesaved = dbConnections.insertCourierNotes(getApplicationContext(), courierNotesModels);
            if (isnotesaved) {
                GlobalVar.ShowDialog(RouteHistory.this, "Info", "Your Notes Sucessfully Saved.", true);
                if (!GlobalVar.isMyServiceRunning(CourierNotesService.class, getApplicationContext())) {
                    startService(
                            new Intent(RouteHistory.this,
                                    com.naqelexpress.naqelpointer.service.CourierNotesService.class));
                }
            }
            dbConnections.close();

        } else
            GlobalVar.ShowDialog(RouteHistory.this, "Error", "Please enter Notes.", true);

        atemptedshp();
    }

    private void RedirectWaybillPlanActivity(String WaybillNo) {
        GlobalVar.GV().myRouteShipmentList.clear();
        GlobalVar.GV().myRouteShipmentList.add(SelectData.GetMyRouteShipmentsbyWaybillNo(WaybillNo, getApplicationContext()));
        if (GlobalVar.GV().myRouteShipmentList.size() > 0) {
            int position = 0;
            Intent intent = new Intent(getApplicationContext(), com.naqelexpress.naqelpointer.Activity.Waybill.WaybillPlanActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("ID", String.valueOf(GlobalVar.GV().myRouteShipmentList.get(position).ID));
            bundle.putString("WaybillNo", GlobalVar.GV().myRouteShipmentList.get(position).ItemNo);
            bundle.putDouble("COD", GlobalVar.GV().myRouteShipmentList.get(position).CODAmount);
            bundle.putString("BT", GlobalVar.GV().myRouteShipmentList.get(position).BillingType);
            bundle.putInt("SeqNo", GlobalVar.GV().myRouteShipmentList.get(position).DsOrderNo);
            bundle.putInt("isOtp", GlobalVar.GV().myRouteShipmentList.get(position).isOtp);
            bundle.putInt("position", position);
            bundle.putBoolean("isupdate", GlobalVar.GV().myRouteShipmentList.get(position).isupdate);
            bundle.putInt("dsID", GlobalVar.GV().myRouteShipmentList.get(position).DeliverySheetID);
            bundle.putBoolean("isEnable", GlobalVar.GV().isSeqComplete(getApplicationContext()));

            intent.putExtras(bundle);
            startActivityForResult(intent, 1);

        } else
            GlobalVar.ShowDialog(RouteHistory.this, "Info", "There is no record ,please try after sometime/restart the app.", true);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == RESULT_OK) { //Refresh Data
            Bundle extras = data.getExtras();

            if (extras != null) {
                if (extras.containsKey("result")) {
                    String result = extras.getString("result");
                    if (result.equals("refreshdata"))
                        atemptedshp();


                }
            }
        }
    }
}
