package com.naqelexpress.naqelpointer.TerminalHandling;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

public class UndoNclbyScan extends AppCompatActivity implements View.OnClickListener {

    ArrayList<HashMap<String, String>> tripdata = new ArrayList<>();
    ArrayList<String> nclno = new ArrayList<>();
    HashMap<String, String> trips = new HashMap<>();
    private RecyclerView recyclerView;
    private UndoNclAdapter adapter;
    private Paint p = new Paint();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.notdeliveredsecondfragement);

        final EditText txtWaybilll = (EditText) findViewById(R.id.txtWaybilll);
        txtWaybilll.setVisibility(View.VISIBLE);

        TextView lbTotal = (TextView) findViewById(R.id.lbTotal);
        lbTotal.setVisibility(View.GONE);

        Button btnOpenCamera = (Button) findViewById(R.id.btnOpenCamera);
        btnOpenCamera.setVisibility(View.GONE);


        txtWaybilll.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if (event.getAction() != KeyEvent.ACTION_DOWN)
                    return true;
                else if (keyCode == KeyEvent.KEYCODE_BACK) {
                    onBackPressed();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (getNclPosition(txtWaybilll.getText().toString()) != -1)
                        confrimDelete(getNclPosition(txtWaybilll.getText().toString()));
                    else
                        GlobalVar.ShowDialog(UndoNclbyScan.this, "Warning", "Kindly Scan Correct NCL No ", true);
                    return true;
                }
                return false;
            }
        });
        Intent intent = getIntent();
        trips = (HashMap<String, String>) intent.getSerializableExtra("tripdata");

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("TripID", trips.get("TripID"));

            new BringNCLData().execute(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initViews();

    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new UndoNclAdapter(tripdata);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        //initSwipe();
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT)//| ItemTouchHelper.RIGHT)
        {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();

                if (position == 0) {
                    adapter.notifyDataSetChanged();
                    return;
                }
                if (direction == ItemTouchHelper.LEFT) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UndoNclbyScan.this);
                    builder.setTitle("Confirm")
                            .setMessage("Are you sure you want to Undo NCL from this Trip(" + tripdata.get(position).get("NclNo") + ")?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    try {
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("ID", tripdata.get(position).get("ID"));

                                        new UndoNCLbyTrip().execute(jsonObject.toString(), String.valueOf(position));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    initViews();
                                }
                            })
                            .setCancelable(false);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0) {
                        p.setColor(Color.BLUE);
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_edit_white);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void confrimDelete(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UndoNclbyScan.this);
        builder.setTitle("Confirm")
                .setMessage("Are you sure you want to Undo NCL from this Trip(" + tripdata.get(position).get("NclNo") + ")?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("ID", tripdata.get(position).get("ID"));

                            new UndoNCLbyTrip().execute(jsonObject.toString(), String.valueOf(position));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        initViews();
                    }
                })
                .setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.tripdetails, menu);
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
            tripdata = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("tripdata");

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
        outState.putSerializable("tripdata", tripdata);

    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit")
                .setMessage("Are you sure you want to exit without saving?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        UndoNclbyScan.super.onBackPressed();
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


    private class BringNCLData extends AsyncTask<String, Integer, String> {
        StringBuffer buffer;

        @Override
        protected void onPreExecute() {

            if (progressDialog == null)
                progressDialog = ProgressDialog.show(UndoNclbyScan.this,
                        "Please wait.", "Bringing NCL data...", true);
            super.onPreExecute();

        }

        protected String doInBackground(String... params) {

            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {

                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "UndoNCLDetailsbyTripID");
                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpURLConnection.setReadTimeout(GlobalVar.GV().ConnandReadtimeout50000);
                httpURLConnection.setConnectTimeout(GlobalVar.GV().ConnandReadtimeout50000);
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

        tripdata.clear();

        try {

            JSONArray status = jsonObject.getJSONArray("Ncl");

            if (status.length() > 0) {

                HashMap<String, String> dummy = new HashMap<>();
                dummy.put("NclID", "0");
                dummy.put("NclNo", trips.get("TripCode"));
                dummy.put("ID", "0");
                tripdata.add(dummy);
                nclno.add("00ismail00");


                for (int i = 0; i < status.length(); i++) {
                    JSONObject jsonObject1 = status.getJSONObject(i);
                    HashMap<String, String> temp = new HashMap<>();
                    temp.put("NclID", String.valueOf(jsonObject1.getInt("NclID")));
                    temp.put("NclNo", jsonObject1.getString("NclNo"));
                    temp.put("ID", jsonObject1.getString("ID"));
                    tripdata.add(temp);
                    nclno.add(jsonObject1.getString("NclNo"));

                }
            } else
                ShowAlertMessage("No Data Belongs to this trip (" + trips.get("TripCode") + ")", 1);
            adapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private int getNclPosition(String ncl) {
        return nclno.indexOf(ncl);
    }

    private void LoadDivisionError() {
        AlertDialog alertDialog = new AlertDialog.Builder(UndoNclbyScan.this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Info.");
        alertDialog.setMessage("Kindly Check your Internet Connection,please try again");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("OriginID", trips.get("OriginID"));
                            jsonObject.put("DestinationID", trips.get("DestinationID"));
                            new BringNCLData().execute(jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Close",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void ErrorAlert(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(UndoNclbyScan.this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Error");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }


    private void ShowAlertMessage(String message, final int finish) {
        AlertDialog alertDialog = new AlertDialog.Builder(UndoNclbyScan.this).create();
        alertDialog.setTitle("Info");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (finish == 1)
                            finish();
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void ConfirmationtoCompleteLoad() {
        AlertDialog alertDialog = new AlertDialog.Builder(UndoNclbyScan.this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Confirmation");
        alertDialog.setMessage("Do you want to Close the Truck?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("TripID", trips.get("TripID"));

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

    private class UndoNCLbyTrip extends AsyncTask<String, Integer, String> {
        StringBuffer buffer;
        int position = 0;

        @Override
        protected void onPreExecute() {

            if (progressDialog == null)
                progressDialog = ProgressDialog.show(UndoNclbyScan.this,
                        "Please wait.", "Undo NCL data...", true);
            super.onPreExecute();

        }

        protected String doInBackground(String... params) {

            String jsonData = params[0];
            position = Integer.parseInt(params[1]);

            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {

                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "UndoNclfromTrip");
                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpURLConnection.setConnectTimeout(GlobalVar.GV().ConnandReadtimeout50000);
                httpURLConnection.setConnectTimeout(GlobalVar.GV().ConnandReadtimeout50000);
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
                        adapter.removeItem(position);

                        if (tripdata.size() == 1)

                            finish();

                    } else
                        ShowAlertMessage(jsonObject.getString("ErrorMessage"), 0);

                } catch (JSONException e) {
                    e.printStackTrace();
                    ShowAlertMessage(e.toString(), 0);
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
}