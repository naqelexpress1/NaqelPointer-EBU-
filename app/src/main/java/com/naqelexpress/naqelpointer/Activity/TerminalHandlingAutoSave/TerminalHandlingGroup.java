package com.naqelexpress.naqelpointer.Activity.TerminalHandlingAutoSave;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.naqelexpress.naqelpointer.Activity.NCL.NclShipmentActivity;
import com.naqelexpress.naqelpointer.DB.DBConnections;
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

// Created by Ismail on 21/03/2018.

public class TerminalHandlingGroup extends AppCompatActivity implements View.OnClickListener {

    ArrayList<HashMap<String, String>> data = new ArrayList<>();
    ArrayList<HashMap<String, String>> reason = new ArrayList<>();
    ArrayList<String> city = new ArrayList<>();
    ArrayList<String> operationalcity = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //countDownTimer = new MyCountDownTimer(startTime, interval);

        setContentView(R.layout.terminalgroup);
        ImageView group1 = (ImageView) findViewById(R.id.group1);
        group1.setOnClickListener(this);
        ImageView group2 = (ImageView) findViewById(R.id.group2);
        group2.setOnClickListener(this);
        ImageView group3 = (ImageView) findViewById(R.id.group3);
        group3.setOnClickListener(this);
        ImageView group4 = (ImageView) findViewById(R.id.group4);
        group4.setOnClickListener(this); //Linehaul
        ImageView group5 = (ImageView) findViewById(R.id.group5);
        group5.setOnClickListener(this);
        ImageView ncl = (ImageView) findViewById(R.id.ncl);
        ncl.setOnClickListener(this);

        //ImageView loadtotrip = (ImageView) findViewById(R.id.loadtrotrip);
        //loadtotrip.setOnClickListener(this);

        //ImageView heldin = (ImageView) findViewById(R.id.heldin);
        // heldin.setOnClickListener(this);

        data.clear();
        reason.clear();
        city.clear();

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("StationID", GlobalVar.GV().StationID);
            jsonObject.put("Type", 3); //usertypeid()
            new terminalhandling().execute(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private int usertypeid() {

        int usertype = 0;
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        Cursor result = dbConnections.Fill("select * from UserME where EmployID = " + GlobalVar.GV().EmployID,
                getApplicationContext());

        if (result.getCount() > 0) {
            result.moveToFirst();
            usertype = result.getInt(result.getColumnIndex("UserTypeID"));
        }
        dbConnections.close();
        result.close();
        return usertype;
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
            data = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("data");
            reason = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("reason");

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
        outState.putSerializable("data", data);
        outState.putSerializable("reason", reason);
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
                        TerminalHandlingGroup.super.onBackPressed();
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
                if (!GlobalVar.getDivision(getApplicationContext()).equals("IRS"))
                    fetchgroup("1");
                else
                    Toast.makeText(getApplicationContext(), "Dont have access , kindly contact concern Person", Toast.LENGTH_SHORT).show();
                break;
            case R.id.group2:
                if (!GlobalVar.getDivision(getApplicationContext()).equals("IRS"))
                    fetchgroup("2");
                else
                    Toast.makeText(getApplicationContext(), "Dont have access , kindly contact concern Person", Toast.LENGTH_SHORT).show();
                break;
            case R.id.group3:
                if (!GlobalVar.getDivision(getApplicationContext()).equals("IRS"))
                    fetchgroup("3");
                else
                    Toast.makeText(getApplicationContext(), "Dont have access , kindly contact concern Person", Toast.LENGTH_SHORT).show();
                break;
            case R.id.group4:

                fetchgroup("4");
                break;
            case R.id.group5:
                if (!GlobalVar.getDivision(getApplicationContext()).equals("IRS"))
                    fetchgroup("5");
                else
                    Toast.makeText(getApplicationContext(), "Dont have access , kindly contact concern Person", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ncl:
                Intent intent = new Intent(this, NclShipmentActivity.class);
                startActivity(intent);
                break;
            case R.id.loadtrotrip:

                Intent tripdetails = new Intent(TerminalHandlingGroup.this, BringTripDetails.class);
                startActivity(tripdetails);

                break;
            case R.id.heldin:

                Intent heldin = new Intent(TerminalHandlingGroup.this, InventoryHeldIn.class);
                startActivity(heldin);

                break;


        }
    }

    private void fetchgroup(String group) {


        ArrayList<HashMap<String, String>> fetchdata = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            if (group.equals(data.get(i).get("GroupID"))) {
                fetchdata.add(data.get(i));
            }
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable("status", fetchdata);
        bundle.putSerializable("reason", reason);
        bundle.putStringArrayList("city", city);
        bundle.putStringArrayList("operationalcity", operationalcity);
        bundle.putString("group", "Group " + group);

        if (group.equals("1")) {
            Intent intent = new Intent(this, InventoryGroup.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
//        else if (group.equals("3")) {
//            Intent intent = new Intent(this, com.naqelexpress.naqelpointer.TerminalHandling.SPGroup.class);
//            intent.putExtras(bundle);
//            startActivity(intent);
//        }
        else if (group.equals("4")) {
            Intent intent = new Intent(this, com.naqelexpress.naqelpointer.TerminalHandling.LinehaulGroup.class);
            bundle.putSerializable("status", data);
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, com.naqelexpress.naqelpointer.TerminalHandling.TerminalHandling.class);
            intent.putExtras(bundle);
            startActivity(intent);

        }

    }

    private class terminalhandling extends AsyncTask<String, Integer, String> {
        StringBuffer buffer;

        @Override
        protected void onPreExecute() {

            if (progressDialog == null)
                progressDialog = ProgressDialog.show(TerminalHandlingGroup.this,
                        "Please wait.", "Bringing Terminal Handling Data.", true);
            super.onPreExecute();

        }

        protected String doInBackground(String... params) {

            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {

                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "GetTerminalHandlingData");
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
            } catch (Exception ignored) {
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
            }
            return null;
//


        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute("");
            if (result != null) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (!jsonObject.getBoolean("HasError")) {
                        fetchData(jsonObject);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                System.out.println(result);
            } else {

                LoadDivisionError();
            }
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }

        }
    }

    private void fetchData(JSONObject jsonObject) {

        data.clear();
        this.reason.clear();
        city.clear();


        try {

            JSONArray status = jsonObject.getJSONArray("TerminalStatus");
            JSONArray reason = jsonObject.getJSONArray("TerminalReason");
            JSONArray city = jsonObject.getJSONArray("TerminalCity");
            JSONArray operationcity = jsonObject.getJSONArray("TerminalOperationCity");
            if (status.length() > 0) {
                for (int i = 0; i < status.length(); i++) {
                    JSONObject jsonObject1 = status.getJSONObject(i);
                    HashMap<String, String> temp = new HashMap<>();
                    temp.put("ID", jsonObject1.getString("ID"));
                    temp.put("Name", jsonObject1.getString("Name"));
                    temp.put("GroupID", jsonObject1.getString("GruopID"));
                    data.add(temp);

                }

            }

            if (reason.length() > 0) {
                for (int i = 0; i < reason.length(); i++) {
                    JSONObject jsonObject1 = reason.getJSONObject(i);
                    HashMap<String, String> temp = new HashMap<>();
                    temp.put("ID", jsonObject1.getString("ID"));
                    temp.put("Name", jsonObject1.getString("Name"));
                    temp.put("StatusID", jsonObject1.getString("TerminalHandlingScanStatusId"));
                    if (jsonObject1.getBoolean("IsDateRequired"))
                        temp.put("IsDaterequired", "1");
                    else
                        temp.put("IsDaterequired", "0");
                    if (jsonObject1.getBoolean("CITY"))
                        temp.put("IsCityrequired", "1");
                    else
                        temp.put("IsCityrequired", "0");

                    this.reason.add(temp);

                }

            }

            if (city.length() > 0) {
                for (int i = 0; i < city.length(); i++) {
                    JSONObject jsonObject1 = city.getJSONObject(i);

                    this.city.add(jsonObject1.getString("Name"));
                }

            }
            if (operationcity.length() > 0) {
                for (int i = 0; i < operationcity.length(); i++) {
                    JSONObject jsonObject1 = operationcity.getJSONObject(i);

                    this.operationalcity.add(jsonObject1.getString("Name"));
                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void LoadDivisionError() {
        AlertDialog alertDialog = new AlertDialog.Builder(TerminalHandlingGroup.this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Info.");
        alertDialog.setMessage("Kindly Check your Internet Connection,Scan Inventory press Cancel");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("StationID", GlobalVar.GV().StationID);
                            jsonObject.put("Type", 3); //usertypeid()
                            new terminalhandling().execute(jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

  /*  private long startTime = 30 * 60 * 1000; // 15 MINS IDLE TIME
    private final long interval = 1 * 1000;
    MyCountDownTimer countDownTimer;

    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
            //DO WHATEVER YOU WANT HERE
            DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
            int id = dbConnections.getMaxID(" UserMeLogin where LogoutDate is NULL ", getApplicationContext());
            UserMeLogin userMeLogin = new UserMeLogin(id);
            dbConnections.UpdateUserMeLogout(userMeLogin, getApplicationContext());
            dbConnections.deleteUserME(GlobalVar.GV().EmployID);

            ActivityCompat.finishAffinity(TerminalHandlingGroup.this);
            Intent intent = new Intent(getApplicationContext(), SplashScreenActivity.class);
            startActivity(intent);
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }
    }

    @Override
    public void onUserInteraction() {

        super.onUserInteraction();

        //Reset the timer on user interaction...
        countDownTimer.cancel();
        countDownTimer.start();
    }*/
}