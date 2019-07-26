package com.naqelexpress.naqelpointer.TerminalHandling;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.Activity.Delivery.DataAdapter;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import org.joda.time.DateTime;
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

public class CloseTruck extends AppCompatActivity implements View.OnClickListener {

    ArrayList<HashMap<String, String>> tripdata = new ArrayList<>();
    HashMap<String, String> trips = new HashMap<>();
    TextView lbTotal;
    private EditText txtBarCode;
    public ArrayList<String> ncl = new ArrayList<>();
    public ArrayList<String> isncl = new ArrayList<>();
    private RecyclerView recyclerView;
    private DataAdapter adapter;
    private Paint p = new Paint();

    EditText seal1, seal2, seal3, seal4, seal5, seal6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tripplandetails);


        Intent intent = getIntent();
        trips = (HashMap<String, String>) intent.getSerializableExtra("tripdata");

        seal1 = (EditText) findViewById(R.id.seal1);
        seal2 = (EditText) findViewById(R.id.seal2);
        seal3 = (EditText) findViewById(R.id.seal3);
        seal4 = (EditText) findViewById(R.id.seal4);
        seal5 = (EditText) findViewById(R.id.seal5);
        seal6 = (EditText) findViewById(R.id.seal6);
        TextView etd = (TextView) findViewById(R.id.etd);

        etd.setText(trips.get("ETA"));

        Button close = (Button) findViewById(R.id.trilerclose);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmationtoCompleteLoad();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tripdetails, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.mnuSave:
                ConfirmationtoCompleteLoad();

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
            trips = (HashMap<String, String>) savedInstanceState.getSerializable("trips");

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
        outState.putSerializable("trips", trips);

    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit")
                .setMessage("Are you sure you want to exit without saving?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        CloseTruck.super.onBackPressed();
                    }
                }).setNegativeButton("Cancel", null).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    ProgressDialog progressDialog;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {


        }
    }


    private void ShowAlertMessage(String message, final int finish) {
        AlertDialog alertDialog = new AlertDialog.Builder(CloseTruck.this).create();
        alertDialog.setTitle("Info");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (finish == 1) {

                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("result", "done");
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        }
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void ConfirmationtoCompleteLoad() {
        AlertDialog alertDialog = new AlertDialog.Builder(CloseTruck.this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Confirmation");
        alertDialog.setMessage("Do you want to Close the Truck?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("TripID", trips.get("TripID"));
                            jsonObject.put("left", seal1.getText().toString());
                            jsonObject.put("right", seal2.getText().toString());
                            jsonObject.put("rr", seal3.getText().toString());
                            jsonObject.put("rl", seal4.getText().toString());
                            jsonObject.put("br", seal5.getText().toString());
                            jsonObject.put("bl", seal6.getText().toString());
                            jsonObject.put("EmployeeID", GlobalVar.GV().EmployID);
                            jsonObject.put("CloseDateTime", DateTime.now());
                            new truckclose().execute(jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private class truckclose extends AsyncTask<String, Integer, String> {
        StringBuffer buffer;

        @Override
        protected void onPreExecute() {

            if (progressDialog == null)
                progressDialog = ProgressDialog.show(CloseTruck.this,
                        "Please wait.", "Your request is process...", true);
            super.onPreExecute();

        }

        protected String doInBackground(String... params) {

            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {

                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "CloseLoadtoTruck");
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
                        ShowAlertMessage(jsonObject.getString("ErrorMessage"), 1);

                    } else
                        ShowAlertMessage(jsonObject.getString("ErrorMessage"), 0);

                } catch (JSONException e) {
                    e.printStackTrace();
                    ShowAlertMessage(e.toString(), 0);
                }

                System.out.println(result);
            } else
                ShowAlertMessage("something went wrong,kindly try again...", 0);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }

        }
    }
}