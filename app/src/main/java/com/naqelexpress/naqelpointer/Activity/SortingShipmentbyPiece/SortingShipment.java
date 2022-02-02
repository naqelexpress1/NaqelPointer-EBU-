package com.naqelexpress.naqelpointer.Activity.SortingShipmentbyPiece;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.naqelexpress.naqelpointer.DB.InsertintoDB;
import com.naqelexpress.naqelpointer.DB.SelectData;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.Models.Enum.Enum;
import com.naqelexpress.naqelpointer.Models.Request.CommonRequest;
import com.naqelexpress.naqelpointer.Models.TLAllocationAreaModels;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.callback.AlertCallback;
import com.naqelexpress.naqelpointer.callback.Callback;
import com.naqelexpress.naqelpointer.utils.CommonApi;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

// Created by Ismail on 21/03/2018.

public class SortingShipment extends AppCompatActivity implements AlertCallback {

    TextView lbTotal, lbl_downloaddatime;
    private EditText txtBarCode;
    public ArrayList<String> arrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private DataAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.notdeliveredsecondfragement);

        Button btn_getTLData = (Button) findViewById(R.id.getsorting);
        btn_getTLData.setVisibility(View.VISIBLE);

        lbl_downloaddatime = (TextView) findViewById(R.id.dowloaddateandcount);
        lbl_downloaddatime.setVisibility(View.VISIBLE);

        lbTotal = (TextView) findViewById(R.id.lbTotal);
        lbTotal.setText("");
        txtBarCode = (EditText) findViewById(R.id.txtWaybilll);
        txtBarCode.setKeyListener(null);
        txtBarCode.setHint("PieceID");
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
                if (!GlobalVar.GV().checkPermission(SortingShipment.this, GlobalVar.PermissionType.Camera)) {
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.NeedCameraPermission),
                            GlobalVar.AlertType.Error);
                    GlobalVar.GV().askPermission(SortingShipment.this, GlobalVar.PermissionType.Camera);
                } else {
                    Intent intent = new Intent(SortingShipment.this, NewBarCodeScanner.class);
                    startActivityForResult(intent, GlobalVar.GV().CAMERA_PERMISSION_REQUEST);
                }
            }
        });

        initViews();
        setDownloadCount();
    }

    private void setDownloadCount() {
        try {
            SelectData selectData = new SelectData();
            lbl_downloaddatime.setText(selectData.TLAllocationAreaDataCount(getApplicationContext()));
        } catch (Exception e) {

        }
    }

    public void btn_getTLData(View view) {

        GlobalVar.GV().alertMsgAll("Info", "Please wait,your request has been process..", SortingShipment.this,
                Enum.PROGRESS_TYPE, "SortingShipment");
        CommonRequest commonRequest = new CommonRequest();
        commonRequest.setStationID(GlobalVar.GV().StationID);

        CommonApi.FetchSortingTlAllocation(new Callback<List<TLAllocationAreaModels>>() {
            @Override
            public void returnResult(List<TLAllocationAreaModels> result) {
                System.out.println();
                InsertintoDB insertintoDB = new InsertintoDB();
                insertintoDB.insertTLAllcocationAreaBulk(result, getApplicationContext());
                //areaModelsList.addAll(result);
                setDownloadCount();
                exitDialog();
            }

            @Override
            public void returnError(String message) {
                //mView.showError(message);
                System.out.println(message);
                exitDialog();
            }


        }, commonRequest);


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


        if (!GlobalVar.ValidateAutomacticDate(getApplicationContext())) {
            GlobalVar.RedirectSettings(SortingShipment.this);
            return;
        }


        try {
            double convert = Double.parseDouble(txtBarCode.getText().toString());
        } catch (Exception e) {
            GlobalVar.MakeSound(getApplicationContext(), R.raw.wrongbarcodescan);
            GlobalVar.ShowDialog(SortingShipment.this, "Error", "Incorrect Piece Barcode(" + txtBarCode.getText().toString() + ")", true
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
            if (txtBarCode.getText().toString().length() == 13 ||
                    txtBarCode.getText().toString().length() == GlobalVar.ScanBarcodeLength) {
                isTLAllocatePieces(txtBarCode.getText().toString());
                arrayList.add(0, txtBarCode.getText().toString());
                lbTotal.setText(String.valueOf(arrayList.size()));
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
                    GlobalVar.RedirectSettings(SortingShipment.this);
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
                .setMessage("Are you sure you want to exit ?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        // countDownTimer.cancel();
                        SortingShipment.super.onBackPressed();
                    }
                }).setNegativeButton("Cancel", null).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void isTLAllocatePieces(String barcode) {
        SelectData selectData = new SelectData();
        String tlname = selectData.isTLAllocationAreabyPiece(getApplicationContext(), barcode);
        if (tlname.length() > 0) {
            GlobalVar.ShowDialog(SortingShipment.this, "", tlname, true);
            GlobalVar.MakeSound(getApplicationContext(), R.raw.delivery);
        }


    }


    @Override
    public void returnOk(int ok, Activity activity) {

    }


    static SweetAlertDialog sweetAlertDialog;

    @Override
    public void returnCancel(int cancel, SweetAlertDialog alertDialog) {
        sweetAlertDialog = alertDialog;
    }

    private void exitDialog() {
        if (sweetAlertDialog != null)
            sweetAlertDialog.dismissWithAnimation();
    }

}