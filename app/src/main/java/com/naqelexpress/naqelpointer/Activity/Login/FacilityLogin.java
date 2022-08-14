package com.naqelexpress.naqelpointer.Activity.Login;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.naqelexpress.naqelpointer.Activity.MainPage.MainPageActivity;
import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.UserFacility;
import com.naqelexpress.naqelpointer.DB.DBObjects.UserMeLogin;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.JSON.Request.GetUserMEDataRequest;
import com.naqelexpress.naqelpointer.R;

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


public class FacilityLogin
        extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    Spinner city, facility;
    ArrayList<HashMap<String, String>> citymap = new ArrayList<>();
    ArrayList<HashMap<String, String>> facilitymap = new ArrayList<>();
    FacilityAdapter facilityAdapter;
    CityAdapter cityAdapter;
    int usertype = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.facility);

        try {
            city = (Spinner) findViewById(R.id.citycode);
            facility = (Spinner) findViewById(R.id.facilitycode);
            facility.setOnItemSelectedListener(this);

            Bundle bundle = getIntent().getExtras();
            usertype = bundle.getInt("usertype"); //getIntent().getIntExtra("usertype", 0);

            GlobalVar.GV().ResetTriedCount();

            FacilityStatus();

            cityAdapter = new CityAdapter(citymap, getApplicationContext());
            city.setAdapter(cityAdapter);

            FacilityAdapter facilityAdapter = new FacilityAdapter(facilitymap, getApplicationContext());
            facility.setAdapter(facilityAdapter);


            if (savedInstanceState != null)
                setSavedInstance(savedInstanceState);

//            String division = GlobalVar.getDivisionID(getApplicationContext(), GlobalVar.GV().EmployID);
//            if (GlobalVar.GV().IsTerminalApp || division.equals("IRS")) {
//                Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
//                startActivity(intent);
//                finish();
//            } else {
//                Intent intent = new Intent(getApplicationContext(), VerifyMobileNo.class);
//                startActivity(intent);
//                finish();
//            }


        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }


    public void Login(View view) {
        updateFacilityLogin();
    }

    private void updateFacilityLogin() {
        int facilityid = Integer.parseInt(facilitymap.get(facility.getSelectedItemPosition()).get("ID"));
        GlobalVar.hideKeyboardFrom(getApplicationContext(), getWindow().getDecorView().getRootView());
        if (facilityid != 0) {

            GetUserMEDataRequest getUserMEDataRequest = new GetUserMEDataRequest();
            getUserMEDataRequest.EmployID = GlobalVar.GV().EmployID;
            if (citymap.size() > 0)
                getUserMEDataRequest.FacilityID = facilityid;
            getUserMEDataRequest.DeviceToken = "";


            String jsonData = JsonSerializerDeserializer.serialize(getUserMEDataRequest, true);

            new GetMasterData().execute(jsonData);
        } else
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly please select Facility ", GlobalVar.AlertType.Error);
    }

    public void FacilityStatus() {

        facilitymap.clear();

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);

        HashMap<String, String> defalt = new HashMap<>();
        defalt.put("ID", "0");
        defalt.put("Code", "0");
        defalt.put("Name", "please select Facility");
        defalt.put("FTID", "0");
        facilitymap.add(defalt);


//        Cursor result = dbConnections.Fill("select * from Station where ID = " + GlobalVar.GV().StationID, getApplicationContext());
        Cursor result = dbConnections.Fill("select * from Facility where FacilityTypeID = " + usertype
                + " and Station = " + GlobalVar.GV().StationID, getApplicationContext());
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {
                HashMap<String, String> temp = new HashMap<>();

                temp.put("ID", String.valueOf(result.getInt(result.getColumnIndex("FacilityID"))));
                temp.put("Code", result.getString(result.getColumnIndex("Code")));
                temp.put("Name", result.getString(result.getColumnIndex("Name")));
                temp.put("FTID", String.valueOf(result.getInt(result.getColumnIndex("FacilityTypeID"))));
                facilitymap.add(temp);

            }
            while (result.moveToNext());
        } else
            Logout();

        dbConnections.close();

    }

    private void Logout() {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        int id = dbConnections.getMaxID(" UserMeLogin where LogoutDate is NULL ", getApplicationContext());
        UserMeLogin userMeLogin = new UserMeLogin(id);
        dbConnections.UpdateUserMeLogout(userMeLogin, getApplicationContext());
        dbConnections.close();
        android.os.Process.killProcess(android.os.Process.myPid());
        Intent intent = new Intent(getApplicationContext(), SplashScreenActivity.class);
        startActivity(intent);

    }


    public void GetRegion_FacilityCode(int FTID) {

        citymap.clear();


        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);

        Cursor result = dbConnections.Fill("select * from Facility where FacilityID = " + FTID, getApplicationContext());
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {
                HashMap<String, String> temp = new HashMap<>();

                temp.put("ID", String.valueOf(result.getInt(result.getColumnIndex("FacilityID"))));
                temp.put("Code", result.getString(result.getColumnIndex("Code")));
                temp.put("Name", result.getString(result.getColumnIndex("Name")));
                temp.put("FTID", String.valueOf(result.getInt(result.getColumnIndex("FacilityTypeID"))));
                citymap.add(temp);

            }
            while (result.moveToNext());
        }

        dbConnections.close();
        cityAdapter.notifyDataSetChanged();

//        facilityAdapter.notifyDataSetChanged();

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (facilitymap.size() > 0) {
//            String item = parent.getItemAtPosition(position).toString();

            GetRegion_FacilityCode(Integer.parseInt(facilitymap.get(position).get("ID")));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putInt("EmployID", GlobalVar.GV().EmployID);
        outState.putInt("UserID", GlobalVar.GV().UserID);
        outState.putInt("StationID", GlobalVar.GV().StationID);
        outState.putString("EmployMobileNo", GlobalVar.GV().EmployMobileNo);
        outState.putString("EmployName", GlobalVar.GV().EmployName);
        outState.putString("EmployStation", GlobalVar.GV().EmployStation);
        outState.putInt("usertype", usertype);


        super.onSaveInstanceState(outState, outPersistentState);
    }

    private void setSavedInstance(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            GlobalVar.GV().EmployID = savedInstanceState.getInt("EmployID");
            GlobalVar.GV().UserID = savedInstanceState.getInt("UserID");
            GlobalVar.GV().StationID = savedInstanceState.getInt("StationID");
            GlobalVar.GV().EmployMobileNo = savedInstanceState.getString("EmployMobileNo");
            GlobalVar.GV().EmployName = savedInstanceState.getString("EmployName");
            GlobalVar.GV().EmployStation = savedInstanceState.getString("EmployStation");
            usertype = savedInstanceState.getInt("usertype");


        }
    }

    ProgressDialog progressDialog;
    String devision = "";

    private class GetMasterData extends AsyncTask<String, Integer, String> {
        StringBuffer buffer;
        String DomainURL = "";
        String isInternetAvailable = "";
        JSONObject GetUserMEDataRequest;

        @Override
        protected void onPreExecute() {

            if (progressDialog == null)
                progressDialog = ProgressDialog.show(FacilityLogin.this,
                        "Please wait.", "Update Facility Login.", true);

            DomainURL = GlobalVar.GV().GetDomainURL(getApplicationContext());
            super.onPreExecute();

        }

        protected String doInBackground(String... params) {

            //GetFacilityStatusRequest getDeliveryStatusRequest = new GetFacilityStatusRequest();
            String jsonData = params[0];

            try {
                GetUserMEDataRequest = new JSONObject(jsonData);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(DomainURL + "UpdateUsermeLogin");
                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setReadTimeout(GlobalVar.GV().loadbalance_ConRedtimeout);
                httpURLConnection.setConnectTimeout(GlobalVar.GV().loadbalance_ConRedtimeout);
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
                isInternetAvailable = ignored.toString();
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

                        //Update user facility
                        UserFacility userFacility = new UserFacility();
                        userFacility.setEmployID(GlobalVar.GV().EmployID);
                        userFacility.setFacilityID(GetUserMEDataRequest.getInt("FacilityID"));

                        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                        dbConnections.FacilityLoggedIn(getApplicationContext(), userFacility);

                        String division = GlobalVar.GV().getDivisionID(getApplicationContext(), GlobalVar.GV().EmployID);
                        if (GlobalVar.GV().IsTerminalApp || division.equals("IRS")) {
                            Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(getApplicationContext(), VerifyMobileNo.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                System.out.println(result);
            } else {
                if (isInternetAvailable.contains("No address associated with hostname")) {
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly check your internet", GlobalVar.AlertType.Error);
                } else {
                    GlobalVar.GV().triedTimes = GlobalVar.GV().triedTimes + 1;
                    if (GlobalVar.GV().triedTimes == GlobalVar.GV().triedTimesCondition) {
                        //dbConnections.UpdateDomaintriedTimes(GlobalVar.GV().triedTimes, DomainURL, getApplicationContext());
                        GlobalVar.GV().SwitchoverDomain(getApplicationContext(), DomainURL);
                    }

                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.servererror), GlobalVar.AlertType.Error);
                }
                LoadDivisionError();
            }
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }

        }
    }

    private void LoadDivisionError() {
        AlertDialog alertDialog = new AlertDialog.Builder(FacilityLogin.this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Info.");
        alertDialog.setMessage("Kindly Check your Internet Connection,please try again");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        GetMasterData asynthread = new GetMasterData();
//                        StartAsyncTaskInParallel(asynthread);
                        updateFacilityLogin();
                        dialog.dismiss();
                        //finish();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Close",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        finish();
                    }
                });
        alertDialog.show();
    }

    private void StartAsyncTaskInParallel(GetMasterData asynthread) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asynthread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            asynthread.execute("");

    }
}