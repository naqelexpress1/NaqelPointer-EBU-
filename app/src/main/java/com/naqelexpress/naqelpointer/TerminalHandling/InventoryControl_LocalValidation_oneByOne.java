package com.naqelexpress.naqelpointer.TerminalHandling;

import android.app.Activity;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.Activity.Delivery.DataAdapter;
import com.naqelexpress.naqelpointer.Activity.OnlineValidation.OnlineValidation;
import com.naqelexpress.naqelpointer.Classes.NewBarCodeScanner;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.CheckPointBarCodeDetails;
import com.naqelexpress.naqelpointer.DB.DBObjects.Station;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.Retrofit.APICall;
import com.naqelexpress.naqelpointer.Retrofit.IAPICallListener;
import com.naqelexpress.naqelpointer.Retrofit.Models.OnLineValidation;
import com.naqelexpress.naqelpointer.callback.AlertCallbackOnlineValidation;

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
import java.util.List;

import Error.ErrorReporter;

// Created by Ismail on 21/03/2018.

public class InventoryControl_LocalValidation_oneByOne extends AppCompatActivity implements View.OnClickListener, IAPICallListener,
        AlertCallbackOnlineValidation {

    ArrayList<HashMap<String, String>> delrtoreq = new ArrayList<>();

    HashMap<String, String> trips = new HashMap<>();
    TextView lbTotal, delreqcount, rtoreqcount, inserteddate, validupto, citccount, cafcount;
    private EditText txtBarCode;//, txtbinlocation;
    Button bringdatawaybillattempt;


    public ArrayList<String> inventorycontrol = new ArrayList<>();
    public ArrayList<String> isdeliveryReq = new ArrayList<>();
    public ArrayList<String> iscitcshipments = new ArrayList<>();
    public ArrayList<String> isrtoReq = new ArrayList<>();
    public ArrayList<String> isHeldout = new ArrayList<>();
    public ArrayList<String> iscafshipments = new ArrayList<>();
    private String division;


    private RecyclerView recyclerView;
    private DataAdapter adapter;
    private Paint p = new Paint();
    public ArrayList<String> isWaybillAttempt = new ArrayList<>();
    private int binMasterCount;
    private List<OnLineValidation> onLineValidationList = new ArrayList<>();
    private DBConnections dbConnections;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ErrorReporter());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.inventory_new);


        dbConnections = new DBConnections(getApplicationContext(), null);
        division = GlobalVar.GV().getDivisionID(getApplicationContext(), GlobalVar.GV().EmployID);

        try {
            if (division.equals("Courier")) {
                if (!isValidValidationFile()) {
                    getOnlineValidation();
                }
            }
        } catch (Exception e) {

        }


        lbTotal = (TextView) findViewById(R.id.lbTotal);
        citccount = (TextView) findViewById(R.id.citccount);
        cafcount = (TextView) findViewById(R.id.cafcount);
        delreqcount = (TextView) findViewById(R.id.delreqcount);
        rtoreqcount = (TextView) findViewById(R.id.rtoreqcount);

        inserteddate = (TextView) findViewById(R.id.inserteddate);
        validupto = (TextView) findViewById(R.id.validupto);

        lbTotal.setText("");

        txtBarCode = (EditText) findViewById(R.id.txtWaybilll);
        txtBarCode.setHint("Bin / Piece Barcode");
        txtBarCode.setKeyListener(null);
        txtBarCode.setInputType(InputType.TYPE_CLASS_TEXT);
        txtBarCode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(25)});

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        txtBarCode.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if (event.getAction() != KeyEvent.ACTION_DOWN)
                    return true;
                else if (keyCode == KeyEvent.KEYCODE_BACK) {
                    onBackPressed();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (!division.equals("Courier"))
                        AddNewPiece();
                    else {
                        String barcode = GlobalVar.GV().ReplaceBarcodeCharcater(txtBarCode.getText().toString());
                        if (OnlineValidation.isValidPieceBarcode(barcode, InventoryControl_LocalValidation_oneByOne.this,
                                getApplicationContext(), "INV", false, false))
                            THAddNewPiece();
                    }

                    return true;
                }
                return false;
            }
        });


        Button btnOpenCamera = (Button) findViewById(R.id.btnOpenCamera);
        btnOpenCamera.setVisibility(View.GONE);
        btnOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!GlobalVar.GV().checkPermission(InventoryControl_LocalValidation_oneByOne.this, GlobalVar.PermissionType.Camera)) {
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.NeedCameraPermission), GlobalVar.AlertType.Error);
                    GlobalVar.GV().askPermission(InventoryControl_LocalValidation_oneByOne.this, GlobalVar.PermissionType.Camera);
                } else {
                    Intent intent = new Intent(InventoryControl_LocalValidation_oneByOne.this, NewBarCodeScanner.class);
                    startActivityForResult(intent, GlobalVar.GV().CAMERA_PERMISSION_REQUEST);
                }
            }
        });

        Intent intent = getIntent();
        trips = (HashMap<String, String>) intent.getSerializableExtra("tripdata");

        bringdatawaybillattempt = (Button) findViewById(R.id.bringdatawaybillattempt);
        bringdatawaybillattempt.setVisibility(View.VISIBLE);
        bringdatawaybillattempt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("OriginID", 0);
                    jsonObject.put("DestinationID", GlobalVar.GV().StationID);
                    new InventoryControl_LocalValidation_oneByOne.BringWaybillattempt().execute(jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        ReadFromLocalWaybillAttempt();

        Button bringdata = (Button) findViewById(R.id.bringdata);
        bringdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("OriginID", 0);
                    jsonObject.put("DestinationID", GlobalVar.GV().StationID);
                    new InventoryControl_LocalValidation_oneByOne.BringNCLData().execute(jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        Cursor result = dbConnections.Fill("select * from RtoReq ", getApplicationContext());
        if (result.getCount() > 0) {
            ReadFromLocal(result, dbConnections);

        } else {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("OriginID", 0);
                jsonObject.put("DestinationID", GlobalVar.GV().StationID);
                new InventoryControl_LocalValidation_oneByOne.BringNCLData().execute(jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        binMasterCount = GlobalVar.getBinMasterCount(getApplicationContext());
        handler = new Handler();
    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DataAdapter(inventorycontrol);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GlobalVar.GV().CAMERA_PERMISSION_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    if (extras.containsKey("barcode")) {
                        String barcode = extras.getString("barcode").trim();
                        txtBarCode.setText(barcode);
                        Log.d("test", "Divison " + division);
                        if (!division.equals("Courier"))
                            AddNewPiece();
                        else {
                            THAddNewPiece();
                        }
                    }
                }

            }
        }
    }

    private void AddNewPiece() {

//        if (!GlobalVar.ValidateAutomacticDate(getApplicationContext())) {
//            GlobalVar.RedirectSettings(InventoryControl_LocalValidation_oneByOne.this);
//            return;
//        }

        if (GlobalVar.ValidateAutomacticDate(getApplicationContext())) {
            if (!GlobalVar.IsAllowtoScan(validupto.getText().toString().replace("Upto : ", ""))) { //validupto.getText().toString()
                GlobalVar.MakeSound(getApplicationContext(), R.raw.wrongbarcodescan);
                ErrorAlert("Info", "Data is Expired kindly Load today Data , (Press Bring Data)");
                return;
            }
        } else {
            GlobalVar.MakeSound(getApplicationContext(), R.raw.wrongbarcodescan);
            GlobalVar.RedirectSettings(InventoryControl_LocalValidation_oneByOne.this);
            return;
        }


        String barcode = GlobalVar.GV().ReplaceBarcodeCharcater(txtBarCode.getText().toString());

        if (barcode.toUpperCase().matches(".*[ABCDEFGH].*")) {

            //Validate Bin location
            if (binMasterCount > 0 && !GlobalVar.isBinMasterValueExists(barcode.toUpperCase(), getApplicationContext())) {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), barcode + " is invalid bin", GlobalVar.AlertType.Error);
                return;
            }

            lbTotal.setText(barcode);
            txtBarCode.requestFocus();
            txtBarCode.setText("");
            inventorycontrol.clear();
            initViews();
            return;
        }

        try {
            double convert = Double.parseDouble(barcode);
        } catch (Exception e) {
            GlobalVar.MakeSound(getApplicationContext(), R.raw.wrongbarcodescan);
            ErrorAlert("Error",
                    "Incorrect Piece Barcode(" + barcode + ")"
            );
            txtBarCode.setText("");
            txtBarCode.requestFocus();
            return;
        }

        if (barcode.length() <= 12) {
            GlobalVar.MakeSound(getApplicationContext(), R.raw.wrongbarcodescan);
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
        boolean ismatch = false;

        //Get barcode info (Delivery req - rto req - citc - NoOfAttempts) and add it to lists
        GetNCLDatafromDB(barcode);


        if (WaybillAttempt.equals("19127") || WaybillAttempt.equals("0"))
            WaybillAttempt = "No Data";


        if (isrtoReq.contains(barcode)) {

            if (!isHeldout.contains(barcode)) {
                ismatch = true;
                rtoreq = true;
                isHeldout.add(barcode);
                HashMap<String, String> temp = new HashMap<>();
                temp.put("WayBillNo", barcode);
                temp.put("Status", "44");
                temp.put("Ref", "Request For RTO");
                delrtoreq.add(temp);
                inventorycontrol.add(barcode);
                initViews();


                GlobalVar.GV().MakeSound(getApplicationContext(), R.raw.rto);
                ErrorAlert("RTO Request", "This Waybill Number(" + barcode + ") is Request For RTO "
                        , 0, barcode);
            } else {
                GlobalVar.GV().MakeSound(getApplicationContext(), R.raw.wrongbarcodescan);
                ErrorAlert("RTO Request", "This Waybill Number(" + barcode + ") is Request For RTO "
                        , 0, barcode);
                return;
            }
        }


        if (iscitcshipments.contains(barcode) && !ismatch) {

            if (!isHeldout.contains(barcode)) {
                ismatch = true;
                isHeldout.add(barcode);
                HashMap<String, String> temp = new HashMap<>();
                temp.put("WayBillNo", barcode);
                temp.put("Status", "44");
                temp.put("Ref", "CITC Complaint Shipment");
                delrtoreq.add(temp);
                inventorycontrol.add(barcode);
                initViews();

                GlobalVar.GV().MakeSound(getApplicationContext(), R.raw.rto);
                ErrorAlert("CITC Complaint", "This Waybill Number(" + barcode + ") has CITC Complaint \n" +
                        "Attempted Count : " + WaybillAttempt, 0, barcode);
            } else {

                GlobalVar.GV().MakeSound(getApplicationContext(), R.raw.wrongbarcodescan);
                ErrorAlert("Info", getString(R.string.AlreadyExists), 0, barcode);
                return;
            }
        }

        if (!ismatch) {
            if (isdeliveryReq.contains(barcode)) {
                if (isrtoReq.contains(barcode)) {
                    ismatch = true;
                    rtoreq = true;

                    if (!isHeldout.contains(barcode)) {
                        isHeldout.add(barcode);
                        HashMap<String, String> temp = new HashMap<>();
                        temp.put("WayBillNo", barcode);
                        temp.put("Status", "44");
                        temp.put("Ref", "Request For Delivery & RTO");
                        delrtoreq.add(temp);
                        inventorycontrol.add(barcode);
//                    txtBarCode.setText("");
//                    txtBarCode.requestFocus();
                        initViews();

                        GlobalVar.GV().MakeSound(getApplicationContext(), R.raw.delivery);
                    }
                    ErrorAlert("Delivery/RTO Request", "This Waybill Number(" + barcode + ") is Request For Delivery & RTO \n" +
                            "Attempted Count : " + WaybillAttempt, 0, barcode);


                    // return;
                } else {

                    if (!isHeldout.contains(barcode)) {
                        ismatch = true;
                        isHeldout.add(barcode);
                        HashMap<String, String> temp = new HashMap<>();
                        temp.put("WayBillNo", barcode);
                        temp.put("Status", "44");
                        temp.put("Ref", "Request For Delivery");
                        delrtoreq.add(temp);
                        inventorycontrol.add(barcode);
                        initViews();
//                    txtBarCode.setText("");
//                    txtBarCode.requestFocus();
                        GlobalVar.MakeSound(getApplicationContext(), R.raw.delivery);
                        ErrorAlert("Delivery Request", "This Waybill Number(" + barcode + ") is Request For Delivery \n" +
                                "Attempted Count : " + WaybillAttempt, 0, barcode);
                    } else {
                        GlobalVar.MakeSound(getApplicationContext(), R.raw.wrongbarcodescan);
                        ErrorAlert("Info", getString(R.string.AlreadyExists), 0, barcode);
                        return;
                    }
                }


            }
        }

        if (!rtoreq) {
            if (isrtoReq.contains(barcode)) {

                if (!isHeldout.contains(barcode)) {
                    ismatch = true;
                    isHeldout.add(barcode);
                    HashMap<String, String> temp = new HashMap<>();
                    temp.put("WayBillNo", barcode);
                    temp.put("Status", "44");
                    temp.put("Ref", "Request For RTO");
                    delrtoreq.add(temp);
                    inventorycontrol.add(barcode);
                    initViews();

                    GlobalVar.GV().MakeSound(getApplicationContext(), R.raw.rto);
                    ErrorAlert("RTO Request", "This Waybill Number(" + barcode + ") is Request For RTO \n" +
                            "Attempted Count : " + WaybillAttempt, 0, barcode);
                } else {
                    GlobalVar.GV().MakeSound(getApplicationContext(), R.raw.wrongbarcodescan);
                    ErrorAlert("Info", getString(R.string.AlreadyExists), 0, barcode);
                    return;
                }
            }
        }

        if (!inventorycontrol.contains(barcode)) {
//            if (txtBarCode.getText().toString().length() == 13) {

            if (!WaybillAttempt.equals("No Data")) {
                GlobalVar.GV().MakeSound(getApplicationContext(), R.raw.delivery);
                ErrorAlert("Attempt Waybill Count", "This Waybill Number(" + barcode + ")  \n" +
                        "Attempted Count : " + WaybillAttempt, 0, barcode);
            }

            // SaveData(txtBarCode.getText().toString());

            HashMap<String, String> temp = new HashMap<>();
            temp.put("WayBillNo", barcode);
            temp.put("Status", "0");
            temp.put("Ref", lbTotal.getText().toString());
            delrtoreq.add(temp);

            inventorycontrol.add(0, barcode);
            txtBarCode.setText("");
            txtBarCode.requestFocus();
            initViews();

//            } else {
//
//                GlobalVar.GV().MakeSound(getApplicationContext(), R.raw.wrongbarcodescan);
//                txtBarCode.setText("");
//                txtBarCode.requestFocus();
//                return;
//
//            }
        } else {
            if (!ismatch) {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.AlreadyExists), GlobalVar.AlertType.Warning);
                GlobalVar.GV().MakeSound(getApplicationContext(), R.raw.wrongbarcodescan);
                txtBarCode.setText("");
                txtBarCode.requestFocus();
            }
        }

        if (delrtoreq.size() == 20) {
            SaveData(2);
        }

    }

    // Group flags from ismail + onlineValidation flags in one pop-up
    private void THAddNewPiece() {


        if (GlobalVar.ValidateAutomacticDate(getApplicationContext())) {
            if (!GlobalVar.IsAllowtoScan(validupto.getText().toString().replace("Upto : ", ""))) { //validupto.getText().toString()
                GlobalVar.MakeSound(getApplicationContext(), R.raw.wrongbarcodescan);
                ErrorAlert("Info", "Data is Expired kindly Load today Data , (Press Bring Data)");
                return;
            }
        } else {
            GlobalVar.MakeSound(getApplicationContext(), R.raw.wrongbarcodescan);
            GlobalVar.RedirectSettings(InventoryControl_LocalValidation_oneByOne.this);
            return;
        }


        String barcode = GlobalVar.GV().ReplaceBarcodeCharcater(txtBarCode.getText().toString());

        if (barcode.toUpperCase().matches(".*[ABCDEFGH].*")) {

            //Validate Bin location
            if (binMasterCount > 0 && !GlobalVar.isBinMasterValueExists(barcode.toUpperCase(), getApplicationContext())) {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), barcode + " is invalid bin", GlobalVar.AlertType.Error);
                return;
            }

            lbTotal.setText(barcode);
            txtBarCode.requestFocus();
            txtBarCode.setText("");
            inventorycontrol.clear();
            initViews();
            return;
        }

        try {
            double convert = Double.parseDouble(barcode);
        } catch (Exception e) {
            GlobalVar.GV().MakeSound(getApplicationContext(), R.raw.wrongbarcodescan);
            ErrorAlert("Error",
                    "Incorrect Piece Barcode(" + barcode + ")"
            );
            txtBarCode.setText("");
            txtBarCode.requestFocus();
            return;
        }

        if (barcode.length() <= 12) {
            GlobalVar.GV().MakeSound(getApplicationContext(), R.raw.wrongbarcodescan);
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


//        OnLineValidation onLineValidation = getOnLineValidation(barcode);
//        onLineValidation.setIsCITCComplaint(0);
//        onLineValidation.setIsRTORequest(0);
//        onLineValidation.setIsDeliveryRequest(0);
//        onLineValidation.setIsCAFRequest(0);
        boolean rtoreq = false;
        boolean ismatch = false;

        //Get barcode info (Delivery req - rto req - citc - NoOfAttempts) and add it to lists
        GetNCLDatafromDB(barcode);


        if (WaybillAttempt.equals("19127") || WaybillAttempt.equals("0"))
            WaybillAttempt = "No Data";

        if (iscafshipments.contains(barcode))
//            onLineValidation.setIsCAFRequest(1);

            if (isrtoReq.contains(barcode)) {
//            onLineValidation.setIsRTORequest(1);
                if (!isHeldout.contains(barcode)) {
                    ismatch = true;
                    rtoreq = true;
                    isHeldout.add(barcode);
                    HashMap<String, String> temp = new HashMap<>();
                    temp.put("WayBillNo", barcode);
                    temp.put("Status", "44");
                    temp.put("Ref", "Request For RTO");
                    delrtoreq.add(temp);
                    inventorycontrol.add(barcode);
                    initViews();
                } else {
                    return;
                }
            }


        if (iscitcshipments.contains(barcode) && !ismatch) {
//            onLineValidation.setIsCITCComplaint(1);

            if (!isHeldout.contains(barcode)) {
                ismatch = true;
                isHeldout.add(barcode);
                HashMap<String, String> temp = new HashMap<>();
                temp.put("WayBillNo", barcode);
                temp.put("Status", "44");
                temp.put("Ref", "CITC Complaint Shipment");
                delrtoreq.add(temp);
                inventorycontrol.add(barcode);
                initViews();

            } else {

                GlobalVar.GV().MakeSound(getApplicationContext(), R.raw.wrongbarcodescan);
                ErrorAlert("Info", getString(R.string.AlreadyExists), 0, barcode);
                return;
            }
        }

        if (!ismatch) {
            if (isdeliveryReq.contains(barcode)) {
//                onLineValidation.setIsDeliveryRequest(1);
                if (isrtoReq.contains(barcode)) {
                    ismatch = true;
                    rtoreq = true;

                    if (!isHeldout.contains(barcode)) {
                        isHeldout.add(barcode);
                        HashMap<String, String> temp = new HashMap<>();
                        temp.put("WayBillNo", barcode);
                        temp.put("Status", "44");
                        temp.put("Ref", "Request For Delivery & RTO");
                        delrtoreq.add(temp);
                        inventorycontrol.add(barcode);

                        initViews();
                    }

                } else {

                    if (!isHeldout.contains(barcode)) {
                        ismatch = true;
                        isHeldout.add(barcode);
                        HashMap<String, String> temp = new HashMap<>();
                        temp.put("WayBillNo", barcode);
                        temp.put("Status", "44");
                        temp.put("Ref", "Request For Delivery");
                        delrtoreq.add(temp);
                        inventorycontrol.add(barcode);
                        initViews();
                    } else {
                        GlobalVar.GV().MakeSound(getApplicationContext(), R.raw.wrongbarcodescan);
                        ErrorAlert("Info", getString(R.string.AlreadyExists), 0, barcode);
                        return;
                    }
                }


            }
        }

        if (!rtoreq) {
            if (isrtoReq.contains(barcode)) {

                if (!isHeldout.contains(barcode)) {
                    ismatch = true;
                    isHeldout.add(barcode);
                    HashMap<String, String> temp = new HashMap<>();
                    temp.put("WayBillNo", barcode);
                    temp.put("Status", "44");
                    temp.put("Ref", "Request For RTO");
                    delrtoreq.add(temp);
                    inventorycontrol.add(barcode);
                    initViews();
                } else {
                    GlobalVar.GV().MakeSound(getApplicationContext(), R.raw.wrongbarcodescan);
                    ErrorAlert("Info", getString(R.string.AlreadyExists), 0, barcode);
                    return;
                }
            }
        }

        if (!inventorycontrol.contains(barcode)) {
            //if (txtBarCode.getText().toString().length() == 13) {

            if (!WaybillAttempt.equals("No Data")) {
                try {
//                    onLineValidation.setNoOfAttempts(Integer.parseInt(WaybillAttempt));
                } catch (Exception ex) {
                    Log.d("test", "Add new piece - no of attempt" + ex.toString());
                }

            }

            HashMap<String, String> temp = new HashMap<>();
            temp.put("WayBillNo", barcode);
            temp.put("Status", "0");
            temp.put("Ref", lbTotal.getText().toString());
            delrtoreq.add(temp);

            inventorycontrol.add(0, barcode);
            txtBarCode.setText("");
            txtBarCode.requestFocus();
            initViews();

//            } else {
//
//                GlobalVar.GV().MakeSound(getApplicationContext(), R.raw.wrongbarcodescan);
//                txtBarCode.setText("");
//                txtBarCode.requestFocus();
//                return;
//
//            }
        } else {
            if (!ismatch) {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.AlreadyExists), GlobalVar.AlertType.Warning);
                GlobalVar.GV().MakeSound(getApplicationContext(), R.raw.wrongbarcodescan);
                txtBarCode.setText("");
                txtBarCode.requestFocus();
            }
        }

        // Show dialog
        if (delrtoreq.size() == 20) {
            SaveData(2);
        }

        txtBarCode.setText("");
//        showDialog(onLineValidation);

    }


    String WaybillAttempt = "19127";

    //Get barcode info (Delivery req - rto req - citc - NoOfAttempts) and add it to lists
    private void GetNCLDatafromDB(String Barcode) {

        WaybillAttempt = "19127";

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);

        try {


            // Delivery Req
            Cursor cursor = dbConnections.Fill("select * from DeliverReq where ReqType = 1 and BarCode='" + Barcode + "'", getApplicationContext());
            if (cursor.getCount() > 0) {
                isdeliveryReq.clear();
                cursor.moveToFirst();
                do {
                    isdeliveryReq.add(cursor.getString(cursor.getColumnIndex("BarCode")));
                } while (cursor.moveToNext());
            }

            //RTO Req
            cursor = dbConnections.Fill("select * from RtoReq  where BarCode='" + Barcode + "'", getApplicationContext());
            if (cursor.getCount() > 0) {
                isrtoReq.clear();
                cursor.moveToFirst();
                do {
                    isrtoReq.add(cursor.getString(cursor.getColumnIndex("BarCode")));
                } while (cursor.moveToNext());
            }

            //CITC
            cursor = dbConnections.Fill("select * from DeliverReq where ReqType = 3 and BarCode='" + Barcode + "'", getApplicationContext());
            if (cursor.getCount() > 0) {
                iscitcshipments.clear();
                cursor.moveToFirst();
                do {
                    iscitcshipments.add(cursor.getString(cursor.getColumnIndex("BarCode")));
                } while (cursor.moveToNext());
            }


            cursor = dbConnections.Fill("select * from DeliverReq where ReqType = 4 and BarCode='" + Barcode + "'", getApplicationContext());
            if (cursor.getCount() > 0) {
                iscafshipments.clear();
                cursor.moveToFirst();
                do {
                    iscafshipments.add(cursor.getString(cursor.getColumnIndex("BarCode")));
                } while (cursor.moveToNext());
            }

            cursor.close();

            //No of attempts
            Cursor result1 = dbConnections.Fill("select Sum(Attempt) Attempt  from WaybillAttempt where BarCode='" + Barcode + "'", getApplicationContext());
            if (result1.getCount() > 0) {
                result1.moveToFirst();
                WaybillAttempt = String.valueOf(result1.getInt(result1.getColumnIndex("Attempt")));
            }


            dbConnections.close();


        } catch (Exception e) {
            GlobalVar.hideKeyboardFrom(getApplicationContext(), getWindow().getDecorView().getRootView());
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Somthing went wrong, kindly scan again",
                    GlobalVar.AlertType.Error);
            GlobalVar.GV().MakeSound(getApplicationContext(), R.raw.wrongbarcodescan);
            txtBarCode.setText("");
            txtBarCode.requestFocus();
            e.printStackTrace();
        }

    }

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
            case R.id.mnuSave: //finish
                if (GlobalVar.ValidateAutomacticDate(getApplicationContext())) {
                    ErrorAlert("Info", "Are yo sure want to Finish the Job?", 2, "");
                } else
                    GlobalVar.RedirectSettings(InventoryControl_LocalValidation_oneByOne.this);
                return true;
            case R.id.manual:
                if (GlobalVar.ValidateAutomacticDate(getApplicationContext())) {
                    ErrorAlert("Info", "Are yo sure want to upload Manual?", 3, "");
                } else
                    GlobalVar.RedirectSettings(InventoryControl_LocalValidation_oneByOne.this);
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

    private void SaveData(int clear) { //43 heldin , 44 heldout

        if (!GlobalVar.ValidateAutomacticDate(getApplicationContext())) {
            GlobalVar.RedirectSettings(InventoryControl_LocalValidation_oneByOne.this);
            return;
        }
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
                        new Intent(InventoryControl_LocalValidation_oneByOne.this,
                                com.naqelexpress.naqelpointer.service.TerminalHandlingBulk.class));
            }
            if (clear == 1)
                finish();
            else if (clear == 2) {
                delrtoreq.clear();
                //inventorycontrol.clear();
                //initViews();
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
                            String.valueOf(Longitude), 0, lbTotal.getText().toString()
                            , "", 0);

            if (dbConnections.InsertTerminalHandling(checkPoint, getApplicationContext())) {
                int ID = dbConnections.getMaxID("CheckPoint", getApplicationContext());


                CheckPointBarCodeDetails checkPointBarCodeDetails = new CheckPointBarCodeDetails(piece, ID);
                dbConnections.InsertCheckPointBarCodeDetails(checkPointBarCodeDetails, getApplicationContext());

                if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.TerminalHandling.class)) {
                    startService(
                            new Intent(InventoryControl_LocalValidation_oneByOne.this,
                                    com.naqelexpress.naqelpointer.service.TerminalHandling.class));
                }
            }
        }
        dbConnections.close();
    }


    private void resetAllData() {
        // lbTotal.setText(getString(R.string.lbCount) + " 0");
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
                        , "", 0);

        if (dbConnections.InsertTerminalHandling(checkPoint, getApplicationContext())) {
            int ID = dbConnections.getMaxID("CheckPoint", getApplicationContext());

            CheckPointBarCodeDetails waybills = new CheckPointBarCodeDetails(piece, ID);
            dbConnections.InsertCheckPointBarCodeDetails(waybills, getApplicationContext());

        }

        if (!isMyServiceRunning(TerminalHandling.class)) {
            startService(
                    new Intent(InventoryControl_LocalValidation_oneByOne.this,
                            com.naqelexpress.naqelpointer.service.TerminalHandling.class));
        }

    }

    private boolean IsValid() {
        boolean isValid = true;

        if (lbTotal.getText().toString().replace(" ", "").length() == 0) {
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
            lbTotal.setText(savedInstanceState.getString("bin"));
            inventorycontrol = savedInstanceState.getStringArrayList("inventorycontrol");

            inserteddate.setText(savedInstanceState.getString("inserteddate"));
            validupto.setText(savedInstanceState.getString("validupto"));
            delreqcount.setText(savedInstanceState.getString("delreqcount"));
            rtoreqcount.setText(savedInstanceState.getString("rtoreqcount"));

            initViews();

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
        outState.putString("bin", lbTotal.getText().toString());
        outState.putString("inserteddate", inserteddate.getText().toString());
        outState.putString("validupto", validupto.getText().toString());
        outState.putString("delreqcount", delreqcount.getText().toString());
        outState.putString("rtoreqcount", rtoreqcount.getText().toString());
        outState.putStringArrayList("inventorycontrol", inventorycontrol);

    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit")
                .setMessage("Are you sure you want to exit without saving?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        InventoryControl_LocalValidation_oneByOne.super.onBackPressed();
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

    @Override
    public void returnOk(int ok, Activity activity, OnLineValidation onLineValidation) {
        if(ok == 1)
            System.out.println("");
//        if (ok == 1) {
//            handler.post(new Runnable() {
//                public void run() {
//                    if (!division.equals("Courier"))
//                        AddNewPiece();
//                    else
//
//                        THAddNewPiece();
//                }
//            });
//        } else
//            ((InventoryControl_LocalValidation_oneByOne) activity).txtBarCode.setText("");
    }


    // Insert delvery Req in DeliverReq table  & Rto req in RtoReq table
    private class BringNCLData extends AsyncTask<String, Integer, String> {
        StringBuffer buffer;

        @Override
        protected void onPreExecute() {

            if (progressDialog == null)
                progressDialog = ProgressDialog.show(InventoryControl_LocalValidation_oneByOne.this,
                        "Please wait.", "Bringing Delivery Request data..." + GlobalVar.GV().getCurrentDateTime(), true);
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
                httpURLConnection.setConnectTimeout(120000);
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

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute("");
            if (result != null) {

                try {

                    JSONObject jsonObject = new JSONObject(result);
                    if (!jsonObject.getBoolean("HasError")) {
                        try {


                            JSONArray deliveryReq = jsonObject.getJSONArray("DeliveryReq");

                            DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                            dbConnections.deleteDeliverRtoReqData(getApplicationContext());

                            if (deliveryReq.length() > 0) {

                                dbConnections.insertDelBulk(deliveryReq, getApplicationContext());
                            }

                            JSONArray rtoreq = jsonObject.getJSONArray("RTOReq");

                            int rtolength = rtoreq.length();
                            if (rtolength > 0) {
                                dbConnections.insertReqBulk(rtoreq, getApplicationContext());
                            }

                            JSONArray caf = jsonObject.getJSONArray("CAF");

                            if (caf.length() > 0)
                                dbConnections.insertCAFBulk(caf, getApplicationContext());


                            Cursor delreq = dbConnections.Fill("select * from RtoReq", getApplicationContext());
                            ReadFromLocal(delreq, dbConnections);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                System.out.println(result);
            } else {

                LoadDivisionError(0);
            }
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }

        }

    }


    private class BringWaybillattempt extends AsyncTask<String, Integer, String> {
        StringBuffer buffer;

        @Override
        protected void onPreExecute() {

            if (progressDialog == null)
                progressDialog = ProgressDialog.show(InventoryControl_LocalValidation_oneByOne.this,
                        "Please wait.", "Bringing Waybill attempt data..." + GlobalVar.GV().getCurrentDateTimeSS(), true);
            super.onPreExecute();

        }

        protected String doInBackground(String... params) {

            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {

                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "BringWaybillattempt");
                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpURLConnection.setConnectTimeout(120000);
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

                    // JsonObject convertedObject = new Gson().fromJson(result, JsonObject.class);
                    //JsonObject jsonObject = (JsonObject) new JsonParser().parse("YourJsonString");


                    JSONObject jsonObject = new JSONObject(result);
                    if (!jsonObject.getBoolean("HasError")) {
                        //fetchData(jsonObject);
                        try {


                            JSONArray WaybillAttempt = jsonObject.getJSONArray("WaybillAttempt");

                            DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                            dbConnections.deleteWaybillAttempt(getApplicationContext());

                            if (WaybillAttempt.length() > 0) {

                                dbConnections.insertwaybillattemptBulk(WaybillAttempt, getApplicationContext());
                                dbConnections.close();

                            }

                            ReadFromLocalWaybillAttempt();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                System.out.println(result);
            } else {

                LoadDivisionError(0);
            }
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }

        }

    }


    private void LoadDivisionError(final int callfunction) {
        AlertDialog alertDialog = new AlertDialog.Builder(InventoryControl_LocalValidation_oneByOne.this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Something went wrong");
        alertDialog.setMessage("Kindly Check your Internet Connection,Scan Inventory press Cancel");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (callfunction == 0)
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("OriginID", 0);
                                jsonObject.put("DestinationID", GlobalVar.GV().StationID);
                                new InventoryControl_LocalValidation_oneByOne.BringNCLData().execute(jsonObject.toString());
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

    private void ErrorAlert(final String title, String message, final int clear, final String piececode) {
        AlertDialog alertDialog = new AlertDialog.Builder(InventoryControl_LocalValidation_oneByOne.this).create();
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

                    }
                });

        alertDialog.show();
    }

    ArrayList<Integer> ids = new ArrayList<>();
    ArrayList<Integer> ids1 = new ArrayList<>();
    int totalsize = 0;
    boolean isRunning = false, somethingwrong = false;


    private void ErrorAlert(final String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(InventoryControl_LocalValidation_oneByOne.this).create();
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

    private void ErrorAlertOnlineValidation(final String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(InventoryControl_LocalValidation_oneByOne.this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Try Again",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getOnlineValidation();
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

    private void SaveData(String PieceCode, String req) {

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);

        com.naqelexpress.naqelpointer.DB.DBObjects.TerminalHandling checkPoint = new com.naqelexpress.naqelpointer.DB.DBObjects.TerminalHandling
                (20, String.valueOf(Latitude),
                        String.valueOf(Longitude), 44, req
                        , "", 0);

        if (dbConnections.InsertTerminalHandling(checkPoint, getApplicationContext())) {
            int ID = dbConnections.getMaxID("CheckPoint", getApplicationContext());

            CheckPointBarCodeDetails waybills = new CheckPointBarCodeDetails(PieceCode, ID);
            dbConnections.InsertCheckPointBarCodeDetails(waybills, getApplicationContext());

        }
        if (!isMyServiceRunning(TerminalHandling.class)) {
            startService(
                    new Intent(InventoryControl_LocalValidation_oneByOne.this,
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

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
    }

    // Handler handler;
    Handler isdeviceonlinehandler;


    int uploaddatacount = 0;


    private class SaveAtTerminalHandlingbyManual extends AsyncTask<String, Integer, String> {
        String result = "";
        StringBuffer buffer;
        int moveddata = 0;

        @Override
        protected void onPreExecute() {

            uploaddatacount = 0;
            if (progressDialog == null) {

                progressDialog = new ProgressDialog(InventoryControl_LocalValidation_oneByOne.this);
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
                            new Intent(InventoryControl_LocalValidation_oneByOne.this,
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
                new Intent(InventoryControl_LocalValidation_oneByOne.this,
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
                new InventoryControl_LocalValidation_oneByOne.SaveAtTerminalHandlingbyManual().execute(String.valueOf(totalsize));
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

    // To set total count (header information)
    private void ReadFromLocal(Cursor result, DBConnections dbConnections) {
        isrtoReq.clear();

        try {
            if (result.getCount() > 0) {
                result.moveToFirst();

                rtoreqcount.setText("RTO Count : " + String.valueOf(result.getCount()));

                try {
                    validupto.setText("Upto : " + result.getString(result.getColumnIndex("ValidDate")) + " 15:00"); //16:30
                    inserteddate.setText("DLD : " + result.getString(result.getColumnIndex("InsertedDate")));
                } catch (Exception e) {
                    System.out.println(e);
                }

            }
            result.close();

            Cursor cursor = dbConnections.Fill("select count(*) total from DeliverReq where ReqType = 1", getApplicationContext());
            if (cursor.getCount() > 0) {

                cursor.moveToFirst();

                delreqcount.setText("DEL Count : " + String.valueOf(cursor.getString(cursor.getColumnIndex("total"))));
            }
            cursor = dbConnections.Fill("select * from DeliverReq where ReqType = 1 Limit 1", getApplicationContext());
            try {
                cursor.moveToFirst();
                validupto.setText("Upto : " + cursor.getString(cursor.getColumnIndex("ValidDate")) + " 16:30");
                inserteddate.setText("DLD : " + cursor.getString(cursor.getColumnIndex("InsertedDate")));
            } catch (Exception e) {
                System.out.println(e);
            }

            cursor = dbConnections.Fill("select count(*) total from DeliverReq where ReqType = 3 ", getApplicationContext());
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                citccount.setText("CITC Count : " + String.valueOf(cursor.getString(cursor.getColumnIndex("total"))));
            }

            cursor = dbConnections.Fill("select count(*) total from DeliverReq where ReqType = 4 ", getApplicationContext());
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                cafcount.setText("CAF Count : " + String.valueOf(cursor.getString(cursor.getColumnIndex("total"))));
            }

            cursor.close();
            result.close();
            dbConnections.close();


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    // To set the total no of attempt in bringdatawaybillattempt button
    private void ReadFromLocalWaybillAttempt() {
        // isWaybillAttempt.clear();
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);

        Cursor result = dbConnections.Fill("select count(*) total from WaybillAttempt", getApplicationContext());
        Cursor result1 = dbConnections.Fill("select *  from WaybillAttempt Limit 1", getApplicationContext());

        try {
            String downloaddate = "";
            if (result.getCount() > 0) {
                result.moveToFirst();

                if (result1.getCount() > 0) {
                    result1.moveToFirst();
                    downloaddate = result1.getString(result1.getColumnIndex("InsertedDate"));
                }

                bringdatawaybillattempt.setText("Waybill Attempt Count : " + String.valueOf(result.getInt(result.getColumnIndex("total"))) + " " + downloaddate);

            }
            result.close();
            result1.close();
            dbConnections.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /* OnLineValidation */

    private boolean isValidValidationFile() {
        boolean isValid;

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        isValid = dbConnections.isValidValidationFile(GlobalVar.DsAndInventoryTHCourier, getApplicationContext());
        if (isValid)
            return true;
        return false;
    }

    private OnLineValidation getOnLineValidation(String barcode) {
        OnLineValidation onLineValidation = new OnLineValidation();
        onLineValidation.setBarcode(barcode);
        try {
            OnLineValidation onLineValidationLocal = dbConnections.getPieceInformationByWaybillNo(GlobalVar.getWaybillFromBarcode(barcode)
                    , barcode, getApplicationContext(), false);

            if (onLineValidationLocal != null) {

                if (onLineValidationLocal.getWaybillDestID() != GlobalVar.GV().StationID) {
                    onLineValidation.setIsWrongDest(1);
                    onLineValidation.setWaybillDestID(onLineValidationLocal.getWaybillDestID());
                }

                if (onLineValidationLocal.getIsMultiPiece() == 1) {
                    onLineValidation.setIsMultiPiece(1);
                }

                if (onLineValidationLocal.getIsStopped() == 1) {
                    onLineValidation.setIsStopped(1);
                }


                //Commented by Ismail

//                if (onLineValidationLocal.getIsRTORequest() == 1) {
//                    onLineValidation.setIsRTORequest(1);
//                }
//
//                if (onLineValidationLocal.getIsDeliveryRequest() == 1) {
//                    onLineValidation.setIsDeliveryRequest(1);
//                }

                if (onLineValidationLocal.getIsRelabel() == 1) {
                    onLineValidation.setIsRelabel(1);
                }

                onLineValidation.setNoOfAttempts(onLineValidationLocal.getNoOfAttempts());

                onLineValidation.setBarcode(barcode);
                onLineValidationList.add(onLineValidation);

            }

        } catch (Exception e) {
            Log.d("test", "isValidPieceBarcode " + e.toString());
        }

        return onLineValidation;
    }

    public void showDialog(OnLineValidation onlineValidation) {
        try {
            if (onlineValidation != null) {

                GlobalVar.GV().MakeSound(getApplicationContext(), R.raw.barcodescanned);
                final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(InventoryControl_LocalValidation_oneByOne.this);
                LayoutInflater inflater = this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.custom_alert_dialog, null);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setCancelable(false);

                TextView tvBarcode = dialogView.findViewById(R.id.tv_barcode);
                tvBarcode.setText("Piece #" + onlineValidation.getBarcode());


                Button btnConfirm = dialogView.findViewById(R.id.btn_confirm);
                btnConfirm.setVisibility(View.VISIBLE);
                btnConfirm.setText("OK");


                if (onlineValidation.getIsWrongDest() == 1) {
                    String stationName = "";
                    try {
                        Station station = null;

                             /*if (pieceDetails.getIsManifested() == 0)
                                 station =  dbConnections.getStationByID(pieceDetails.getCustomerWaybillDestID() , getApplicationContext());
                             else
                                 station =  dbConnections.getStationByID(pieceDetails.getWaybillDestID() , getApplicationContext());*/

                        station = dbConnections.getStationByID(onlineValidation.getWaybillDestID(), getApplicationContext());


                        if (station != null)
                            stationName = station.Name;
                        else
                            Log.d("test", "Station is null");
                    } catch (Exception e) {
                        Log.d("test", "showDialog " + e.toString());
                    }


                    LinearLayout llWrongDest = dialogView.findViewById(R.id.ll_wrong_dest);
                    llWrongDest.setVisibility(View.VISIBLE);

                    TextView tvWrongDestHeader = dialogView.findViewById(R.id.tv_wrong_dest_header);
                    tvWrongDestHeader.setText("Wrong Destination");

                    TextView tvWrongDestBody = dialogView.findViewById(R.id.tv_wrong_dest_body);
                    tvWrongDestBody.setText("Shipment destination station : " + stationName);
                }

                if (onlineValidation.getIsStopped() == 1) {
                    LinearLayout llStopShipment = dialogView.findViewById(R.id.ll_is_stop_shipment);
                    llStopShipment.setVisibility(View.VISIBLE);

                    TextView tvStopShipmentHeader = dialogView.findViewById(R.id.tv_stop_shipment_header);
                    tvStopShipmentHeader.setText("Stop Shipment");

                    TextView tvStopShipmentBody = dialogView.findViewById(R.id.tv_stop_shipment_body);
                    tvStopShipmentBody.setText("Stop shipment.Please Hold.");
                }

                if (onlineValidation.getIsRTORequest() == 1) {
                    LinearLayout llRto = dialogView.findViewById(R.id.ll_is_rto);
                    llRto.setVisibility(View.VISIBLE);

                    TextView tvRTOHeader = dialogView.findViewById(R.id.tv_rto_header);
                    tvRTOHeader.setText("RTO Request");

                    TextView tvRTOBody = dialogView.findViewById(R.id.tv_rto_body);
                    tvRTOBody.setText("RTO Request.");
                }

                if (onlineValidation.getIsDeliveryRequest() == 1) {
                    LinearLayout llDeliveryReq = dialogView.findViewById(R.id.ll_is_delivery_req);
                    llDeliveryReq.setVisibility(View.VISIBLE);

                    TextView tvDeliveryRequestHeader = dialogView.findViewById(R.id.tv_delivery_req_header);
                    tvDeliveryRequestHeader.setText("Delivery Request");

                    TextView tvDeliveryRequestBody = dialogView.findViewById(R.id.tv_delivery_req_body);
                    tvDeliveryRequestBody.setText("Delivery Request.");
                }

                if (onlineValidation.getIsCITCComplaint() == 1) {
                    LinearLayout llDeliveryReq = dialogView.findViewById(R.id.ll_citc_complaint);
                    llDeliveryReq.setVisibility(View.VISIBLE);

                    TextView tvDeliveryRequestHeader = dialogView.findViewById(R.id.tv_citc_header);
                    tvDeliveryRequestHeader.setText("CITC Complaint");

                    TextView tvDeliveryRequestBody = dialogView.findViewById(R.id.tv_citc_body);
                    tvDeliveryRequestBody.setText("The Shipment has a CITC Complaint.");
                }


                if (onlineValidation.getIsCAFRequest() == 1) {
                    LinearLayout llcaf = dialogView.findViewById(R.id.ll_caf_complaint);
                    llcaf.setVisibility(View.VISIBLE);

                    TextView tvDeliveryRequestHeader = dialogView.findViewById(R.id.tv_caf_header);
                    tvDeliveryRequestHeader.setText("CAF Complaint");

                    TextView tvDeliveryRequestBody = dialogView.findViewById(R.id.tv_caf_body);
                    tvDeliveryRequestBody.setText("The Shipment has a CAF Complaint.");
                }

                   /* if (pieceDetails.getIsRelabel() == 1) {
                        LinearLayout llIsRelabel = dialogView.findViewById(R.id.ll_is_relabel);
                        llIsRelabel.setVisibility(View.VISIBLE);
                        Log.d("test" , "IsRelabel");
                    }*/

                OnLineValidation onLineValidationLocal = dbConnections.getPieceInformationByWaybillNo(GlobalVar.getWaybillFromBarcode(onlineValidation.getBarcode())
                        , onlineValidation.getBarcode(), getApplicationContext(), false);
                String noOfAttempts = "";
                if (onLineValidationLocal != null || !WaybillAttempt.equals("No Data")) {
                    noOfAttempts = String.valueOf(onlineValidation.getNoOfAttempts());
                } else {
                    noOfAttempts = "No Data";
                }

                LinearLayout llNoOfAttempts = dialogView.findViewById(R.id.ll_no_attempts);
                llNoOfAttempts.setVisibility(View.VISIBLE);
                TextView tvNoOfAttemptsHeader = dialogView.findViewById(R.id.tv_no_attempts_header);
                tvNoOfAttemptsHeader.setText("Number of attempts");
                TextView tvNoOfAttemptsBody = dialogView.findViewById(R.id.tv_no_of_attempts_body);
                tvNoOfAttemptsBody.setText("Number of attempts : " + noOfAttempts);


                final android.app.AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // To avoid leaked window
                        if (alertDialog != null && alertDialog.isShowing()) {
                            alertDialog.dismiss();
                            txtBarCode.setText("");
                        }
                    }
                });


            }
        } catch (Exception e) {
            Log.d("test", "showDialog " + e.toString());
        }
    }

    private OnLineValidation getOnLineValidationPiece(String barcode) {
        try {
            for (OnLineValidation pieceDetail : onLineValidationList) {
                if (pieceDetail.getBarcode().equals(barcode))
                    return pieceDetail;
            }

        } catch (Exception e) {
            Log.d("test", "getOnLineValidationPiece " + e.toString());
        }
        return null;
    }

    @Override
    public void onCallComplete(boolean hasError, String errorMessage) {
        if (hasError)
            ErrorAlertOnlineValidation("Failed Loading File", "Kindly Try Again \n \n " + errorMessage);
        else
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "File uploaded successfully", GlobalVar.AlertType.Info);
    }


    private void getOnlineValidation() {
        APICall apiCall = new APICall(getApplicationContext(), InventoryControl_LocalValidation_oneByOne.this, this);
        apiCall.getOnlineValidationDataOffset(GlobalVar.DsAndInventoryTHCourier, 0, 1);
    }



    /* private void ReadFromLocal(Cursor result, DBConnections dbConnections) {


        isrtoReq.clear();
        try {
            if (result.getCount() > 0) {
                result.moveToFirst();

                do {

                    isrtoReq.add(result.getString(result.getColumnIndex("BarCode")));
                    try {
                        validupto.setText("Upto : " + result.getString(result.getColumnIndex("ValidDate")) + " 16:30");
                        inserteddate.setText("DLD : " + result.getString(result.getColumnIndex("InsertedDate")));
                    } catch (Exception e) {
                        System.out.println(e);
                    }

                } while (result.moveToNext());
            }
            result.close();

            Cursor cursor = dbConnections.Fill("select * from DeliverReq where ReqType = 1", getApplicationContext());
            if (cursor.getCount() > 0) {
                isdeliveryReq.clear();
                cursor.moveToFirst();
                do {

                    isdeliveryReq.add(cursor.getString(cursor.getColumnIndex("BarCode")));
                    try {
                        validupto.setText("Upto : " + cursor.getString(cursor.getColumnIndex("ValidDate")) + " 16:30");
                        inserteddate.setText("DLD : " + cursor.getString(cursor.getColumnIndex("InsertedDate")));
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                } while (cursor.moveToNext());
            }

            cursor = dbConnections.Fill("select * from DeliverReq where ReqType = 3 ", getApplicationContext());
            if (cursor.getCount() > 0) {
                iscitcshipments.clear();
                cursor.moveToFirst();
                do {

                    iscitcshipments.add(cursor.getString(cursor.getColumnIndex("BarCode")));

                } while (cursor.moveToNext());
            }


            delreqcount.setText("DEL Count : " + String.valueOf(isdeliveryReq.size()));
            rtoreqcount.setText("RTO Count : " + String.valueOf(isrtoReq.size()));
            citccount.setText("CITC Count : " + String.valueOf(iscitcshipments.size()));
            cursor.close();
            result.close();
            dbConnections.close();


        } catch (Exception e) {
            e.printStackTrace();
        }


    }*/
    /* private void isDeviceonline() {
        try {
            isdeviceonlinehandler = new Handler();
            isdeviceonlinehandler.postDelayed(new Runnable() {
                public void run() {
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

                        isdeviceonlinehandler.postDelayed(this, 10000);
                    } catch (Exception e) {

                        isdeviceonlinehandler.postDelayed(this, 10000);
                        Log.e("Dashboard thread", e.toString());
                    }

                }
            }, 10000);
        } catch (Exception e) {

        }

    }*/
    /* private void refreshData() {
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
    /* private long startTime = 1; // 15 MINS IDLE TIME
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

            ActivityCompat.finishAffinity(InventoryControlOnetab.this);
            Intent intent = new Intent(getApplicationContext(), SplashScreenActivity.class);
            startActivity(intent);
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }
    }*/
    /*private void deleteEmploy() {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        int id = dbConnections.getMaxID(" UserMeLogin where LogoutDate is NULL ", getApplicationContext());
        UserMeLogin userMeLogin = new UserMeLogin(id);
        dbConnections.UpdateUserMeLogout(userMeLogin, getApplicationContext());
        dbConnections.deleteUserME(GlobalVar.GV().EmployID);

        ActivityCompat.finishAffinity(InventoryControl_LocalValidation_oneByOne.this);
        Intent intent = new Intent(getApplicationContext(), SplashScreenActivity.class);
        startActivity(intent);

    }*/
    /*private void SaveData(String PieceCode, String req) {

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);

        com.naqelexpress.naqelpointer.DB.DBObjects.TerminalHandling checkPoint = new com.naqelexpress.naqelpointer.DB.DBObjects.TerminalHandling
                (20, String.valueOf(Latitude),
                        String.valueOf(Longitude), 44, req
                        , "", 0);

        if (dbConnections.InsertTerminalHandling(checkPoint, getApplicationContext())) {
            int ID = dbConnections.getMaxID("CheckPoint", getApplicationContext());

            CheckPointBarCodeDetails waybills = new CheckPointBarCodeDetails(PieceCode, ID);
            dbConnections.InsertCheckPointBarCodeDetails(waybills, getApplicationContext());

        }
        if (!isMyServiceRunning(TerminalHandling.class)) {
            startService(
                    new Intent(InventoryControl_LocalValidation_oneByOne.this,
                            com.naqelexpress.naqelpointer.service.TerminalHandling.class));
        }
    }*/
    /*private void insertManual() {
        somethingwrong = false;

        stopService(
                new Intent(InventoryControl_LocalValidation_oneByOne.this,
                        com.naqelexpress.naqelpointer.service.TerminalHandling.class));

        stopService(
                new Intent(InventoryControl_LocalValidation_oneByOne.this,
                        com.naqelexpress.naqelpointer.service.TerminalHandlingBulk.class));

        ids.clear();
        ids1.clear();
        try {
            DBConnections db = new DBConnections(getApplicationContext(), null);

            Cursor result = db.Fill("select * from CheckPoint where IsSync = 0 order by ID Limit 20 ", getApplicationContext());
            int count = 0;//result.getCount()
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
                new InventoryControl_LocalValidation_oneByOne.SaveAtTerminalHandling().execute(jsonData);


            } else {

                Cursor loop = db.Fill("select * from TerminalHandling order by ID", getApplicationContext());
                Cursor ts = db.Fill("select SUM(Count) As totalRecord  from TerminalHandling", getApplicationContext());
                ts.moveToFirst();
                totalsize = ts.getInt(ts.getColumnIndex("totalRecord"));
                ts.close();

                if (loop.getCount() > 0) {

                    loop.moveToFirst();
                    do {

                        if (somethingwrong)
                            break;
                        String jsonData = loop.getString(loop.getColumnIndex("Json"));
                        int jsonlegth = loop.getInt(loop.getColumnIndex("Count"));
                        int ID = loop.getInt(loop.getColumnIndex("ID"));
                        ids.add(ID);
                        jsonData = jsonData.replace("Date(-", "Date(");


                        new InventoryControl_LocalValidation_oneByOne.SaveAtTerminalHandling().execute(jsonData, String.valueOf(jsonlegth));


                    } while (loop.moveToNext());
                } else {
                    ErrorAlert("No Data",
                            "All Data Synchronized Successfully"
                    );
                }

                loop.close();


            }
            startService(
                    new Intent(InventoryControl_LocalValidation_oneByOne.this,
                            com.naqelexpress.naqelpointer.service.TerminalHandling.class));

//            startService(
//                    new Intent(InventoryControlOnetab.this,
//                            com.naqelexpress.naqelpointer.service.TerminalHandlingBulk.class));
        } catch (Exception e) {
            System.out.println(e);
        }

    }*/
    /*private class SaveAtTerminalHandling extends AsyncTask<String, Integer, String> {
        String result = "";
        StringBuffer buffer;
        int id = 0;
        String jsonData = "";


        @Override
        protected void onPreExecute() {


            if (progressDialog == null) {
//                progressDialog = ProgressDialog.show(InventoryControlOnetab.this,
//                        "Please wait.", "Your data is inserting by Manual...", true);

                progressDialog = new ProgressDialog(InventoryControl_LocalValidation_oneByOne.this);
                progressDialog.setMessage("your request is being process...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setMax(100);
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();

            }

            super.onPreExecute();

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values[0]);
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            jsonData = params[0];
            try {
                uploaddatacount = uploaddatacount + Integer.parseInt(params[1]);
            } catch (Exception e) {

            }

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

        @SuppressLint("WrongThread")
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

                    publishProgress((int) ((uploaddatacount * 100) / totalsize));
                    super.onPostExecute(String.valueOf(finalJson));

                } else {
                    somethingwrong = true;
                    LoadDivisionError(1);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                }
                isRunning = false;

                //insertManual();
                //publishProgress("" + (int) ((totalSize * 100) / FileSize));


//                if (progressDialog != null && progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                    progressDialog = null;
//                }

            } catch (Exception e) {
                System.out.println(e);
                //  insertManual();
            }
        }
    }*/
    /*private void fetchData(JSONObject jsonObject) {

        try {


            JSONArray deliveryReq = jsonObject.getJSONArray("DeliveryReq");

            DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
            dbConnections.deleteDeliverRtoReqData(getApplicationContext());

            if (deliveryReq.length() > 0) {

                // isdeliveryReq.clear();
                for (int i = 0; i < deliveryReq.length(); i++) {
                    JSONObject jsonObject1 = deliveryReq.getJSONObject(i);
                    // isdeliveryReq.add(jsonObject1.getString("WayBillNo"));
                    // isdeliveryReq.add(jsonObject1.getString("WayBillNo"));
                    // isdeliveryReq.add(jsonObject1.getString("BarCode"));
                    // String insertdata[] = jsonObject1.getString("InsertedDate").split("T");
                    dbConnections.InsertDeliverReq(jsonObject1.getInt("WayBillNo"),
                            jsonObject1.getString("BarCode"), GlobalVar.GV().getDateAdd1Day() + " 16:30:", getApplicationContext());

                    //tripdata.add(temp);
                }
            }

            JSONArray rtoreq = jsonObject.getJSONArray("RTOReq");

            int rtolength = rtoreq.length();
            if (rtolength > 0) {

                // isrtoReq.clear();
                for (int i = 0; i < 1; i++) {
                    JSONObject jsonObject1 = rtoreq.getJSONObject(i);
//                    isrtoReq.add(jsonObject1.getString("WayBillNo"));
                    // isrtoReq.add(jsonObject1.getString("BarCode"));
                    //tripdata.add(temp);
                    //String insertdata[] = jsonObject1.getString("InsertedDate").split("T");
                    dbConnections.InsertRtoReq(jsonObject1.getInt("WayBillNo"),
                            jsonObject1.getString("BarCode"), GlobalVar.GV().getDateAdd1Day() + " 16:30:", getApplicationContext());

                }
            }


            Cursor result = dbConnections.Fill("select * from RtoReq", getApplicationContext());
            ReadFromLocal(result, dbConnections);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/
    /*private void SaveData(String piece) { //43 heldin , 44 heldout

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        if (IsValid()) {
            requestLocation();
            com.naqelexpress.naqelpointer.DB.DBObjects.TerminalHandling checkPoint = new com.naqelexpress.naqelpointer.DB.DBObjects.TerminalHandling
                    (20, String.valueOf(Latitude),
                            String.valueOf(Longitude), 0, lbTotal.getText().toString()
                            , "", 0);

            if (dbConnections.InsertTerminalHandling(checkPoint, getApplicationContext())) {
                int ID = dbConnections.getMaxID("CheckPoint", getApplicationContext());


                CheckPointBarCodeDetails checkPointBarCodeDetails = new CheckPointBarCodeDetails(piece, ID);
                dbConnections.InsertCheckPointBarCodeDetails(checkPointBarCodeDetails, getApplicationContext());

                if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.TerminalHandling.class)) {
                    startService(
                            new Intent(InventoryControl_LocalValidation_oneByOne.this,
                                    com.naqelexpress.naqelpointer.service.TerminalHandling.class));
                }
            }
        }
        dbConnections.close();
    }*/
    /* private void resetAllData() {
        // lbTotal.setText(getString(R.string.lbCount) + " 0");
        inventorycontrol.clear();
        delrtoreq.clear();
        adapter.notifyDataSetChanged();
    }*/
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
    /*private void SaveHeldOutData(String piece, String refno) {

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        com.naqelexpress.naqelpointer.DB.DBObjects.TerminalHandling checkPoint = new com.naqelexpress.naqelpointer.DB.DBObjects.TerminalHandling
                (20, String.valueOf(Latitude),
                        String.valueOf(Longitude), 44, refno
                        , "", 0);

        if (dbConnections.InsertTerminalHandling(checkPoint, getApplicationContext())) {
            int ID = dbConnections.getMaxID("CheckPoint", getApplicationContext());

            CheckPointBarCodeDetails waybills = new CheckPointBarCodeDetails(piece, ID);
            dbConnections.InsertCheckPointBarCodeDetails(waybills, getApplicationContext());

        }

        if (!isMyServiceRunning(TerminalHandling.class)) {
            startService(
                    new Intent(InventoryControl_LocalValidation_oneByOne.this,
                            com.naqelexpress.naqelpointer.service.TerminalHandling.class));
        }

    }*/
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
    /*private void initSwipe() {
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(InventoryControl_LocalValidation_oneByOne.this);
                    builder.setTitle("Confirm Deleting")
                            .setMessage("Are you sure you want to delete?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    adapter.removeItem(position);
                                    lbTotal.setText(getString(R.string.lbCount) + inventorycontrol.size());
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
}

