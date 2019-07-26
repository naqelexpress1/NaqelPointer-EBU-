package com.naqelexpress.naqelpointer.Activity.ValidationDS;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.Classes.NewBarCodeScanner;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.JSON.Request.BringMyRouteShipmentsRequest;
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

/**
 * Created by Hasna on 11/11/18.
 */

public class ValidationDS extends AppCompatActivity {

    ArrayList<HashMap<String, String>> conflict = new ArrayList<>();
    ArrayList<String> scannedBarCode = new ArrayList<>();
    ArrayList<HashMap<String, String>> waybilldetails = new ArrayList<>();
    ArrayList<HashMap<String, String>> ScannedBarCode = new ArrayList<>();
    private GridView waybilgrid;
    BarCode adapter;
    EditText txtBarcode;
    TextView count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.validationds);

        waybilgrid = (GridView) findViewById(R.id.barcode);
        adapter = new BarCode(ScannedBarCode, getApplicationContext());
        waybilgrid.setAdapter(adapter);

        txtBarcode = (EditText) findViewById(R.id.txtBarcode);
        final EditText employid = (EditText) findViewById(R.id.employ);
        count = (TextView) findViewById(R.id.count);

        Button btnOpenCamera = (Button) findViewById(R.id.btnOpenCamera);
        Button validate = (Button) findViewById(R.id.validate);
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (employid.getText().toString().length() > 4) {


                    GlobalVar.hideKeyboardFrom(getApplicationContext(), getWindow().getDecorView().getRootView());
                    BringMyRouteShipmentsRequest bringMyRouteShipmentsRequest = new BringMyRouteShipmentsRequest();
                    bringMyRouteShipmentsRequest.EmployID = Integer.parseInt(employid.getText().toString());

                    new BringMyRouteShipmentsList().execute(JsonSerializerDeserializer.serialize(bringMyRouteShipmentsRequest, true));

                } else
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly enter valid employ ID", GlobalVar.AlertType.Error);
            }
        });
        btnOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!GlobalVar.GV().checkPermission(ValidationDS.this, GlobalVar.PermissionType.Camera)) {
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.NeedCameraPermission), GlobalVar.AlertType.Error);
                    GlobalVar.GV().askPermission(ValidationDS.this, GlobalVar.PermissionType.Camera);
                } else {
                    Intent intent = new Intent(ValidationDS.this.getApplicationContext(), NewBarCodeScanner.class);
                    startActivityForResult(intent, GlobalVar.GV().CAMERA_PERMISSION_REQUEST);
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
                if (txtBarcode != null && txtBarcode.getText().length() == 13)
                    AddNewPiece();
            }
        });

    }

    private void AddNewPiece() {
        if (!scannedBarCode.contains(txtBarcode.getText().toString())) {

            GlobalVar.GV().MakeSound(this, R.raw.barcodescanned);

            HashMap<String, String> temp = new HashMap<>();
            temp.put("BarCode", txtBarcode.getText().toString());
            temp.put("bgcolor", "0");
            conflict.add(temp);
            ScannedBarCode.add(temp);
            scannedBarCode.add(txtBarcode.getText().toString());
            count.setText(getString(R.string.lbCount) + scannedBarCode.size());
            adapter.notifyDataSetChanged();
            txtBarcode.setText("");

        } else {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.AlreadyExists), GlobalVar.AlertType.Warning);
            GlobalVar.GV().MakeSound(this, R.raw.wrongbarcodescan);
            txtBarcode.setText("");
        }
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
                    ScannedBarCode.clear();
                    ScannedBarCode.addAll(conflict);
                    ArrayList<HashMap<String, String>> ds = new ArrayList<>();
                    JSONObject jsonObjectHeader = new JSONObject(finalJson);
                    JSONArray jsonObjectDeliverySheet = jsonObjectHeader.getJSONArray("DeliverySheet");
                    for (int i = 0; i < jsonObjectDeliverySheet.length(); i++) {
                        JSONObject jsonObject = jsonObjectDeliverySheet.getJSONObject(i);
                        HashMap<String, String> temp = new HashMap<>();
                        temp.put("WayBillNo", jsonObject.getString("WayBillNo"));
                        temp.put("BarCode", jsonObject.getString("BarCode"));
                        temp.put("bgcolor", "0");

                        waybilldetails.add(temp);
                    }

                    for (int j = 0; j < ScannedBarCode.size(); j++) {
                        boolean add = false;
                        for (int i = 0; i < waybilldetails.size(); i++) {

                            if (ScannedBarCode.get(j).get("BarCode").equals(waybilldetails.get(i).get("BarCode"))) {
                                add = true;
                                waybilldetails.get(i).put("bgcolor", "1");
                                ScannedBarCode.get(j).put("bgcolor", "1");
                                break;
                            }
                        }
                        if (!add) {
                            ScannedBarCode.get(j).put("bgcolor", "2");
                        }
                    }

                    for (int k = 0; k < waybilldetails.size(); k++) {
                        for (int i = 0; i < ScannedBarCode.size(); i++) {
                            if (!waybilldetails.get(k).get("BarCode").equals(ScannedBarCode.get(i).get("BarCode"))) {
                                waybilldetails.get(k).put("bgcolor", "3");
                                ScannedBarCode.add(waybilldetails.get(k));
                                break;
                            }

                        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GlobalVar.GV().CAMERA_PERMISSION_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    if (extras.containsKey("barcode")) {
                        String barcode = extras.getString("barcode");
                        txtBarcode.setText(barcode);

                    }
                }

            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("EmployID", GlobalVar.GV().EmployID);
        outState.putInt("UserID", GlobalVar.GV().UserID);
        outState.putInt("StationID", GlobalVar.GV().StationID);
        outState.putString("EmployMobileNo", GlobalVar.GV().EmployMobileNo);
        outState.putString("EmployName", GlobalVar.GV().EmployName);
        outState.putString("EmployStation", GlobalVar.GV().EmployStation);
        outState.putParcelable("currentSettings", GlobalVar.GV().currentSettings);
        outState.putInt("currentSettingsID", GlobalVar.GV().currentSettings.ID);
        outState.putStringArrayList("scannedBarCode", scannedBarCode);
        outState.putSerializable("conflict", conflict);
        outState.putSerializable("waybilldetails", waybilldetails);
        outState.putSerializable("ScannedBarCode", ScannedBarCode);


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
            GlobalVar.GV().currentSettings = savedInstanceState.getParcelable("currentSettings");
            GlobalVar.GV().currentSettings.ID = savedInstanceState.getInt("currentSettingsID");
            scannedBarCode = savedInstanceState.getStringArrayList("scannedBarCode");
            conflict = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("conflict");
            waybilldetails = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("waybilldetails");
            ScannedBarCode = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("ScannedBarCode");

        }
    }
}
