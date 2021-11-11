package com.naqelexpress.naqelpointer.TerminalHandling;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.NCLBlockWaybills.NclShipmentActivity;
import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;
import java.util.HashMap;

//import com.naqelexpress.naqelpointer.NCLBulk.NclShipmentActivity;

// Created by Ismail on 21/03/2018. this package is using

public class LinehaulGroup extends AppCompatActivity implements View.OnClickListener {

    ArrayList<HashMap<String, String>> status = new ArrayList<>();
    ArrayList<HashMap<String, String>> reason = new ArrayList<>();
    ArrayList<String> city = new ArrayList<>();

    String group;
    ArrayList<String> operationalcity = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("test", "Linhul");
        setContentView(R.layout.linehaulgroup);

        //countDownTimer = new MyCountDownTimer(startTime, interval);

        ImageView group3 = (ImageView) findViewById(R.id.group3);
        group3.setOnClickListener(this);

//        this one
//        LinearLayout shipmentarrrivedatdest = (LinearLayout) findViewById(R.id.shipmentarrrivedatdest);
//        shipmentarrrivedatdest.setOnClickListener(this);

        LinearLayout llgroup4 = (LinearLayout) findViewById(R.id.llgroup4);
        llgroup4.setOnClickListener(this); // Transist

        LinearLayout llgroup5 = (LinearLayout) findViewById(R.id.llgroup5);
        llgroup5.setOnClickListener(this); // Transport Delay

        ImageView ncl = (ImageView) findViewById(R.id.ncl);
        ncl.setOnClickListener(this);

        ImageView loadtotrip = (ImageView) findViewById(R.id.loadtrotrip);
        //Button loadtotrip = (Button) findViewById(R.id.loadtrotrip);
        loadtotrip.setOnClickListener(this);
        loadtotrip.setVisibility(View.VISIBLE);

        LinearLayout arrivedatdestlayout = (LinearLayout) findViewById(R.id.arrivedatdestlayout);
        arrivedatdestlayout.setVisibility(View.VISIBLE);

        ImageView undo = (ImageView) findViewById(R.id.undoncl);
        undo.setOnClickListener(this);

        ImageView arrivedatdest = (ImageView) findViewById(R.id.arrivedatdest);
        //Button arrivedatdest = (Button) findViewById(R.id.arrivedatdest);
        arrivedatdest.setVisibility(View.VISIBLE);
        arrivedatdest.setOnClickListener(this);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        status.clear();
        reason.clear();
        city.clear();

        status = (ArrayList<HashMap<String, String>>) bundle.getSerializable("status");
        reason = (ArrayList<HashMap<String, String>>) bundle.getSerializable("reason");
        city = bundle.getStringArrayList("city");
        operationalcity = bundle.getStringArrayList("operationalcity");

        group = bundle.getString("group");
        Log.d("test", "Linhul group " + group);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.checkpointmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.mnuSave:

                return true;
            default:
                return super.onOptionsItemSelected(item);
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
            status = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("status");
            reason = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("reason");
            group = savedInstanceState.getString("group");
            city = savedInstanceState.getStringArrayList("city");
            operationalcity = savedInstanceState.getStringArrayList("operationalcity");

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("EmployID", GlobalVar.GV().EmployID);
        outState.putInt("UserID", GlobalVar.GV().UserID);
        outState.putInt("StationID", GlobalVar.GV().StationID);
        outState.putString("EmployMobileNo", GlobalVar.GV().EmployMobileNo);
        outState.putString("EmployName", GlobalVar.GV().EmployName);
        outState.putString("EmployStation", GlobalVar.GV().EmployStation);
        outState.putSerializable("status", status);
        outState.putSerializable("reason", reason);
        outState.putSerializable("city", city);
        outState.putSerializable("operationalcity", operationalcity);
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit")
                .setMessage("Are you sure you want to exit without saving?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        //countDownTimer.cancel();
                        LinehaulGroup.super.onBackPressed();
                    }
                }).setNegativeButton("Cancel", null).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    ProgressDialog progressDialog;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.group1:

                break;
            case R.id.shipmentarrrivedatdest:
                fetchgroup("8");
                break;
//            case R.id.group3:
//                fetchgroup("4");
//                break;
            case R.id.llgroup4:
                if (!GlobalVar.getDivision(getApplicationContext()).equals("IRS"))
                    fetchgroup("4");
                else
                    Toast.makeText(getApplicationContext(), "Dont have access , kindly contact concern Person", Toast.LENGTH_SHORT).show();
                break;
            case R.id.llgroup5:
                fetchgroup("5");
                break;
            case R.id.ncl:
                if (GlobalVar.getDivision(getApplicationContext()).equals("IRS")) {
                    Intent intent = new Intent(this, NclShipmentActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(this, com.naqelexpress.naqelpointer.NCLBulk.NclShipmentActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.loadtrotrip:
                //if (!GlobalVar.getDivision(getApplicationContext()).equals("IRS")) {
                Intent tripdetails = new Intent(LinehaulGroup.this, BringTripDetails.class);
                tripdetails.putExtra("Function", 0);
                startActivity(tripdetails);
                //  } else
                //      Toast.makeText(getApplicationContext(), "Dont have access , kindly contact concern Person", Toast.LENGTH_SHORT).show();
                break;
            case R.id.heldin:
                if (!GlobalVar.getDivision(getApplicationContext()).equals("IRS")) {
                    Intent heldin = new Intent(LinehaulGroup.this, InventoryHeldIn.class);
                    startActivity(heldin);
                } else
                    Toast.makeText(getApplicationContext(), "Dont have access , kindly contact concern Person", Toast.LENGTH_SHORT).show();
                break;
            case R.id.arrivedatdest:
                Intent arrivedat = new Intent(LinehaulGroup.this, BringTripDetails.class);
                arrivedat.putExtra("Function", 1);
                startActivity(arrivedat);
                break;
            case R.id.undoncl:
                if (!GlobalVar.getDivision(getApplicationContext()).equals("IRS")) {
                    Intent undo = new Intent(LinehaulGroup.this, BringTripDetails.class);
                    undo.putExtra("Function", 2);
                    startActivity(undo);
                } else
                    Toast.makeText(getApplicationContext(), "Dont have access , kindly contact concern Person", Toast.LENGTH_SHORT).show();
                break;

        }
    }

    private void fetchgroup(String group) {

        ArrayList<HashMap<String, String>> fetchdata = new ArrayList<>();

        for (int i = 0; i < status.size(); i++) {
            if (group.equals(status.get(i).get("GroupID"))) {
                fetchdata.add(status.get(i));
            }
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable("status", fetchdata);
        bundle.putSerializable("reason", reason);
        bundle.putStringArrayList("city", city);
        bundle.putStringArrayList("operationalcity", operationalcity);
        bundle.putString("group", "Group " + group);


        Intent intent = new Intent(this, TerminalHandling.class);
        intent.putExtras(bundle);
        startActivity(intent);

    }

//    private long startTime = 30 * 60 * 1000; // 15 MINS IDLE TIME
//    private final long interval = 1 * 1000;
//    MyCountDownTimer countDownTimer;
//
//    public class MyCountDownTimer extends CountDownTimer {
//        public MyCountDownTimer(long startTime, long interval) {
//            super(startTime, interval);
//        }
//
//        @Override
//        public void onFinish() {
//            //DO WHATEVER YOU WANT HERE
//            DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
//            int id = dbConnections.getMaxID(" UserMeLogin where LogoutDate is NULL ", getApplicationContext());
//            UserMeLogin userMeLogin = new UserMeLogin(id);
//            dbConnections.UpdateUserMeLogout(userMeLogin, getApplicationContext());
//            dbConnections.deleteUserME(GlobalVar.GV().EmployID);
//
//            ActivityCompat.finishAffinity(SPGroup.this);
//            Intent intent = new Intent(getApplicationContext(), SplashScreenActivity.class);
//            startActivity(intent);
//        }
//
//        @Override
//        public void onTick(long millisUntilFinished) {
//        }
//    }

    @Override
    public void onUserInteraction() {

        super.onUserInteraction();

        //Reset the timer on user interaction...
        //countDownTimer.cancel();
        //countDownTimer.start();
    }
}