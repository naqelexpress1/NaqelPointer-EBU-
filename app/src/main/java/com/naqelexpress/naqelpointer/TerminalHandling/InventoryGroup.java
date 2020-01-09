package com.naqelexpress.naqelpointer.TerminalHandling;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.naqelexpress.naqelpointer.Activity.NCL.NclShipmentActivity;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;
import java.util.HashMap;

// Created by Ismail on 21/03/2018.

public class InventoryGroup extends AppCompatActivity implements View.OnClickListener {

    ArrayList<HashMap<String, String>> status = new ArrayList<>();
    ArrayList<HashMap<String, String>> reason = new ArrayList<>();
    ArrayList<String> city = new ArrayList<>();
    ArrayList<String> operationalcity = new ArrayList<>();
    String group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.inventorygroup);

        // countDownTimer = new MyCountDownTimer(startTime, interval);

        ImageView group1 = (ImageView) findViewById(R.id.group1);
        group1.setOnClickListener(this);


        ImageView heldin = (ImageView) findViewById(R.id.heldin);
        //Button heldin = (Button) findViewById(R.id.heldin);
        heldin.setOnClickListener(this);


        ImageView heldout = (ImageView) findViewById(R.id.heldout);
        heldout.setOnClickListener(this);

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
                        // countDownTimer.cancel();
                        InventoryGroup.super.onBackPressed();
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
                fetchgroup("1");

                break;
            case R.id.group2:
//                fetchgroup("2");
                break;
            case R.id.group3:
//                fetchgroup("3");
                break;
            case R.id.group4:
//                fetchgroup("4");
                break;
            case R.id.group5:
//                fetchgroup("5");
                break;
            case R.id.ncl:
                Intent intent = new Intent(this, NclShipmentActivity.class);
                startActivity(intent);
                break;
            case R.id.loadtrotrip:

                Intent tripdetails = new Intent(InventoryGroup.this, BringTripDetails.class);
                startActivity(tripdetails);

                break;
            case R.id.heldin:

                Intent heldin = new Intent(InventoryGroup.this, InventoryHeldIn.class);
                startActivity(heldin);

                break;
            case R.id.heldout:

                Intent heldout = new Intent(InventoryGroup.this, InventoryHeldOut.class);
                startActivity(heldout);

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


        // Intent intent = new Intent(this, InventoryControlOnetab.class);
       // Intent intent = new Intent(this, InventoryControl_LocalValidation.class);
        Intent intent = new Intent(this, InventoryControl_LocalValidation.class);
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
//            ActivityCompat.finishAffinity(InventoryGroup.this);
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