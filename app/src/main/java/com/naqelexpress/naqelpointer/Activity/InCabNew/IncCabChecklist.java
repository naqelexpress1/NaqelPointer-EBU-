package com.naqelexpress.naqelpointer.Activity.InCabNew;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.naqelexpress.naqelpointer.Activity.Login.FindVehicle;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.FindVehilceObject;
import com.naqelexpress.naqelpointer.GlobalVar;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

// Created by Ismail on 19/02/2019.

public class IncCabChecklist extends AppCompatActivity {

    private InCabAdapter adapter;
    private RecyclerView recyclerView;
    ArrayList<HashMap<String, String>> reason = new ArrayList<>();
    public static HashSet<Integer> selectedreason = new HashSet<>();
    public static HashSet<Integer> header = new HashSet<>();
    EditText speedorating, fleetno;
    int close = 0;
    int truckID = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.incabnew);

        close = getIntent().getIntExtra("close", 0);
        speedorating = (EditText) findViewById(R.id.speedorating);
        fleetno = (EditText) findViewById(R.id.fleetNo);
        recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);

        final ImageView image = (ImageView) findViewById(R.id.image1);
        final ImageView image1 = (ImageView) findViewById(R.id.image2);
        final ImageView image2 = (ImageView) findViewById(R.id.image3);
        final ImageView image3 = (ImageView) findViewById(R.id.image4);

        //header.clear();
        //selectedreason.clear();

        fleetno.setKeyListener(null);
        ReadFromLocal();
        fleetno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vehicles.size() > 0)
                    RedirectVechicleClass();
            }
        });


        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable highlight = getResources().getDrawable(R.drawable.highlight);

                if (selectedreason.contains(34)) {
                    selectedreason.remove(new Integer(34));
                    image.setBackground(null);
                } else {
                    selectedreason.add(34);
                    image.setBackground(highlight);
                }
            }
        });


        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable highlight = getResources().getDrawable(R.drawable.highlight);

                if (selectedreason.contains(35)) {
                    selectedreason.remove(new Integer(35));
                    image1.setBackground(null);
                } else {
                    selectedreason.add(35);
                    image1.setBackground(highlight);
                }
            }
        });

        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable highlight = getResources().getDrawable(R.drawable.highlight);

                if (selectedreason.contains(36)) {
                    selectedreason.remove(new Integer(36));
                    image2.setBackground(null);
                } else {
                    selectedreason.add(36);
                    image2.setBackground(highlight);
                }
            }
        });

        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable highlight = getResources().getDrawable(R.drawable.highlight);

                if (selectedreason.contains(37)) {
                    selectedreason.remove(new Integer(37));
                    image3.setBackground(null);
                } else {
                    selectedreason.add(37);
                    image3.setBackground(highlight);
                }
            }
        });

        initViews();

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("OriginID", 0);
            jsonObject.put("DestinationID", GlobalVar.GV().StationID);
            new BringInCabcheckListData().execute(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.notdeliveredmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    ArrayList<FindVehilceObject> vehicles;

    private void RedirectVechicleClass() {
        Intent intent = new Intent(this, FindVehicle.class);
        intent.putExtra("Vehicles", vehicles);
        startActivityForResult(intent, 99);
    }

    private void ReadFromLocal() {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        Cursor result = dbConnections.Fill("select * from Truck", getApplicationContext());

        result.moveToFirst();
        vehicles = new ArrayList<FindVehilceObject>();
        vehicles.clear();
        setDefault();
        try {
            if (result.getCount() > 0) {
                result.moveToFirst();
                do {
                    FindVehilceObject fvo = new FindVehilceObject();
                    fvo.ID = result.getInt(result.getColumnIndex("TruckID"));
                    fvo.Name = result.getString(result.getColumnIndex("Name"));
                    vehicles.add(fvo);
                } while (result.moveToNext());
            } else
                GlobalVar.GV().Logout(getApplicationContext());

            result.close();
            dbConnections.close();
            // RedirectVechicleClass();


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void setDefault() {
        FindVehilceObject fvo = new FindVehilceObject();
        fvo.ID = 0;
        fvo.Name = "At Yard";

        vehicles.add(fvo);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.mnuSave:
                if (!isValid())
                    return false;

                if (GlobalVar.ValidateAutomacticDate(getApplicationContext())) {
                    List<Integer> list = new ArrayList<Integer>(selectedreason);
                    Collections.sort(list);

//                    String ids = "";
//                    for (int i = 0; i < list.size(); i++) {
//                        if (i == 0)
//                            ids = list.get(i).toString();
//                        else
//                            ids = ids + "," + list.get(i).toString();
//                    }
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("ReasonID", list.toString().replace("]", "").replace("[", ""));
                        jsonObject.put("EmployID", GlobalVar.GV().EmployID);
                        jsonObject.put("Date", DateTime.now());
                        jsonObject.put("speedorating", speedorating.getText().toString());
                        jsonObject.put("fleetno", fleetno.getText().toString());
                        new InsertInCabcheckListData().execute(jsonObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else
                    GlobalVar.RedirectSettings(IncCabChecklist.this);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private boolean isValid() {

        if (speedorating.getText().toString().replace(" ", "").length() > 0)
            if (selectedreason.size() > 0)
                return true;
            else
                ShowAlertError("Info", "Kindly select at leaset one Damage Reason", 0);
        else
            ShowAlertError("Info", "Kindly enter the Speedo Rating value", 0);
        return false;

    }

    private void initViews() {

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new InCabAdapter(reason, IncCabChecklist.this, header);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    ProgressDialog progressDialog;

    private class BringInCabcheckListData extends AsyncTask<String, Integer, String> {
        StringBuffer buffer;

        @Override
        protected void onPreExecute() {

            if (progressDialog == null)
                progressDialog = ProgressDialog.show(IncCabChecklist.this,
                        "Please wait.", "Bringing InCabChecklist data...", true);
            super.onPreExecute();

        }

        protected String doInBackground(String... params) {

            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {

                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "BringInCabCheckListReasonsWithHeader");
                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpURLConnection.setConnectTimeout(60000);
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

    private class InsertInCabcheckListData extends AsyncTask<String, Integer, String> {
        StringBuffer buffer;

        @Override
        protected void onPreExecute() {

            if (progressDialog == null)
                progressDialog = ProgressDialog.show(IncCabChecklist.this,
                        "Please wait.", "your request is being process...", true);
            super.onPreExecute();

        }

        protected String doInBackground(String... params) {

            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {

                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "InsertIncabList");
                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpURLConnection.setConnectTimeout(60000);
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
                        // if (close == 1)
                        ShowAlertError("Info", "your request is sucessfully inserted", 1);
                        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                        dbConnections.UpdateInCabChecklist(getApplicationContext(), GlobalVar.GV().EmployID);
                        // Intent returnIntent = new Intent();
                        // returnIntent.putExtra("result", "done");
                        // setResult(Activity.RESULT_OK, returnIntent);

                    } else
                        ShowAlertError("Error", jsonObject.getString("ErrorMessage"), 0);

                } catch (JSONException e) {
                    e.printStackTrace();
                    ShowAlertError("Error", e.toString(), 0);
                }

                System.out.println(result);
            } else {

                ShowAlertError("Error", "No Internet / Server busy / Something went wrong , kindly try again", 0);
            }
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }

        }

    }

    private void fetchData(JSONObject jsonObject) {

        try {

            JSONArray rsn = jsonObject.getJSONArray("Reasons");
            int addheader = jsonObject.getInt("Header");

            header.clear();
            String preheader = "";
            if (rsn.length() > 0) {

                int j = 0;
                for (int i = 0; i < rsn.length(); i++) {
                    JSONObject jsonObject1 = rsn.getJSONObject(i);

                    if (!preheader.equals(jsonObject1.getString("Header"))) {

                        header.add(i + j);
                        preheader = jsonObject1.getString("Header");

                        HashMap<String, String> temp = new HashMap<>();
                        temp.put("Header", jsonObject1.getString("Header"));
                        temp.put("Name", jsonObject1.getString("Header"));
                        temp.put("ID", "0");
                        temp.put("ischecked", "0");
                        reason.add(temp);
                        j++;

                    }

                    HashMap<String, String> temp = new HashMap<>();
                    temp.put("Header", jsonObject1.getString("Header"));
                    temp.put("Name", jsonObject1.getString("Name"));
                    temp.put("ID", jsonObject1.getString("ID"));
                    temp.put("ischecked", "0");


                    reason.add(temp);
                }
            }

            adapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void LoadDivisionError() {
        AlertDialog alertDialog = new AlertDialog.Builder(IncCabChecklist.this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Info.");
        alertDialog.setMessage("Kindly Check your Internet Connection,please try again");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("OriginID", 0);
                            jsonObject.put("DestinationID", GlobalVar.GV().StationID);
                            new BringInCabcheckListData().execute(jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                });
//        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Close",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
        alertDialog.show();
    }

    private void ShowAlertError(String title, String message, final int close) {
        AlertDialog alertDialog = new AlertDialog.Builder(IncCabChecklist.this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (close == 1)
                            finish();
                        dialog.dismiss();

                    }
                });
//        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Close",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
        alertDialog.show();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            GlobalVar.GV().EmployID = savedInstanceState.getInt("EmployID");
            GlobalVar.GV().UserID = savedInstanceState.getInt("UserID");
            GlobalVar.GV().StationID = savedInstanceState.getInt("StationID");
            GlobalVar.GV().EmployMobileNo = savedInstanceState.getString("EmployMobileNo");
            GlobalVar.GV().EmployName = savedInstanceState.getString("EmployName");
            GlobalVar.GV().EmployStation = savedInstanceState.getString("EmployStation");
            GlobalVar.GV().currentSettings = savedInstanceState.getParcelable("currentSettings");
            // GlobalVar.GV().currentSettings.ID = savedInstanceState.getInt("currentSettingsID");

        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("EmployID", GlobalVar.GV().EmployID);
        outState.putInt("UserID", GlobalVar.GV().UserID);
        outState.putInt("StationID", GlobalVar.GV().StationID);
        outState.putString("EmployMobileNo", GlobalVar.GV().EmployMobileNo);
        outState.putString("EmployName", GlobalVar.GV().EmployName);
        outState.putString("EmployStation", GlobalVar.GV().EmployStation);
        outState.putParcelable("currentSettings", GlobalVar.GV().currentSettings);
//        outState.putInt("currentSettingsID", GlobalVar.GV().currentSettings.ID);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 99: {
                if (resultCode == Activity.RESULT_OK) {

                    fleetno.setText(data.getStringExtra("name"));
                    truckID = data.getIntExtra("truckid", 0);
                }
                break;
            }
        }
    }
}