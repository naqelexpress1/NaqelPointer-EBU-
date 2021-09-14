package com.naqelexpress.naqelpointer.Activity.ValidationDS;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.Classes.NewBarCodeScannerForVS;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.JSON.Request.BringMyRouteShipmentsRequest;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.Retrofit.APICall;
import com.naqelexpress.naqelpointer.Retrofit.IAPICallListener;
import com.naqelexpress.naqelpointer.Retrofit.Models.OnLineValidation;
import com.naqelexpress.naqelpointer.service.Discrepancy;

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

/**
 * Created by Hasna on 11/11/18.
 */

public class ValidationDS extends AppCompatActivity implements IAPICallListener {

    ArrayList<HashMap<String, String>> conflict = new ArrayList<>();
    public static ArrayList<String> scannedBarCode = new ArrayList<>();
    ArrayList<HashMap<String, String>> waybilldetails = new ArrayList<>();
    ArrayList<HashMap<String, String>> ScannedBarCode = new ArrayList<>();
    public static ArrayList<String> ScanbyDevice = new ArrayList<>();
    public static ArrayList<String> ConflictBarcode = new ArrayList<>();
    private List<OnLineValidation> onLineValidationList = new ArrayList<>();
    private DBConnections dbConnections;
    private String division;

    private GridView waybilgrid;
    BarCode adapter;
    EditText txtBarcode;
    TextView count;
    EditText employid;

    private static final String TAG = "ValidationDS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.validationds);

        dbConnections = new DBConnections(getApplicationContext(), null);
        division = GlobalVar.GV().getDivisionID(getApplicationContext(), GlobalVar.GV().EmployID);


        try {
            // Get shipment info for Courier || TH
            if (division.equals("Courier")) {
                if (!isValidValidationFile()) {
                    getOnlineValidationFile();
                }
            }
        } catch (Exception e) {
            Log.d("test", TAG + " " + e.toString());
        }

        conflict.clear();
        scannedBarCode.clear();
        waybilldetails.clear();
        ScannedBarCode.clear();
        ScanbyDevice.clear();
        ConflictBarcode.clear();

        waybilgrid = (GridView) findViewById(R.id.barcode);
//        adapter = new BarCode(ScannedBarCode, getApplicationContext());
        adapter = new BarCode(conflict, getApplicationContext());
        waybilgrid.setAdapter(adapter);

        txtBarcode = (EditText) findViewById(R.id.txtBarcode);
//        txtBarcode.setKeyListener(null);
        employid = (EditText) findViewById(R.id.employ);
        count = (TextView) findViewById(R.id.count);

        Button btnOpenCamera = (Button) findViewById(R.id.btnOpenCamera);
        Button validate = (Button) findViewById(R.id.validate);
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetDeliverysheet();
            }
        });
        btnOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!GlobalVar.GV().checkPermission(ValidationDS.this, GlobalVar.PermissionType.Camera)) {
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.NeedCameraPermission), GlobalVar.AlertType.Error);
                    GlobalVar.GV().askPermission(ValidationDS.this, GlobalVar.PermissionType.Camera);
                } else {
                    if (waybilldetails.size() > 0) {
                        scannedBarCode.clear();
                        ScanbyDevice.clear();
                        ConflictBarcode.clear();
                        Bundle bundle = new Bundle();
                        Intent intent = new Intent(ValidationDS.this.getApplicationContext(), NewBarCodeScannerForVS.class);
                        bundle.putSerializable("scannedBarCode", scannedBarCode);
                        bundle.putSerializable("ScanbyDevice", ScanbyDevice);
                        bundle.putSerializable("ConflictBarcode", ConflictBarcode);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, 2);
                    } else {
                        NoData();
                    }
                }
            }
        });

        txtBarcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (txtBarcode != null && txtBarcode.getText().length() == GlobalVar.ScanBarcodeLength || txtBarcode.getText().length() == 13) {
                    String result = txtBarcode.getText().toString();
                    if (!GlobalVar.GV().isValidBarcode(result)) {
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Wrong Barcode", GlobalVar.AlertType.Warning);
                        GlobalVar.GV().MakeSound(getApplicationContext(), R.raw.wrongbarcodescan);
                        txtBarcode.setText("");
                        return;
                    }


                    // To show onlineValidation warning if any
                    boolean isConflict = !scannedBarCode.contains(result);
                    if (division.equals("Courier")) {
                        onlineValidation(result, isConflict);
                    }


                    if (!GlobalVar.GV().isValidBarcode(result)) {
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Wrong Barcode", GlobalVar.AlertType.Warning);
                        GlobalVar.GV().MakeSound(getApplicationContext(), R.raw.wrongbarcodescan);
                        txtBarcode.setText("");
                        return;
                    }


                    if (scannedBarCode.contains(result)) {
                        GlobalVar.MakeSound(getApplicationContext(), R.raw.barcodescanned);
                        if (!ScanbyDevice.contains(result))
                            ScanbyDevice.add(result);

                    } else {
                        GlobalVar.MakeSound(getApplicationContext(), R.raw.wrongbarcodescan);
                        if (!ConflictBarcode.contains(result))
                            ConflictBarcode.add(result);

                        if (!division.equals("Courier")) //For courier popup will be shown in onlineValidation
                            conflict(result);
                    }
                    txtBarcode.setText("");
                    doaction();
                }
            }
        });

    }

    public void conflict(String Barcode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ValidationDS.this);
        builder.setTitle("Warning " + Barcode)
                .setMessage("This Piece is not belongs to this Employee")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void GetDeliverysheet() {
        GlobalVar.hideKeyboardFrom(getApplicationContext(), getWindow().getDecorView().getRootView());

        if (employid.getText().toString().length() > 4) {

            insertDiscrepancy();

            count.setText("Count - 0");
            conflict.clear();
            scannedBarCode.clear();
            waybilldetails.clear();
            ScannedBarCode.clear();
            ScanbyDevice.clear();
            ConflictBarcode.clear();
            adapter.notifyDataSetChanged();

//            GlobalVar.hideKeyboardFrom(getApplicationContext(), getWindow().getDecorView().getRootView());
            BringMyRouteShipmentsRequest bringMyRouteShipmentsRequest = new BringMyRouteShipmentsRequest();
            bringMyRouteShipmentsRequest.EmployID = Integer.parseInt(employid.getText().toString());

            new BringMyRouteShipmentsList().execute(JsonSerializerDeserializer.serialize(bringMyRouteShipmentsRequest, true));

        } else
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly enter valid employ ID", GlobalVar.AlertType.Error);
    }

    public void NoData() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ValidationDS.this);
        builder.setTitle("Warning ")
                .setMessage("Kindly please Load the Deliverysheet")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        GetDeliverysheet();
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void insertDiscrepancy() {
        if (ConflictBarcode.size() > 0) {
            try {
                JSONObject header = new JSONObject();
                JSONArray jsonArray = new JSONArray();

                for (int i = 0; i < ConflictBarcode.size(); i++) {
                    JSONObject jsonObject = new JSONObject();

                    jsonObject.put("BarCode", ConflictBarcode.get(i));
                    jsonObject.put("OperationEmp", GlobalVar.GV().EmployID);
                    jsonObject.put("EmployID", employid.getText().toString());
                    jsonObject.put("ScanDate", DateTime.now().toString());
                    jsonArray.put(jsonObject);

                }

                header.put("Discrepancy", jsonArray);
                DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                dbConnections.InsertDiscrepancy(header.toString(), getApplicationContext());
                dbConnections.close();

                stopService(new Intent(ValidationDS.this, Discrepancy.class));
                startService(new Intent(ValidationDS.this, Discrepancy.class));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

   /* @Override
    public void onTaskComplete(boolean hasError, String errorMessage) {
        if (hasError)
            ErrorAlert("Failed Loading File" , "Kindly contact your supervisor \n \n " + errorMessage);
        else
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "File uploaded successfully", GlobalVar.AlertType.Info);
    }*/

    @Override
    public void onCallComplete(boolean hasError, String errorMessage) {
        try {
            if (hasError)
                ErrorAlert("File Not Loaded", "Kindly check your internet connection & Try Again \n \n " + errorMessage);
            else
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "File uploaded successfully", GlobalVar.AlertType.Info);
        } catch (Exception ex) {
        }
    }

    private void ErrorAlert(final String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(ValidationDS.this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Try Again",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getOnlineValidationFile();
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

    private class BringMyRouteShipmentsList extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;
        String result = "";
        StringBuffer buffer;
        int buttonclick;

        @Override
        protected void onPreExecute() {

            progressDialog = ProgressDialog.show(ValidationDS.this, "Please wait.", "Downloading Shipments Details.", true);
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];

            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "ValidationDS");
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

            if (finalJson != null) {

                try {
                    waybilldetails.clear();
                    scannedBarCode.clear();

                    ArrayList<HashMap<String, String>> ds = new ArrayList<>();
                    JSONObject jsonObjectHeader = new JSONObject(finalJson);
                    JSONArray jsonObjectDeliverySheet = jsonObjectHeader.getJSONArray("DeliverySheet");
                    for (int i = 0; i < jsonObjectDeliverySheet.length(); i++) {
                        JSONObject jsonObject = jsonObjectDeliverySheet.getJSONObject(i);
                        HashMap<String, String> temp = new HashMap<>();
                        temp.put("WayBillNo", jsonObject.getString("WayBillNo"));
                        temp.put("BarCode", jsonObject.getString("BarCode"));
                        temp.put("bgcolor", "2");
                        scannedBarCode.add(jsonObject.getString("BarCode"));
                        waybilldetails.add(temp);
                        conflict.add(temp);
                        count.setText("Piece Count - " + String.valueOf(waybilldetails.size()));
                    }

                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } else
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.wentwrong), GlobalVar.AlertType.Error);
            if (progressDialog != null)
                progressDialog.dismiss();
        }
    }

//    private class BringMyRouteShipmentsList extends AsyncTask<String, Void, String> {
//        private ProgressDialog progressDialog;
//        String result = "";
//        StringBuffer buffer;
//        int buttonclick;
//
//        @Override
//        protected void onPreExecute() {
//
//            progressDialog = ProgressDialog.show(ValidationDS.this, "Please wait.", "Downloading Shipments Details.", true);
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            String jsonData = params[0];
//
//            HttpURLConnection httpURLConnection = null;
//            OutputStream dos = null;
//            InputStream ist = null;
//
//            try {
//                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "ValidationDS");
//                httpURLConnection = (HttpURLConnection) url.openConnection();
//
//                httpURLConnection.setRequestMethod("POST");
//                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//                httpURLConnection.setDoInput(true);
//                httpURLConnection.setDoOutput(true);
//                httpURLConnection.connect();
//
//                dos = httpURLConnection.getOutputStream();
//                httpURLConnection.getOutputStream();
//                dos.write(jsonData.getBytes());
//
//                ist = httpURLConnection.getInputStream();
//                String line;
//                BufferedReader reader = new BufferedReader(new InputStreamReader(ist));
//                buffer = new StringBuffer();
//
//                while ((line = reader.readLine()) != null) {
//                    buffer.append(line);
//                }
//                return String.valueOf(buffer);
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    if (ist != null)
//                        ist.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    if (dos != null)
//                        dos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                if (httpURLConnection != null)
//                    httpURLConnection.disconnect();
//                result = String.valueOf(buffer);
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String finalJson) {
//
//            if (finalJson != null) {
//
//                try {
//                    waybilldetails.clear();
//                    ScannedBarCode.clear();
//                    ScannedBarCode.addAll(conflict);
//                    ArrayList<HashMap<String, String>> ds = new ArrayList<>();
//                    JSONObject jsonObjectHeader = new JSONObject(finalJson);
//                    JSONArray jsonObjectDeliverySheet = jsonObjectHeader.getJSONArray("DeliverySheet");
//                    for (int i = 0; i < jsonObjectDeliverySheet.length(); i++) {
//                        JSONObject jsonObject = jsonObjectDeliverySheet.getJSONObject(i);
//                        HashMap<String, String> temp = new HashMap<>();
//                        temp.put("WayBillNo", jsonObject.getString("WayBillNo"));
//                        temp.put("BarCode", jsonObject.getString("BarCode"));
//                        temp.put("bgcolor", "0");
//
//                        waybilldetails.add(temp);
//                    }
//
//                    for (int j = 0; j < ScannedBarCode.size(); j++) {
//                        boolean add = false;
//                        for (int i = 0; i < waybilldetails.size(); i++) {
//
//                            if (ScannedBarCode.get(j).get("BarCode").equals(waybilldetails.get(i).get("BarCode"))) {
//                                add = true;
//                                waybilldetails.get(i).put("bgcolor", "1");
//                                ScannedBarCode.get(j).put("bgcolor", "1");
//                                break;
//                            }
//                        }
//                        if (!add) {
//                            ScannedBarCode.get(j).put("bgcolor", "2");
//                        }
//                    }
//
//                    for (int k = 0; k < waybilldetails.size(); k++) {
//                        for (int i = 0; i < ScannedBarCode.size(); i++) {
//                            if (!waybilldetails.get(k).get("BarCode").equals(ScannedBarCode.get(i).get("BarCode"))) {
//                                waybilldetails.get(k).put("bgcolor", "3");
//                                ScannedBarCode.add(waybilldetails.get(k));
//                                break;
//                            }
//
//                        }
//                    }
//                    adapter.notifyDataSetChanged();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//
//            } else
//                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.wentwrong), GlobalVar.AlertType.Error);
//            if (progressDialog != null)
//                progressDialog.dismiss();
//        }
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == 2 && resultCode == RESULT_OK) {
                if (data != null) {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        if (extras.containsKey("scannedBarCode")) {
                            scannedBarCode.clear();
                            scannedBarCode = (ArrayList<String>) extras.getSerializable("scannedBarCode");

                        }
                        if (extras.containsKey("ScanbyDevice")) {
                            ScanbyDevice.clear();
                            ScanbyDevice = (ArrayList<String>) extras.getSerializable("ScanbyDevice");

                        }
                        if (extras.containsKey("ConflictBarcode")) {
                            ConflictBarcode.clear();
                            ConflictBarcode = (ArrayList<String>) extras.getSerializable("ConflictBarcode");

                        }
                    }

                }
            }
        } catch (Exception ex) {
            Toast.makeText(ValidationDS.this, ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }

    }

//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == 2 && resultCode == RESULT_OK) {
//            if (data != null) {
//                Bundle extras = data.getExtras();
//                if (extras != null) {
//                    if (extras.containsKey("scannedBarCode")) {
//                        scannedBarCode.clear();
//                        scannedBarCode = (ArrayList<String>) extras.getSerializable("scannedBarCode");
//
//                    }
//                    if (extras.containsKey("ScanbyDevice")) {
//                        ScanbyDevice.clear();
//                        ScanbyDevice = (ArrayList<String>) extras.getSerializable("ScanbyDevice");
//
//                    }
//                    if (extras.containsKey("ConflictBarcode")) {
//                        ConflictBarcode.clear();
//                        ConflictBarcode = (ArrayList<String>) extras.getSerializable("ConflictBarcode");
//
//                    }
//                }
//
//            }
//        }
//    }

    @Override
    protected void onStart() {
        super.onStart();
        doaction();

    }

    private void doaction() {
        conflict.clear();
        for (int i = 0; i < ConflictBarcode.size(); i++) {
            HashMap<String, String> temp = new HashMap<>();
            temp.put("WayBillNo", "");
            temp.put("BarCode", ConflictBarcode.get(i));
            temp.put("bgcolor", "3");
            conflict.add(temp);
        }

        for (int i = 0; i < waybilldetails.size(); i++) {
            String barcode = waybilldetails.get(i).get("BarCode");
            boolean added = false;
            for (int j = 0; j < ScanbyDevice.size(); j++) {
                if (barcode.equals(ScanbyDevice.get(j))) {
                    added = true;
                    waybilldetails.get(i).put("bgcolor", "1");
                    conflict.add(waybilldetails.get(i));
                    break;
                }
            }
            if (!added) {
                waybilldetails.get(i).put("bgcolor", "2");
                conflict.add(waybilldetails.get(i));
            }

        }

        adapter.notifyDataSetChanged();

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (employid.getText().toString().trim().replace(" ", "").length() > 0)
            outState.putString("CourierID", employid.getText().toString());
        else
            outState.putString("CourierID", "");
        outState.putString("Count", count.getText().toString());
        outState.putInt("EmployID", GlobalVar.GV().EmployID);
        outState.putInt("UserID", GlobalVar.GV().UserID);
        outState.putInt("StationID", GlobalVar.GV().StationID);
        outState.putString("EmployMobileNo", GlobalVar.GV().EmployMobileNo);
        outState.putString("EmployName", GlobalVar.GV().EmployName);
        outState.putString("EmployStation", GlobalVar.GV().EmployStation);
        outState.putParcelable("currentSettings", GlobalVar.GV().currentSettings);
        outState.putInt("currentSettingsID", GlobalVar.GV().currentSettings.ID);
        outState.putStringArrayList("scannedBarCode", scannedBarCode);
        outState.putStringArrayList("ScanbyDevice", ScanbyDevice);
        outState.putStringArrayList("ConflictBarcode", ConflictBarcode);
        outState.putSerializable("conflict", conflict);
        outState.putSerializable("waybilldetails", waybilldetails);
        outState.putSerializable("ScannedBarCode", ScannedBarCode);
        //Parcelable state = waybilgrid.onSaveInstanceState();
        //outState.putParcelable("state", state);


    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            employid.setText(savedInstanceState.getString("CourierID"));
            count.setText(savedInstanceState.getString("Count"));
            GlobalVar.GV().EmployID = savedInstanceState.getInt("EmployID");
            GlobalVar.GV().UserID = savedInstanceState.getInt("UserID");
            GlobalVar.GV().StationID = savedInstanceState.getInt("StationID");
            GlobalVar.GV().EmployMobileNo = savedInstanceState.getString("EmployMobileNo");
            GlobalVar.GV().EmployName = savedInstanceState.getString("EmployName");
            GlobalVar.GV().EmployStation = savedInstanceState.getString("EmployStation");
            GlobalVar.GV().currentSettings = savedInstanceState.getParcelable("currentSettings");
            GlobalVar.GV().currentSettings.ID = savedInstanceState.getInt("currentSettingsID");
            scannedBarCode = savedInstanceState.getStringArrayList("scannedBarCode");
            ScanbyDevice = savedInstanceState.getStringArrayList("ScanbyDevice");
            ConflictBarcode = savedInstanceState.getStringArrayList("ConflictBarcode");
            conflict = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("conflict");
            waybilldetails = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("waybilldetails");
            ScannedBarCode = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("ScannedBarCode");
//            waybilgrid.setAdapter(adapter);
//            waybilgrid.onRestoreInstanceState(savedInstanceState.getParcelable("state"));

            adapter = new BarCode(conflict, getApplicationContext());
            waybilgrid.setAdapter(adapter);

            doaction();

        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit ValidationDS")
                .setMessage("Are you sure you want to exit without saving?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        insertDiscrepancy();
                        ValidationDS.super.onBackPressed();
                    }
                }).setNegativeButton("Cancel", null).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private boolean isValidValidationFile() {
        boolean isValid;

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        isValid = dbConnections.isValidValidationFile(GlobalVar.DsValidationCourier, getApplicationContext());
        if (isValid)
            return true;
        return false;
    }

    private void onlineValidation(String barcode, boolean isConflict) {
        boolean isShowWarning = false;
        try {
            OnLineValidation onLineValidationLocal = dbConnections.getPieceInformationByWaybillNo(GlobalVar.getWaybillFromBarcode(barcode)
                    , barcode, getApplicationContext());

            OnLineValidation onLineValidation = new OnLineValidation();

            if (onLineValidationLocal != null) {


                if (onLineValidationLocal.getIsMultiPiece() == 1) {
                    onLineValidation.setIsMultiPiece(1);
                    isShowWarning = true;
                }

                if (onLineValidationLocal.getIsStopped() == 1) {
                    onLineValidation.setIsStopped(1);
                    isShowWarning = true;
                }
            }

            if (isConflict) {
                onLineValidation.setIsConflict(1);
                isShowWarning = true;
            }

            if (isShowWarning) {
                onLineValidation.setBarcode(barcode);
                onLineValidationList.add(onLineValidation);
                showDialog(getOnLineValidationPiece(barcode));
            }

        } catch (Exception e) {
            Log.d("test", "isValidPieceBarcode " + e.toString());
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

    public void showDialog(OnLineValidation pieceDetails) {
        try {
            if (pieceDetails != null) {
                final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(ValidationDS.this);
                LayoutInflater inflater = this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.custom_alert_dialog, null);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setCancelable(false);

                TextView tvBarcode = dialogView.findViewById(R.id.tv_barcode);
                tvBarcode.setText("Piece #" + pieceDetails.getBarcode());


                Button btnConfirm = dialogView.findViewById(R.id.btn_confirm);
                btnConfirm.setVisibility(View.VISIBLE);
                btnConfirm.setText("OK");


                if (pieceDetails.getIsMultiPiece() == 1) {
                    LinearLayout llMultiPiece = dialogView.findViewById(R.id.ll_is_multi_piece);
                    llMultiPiece.setVisibility(View.VISIBLE);

                    TextView tvMultiPieceHeader = dialogView.findViewById(R.id.tv_multiPiece_header);
                    tvMultiPieceHeader.setText("Multi Piece");

                    TextView tvMultiPieceBody = dialogView.findViewById(R.id.tv_multiPiece_body);
                    tvMultiPieceBody.setText("Please check pieces.");
                }

                if (pieceDetails.getIsStopped() == 1) {
                    LinearLayout llStopShipment = dialogView.findViewById(R.id.ll_is_stop_shipment);
                    llStopShipment.setVisibility(View.VISIBLE);

                    TextView tvStopShipmentHeader = dialogView.findViewById(R.id.tv_stop_shipment_header);
                    tvStopShipmentHeader.setText("Stop Shipment");

                    TextView tvStopShipmentBody = dialogView.findViewById(R.id.tv_stop_shipment_body);
                    tvStopShipmentBody.setText("Stop shipment.Please Hold.");
                }

                if (pieceDetails.getIsConflict() == 1) {
                    LinearLayout llStopShipment = dialogView.findViewById(R.id.ll_ds_validation);
                    llStopShipment.setVisibility(View.VISIBLE);

                    TextView tvStopShipmentHeader = dialogView.findViewById(R.id.tv_ds_validation_header);
                    tvStopShipmentHeader.setText("DS Validation");

                    TextView tvStopShipmentBody = dialogView.findViewById(R.id.tv_ds_validation_body);
                    tvStopShipmentBody.setText("Shipment is not belong to employee.");
                }


                final android.app.AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // To avoid leaked window
                        if (alertDialog != null && alertDialog.isShowing()) {
                            alertDialog.dismiss();
                        }
                    }
                });


            }
        } catch (Exception e) {
            Log.d("test", "showDialog " + e.toString());
        }
    }

    public void getOnlineValidationFile() {
        APICall apiCall = new APICall(getApplicationContext(), ValidationDS.this, this);
        apiCall.getOnlineValidationDataOffset(GlobalVar.DsValidationCourier, 0, 1);

    }


}
