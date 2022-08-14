package com.naqelexpress.naqelpointer.TerminalHandling;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.location.Location;
import android.os.Bundle;
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// Created by Ismail on 21/03/2018.

public class InventoryHeldOut extends AppCompatActivity implements View.OnClickListener {


    ArrayList<HashMap<String, String>> delrtoreq = new ArrayList<>();
    private Spinner heldoutreasons;

    TextView lbTotal;
    private EditText txtBarCode, txtbinlocation;
    public ArrayList<String> inventorycontrol = new ArrayList<>();
    public ArrayList<String> isdeliveryReq = new ArrayList<>();

    public ArrayList<String> isrtoReq = new ArrayList<>();
    public ArrayList<String> isHeldout = new ArrayList<>();

    private RecyclerView recyclerView;
    private DataAdapter adapter;
    private Paint p = new Paint();
    List<String> heldoutlist, heldoutid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.notdeliveredsecondfragement);

        //countDownTimer = new MyCountDownTimer(startTime, interval);
        //countDownTimer.start();

        lbTotal = (TextView) findViewById(R.id.lbTotal);

        txtBarCode = (EditText) findViewById(R.id.txtWaybilll);
        txtbinlocation = (EditText) findViewById(R.id.txtbinlocation);
        txtbinlocation.setVisibility(View.GONE);

        txtBarCode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(GlobalVar.BarcodeLength)});

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
        btnOpenCamera.setVisibility(recyclerView.GONE);
        btnOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!GlobalVar.GV().checkPermission(InventoryHeldOut.this, GlobalVar.PermissionType.Camera)) {
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.NeedCameraPermission), GlobalVar.AlertType.Error);
                    GlobalVar.GV().askPermission(InventoryHeldOut.this, GlobalVar.PermissionType.Camera);
                } else {
                    Intent intent = new Intent(InventoryHeldOut.this, NewBarCodeScanner.class);
                    startActivityForResult(intent, GlobalVar.GV().CAMERA_PERMISSION_REQUEST);
                }
            }
        });

        heldoutreasons = (Spinner) findViewById(R.id.heloutreason);
        heldoutreasons.setVisibility(View.VISIBLE);
        heldoutlist = new ArrayList<String>();

        heldoutlist.add("Handover to outlet");
        heldoutlist.add("Miscode");
        heldoutlist.add("Urgent delivery");
        heldoutlist.add("Handover to claim");
        heldoutid = new ArrayList<String>();
        heldoutid.add("47");
        heldoutid.add("48");
        heldoutid.add("49");
        heldoutid.add("50");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, heldoutlist);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        heldoutreasons.setAdapter(dataAdapter);

    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DataAdapter(inventorycontrol);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        //initSwipe();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GlobalVar.GV().CAMERA_PERMISSION_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    if (extras.containsKey("barcode")) {
                        String barcode = extras.getString("barcode");
                        if (barcode.length() == 13 || barcode.length() == GlobalVar.ScanBarcodeLength)
                            txtBarCode.setText(barcode);

                    }
                }

            }
        }
    }

    private void AddNewPiece() {

//        if (inventorycontrol.size() == 50) {
//            ErrorAlert("Kindly save the Data and Scan Again");
//            return;
//        }

        if (!GlobalVar.ValidateAutomacticDate(getApplicationContext())) {
            GlobalVar.RedirectSettings(InventoryHeldOut.this);
            return;
        }

        if (!GlobalVar.GV().isValidBarcode(txtBarCode.getText().toString())) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Wrong Barcode", GlobalVar.AlertType.Warning);
            GlobalVar.GV().MakeSound(getApplicationContext(), R.raw.wrongbarcodescan);
            txtBarCode.setText("");
            return;
        }
        String barcode = GlobalVar.GV().ReplaceBarcodeCharcater(txtBarCode.getText().toString());

        try {
            double convert = Double.parseDouble(barcode);
        } catch (Exception e) {
            GlobalVar.GV().MakeSound(getApplicationContext(), R.raw.wrongbarcodescan);
            ErrorAlert(
                    "Incorrect Piece Barcode(" + barcode + ")"
            );
            txtBarCode.setText("");
            return;
        }

        if (!inventorycontrol.contains(barcode)) {
            // if (txtBarCode.getText().toString().length() == 13) {

            //SaveData(txtBarCode.getText().toString());

            HashMap<String, String> temp = new HashMap<>();
            temp.put("WayBillNo", barcode);
            temp.put("Status", "0");
            temp.put("Ref", "");
            delrtoreq.add(temp);

            inventorycontrol.add(0, barcode);
            lbTotal.setText(getString(R.string.lbCount) + inventorycontrol.size());
            txtBarCode.setText("");
            initViews();
            // }
        } else {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.AlreadyExists), GlobalVar.AlertType.Warning);
            GlobalVar.GV().MakeSound(getApplicationContext(), R.raw.wrongbarcodescan);
            txtBarCode.setText("");
        }

        if (delrtoreq.size() == 20) {
            SaveData(2);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(InventoryHeldOut.this);
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
            case R.id.mnuSave:
                if (GlobalVar.ValidateAutomacticDate(getApplicationContext())) {
                    ErrorAlert("Info", "Are yo sure want to Finish the Job?", 2, "");
                } else
                    GlobalVar.RedirectSettings(InventoryHeldOut.this);
                return true;
            case R.id.manual:
                if (GlobalVar.ValidateAutomacticDate(getApplicationContext())) {
                    ErrorAlert("Info", "Are yo sure want to upload Manual?", 3, "");
                } else
                    GlobalVar.RedirectSettings(InventoryHeldOut.this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void ErrorAlert(final String title, String message, final int clear, final String piececode) {
        AlertDialog alertDialog = new AlertDialog.Builder(InventoryHeldOut.this).create();
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
                            // insertManual1();
                            // insertManual1();

                            if (delrtoreq.size() == 20) {
                                SaveData(2);
                            }
//                        if (piececode.length() > 0)
//                            SaveData(piececode, title);
                    }
                });

        alertDialog.show();
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

    private void SaveData(String Piece) {

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        if (IsValid()) {
            requestLocation();
            boolean IsSaved = true;

            com.naqelexpress.naqelpointer.DB.DBObjects.TerminalHandling checkPoint = new com.naqelexpress.naqelpointer.DB.DBObjects.TerminalHandling
                    (20, String.valueOf(Latitude),
                            String.valueOf(Longitude), 44, ""
                            , "", 0);

            if (dbConnections.InsertTerminalHandling(checkPoint, getApplicationContext())) {
                int ID = dbConnections.getMaxID("CheckPoint", getApplicationContext());


                CheckPointBarCodeDetails checkPointBarCodeDetails = new CheckPointBarCodeDetails(Piece, ID);
                dbConnections.InsertCheckPointBarCodeDetails(checkPointBarCodeDetails, getApplicationContext());

                if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.TerminalHandling.class)) {
                    startService(
                            new Intent(InventoryHeldOut.this,
                                    com.naqelexpress.naqelpointer.service.TerminalHandling.class));
                }

            }
        }
        dbConnections.close();
    }


    private void SaveData(int clear) { //43 heldin , 44 heldout

        if (!GlobalVar.ValidateAutomacticDate(getApplicationContext())) {
            GlobalVar.RedirectSettings(InventoryHeldOut.this);
            return;
        }


        if (delrtoreq.size() == 0) {
            ErrorAlert("Kindly Scan atleast one Piece Barcode");
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
                    jsonObject.put("TerminalHandlingScanStatusID", 24);
                    jsonObject.put("TerminalHandlingScanStatusReasonID",
                            heldoutid.get(heldoutreasons.getSelectedItemPosition()));
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
                        new Intent(InventoryHeldOut.this,
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

    private void resetAllData() {
        txtbinlocation.setText("");
        lbTotal.setText(getString(R.string.lbCount) + " 0");
        inventorycontrol.clear();
        delrtoreq.clear();
        adapter.notifyDataSetChanged();
    }

    private boolean IsValid() {
        boolean isValid = true;


        if (inventorycontrol.size() <= 0) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to scan the PieceBarcode",
                    GlobalVar.AlertType.Error);
            return false;
        }

//        if (txtbinlocation.getText().toString().replace(" ", "").length() == 0) {
//            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to scan Bin Location",
//                    GlobalVar.AlertType.Error);
//            return false;
//        }
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
                        // countDownTimer.cancel();
                        InventoryHeldOut.super.onBackPressed();
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

    private void ErrorAlert(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(InventoryHeldOut.this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Error");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        txtBarCode.setText("");
                    }
                });

        alertDialog.show();
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

//    private long startTime = 1000; // 15 MINS IDLE TIME
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
//            ActivityCompat.finishAffinity(InventoryHeldOut.this);
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