package com.naqelexpress.naqelpointer.Activity.AddtoScope;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.Activity.Delivery.DataAdapter;
import com.naqelexpress.naqelpointer.Classes.NewBarCodeScanner;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.Models.AddtoScopeModels;
import com.naqelexpress.naqelpointer.R;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;

// Created by Ismail on 21/03/2018.

public class AddtoScope extends AppCompatActivity implements View.OnClickListener {


    ArrayList<HashMap<String, String>> delrtoreq = new ArrayList<>();

    HashMap<String, String> trips = new HashMap<>();
    TextView lbTotal;
    private EditText txtBarCode;

    public ArrayList<String> arrayList = new ArrayList<>();


    private RecyclerView recyclerView;
    private DataAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.notdeliveredsecondfragement);


        lbTotal = (TextView) findViewById(R.id.lbTotal);
        lbTotal.setText("");
        txtBarCode = (EditText) findViewById(R.id.txtWaybilll);
        txtBarCode.setKeyListener(null);
        txtBarCode.setHint("NCL / PID");
        txtBarCode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(GlobalVar.ScanBarcodeLength)});
        txtBarCode.setInputType(InputType.TYPE_CLASS_TEXT);

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
                if (!GlobalVar.GV().checkPermission(AddtoScope.this, GlobalVar.PermissionType.Camera)) {
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.NeedCameraPermission), GlobalVar.AlertType.Error);
                    GlobalVar.GV().askPermission(AddtoScope.this, GlobalVar.PermissionType.Camera);
                } else {
                    Intent intent = new Intent(AddtoScope.this, NewBarCodeScanner.class);
                    startActivityForResult(intent, GlobalVar.GV().CAMERA_PERMISSION_REQUEST);
                }
            }
        });

        initViews();

    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DataAdapter(arrayList);
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
                        if (barcode.length() == 10 || barcode.length() == 13 || barcode.length() == GlobalVar.ScanBarcodeLength)
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
        if (!GlobalVar.ValidateAutomacticDate(getApplicationContext())) {
            GlobalVar.RedirectSettings(AddtoScope.this);
            return;
        }


        try {
            double convert = Double.parseDouble(txtBarCode.getText().toString());
        } catch (Exception e) {
            GlobalVar.MakeSound(getApplicationContext(), R.raw.wrongbarcodescan);
            GlobalVar.ShowDialog(AddtoScope.this, "Error", "Incorrect Piece Barcode(" + txtBarCode.getText().toString() + ")", true
            );

            txtBarCode.setText("");
            return;
        }

      /*  if (lbTotal.getText().toString().replace(" ", "").length() == 0) {
            GlobalVar.hideKeyboardFrom(getApplicationContext(), getWindow().getDecorView().getRootView());
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to scan Bin Location",
                    GlobalVar.AlertType.Error);
            GlobalVar.MakeSound(getApplicationContext(), R.raw.wrongbarcodescan);
            return;
        }*/


        if (!arrayList.contains(txtBarCode.getText().toString())) {
            if (txtBarCode.getText().toString().length() == 13 || txtBarCode.getText().toString().length() == 10 ||
                    txtBarCode.getText().toString().length() == GlobalVar.ScanBarcodeLength) {

                SaveData(txtBarCode.getText().toString());
                arrayList.add(0, txtBarCode.getText().toString());
                txtBarCode.setText("");
                initViews();

            }
        } else {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.AlreadyExists), GlobalVar.AlertType.Warning);
            GlobalVar.MakeSound(getApplicationContext(), R.raw.wrongbarcodescan);
            txtBarCode.setText("");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // MenuInflater inflater = getMenuInflater();
        // inflater.inflate(R.menu.checkpointmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.mnuSave:
                if (GlobalVar.ValidateAutomacticDate(getApplicationContext())) {
                    //SaveData();
                } else
                    GlobalVar.RedirectSettings(AddtoScope.this);
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


    private void SaveData(String piece) {

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        requestLocation();
        if (GlobalVar.GV().EmployID == 0 || String.valueOf(GlobalVar.GV().EmployID).length() < 2)
            GlobalVar.GV().EmployID = GlobalVar.GetEmployID(getApplicationContext());

        AddtoScopeModels addtoScopeModels = new AddtoScopeModels();
        addtoScopeModels.setLatitude(String.valueOf(Latitude));
        addtoScopeModels.setLongitude(String.valueOf(Longitude));
        addtoScopeModels.setPIDNCL(piece);
        addtoScopeModels.setTimeIn(DateTime.now());
        addtoScopeModels.setUserID(GlobalVar.GV().EmployID);


        if (!dbConnections.insertAddtoScope(getApplicationContext(), addtoScopeModels)) {
            GlobalVar.ShowDialog(AddtoScope.this, "Error", "Scanned PID/NCL(" + piece + ") Not Save , Please Scan Again", true);
            arrayList.remove(piece);
            adapter.notifyDataSetChanged();


        }

        StartService();

        dbConnections.close();
    }

    private void StartService() {
        if (!GlobalVar.isMyServiceRunning(com.naqelexpress.naqelpointer.service.AddtoScopeService.class, getApplicationContext())) {
            startService(
                    new Intent(AddtoScope.this,
                            com.naqelexpress.naqelpointer.service.AddtoScopeService.class));
        }
    }

    private void resetAllData() {
        lbTotal.setText(getString(R.string.lbCount) + " 0");
        arrayList.clear();
        delrtoreq.clear();
        adapter.notifyDataSetChanged();
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
            arrayList = savedInstanceState.getStringArrayList("arrayList");

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
        outState.putSerializable("arrayList", arrayList);
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
                        AddtoScope.super.onBackPressed();
                    }
                }).setNegativeButton("Cancel", null).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {


        }
    }


}