package com.naqelexpress.naqelpointer.TerminalHandlingOLD;

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

import com.naqelexpress.naqelpointer.Activity.Delivery.DataAdapter;
import com.naqelexpress.naqelpointer.Classes.NewBarCodeScanner;
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
import java.util.HashMap;

// Created by Ismail on 21/03/2018.

public class TripArrviedatDest extends AppCompatActivity implements View.OnClickListener {

    ArrayList<HashMap<String, String>> tripdata = new ArrayList<>();
    HashMap<String, String> trips = new HashMap<>();
    TextView lbTotal;
    private EditText txtBarCode;
    public ArrayList<String> ncl = new ArrayList<>();
    public ArrayList<String> isncl = new ArrayList<>();
    private RecyclerView recyclerView;
    private DataAdapter adapter;
    private Paint p = new Paint();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.notdeliveredsecondfragement);


        lbTotal = (TextView) findViewById(R.id.lbTotal);

        txtBarCode = (EditText) findViewById(R.id.txtWaybilll);
        txtBarCode.setHint("Scan TripID");

//        txtBarCode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});
//        txtBarCode.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (txtBarCode != null && txtBarCode.getText().length() == 2)
//                    AddNewPiece();
//            }
//        });

        txtBarCode.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if (event.getAction() != KeyEvent.ACTION_DOWN)
                    return true;
                else if (keyCode == KeyEvent.KEYCODE_BACK) {
                    onBackPressed();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    AddNewPiece();
                    return true;
                }
                return false;
            }
        });
        Button btnOpenCamera = (Button) findViewById(R.id.btnOpenCamera);
        btnOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!GlobalVar.GV().checkPermission(TripArrviedatDest.this, GlobalVar.PermissionType.Camera)) {
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.NeedCameraPermission), GlobalVar.AlertType.Error);
                    GlobalVar.GV().askPermission(TripArrviedatDest.this, GlobalVar.PermissionType.Camera);
                } else {
                    Intent intent = new Intent(TripArrviedatDest.this, NewBarCodeScanner.class);
                    startActivityForResult(intent, GlobalVar.GV().CAMERA_PERMISSION_REQUEST);
                }
            }
        });


        Intent intent = getIntent();
        trips = (HashMap<String, String>) intent.getSerializableExtra("tripdata");

//        try {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("OriginID", trips.get("OriginID"));
//            jsonObject.put("DestinationID", trips.get("DestinationID"));
//            jsonObject.put("TripID", Integer.parseInt(trips.get("ID")));
//            new BringNCLData().execute(jsonObject.toString());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        initViews();

    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DataAdapter(ncl);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        initSwipe();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GlobalVar.GV().CAMERA_PERMISSION_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    if (extras.containsKey("barcode")) {
                        String barcode = extras.getString("barcode");
                        if (barcode.length() == 2)
                            txtBarCode.setText(barcode);

                    }
                }

            }
        }
    }

    private void AddNewPiece() {

//        if (!isncl.contains(txtBarCode.getText().toString())) {
//            ErrorAlert("This Ncl(" + txtBarCode.getText().toString() + ") not in this Trip(" + trips.get("TripCode") + ")");
//            //return;
//            GlobalVar.GV().MakeSound(getApplicationContext(), R.raw.wrongbarcodescan);
//        }

        if (!ncl.contains(txtBarCode.getText().toString())) {
            if (txtBarCode.getText().toString().length() == 2) {
                ncl.add(0, txtBarCode.getText().toString());
                lbTotal.setText(getString(R.string.lbCount) + ncl.size());
                txtBarCode.setText("");
                initViews();
            }
        } else {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.AlreadyExists), GlobalVar.AlertType.Warning);
            GlobalVar.GV().MakeSound(getApplicationContext(), R.raw.wrongbarcodescan);
            txtBarCode.setText("");
        }
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

                if (direction == ItemTouchHelper.LEFT) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TripArrviedatDest.this);
                    builder.setTitle("Confirm Deleting")
                            .setMessage("Are you sure you want to delete?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    adapter.removeItem(position);
                                    lbTotal.setText(getString(R.string.lbCount) + ncl.size());
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
                if (ncl.size() > 0)
                    new SaveTriptoServer().execute("");
                else
                    ShowAlertMessage("Kindly scan at least one.", 1);


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveTripDetails() {

        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            jsonObject.put("OriginID", trips.get("OriginID"));
            jsonObject.put("DestinationID", trips.get("DestinationID"));
            jsonObject.put("TripID", trips.get("ID"));

            for (String ncl : isncl) {
                for (HashMap data : tripdata) {
                    if (ncl.equals(data.get("NclNo"))) {
                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put("NclID", data.get("NCLID"));
                        jsonArray.put(jsonObject1);
                        break;
                    }
                }
            }
            jsonObject.put("TripPlanDetails", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
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
                        TripArrviedatDest.super.onBackPressed();
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
                progressDialog = ProgressDialog.show(TripArrviedatDest.this,
                        "Please wait.", "Bringing NCL data...", true);
            super.onPreExecute();

        }

        protected String doInBackground(String... params) {

            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {

                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "BringNCLbyLineHaul");
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

        tripdata.clear();

        try {

            JSONArray status = jsonObject.getJSONArray("NCLDetails");

            if (status.length() > 0) {
                for (int i = 0; i < status.length(); i++) {
                    JSONObject jsonObject1 = status.getJSONObject(i);
                    HashMap<String, String> temp = new HashMap<>();
                    temp.put("NclID", jsonObject1.getString("NCLID"));
                    temp.put("NclNo", jsonObject1.getString("NclNo"));
                    isncl.add(jsonObject1.getString("NclNo"));
                    tripdata.add(temp);

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void LoadDivisionError() {
        AlertDialog alertDialog = new AlertDialog.Builder(TripArrviedatDest.this).create();
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
                            jsonObject.put("TripID", Integer.parseInt(trips.get("ID")));
                            new BringNCLData().execute(jsonObject.toString());
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

    private void ErrorAlert(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(TripArrviedatDest.this).create();
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

    private class SaveTriptoServer extends AsyncTask<String, Integer, String> {
        StringBuffer buffer;
        String jsonData;

        @Override
        protected void onPreExecute() {

            if (progressDialog == null)
                progressDialog = ProgressDialog.show(TripArrviedatDest.this,
                        "Please wait.", "your request is progress...", true);

            try {
                JSONObject jsonObject = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                jsonObject.put("OriginID", trips.get("OriginID"));
                jsonObject.put("DestinationID", trips.get("DestinationID"));
                jsonObject.put("TripID", trips.get("ID"));
                jsonObject.put("EmployeeID", GlobalVar.GV().EmployID);
                jsonObject.put("OriginID", GlobalVar.GV().StationID);
                jsonObject.put("CloseDateTime", DateTime.now());
                /*for (String ncl : isncl) {
                    for (HashMap data : tripdata) {
                        if (ncl.equals(data.get("NclNo"))) {
                            JSONObject jsonObject1 = new JSONObject();
                            jsonObject1.put("NclID", Integer.parseInt(data.get("NclID").toString()));
                            jsonArray.put(jsonObject1);
                            break;
                        }
                    }
                }*/

                for (String nclno : ncl) {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("NclNo", nclno);
                    jsonArray.put(jsonObject1);

                }

                jsonObject.put("NclNos", jsonArray);
                jsonData = jsonObject.toString().replace("Date(-", "Date(");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            super.onPreExecute();

        }

        protected String doInBackground(String... params) {

//            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {

                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "InsertArrivedatDestkForCBU");
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

                        ShowAlertMessage(jsonObject.getString("ErrorMessage"), 0);
                    } else
                        ShowAlertMessage(jsonObject.getString("ErrorMessage"), 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                System.out.println(result);
            } else {
                ShowAlertMessage(result, 0);
            }

            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }

        }
    }

    private void ShowAlertMessage(String message, final int finish) {
        AlertDialog alertDialog = new AlertDialog.Builder(TripArrviedatDest.this).create();
        alertDialog.setTitle("Info");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (finish == 0)
                            finish();
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }


}