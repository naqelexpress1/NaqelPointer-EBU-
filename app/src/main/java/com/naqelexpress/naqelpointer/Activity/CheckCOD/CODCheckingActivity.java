package com.naqelexpress.naqelpointer.Activity.CheckCOD;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.JSON.Request.CheckBeforeSubmitCODRequest;
import com.naqelexpress.naqelpointer.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

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
import java.util.Calendar;
import java.util.HashMap;

public class CODCheckingActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    EditText lbEmployID, lbDeliverySheetID, lbCashAmount, lbPOSAmount;

    Button btnCheck;
    TextView lbResult, posamount, total;
    RecyclerView deliverysheet;
    private CheckCODAdapter adapter;
    private ArrayList<HashMap<String, String>> coddata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.codchecking);


        lbEmployID = (EditText) findViewById(R.id.lbEmployID);
        lbDeliverySheetID = (EditText) findViewById(R.id.lbDeliverySheetID);
        lbDeliverySheetID.setKeyListener(null);
        deliverysheet = (RecyclerView) findViewById(R.id.deliverysheet);
        coddata = new ArrayList<>();

        lbDeliverySheetID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CODCheckingActivity.this,
                        now.get(Calendar.YEAR), // Initial year selection
                        now.get(Calendar.MONTH), // Initial month selection
                        now.get(Calendar.DAY_OF_MONTH) // Inital day selection
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });
        lbCashAmount = (EditText) findViewById(R.id.lbCashAmount);
        lbPOSAmount = (EditText) findViewById(R.id.lbPOSAmount);
        btnCheck = (Button) findViewById(R.id.btnCheck);
        lbResult = (TextView) findViewById(R.id.lbResult);
        posamount = (TextView) findViewById(R.id.posamt);
        total = (TextView) findViewById(R.id.totalamount);

        lbEmployID.setText(String.valueOf(GlobalVar.GV().EmployID));
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid())
                    CheckCOD();
            }
        });
        GlobalVar.hideKeyboardFrom(getApplicationContext(), getWindow().getDecorView().getRootView());
    }

    private boolean isValid() {
        boolean result = true;
        GlobalVar.hideKeyboardFrom(getApplicationContext(), getWindow().getDecorView().getRootView());
        if (lbEmployID.getText().toString().replace(" ", "").length() == 0) {
            //result = false;
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You to enter the Employ ID", GlobalVar.AlertType.Error);
            return false;
        }

        if (lbDeliverySheetID.getText().toString().replace(" ", "").length() == 0) {
            // result = false;
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You to enter the Delivery Sheet No", GlobalVar.AlertType.Error);
            return false;
        }

//        if (lbCashAmount.getText().toString().replace(" ", "").length() == 0) {
//            // result = false;
//            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You to enter the Cash Amount", GlobalVar.AlertType.Error);
//            return false;
//        }
//
//        if (lbPOSAmount.getText().toString().replace(" ", "").length() == 0) {
//            //result = false;
//            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You to enter the POS Amount", GlobalVar.AlertType.Error);
//            return false;
//        }

        return result;
    }

    private void setadapter() {
        deliverysheet.setHasFixedSize(true);
        deliverysheet.addItemDecoration(new DividerItemDecoration(deliverysheet.getContext(), DividerItemDecoration.VERTICAL));
        //RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(LinearLayo);
        //deliverysheet.setLayoutManager(layoutManager);
        adapter = new CheckCODAdapter(coddata);
        deliverysheet.setAdapter(adapter);


    }

    //--------------Checking COD -------------------------------
    public void CheckCOD() {


        CheckBeforeSubmitCODRequest checkBeforeSubmitCODRequest = new CheckBeforeSubmitCODRequest();
        checkBeforeSubmitCODRequest.EmployID = GlobalVar.GV().getIntegerFromString(lbEmployID.getText().toString());
        //checkBeforeSubmitCODRequest.DeliverySheetID = GlobalVar.GV().getIntegerFromString(lbDeliverySheetID.getText().toString());
        checkBeforeSubmitCODRequest.DeliverySheetDate = lbDeliverySheetID.getText().toString();


//        if (!lbCashAmount.getText().toString().equals(""))
//            checkBeforeSubmitCODRequest.TotalCash = GlobalVar.GV().getDoubleFromString(lbCashAmount.getText().toString());
//        if (!lbPOSAmount.getText().toString().equals(""))
//            checkBeforeSubmitCODRequest.TotalPOS = GlobalVar.GV().getDoubleFromString(lbPOSAmount.getText().toString());

        String jsonData = JsonSerializerDeserializer.serialize(checkBeforeSubmitCODRequest, true);
        new CheckCODData().execute(jsonData);
    }

    private class CheckCODData extends AsyncTask<String, Void, String> {
        String result = "";
        StringBuffer buffer;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(CODCheckingActivity.this, "Please wait.", "Checking your COD Request"
                    , true);
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "CheckCODAmount");//CheckBeforeSubmitCOD
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
            super.onPostExecute(String.valueOf(finalJson));
            if (finalJson != null) {

                setCODData(finalJson);
//                CheckBeforeSubmitCODResult result = new CheckBeforeSubmitCODResult(finalJson);
//
//                if (result.Notes != null && !result.Notes.equals("null"))
//                    lbResult.setText("Result :-" + result.Notes);
//                else
//                    lbResult.setText("No Data");

            } else
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.wentwrong), GlobalVar.AlertType.Error);

            progressDialog.dismiss();
        }
    }

    private void setCODData(String finalJson) {
        coddata.clear();
        double totalCOD = 0.0, pos = 0.0;
        try {
            JSONObject jsonObject = new JSONObject(finalJson);
            if (jsonObject.getString("HasError").equals("false")) {
                JSONArray jsonArray = jsonObject.getJSONArray("COD");
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        HashMap<String, String> temp = new HashMap<>();
                        temp.put("WayBillNo", jsonObject1.getString("WayBillNo"));

                        double roundOff = Math.round(jsonObject1.getDouble("CODAMOUNT") * 100.0) / 100.0;
                        temp.put("CODAMOUNT", String.valueOf(roundOff));
                        roundOff = Math.round(jsonObject1.getDouble("POSAmount") * 100.0) / 100.0;
                        double fcash = (Math.round(jsonObject1.getDouble("Cash") * 100.0) / 100.0);
                        double differ = (Math.round(jsonObject1.getDouble("CODAMOUNT") * 100.0) / 100.0) - (roundOff + fcash);
                        temp.put("POS", String.valueOf(roundOff));
                        totalCOD = totalCOD + jsonObject1.getDouble("CODAMOUNT");
                        pos = pos + jsonObject1.getDouble("POSAmount");
                        temp.put("Cash", String.valueOf(fcash));
                        temp.put("Differ", String.valueOf((Math.round(differ * 100.0) / 100.0)));

                        coddata.add(temp);

                    }
                    totalCOD = Math.round(totalCOD * 100.0) / 100.0;
                    lbResult.setText(String.valueOf(totalCOD));
                    pos = Math.round(pos * 100.0) / 100.0;
                    posamount.setText(String.valueOf(pos));
                    total.setText(String.valueOf(totalCOD - pos));

                } else
                    lbResult.setText("No Data with " + lbDeliverySheetID.getText().toString() + " this Date");
            } else
                lbResult.setText("No Data with " + lbDeliverySheetID.getText().toString() + " this Date");

            setadapter();
        } catch (JSONException e1) {
            e1.printStackTrace();


        }


    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        String selecteddate = String.valueOf(year) + "-" + String.valueOf(monthOfYear + 1) + "-" + String.valueOf(dayOfMonth);
        lbDeliverySheetID.setText(selecteddate);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("lbDeliverySheetID", lbDeliverySheetID.getText().toString());
        outState.putString("lbCashAmount", lbCashAmount.getText().toString());
        outState.putString("lbPOSAmount", lbPOSAmount.getText().toString());
        outState.putString("lbResult", lbResult.getText().toString());
        outState.putInt("EmployID", GlobalVar.GV().EmployID);
        outState.putInt("UserID", GlobalVar.GV().UserID);
        outState.putInt("StationID", GlobalVar.GV().StationID);
        outState.putString("EmployMobileNo", GlobalVar.GV().EmployMobileNo);
        outState.putString("EmployName", GlobalVar.GV().EmployName);
        outState.putString("EmployStation", GlobalVar.GV().EmployStation);
        outState.putParcelable("currentSettings", GlobalVar.GV().currentSettings);
        outState.putInt("currentSettingsID", GlobalVar.GV().currentSettings.ID);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        lbDeliverySheetID.setText(savedInstanceState.getString("lbDeliverySheetID"));
        lbCashAmount.setText(savedInstanceState.getString("lbCashAmount"));
        lbPOSAmount.setText(savedInstanceState.getString("lbPOSAmount"));
        lbResult.setText(savedInstanceState.getString("lbResult"));

        GlobalVar.GV().EmployID = savedInstanceState.getInt("EmployID");
        GlobalVar.GV().UserID = savedInstanceState.getInt("UserID");
        GlobalVar.GV().StationID = savedInstanceState.getInt("StationID");
        GlobalVar.GV().EmployMobileNo = savedInstanceState.getString("EmployMobileNo");
        GlobalVar.GV().EmployName = savedInstanceState.getString("EmployName");
        GlobalVar.GV().EmployStation = savedInstanceState.getString("EmployStation");
        GlobalVar.GV().currentSettings = savedInstanceState.getParcelable("currentSettings");
        GlobalVar.GV().currentSettings.ID = savedInstanceState.getInt("currentSettingsID");
    }
}