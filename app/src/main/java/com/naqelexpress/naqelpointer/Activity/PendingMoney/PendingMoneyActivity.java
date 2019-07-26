package com.naqelexpress.naqelpointer.Activity.PendingMoney;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.JSON.Request.CheckPendingCODRequest;
import com.naqelexpress.naqelpointer.JSON.Results.CheckPendingCODResult;
import com.naqelexpress.naqelpointer.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class PendingMoneyActivity
        extends AppCompatActivity {
    private SwipeMenuListView listview;
    private PendingListAdapter adapter;
    Button btnCheck;
    EditText txtEmploy;
    public static ArrayList<CheckPendingCODResult> checkPendingCODList;
    TextView nodata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pendingmoney);

        listview = (SwipeMenuListView) findViewById(R.id.pendingCODView);

        txtEmploy = (EditText) findViewById(R.id.txtEmployID);
        nodata = (TextView) findViewById(R.id.nodata);
        txtEmploy.setText(String.valueOf(GlobalVar.GV().EmployID));
        btnCheck = (Button) findViewById(R.id.btnCheck);
        checkPendingCODList = new ArrayList<>();

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!txtEmploy.getText().toString().equals("")) {
                    CheckPendingCODRequest checkPendingCODRequest = new CheckPendingCODRequest();
                    checkPendingCODRequest.EmployID = Integer.parseInt(txtEmploy.getText().toString());
                    BringPendingMoney(checkPendingCODRequest);
                }
            }
        });
        setAdapter();
    }

    private void setAdapter() {
        adapter = new PendingListAdapter(getApplicationContext(), checkPendingCODList);
        listview.setAdapter(adapter);
    }

    //------------------------Bring Pending Money -------------------------------
    public void BringPendingMoney(CheckPendingCODRequest checkCODPendingRequest) {

        String jsonData = JsonSerializerDeserializer.serialize(checkCODPendingRequest, true);
        new BringPendingMoneyList().execute(jsonData);
    }

    private class BringPendingMoneyList extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;
        String result = "";
        StringBuffer buffer;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(PendingMoneyActivity.this, "Please wait.", "Downloading Pending Money Details.", true);
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "CheckPendingCOD");
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
            progressDialog.dismiss();
            super.onPostExecute(String.valueOf(finalJson));
            if (finalJson != null) {
                checkPendingCODList.clear();
                new CheckPendingCODResult(finalJson);
                adapter.notifyDataSetChanged();
                nodata.setVisibility(View.GONE);

            } else {
                //GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.NoPendingCOD), GlobalVar.AlertType.Info);
                nodata.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("txtEmploy", txtEmploy.getText().toString());
        outState.putParcelableArrayList("checkPendingCODList", checkPendingCODList);
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
        if (savedInstanceState != null) {
            checkPendingCODList = savedInstanceState.getParcelableArrayList("checkPendingCODList");
            txtEmploy.setText(savedInstanceState.getString("txtEmploy"));
            GlobalVar.GV().EmployID = savedInstanceState.getInt("EmployID");
            GlobalVar.GV().UserID = savedInstanceState.getInt("UserID");
            GlobalVar.GV().StationID = savedInstanceState.getInt("StationID");
            GlobalVar.GV().EmployMobileNo = savedInstanceState.getString("EmployMobileNo");
            GlobalVar.GV().EmployName = savedInstanceState.getString("EmployName");
            GlobalVar.GV().EmployStation = savedInstanceState.getString("EmployStation");
            GlobalVar.GV().currentSettings = savedInstanceState.getParcelable("currentSettings");
            GlobalVar.GV().currentSettings.ID = savedInstanceState.getInt("currentSettingsID");
            setAdapter();
        }

    }
}