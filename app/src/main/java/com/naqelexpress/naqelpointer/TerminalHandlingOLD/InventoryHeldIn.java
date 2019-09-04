package com.naqelexpress.naqelpointer.TerminalHandlingOLD;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.Activity.Delivery.DataAdapter;
import com.naqelexpress.naqelpointer.Classes.NewBarCodeScanner;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.CheckPointBarCodeDetails;
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

public class InventoryHeldIn extends AppCompatActivity implements View.OnClickListener {


    ArrayList<HashMap<String, String>> delrtoreq = new ArrayList<>();

    HashMap<String, String> trips = new HashMap<>();
    TextView lbTotal;
    private EditText txtBarCode, txtbinlocation;
    public ArrayList<String> inventorycontrol = new ArrayList<>();
    public ArrayList<String> isdeliveryReq = new ArrayList<>();
    public ArrayList<String> isrtoReq = new ArrayList<>();
    public ArrayList<String> isHeldout = new ArrayList<>();

    private RecyclerView recyclerView;
    private DataAdapter adapter;
    private Paint p = new Paint();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.inventory);


        //countDownTimer = new MyCountDownTimer(startTime, interval);

        lbTotal = (TextView) findViewById(R.id.lbTotal);
        lbTotal.setText("");
        txtBarCode = (EditText) findViewById(R.id.txtWaybilll);
        txtBarCode.setKeyListener(null);
        txtBarCode.setHint("Bin / Piece Barcode");
        txtBarCode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(25)});
        txtBarCode.setInputType(InputType.TYPE_CLASS_TEXT);
        txtbinlocation = (EditText) findViewById(R.id.txtbinlocation);
        txtbinlocation.setKeyListener(null);
        txtbinlocation.setVisibility(View.VISIBLE);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        isDeviceonline();

//        txtBarCode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(13)});
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
//                if (txtBarCode != null && txtBarCode.getText().length() == 13)
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
                if (!GlobalVar.GV().checkPermission(InventoryHeldIn.this, GlobalVar.PermissionType.Camera)) {
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.NeedCameraPermission), GlobalVar.AlertType.Error);
                    GlobalVar.GV().askPermission(InventoryHeldIn.this, GlobalVar.PermissionType.Camera);
                } else {
                    Intent intent = new Intent(InventoryHeldIn.this, NewBarCodeScanner.class);
                    startActivityForResult(intent, GlobalVar.GV().CAMERA_PERMISSION_REQUEST);
                }
            }
        });


        Intent intent = getIntent();
        trips = (HashMap<String, String>) intent.getSerializableExtra("tripdata");

        Button bringdata = (Button) findViewById(R.id.bringdata);
        bringdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("OriginID", 0);
                    jsonObject.put("DestinationID", GlobalVar.GV().StationID);
                    new BringNCLData().execute(jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

//        try {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("OriginID", 0);
//            jsonObject.put("DestinationID", GlobalVar.GV().StationID);
//            new BringNCLData().execute(jsonObject.toString());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        initViews();
        // refreshData();

    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DataAdapter(inventorycontrol);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        // initSwipe();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GlobalVar.GV().CAMERA_PERMISSION_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    if (extras.containsKey("barcode")) {
                        String barcode = extras.getString("barcode");
                        if (barcode.length() == 13)
                            txtBarCode.setText(barcode);

                    }
                }

            }
        }
    }

    private void AddNewPiece() {
//        if (inventorycontrol.size() == 50) {
//            ErrorAlert("Info", "Kindly save the Data and Scan Again");
//            return;
//        }


        if (txtBarCode.getText().toString().toUpperCase().matches(".*[ABCDEFGH].*")) {
            lbTotal.setText(txtBarCode.getText().toString());
            txtBarCode.setText("");
            txtBarCode.requestFocus();
            inventorycontrol.clear();
            initViews();
            return;
        }


        try {
            double convert = Double.parseDouble(txtBarCode.getText().toString());
        } catch (Exception e) {
            GlobalVar.GV().MakeSound(getApplicationContext(), R.raw.wrongbarcodescan);
            ErrorAlert("Error",
                    "Incorrect Piece Barcode(" + txtBarCode.getText().toString() + ")"
            );
            txtBarCode.setText("");
            txtBarCode.requestFocus();
            return;
        }

        if (lbTotal.getText().toString().replace(" ", "").length() == 0) {
            GlobalVar.hideKeyboardFrom(getApplicationContext(), getWindow().getDecorView().getRootView());
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to scan Bin Location",
                    GlobalVar.AlertType.Error);
            GlobalVar.GV().MakeSound(getApplicationContext(), R.raw.wrongbarcodescan);
            txtBarCode.setText("");
            txtBarCode.requestFocus();
            return;
        }

        boolean rtoreq = false;
        if (isdeliveryReq.contains(txtBarCode.getText().toString())) {
            if (isrtoReq.contains(txtBarCode.getText().toString())) {
                rtoreq = true;


                if (!isHeldout.contains(txtBarCode.getText().toString())) {
                    isHeldout.add(txtBarCode.getText().toString());
                    HashMap<String, String> temp = new HashMap<>();
                    temp.put("WayBillNo", txtBarCode.getText().toString());
                    temp.put("Status", "44");
                    temp.put("Ref", "Request For Delivery & RTO");
                    delrtoreq.add(temp);
                    inventorycontrol.add(txtBarCode.getText().toString());

                    initViews();
                    GlobalVar.GV().MakeSound(getApplicationContext(), R.raw.delivery);
                } else
                    GlobalVar.GV().MakeSound(getApplicationContext(), R.raw.delivery);
                ErrorAlert("Delivery/RTO Request", "This Waybill Number(" + txtBarCode.getText().toString() + ") is Request For Delivery & RTO ", 0, txtBarCode.getText().toString());

            } else {

                if (!isHeldout.contains(txtBarCode.getText().toString())) {
                    isHeldout.add(txtBarCode.getText().toString());
                    HashMap<String, String> temp = new HashMap<>();
                    temp.put("WayBillNo", txtBarCode.getText().toString());
                    temp.put("Status", "44");
                    temp.put("Ref", "Request For Delivery");
                    delrtoreq.add(temp);
                    inventorycontrol.add(txtBarCode.getText().toString());
                    initViews();

                    GlobalVar.GV().MakeSound(getApplicationContext(), R.raw.delivery);
                } else
                    GlobalVar.GV().MakeSound(getApplicationContext(), R.raw.delivery);
                ErrorAlert("Delivery Request", "This Waybill Number(" + txtBarCode.getText().toString() + ") is Request For Delivery ", 0, txtBarCode.getText().toString());
            }

            return;
        }

        if (!rtoreq) {
            if (isrtoReq.contains(txtBarCode.getText().toString())) {

                if (!isHeldout.contains(txtBarCode.getText().toString())) {
                    isHeldout.add(txtBarCode.getText().toString());
                    HashMap<String, String> temp = new HashMap<>();
                    temp.put("WayBillNo", txtBarCode.getText().toString());
                    temp.put("Status", "44");
                    temp.put("Ref", "Request For RTO");
                    delrtoreq.add(temp);
                    inventorycontrol.add(txtBarCode.getText().toString());
                    initViews();

                    GlobalVar.GV().MakeSound(getApplicationContext(), R.raw.rto);
                } else
                    GlobalVar.GV().MakeSound(getApplicationContext(), R.raw.rto);
                ErrorAlert("RTO Request", "This Waybill Number(" + txtBarCode.getText().toString() + ") is Request For RTO ", 0, txtBarCode.getText().toString());
                return;
            }
        }

        if (!inventorycontrol.contains(txtBarCode.getText().toString())) {
            if (txtBarCode.getText().toString().length() == 13) {
                // SaveData(txtBarCode.getText().toString());

                HashMap<String, String> temp = new HashMap<>();
                temp.put("WayBillNo", txtBarCode.getText().toString());
                temp.put("Status", "0");
                temp.put("Ref", lbTotal.getText().toString());
                delrtoreq.add(temp);

                inventorycontrol.add(0, txtBarCode.getText().toString());
//                lbTotal.setText(getString(R.string.lbCount) + inventorycontrol.size());
                txtBarCode.setText("");
                txtBarCode.requestFocus();
                initViews();
//                if (inventorycontrol.size() > 5)
//                    inventorycontrol.remove(5);
            }
        } else {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.AlreadyExists), GlobalVar.AlertType.Warning);
            GlobalVar.GV().MakeSound(getApplicationContext(), R.raw.wrongbarcodescan);
            txtBarCode.setText("");
            txtBarCode.requestFocus();
        }

        if (delrtoreq.size() == 20) {
            SaveData(2);
        }

    }

  /*  private void initSwipe() {
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(InventoryHeldIn.this);
                    builder.setTitle("Confirm Deleting")
                            .setMessage("Are you sure you want to delete?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    adapter.removeItem(position);
                                   // lbTotal.setText(getString(R.string.lbCount) + inventorycontrol.size());
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
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.terminalmanu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.mnuSave:
                if (GlobalVar.ValidateAutomacticDate(getApplicationContext())) {
                    ErrorAlert("Info", "Are yo sure want to Finish the Job?", 2, "");
                } else
                    GlobalVar.RedirectSettings(InventoryHeldIn.this);
                return true;
            case R.id.manual:
                if (GlobalVar.ValidateAutomacticDate(getApplicationContext())) {
                    ErrorAlert("Info", "Are yo sure want to upload Manual?", 3, "");
                } else
                    GlobalVar.RedirectSettings(InventoryHeldIn.this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public double Latitude = 0;
    public double Longitude = 0;

    private void requestLocation() {
        Location location = GlobalVar.getLastKnownLocation(getApplicationContext());
        if (location != null) {
            Latitude = location.getLatitude();
            Longitude = location.getLongitude();
        }
    }

  /*  private void SaveData() {

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        if (IsValid()) {
            requestLocation();
            boolean IsSaved = true;

            com.naqelexpress.naqelpointer.DB.DBObjects.TerminalHandling checkPoint = new com.naqelexpress.naqelpointer.DB.DBObjects.TerminalHandling
                    (20, String.valueOf(Latitude),
                            String.valueOf(Longitude), 43, txtbinlocation.getText().toString()
                            , "");

            if (dbConnections.InsertTerminalHandling(checkPoint, getApplicationContext())) {
                int ID = dbConnections.getMaxID("CheckPoint", getApplicationContext());

//                for (int i = 0; i < inventorycontrol.size(); i++) {
//                    CheckPointWaybillDetails waybills = new CheckPointWaybillDetails(inventorycontrol.get(i), ID);
//                    if (!dbConnections.InsertCheckPointWaybillDetails(waybills, getApplicationContext())) {
//                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving),
//                                GlobalVar.AlertType.Error);
//                        IsSaved = false;
//                        break;
//                    }
//                }

                for (int i = 0; i < inventorycontrol.size(); i++) {
                    CheckPointBarCodeDetails checkPointBarCodeDetails = new CheckPointBarCodeDetails(inventorycontrol.get(i), ID);
                    if (!dbConnections.InsertCheckPointBarCodeDetails(checkPointBarCodeDetails, getApplicationContext())) {
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving),
                                GlobalVar.AlertType.Error);
                        IsSaved = false;
                        break;
                    }
                }

                if (delrtoreq.size() > 0)
                    SaveHeldOutData(1);

//                for (int i = 0; i < thirdFragment.BarCodeList.size(); i++) {
//                    CheckPointBarCodeDetails checkPointBarCodeDetails = new CheckPointBarCodeDetails(thirdFragment.BarCodeList.get(i), ID);
//                    if (!dbConnections.InsertCheckPointBarCodeDetails(checkPointBarCodeDetails, getApplicationContext())) {
//                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving),
//                                GlobalVar.AlertType.Error);
//                        IsSaved = false;
//                        break;
//                    }
//                }


                if (IsSaved) {
                    stopService(
                            new Intent(InventoryHeldIn.this,
                                    com.naqelexpress.naqelpointer.service.TerminalHandling.class));

                    if (!isMyServiceRunning(TerminalHandling.class)) {
                        startService(
                                new Intent(InventoryHeldIn.this,
                                        com.naqelexpress.naqelpointer.service.TerminalHandling.class));
                    }
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.SaveSuccessfully), GlobalVar.AlertType.Info);

                    resetAllData();

                } else
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.NotSaved),
                            GlobalVar.AlertType.Error);
            } else
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving),
                        GlobalVar.AlertType.Error);
        }
        dbConnections.close();
    }*/

    private void SaveData(int clear) { //43 heldin , 44 heldout

        if (delrtoreq.size() == 0) {
            ErrorAlert("Info",
                    "Kindly Scan atleast one Piece Barcode"
            );
            return;
        }

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        if (IsValid()) {
            requestLocation();

            JSONArray jsonArray = new JSONArray();
            JSONObject data = new JSONObject();

            for (int i = 0; i < delrtoreq.size(); i++) {

                JSONObject jsonObject = new JSONObject();
                HashMap<String, String> temp = delrtoreq.get(i);


                int ID = dbConnections.getMaxID("TerminalHandling", getApplicationContext());
                try {
                    jsonObject.put("ID", ID + 1);
                    jsonObject.put("BarCode", temp.get("WayBillNo"));
                    jsonObject.put("IsSync", false);
                    jsonObject.put("EmployID", GlobalVar.GV().EmployID);
                    jsonObject.put("Date", DateTime.now());
                    jsonObject.put("TerminalHandlingScanStatusID", 20);
                    jsonObject.put("TerminalHandlingScanStatusReasonID", Integer.parseInt(temp.get("Status")));
                    jsonObject.put("AppVersion", GlobalVar.GV().AppVersion);
                    jsonObject.put("Latitude", String.valueOf(Latitude));
                    jsonObject.put("Longitude", String.valueOf(Longitude));
                    jsonObject.put("StatusID", 0);
                    jsonObject.put("UserID", GlobalVar.GV().UserID);
                    jsonObject.put("stationID", GlobalVar.GV().StationID);
                    jsonObject.put("Reference", temp.get("Ref"));

                    jsonArray.put(jsonObject);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            try {
                data.put("TerminalHandlingBarCodeDetails", jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            dbConnections.InsertTerminalHandlingBulk(data.toString(), getApplicationContext(), delrtoreq.size());


            if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.TerminalHandlingBulk.class)) {
                startService(
                        new Intent(InventoryHeldIn.this,
                                com.naqelexpress.naqelpointer.service.TerminalHandlingBulk.class));
            }
            if (clear == 1)
                finish();
            else if (clear == 2) {
                delrtoreq.clear();
                inventorycontrol.clear();
                initViews();
            }

        }
        dbConnections.close();
    }

    private void SaveData(String piece) { //43 heldin , 44 heldout

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        if (IsValid()) {
            requestLocation();
            com.naqelexpress.naqelpointer.DB.DBObjects.TerminalHandling checkPoint = new com.naqelexpress.naqelpointer.DB.DBObjects.TerminalHandling
                    (20, String.valueOf(Latitude),
                            String.valueOf(Longitude), 43, lbTotal.getText().toString()
                            , "" ,0);

            if (dbConnections.InsertTerminalHandling(checkPoint, getApplicationContext())) {
                int ID = dbConnections.getMaxID("CheckPoint", getApplicationContext());


                CheckPointBarCodeDetails checkPointBarCodeDetails = new CheckPointBarCodeDetails(piece, ID);
                dbConnections.InsertCheckPointBarCodeDetails(checkPointBarCodeDetails, getApplicationContext());

                if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.TerminalHandling.class)) {
                    startService(
                            new Intent(InventoryHeldIn.this,
                                    com.naqelexpress.naqelpointer.service.TerminalHandling.class));
                }
            }
        }
        dbConnections.close();
    }


    private void resetAllData() {
        lbTotal.setText(getString(R.string.lbCount) + " 0");
        inventorycontrol.clear();
        delrtoreq.clear();
        adapter.notifyDataSetChanged();
    }

   /* private void SaveHeldOutData(int close) {

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        for (HashMap temp : delrtoreq) {
            com.naqelexpress.naqelpointer.DB.DBObjects.TerminalHandling checkPoint = new com.naqelexpress.naqelpointer.DB.DBObjects.TerminalHandling
                    (20, String.valueOf(Latitude),
                            String.valueOf(Longitude), 44, temp.get("Ref").toString()
                            , "");

            if (dbConnections.InsertTerminalHandling(checkPoint, getApplicationContext())) {
                int ID = dbConnections.getMaxID("CheckPoint", getApplicationContext());

                CheckPointBarCodeDetails waybills = new CheckPointBarCodeDetails(temp.get("WayBillNo").toString(), ID);
                if (!dbConnections.InsertCheckPointBarCodeDetails(waybills, getApplicationContext())) {
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving),
                            GlobalVar.AlertType.Error);
                    break;

                }
            }

        }
        if (close == 0) {
            stopService(
                    new Intent(InventoryHeldIn.this,
                            com.naqelexpress.naqelpointer.service.TerminalHandling.class));

            if (!isMyServiceRunning(TerminalHandling.class)) {
                startService(
                        new Intent(InventoryHeldIn.this,
                                com.naqelexpress.naqelpointer.service.TerminalHandling.class));
            }
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.SaveSuccessfully), GlobalVar.AlertType.Info);

            resetAllData();
            finish();
        }

    }*/


    private void SaveHeldOutData(String piece, String refno) {

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        com.naqelexpress.naqelpointer.DB.DBObjects.TerminalHandling checkPoint = new com.naqelexpress.naqelpointer.DB.DBObjects.TerminalHandling
                (20, String.valueOf(Latitude),
                        String.valueOf(Longitude), 44, refno
                        , "" , 0);

        if (dbConnections.InsertTerminalHandling(checkPoint, getApplicationContext())) {
            int ID = dbConnections.getMaxID("CheckPoint", getApplicationContext());

            CheckPointBarCodeDetails waybills = new CheckPointBarCodeDetails(piece, ID);
            dbConnections.InsertCheckPointBarCodeDetails(waybills, getApplicationContext());

        }

        if (!isMyServiceRunning(TerminalHandling.class)) {
            startService(
                    new Intent(InventoryHeldIn.this,
                            com.naqelexpress.naqelpointer.service.TerminalHandling.class));
        }

    }

    private boolean IsValid() {
        boolean isValid = true;

//
//        if (inventorycontrol.size() <= 0) {
//            if (delrtoreq.size() > 0)
//                SaveHeldOutData(0);
//
//            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to scan the Waybills",
//                    GlobalVar.AlertType.Error);
//            return false;
//        }

        if (txtbinlocation.getText().toString().replace(" ", "").length() == 0) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to scan Bin Location",
                    GlobalVar.AlertType.Error);
            return false;
        }


        return isValid;
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
            delrtoreq = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("delrtoreq");
            isrtoReq = savedInstanceState.getStringArrayList("isrtoReq");
            isHeldout = savedInstanceState.getStringArrayList("isHeldout");
            isdeliveryReq = savedInstanceState.getStringArrayList("isdeliveryReq");

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
        outState.putSerializable("delrtoreq", delrtoreq);
        outState.putStringArrayList("isdeliveryReq", isdeliveryReq);
        outState.putStringArrayList("isHeldout", isHeldout);
        outState.putStringArrayList("isrtoReq", isrtoReq);

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
                        isdeviceonlinehandler.removeCallbacksAndMessages(null);
                        InventoryHeldIn.super.onBackPressed();
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
                progressDialog = ProgressDialog.show(InventoryHeldIn.this,
                        "Please wait.", "Bringing Delivery Request data...", true);
            super.onPreExecute();

        }

        protected String doInBackground(String... params) {

            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {

                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "BringDeliveryReqandRtoReq");
                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setConnectTimeout(60000);
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


        try {

            JSONArray deliveryReq = jsonObject.getJSONArray("DeliveryReq");

            if (deliveryReq.length() > 0) {
                isdeliveryReq.clear();

                for (int i = 0; i < deliveryReq.length(); i++) {
                    JSONObject jsonObject1 = deliveryReq.getJSONObject(i);
//                    isdeliveryReq.add(jsonObject1.getString("WayBillNo"));
                    isdeliveryReq.add(jsonObject1.getString("BarCode"));

                    //tripdata.add(temp);
                }
            }

            JSONArray rtoreq = jsonObject.getJSONArray("RTOReq");

            if (rtoreq.length() > 0) {
                isrtoReq.clear();
                for (int i = 0; i < rtoreq.length(); i++) {
                    JSONObject jsonObject1 = rtoreq.getJSONObject(i);
//                    isrtoReq.add(jsonObject1.getString("WayBillNo"));
                    isrtoReq.add(jsonObject1.getString("BarCode"));
                    //tripdata.add(temp);
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void LoadDivisionError() {
        AlertDialog alertDialog = new AlertDialog.Builder(InventoryHeldIn.this).create();
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

    private void ErrorAlert(final String title, String message, final int clear, final String piececode) {
        AlertDialog alertDialog = new AlertDialog.Builder(InventoryHeldIn.this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (clear == 0) {
                            txtBarCode.setText("");
                            txtBarCode.requestFocus();
                        } else if (clear == 2)
                            SaveData(1);
                        else if (clear == 3)
                            insertManual1();

                        if (delrtoreq.size() == 20) {
                            SaveData(2);
                        }
//                        if (piececode.length() > 0)
//                            SaveData(piececode, title);
                    }
                });

        alertDialog.show();
    }

    private void ErrorAlert(final String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(InventoryHeldIn.this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }

    private void SaveData(String PieceCode, String req) {

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);

        com.naqelexpress.naqelpointer.DB.DBObjects.TerminalHandling checkPoint = new com.naqelexpress.naqelpointer.DB.DBObjects.TerminalHandling
                (20, String.valueOf(Latitude),
                        String.valueOf(Longitude), 44, req
                        , "" , 0);

        if (dbConnections.InsertTerminalHandling(checkPoint, getApplicationContext())) {
            int ID = dbConnections.getMaxID("CheckPoint", getApplicationContext());

            CheckPointBarCodeDetails waybills = new CheckPointBarCodeDetails(PieceCode, ID);
            dbConnections.InsertCheckPointBarCodeDetails(waybills, getApplicationContext());

        }
        if (!isMyServiceRunning(TerminalHandling.class)) {
            startService(
                    new Intent(InventoryHeldIn.this,
                            com.naqelexpress.naqelpointer.service.TerminalHandling.class));
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

    //private long startTime = 30 * 60 * 1000; // 15 MINS IDLE TIME
    //private final long interval = 1 * 1000;
    //MyCountDownTimer countDownTimer;

 /*   public class MyCountDownTimer extends CountDownTimer {
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

            ActivityCompat.finishAffinity(InventoryHeldIn.this);
            Intent intent = new Intent(getApplicationContext(), SplashScreenActivity.class);
            startActivity(intent);
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }
    }*/

    @Override
    public void onUserInteraction() {

        super.onUserInteraction();

        //Reset the timer on user interaction...
        //countDownTimer.cancel();
        //countDownTimer.start();
    }

    Handler handler;

  /*  private void refreshData() {
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // new DownloadJSON().execute();
                try {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("OriginID", 0);
                        jsonObject.put("DestinationID", GlobalVar.GV().StationID);
                        new BringNCLData().execute(jsonObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    handler.postDelayed(this, 15 * 60 * 1000);
                } catch (Exception e) {

                    handler.postDelayed(this, 15 * 60 * 1000);
                    Log.e("Dashboard thread", e.toString());
                }

            }
        }, 15 * 60 * 1000);
    }*/

    Handler isdeviceonlinehandler;

    private void isDeviceonline() {
        try {
            isdeviceonlinehandler = new Handler();
            isdeviceonlinehandler.postDelayed(new Runnable() {
                public void run() {
                    // new DownloadJSON().execute();
                    try {

                        try {
                            HttpURLConnection urlc = (HttpURLConnection)
                                    (new URL("http://clients3.google.com/generate_204")
                                            .openConnection());
                            urlc.setRequestProperty("User-Agent", "Android");
                            urlc.setRequestProperty("Connection", "close");
                            urlc.setConnectTimeout(1500);
                            urlc.connect();
                            if (urlc.getResponseCode() == 204 && urlc.getContentLength() == 0) {
                                txtbinlocation.setText("Device is Online!");

                            } else
                                txtbinlocation.setText("No Internet!");
                        } catch (IOException e) {
                            Log.e("", "Error checking internet connection", e);
                            txtbinlocation.setText("No Internet,Error checking internet connection!");
                        }

                        isdeviceonlinehandler.postDelayed(this, 3000);
                    } catch (Exception e) {

                        isdeviceonlinehandler.postDelayed(this, 3000);
                        Log.e("Dashboard thread", e.toString());
                    }

                }
            }, 3000);
        } catch (Exception e) {

        }

    }

    ArrayList<Integer> ids = new ArrayList<>();
    ArrayList<Integer> ids1 = new ArrayList<>();

    private void insertManual() {

        stopService(
                new Intent(InventoryHeldIn.this,
                        com.naqelexpress.naqelpointer.service.TerminalHandling.class));

        stopService(
                new Intent(InventoryHeldIn.this,
                        com.naqelexpress.naqelpointer.service.TerminalHandlingBulk.class));

        ids.clear();
        ids1.clear();
        try {
            DBConnections db = new DBConnections(getApplicationContext(), null);

            Cursor result = db.Fill("select * from CheckPoint where IsSync = 0 order by ID Limit 20 ", getApplicationContext());
            int count = 0; //result.getCount()
            if (count > 0) {

                JSONArray jsonArray = new JSONArray();
                JSONObject data = new JSONObject();

                result.moveToFirst();
                do {

                    JSONObject jsonObject = new JSONObject();

                    int ID = result.getInt(result.getColumnIndex("ID"));

                    try {

                        Cursor resultDetail = db.Fill("select * from CheckPointBarCodeDetails where CheckPointID = " + ID, getApplicationContext());
                        if (resultDetail.getCount() > 0) {
                            resultDetail.moveToFirst();
                            jsonObject.put("ID", result.getInt(result.getColumnIndex("ID")));
                            ids.add(result.getInt(result.getColumnIndex("ID")));
                            ids1.add(resultDetail.getInt(resultDetail.getColumnIndex("ID")));
                            jsonObject.put("BarCode", resultDetail.getString(resultDetail.getColumnIndex("BarCode")));
                            jsonObject.put("IsSync", false);
                            jsonObject.put("EmployID", Integer.parseInt(result.getString(result.getColumnIndex("EmployID"))));
                            jsonObject.put("Date", DateTime.parse(result.getString(result.getColumnIndex("Date"))));
                            jsonObject.put("TerminalHandlingScanStatusID", Integer.parseInt(result.getString(result.getColumnIndex("CheckPointTypeID"))));
                            jsonObject.put("TerminalHandlingScanStatusReasonID", Integer.parseInt(result.getString(result.getColumnIndex("CheckPointTypeDetailID"))));
                            jsonObject.put("AppVersion", GlobalVar.GV().AppVersion);
                            jsonObject.put("Latitude", result.getString(result.getColumnIndex("Latitude")));
                            jsonObject.put("Longitude", result.getString(result.getColumnIndex("Longitude")));
                            jsonObject.put("StatusID", 0);
                            jsonObject.put("Reference", result.getString(result.getColumnIndex("Ref")));

                            jsonArray.put(jsonObject);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        data.put("TerminalHandlingBarCodeDetails", jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                while (result.moveToNext());


                String jsonData = data.toString().replace("Date(-", "Date(");
                new SaveAtTerminalHandling().execute(jsonData);


            } else {

                Cursor result1 = db.Fill("select * from TerminalHandling order by ID Limit 1 ", getApplicationContext());

                if (result1.getCount() > 0) {

                    if (result1.moveToFirst()) {


                        String jsonData = result1.getString(result1.getColumnIndex("Json"));
                        int ID = result1.getInt(result1.getColumnIndex("ID"));
                        ids.add(ID);
                        jsonData = jsonData.replace("Date(-", "Date(");

                        new SaveAtTerminalHandling().execute(jsonData.toString());
                    }
                } else {
                    ErrorAlert("No Data",
                            "All Data Synchronized Successfully"
                    );
                }
            }

            startService(
                    new Intent(InventoryHeldIn.this,
                            com.naqelexpress.naqelpointer.service.TerminalHandling.class));

            startService(
                    new Intent(InventoryHeldIn.this,
                            com.naqelexpress.naqelpointer.service.TerminalHandlingBulk.class));
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    private class SaveAtTerminalHandling extends AsyncTask<String, Void, String> {
        String result = "";
        StringBuffer buffer;
        int id = 0;
        String jsonData = "";

        @Override
        protected void onPreExecute() {

            if (progressDialog == null)
                progressDialog = ProgressDialog.show(InventoryHeldIn.this,
                        "Please wait.", "Your data is inserting by Manual...", true);
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            jsonData = params[0];

            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "InsertTerminalHandlingByPieceBulk"); //LoadtoDestination
                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpURLConnection.setDoInput(true);
                // httpURLConnection.setConnectTimeout(180000);
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
            } catch (Exception e) {
                e.printStackTrace();
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
                result = String.valueOf(buffer);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String finalJson) {
            try {

                if (finalJson != null) {
                    if (finalJson.contains("Created")) {

                        if (ids1.size() > 0) {
                            for (int i = 0; i < ids.size(); i++) {
                                DBConnections db = new DBConnections(getApplicationContext(), null);
                                db.deleteCheckPointID(ids.get(i), getApplicationContext());
                                db.close();
                            }
                            for (int i = 0; i < ids1.size(); i++) {
                                DBConnections db = new DBConnections(getApplicationContext(), null);
                                db.deleteCheckPointBarcode(ids1.get(i), getApplicationContext());
                                db.close();
                            }
                        } else {
                            for (int i = 0; i < ids.size(); i++) {
                                DBConnections db = new DBConnections(getApplicationContext(), null);
                                db.deleteTerminalHandlingID(ids.get(i), getApplicationContext());
                                db.close();
                            }
                        }
                    }

                    super.onPostExecute(String.valueOf(finalJson));

                }

                insertManual();
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }

            } catch (Exception e) {
                insertManual();
            }
        }
    }

    int totalsize = 0;
    int uploaddatacount = 0;

    private class SaveAtTerminalHandlingbyManual extends AsyncTask<String, Integer, String> {
        String result = "";
        StringBuffer buffer;
        int moveddata = 0;

        @Override
        protected void onPreExecute() {

            uploaddatacount = 0;
            if (progressDialog == null) {

                progressDialog = new ProgressDialog(InventoryHeldIn.this);
                progressDialog.setTitle("Request is being process,please wait...");
                progressDialog.setMessage("Remaining " + String.valueOf(totalsize) + " / " + String.valueOf(totalsize));
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setMax(100);
                progressDialog.setCancelable(false);
                progressDialog.setProgress(1);
                progressDialog.show();

            }

            super.onPreExecute();

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values[0]);
            progressDialog.setMessage("Remaining  " + String.valueOf(totalsize - moveddata) + " / " + String.valueOf(totalsize));
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected String doInBackground(String... params) {
            totalsize = Integer.parseInt(params[0]);
            DBConnections db = new DBConnections(getApplicationContext(), null);
            Cursor loop = db.Fill("select * from TerminalHandling order by ID", getApplicationContext());
            loop.moveToFirst();
            do {
                result = "";
                buffer = new StringBuffer();
                buffer.setLength(0);

                String jsonData = loop.getString(loop.getColumnIndex("Json"));
                int jsonlegth = loop.getInt(loop.getColumnIndex("Count"));
                int ID = loop.getInt(loop.getColumnIndex("ID"));

                jsonData = jsonData.replace("Date(-", "Date(");

                HttpURLConnection httpURLConnection = null;
                OutputStream dos = null;
                InputStream ist = null;

                try {
                    URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "InsertTerminalHandlingByPieceBulk"); //LoadtoDestination
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


                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    result = String.valueOf(buffer);

                } catch (Exception e) {
                    e.printStackTrace();
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
                    result = String.valueOf(buffer);
                }


                if (result.contains("Created")) {
                    moveddata = moveddata + jsonlegth;
                    db.deleteTerminalHandlingID(ID, getApplicationContext());
                }
                try {
                    uploaddatacount = uploaddatacount + jsonlegth;
                } catch (Exception e) {

                }
                publishProgress((int) ((uploaddatacount * 100) / totalsize));

            } while (loop.moveToNext());

            loop.close();
            db.close();
            return null;
        }

        @Override
        protected void onPostExecute(String finalJson) {
            try {

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }

                DBConnections db = new DBConnections(getApplicationContext(), null);
                Cursor ts = db.Fill("select SUM(Count) As totalRecord  from TerminalHandling", getApplicationContext());
                ts.moveToFirst();
                //int totalsize = ts.getInt(ts.getColumnIndex("totalRecord"));
                int tls = 0;
                try {
                    tls = ts.getInt(ts.getColumnIndex("totalRecord"));
                } catch (Exception e) {
                    tls = 0;
                }

                if (tls > 0) {
                    ErrorAlert("Something went wrong",
                            "Pending Data :- " + String.valueOf(tls) + " Check your internet connection,and try again"
                    );
                    startService(
                            new Intent(InventoryHeldIn.this,
                                    com.naqelexpress.naqelpointer.service.TerminalHandlingBulk.class));
                } else {
                    ErrorAlert("No Data",
                            "All Data Synchronized Successfully");
                }
                ts.close();
                db.close();

                super.onPostExecute(String.valueOf(finalJson));


            } catch (Exception e) {
                System.out.println(e);
                //  insertManual();
            }
        }
    }


    private void insertManual1() {

        stopService(
                new Intent(InventoryHeldIn.this,
                        com.naqelexpress.naqelpointer.service.TerminalHandlingBulk.class));

        try {
            DBConnections db = new DBConnections(getApplicationContext(), null);


            Cursor ts = db.Fill("select SUM(Count) As totalRecord  from TerminalHandling", getApplicationContext());
            ts.moveToFirst();
            try {
                totalsize = ts.getInt(ts.getColumnIndex("totalRecord"));
            } catch (Exception e) {
                totalsize = 0;
            }
            ts.close();

            if (totalsize > 0) {
                new SaveAtTerminalHandlingbyManual().execute(String.valueOf(totalsize));
            } else {
                ErrorAlert("No Data",
                        "All Data Synchronized Successfully"
                );
            }
            db.close();

        } catch (Exception e) {
            System.out.println(e);
        }

    }
}