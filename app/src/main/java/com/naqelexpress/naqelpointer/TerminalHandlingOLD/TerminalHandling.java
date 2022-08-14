package com.naqelexpress.naqelpointer.TerminalHandlingOLD;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import android.support.v4.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
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

public class TerminalHandling extends AppCompatActivity {

    FirstFragment firstFragment;
    // SecondFragment secondFragment;
    ThirdFragment thirdFragment;
    DateTime TimeIn;
    public static double Latitude = 0;
    public static double Longitude = 0;

    static ArrayList<HashMap<String, String>> status = new ArrayList<>();
    static ArrayList<HashMap<String, String>> reason = new ArrayList<>();
    static String group = "";
    public static ArrayList<String> isdeliveryReq = new ArrayList<>();
    public static ArrayList<String> isrtoReq = new ArrayList<>();
    public static ArrayList<String> isHeldout = new ArrayList<>();
    static ArrayList<HashMap<String, String>> delrtoreq = new ArrayList<>();
    static ArrayList<String> city = new ArrayList<>();
    static ArrayList<String> operationalcity = new ArrayList<>();
    //MyCountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.checkpoints);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        status.clear();
        reason.clear();
        delrtoreq.clear();
        city.clear();
        operationalcity.clear();
        Latitude = 0;
        Latitude = 0;

        status = (ArrayList<HashMap<String, String>>) bundle.getSerializable("status");
        reason = (ArrayList<HashMap<String, String>>) bundle.getSerializable("reason");
        city = bundle.getStringArrayList("city");
        operationalcity = bundle.getStringArrayList("operationalcity");

        group = bundle.getString("group");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        TimeIn = DateTime.now();

//        countDownTimer = new MyCountDownTimer(startTime, interval);
//
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

            requestLocation();
        } else {
            ActivityCompat.requestPermissions(TerminalHandling.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            finish();
        }

        if (TerminalHandling.group.equals("Group 1")) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("OriginID", 0);
                jsonObject.put("DestinationID", GlobalVar.GV().StationID);
                new BringNCLData().execute(jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        firstFragment.CheckPointTypeID = 0;
    }

    private void requestLocation() {
        Location location = GlobalVar.getLastKnownLocation(getApplicationContext());
        if (location != null) {
            Latitude = location.getLatitude();
            Longitude = location.getLongitude();
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
            case R.id.mnuSave:
                if (GlobalVar.ValidateAutomacticDate(getApplicationContext())) {
                    ErrorAlert("Info", "Are yo sure want to Finish the Job?", 2);
                } else
                    GlobalVar.RedirectSettings(TerminalHandling.this);
                return true;
            case R.id.manual:
                if (GlobalVar.ValidateAutomacticDate(getApplicationContext())) {
                    ErrorAlert("Info", "Are yo sure want to upload Manual?", 3);
                } else
                    GlobalVar.RedirectSettings(TerminalHandling.this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void ErrorAlert(final String title, String message, final int clear) {
        AlertDialog alertDialog = new AlertDialog.Builder(TerminalHandling.this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        if (clear == 2)
                            SaveData();
                        else if (clear == 3)
                            insertManual1();

                    }
                });

        alertDialog.show();
    }

    private void SaveData() {

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        if (IsValid()) {
            boolean IsSaved = true;
            String Comments = "";
            if (firstFragment.CheckPointTypeID == 18) {
                Comments = "Weight " + firstFragment.txtweight.getText().toString() + " KG " + "  W * L * H" +
                        firstFragment.txtwidth.getText().toString() + " * " +
                        firstFragment.txtlength.getText().toString() + " * " +
                        firstFragment.txtheight.getText().toString();
                firstFragment.txtCheckPointTypeDDetail.setText(Comments);

            }


            com.naqelexpress.naqelpointer.DB.DBObjects.TerminalHandling checkPoint = new com.naqelexpress.naqelpointer.DB.DBObjects.TerminalHandling
                    (firstFragment.CheckPointTypeID, String.valueOf(Latitude),
                            String.valueOf(Longitude), firstFragment.CheckPointTypeDetailID, firstFragment.txtCheckPointTypeDDetail.getText().toString()
                            , "", thirdFragment.Barcodes.size());

            if (dbConnections.InsertTerminalHandling(checkPoint, getApplicationContext())) {
                int ID = dbConnections.getMaxID("CheckPoint", getApplicationContext());


                for (int i = 0; i < thirdFragment.Barcodes.size(); i++) {
                    CheckPointBarCodeDetails checkPointBarCodeDetails = new CheckPointBarCodeDetails(thirdFragment.Barcodes.get(i), ID);
                    if (!dbConnections.InsertCheckPointBarCodeDetails(checkPointBarCodeDetails, getApplicationContext())) {
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving),
                                GlobalVar.AlertType.Error);
                        IsSaved = false;
                        break;
                    }
                }


                if (IsSaved) {
//
                    if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.TerminalHandling.class)) {
                        startService(
                                new Intent(TerminalHandling.this,
                                        com.naqelexpress.naqelpointer.service.TerminalHandling.class));
                    }
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.SaveSuccessfully), GlobalVar.AlertType.Info);

                    finish();
                } else
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.NotSaved),
                            GlobalVar.AlertType.Error);
            } else
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving),
                        GlobalVar.AlertType.Error);
        }
        dbConnections.close();
    }

    private boolean IsValid() {
        boolean isValid = true;
        if (firstFragment == null || firstFragment.CheckPointTypeID <= 0) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to select the Check Point Type",
                    GlobalVar.AlertType.Error);
            return false;
        } else if (firstFragment.txtCheckPointTypeDetail.getVisibility() == View.VISIBLE &&
                firstFragment.CheckPointTypeDetailID == 0) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to select the reason",
                    GlobalVar.AlertType.Error);
            return false;
        } else if (firstFragment.txtCheckPointTypeDDetail.getVisibility() == View.VISIBLE) {
            if (firstFragment.CheckPointTypeDDetailID == 1 && firstFragment.txtCheckPointTypeDDetail.getText().toString().length() == 0) {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to select the Date",
                        GlobalVar.AlertType.Error);
                return false;
            } else if (firstFragment.CheckPointTypeID == 20 && firstFragment.txtCheckPointTypeDDetail.getText().toString().length() == 0) {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have enter the Bin Location",
                        GlobalVar.AlertType.Error);
                return false;
            }

        }

        if (thirdFragment == null) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to scan the pieces list",
                    GlobalVar.AlertType.Error);
            return false;

        }


        if (firstFragment.CheckPointTypeID == 18) {
            if (firstFragment.txtweight.getText().toString().length() == 0) {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to enter weight",
                        GlobalVar.AlertType.Error);
                return false;
            }

            if (firstFragment.txtheight.getText().toString().length() == 0) {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to enter height",
                        GlobalVar.AlertType.Error);
                return false;
            }

            if (firstFragment.txtlength.getText().toString().length() == 0) {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to enter length",
                        GlobalVar.AlertType.Error);
                return false;
            }
            if (firstFragment.txtwidth.getText().toString().length() == 0) {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to enter length",
                        GlobalVar.AlertType.Error);
                return false;
            }

        }

        if (thirdFragment != null) {
            if (thirdFragment.BarCodeList.size() <= 0) {

//                if (delrtoreq.size() > 0) {
//                    SaveHeldOutData(0);
//                }

                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to scan the pieces list",
                        GlobalVar.AlertType.Error);
                return false;
            }
        }


        return isValid;
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
                    new Intent(TerminalHandling.this,
                            com.naqelexpress.naqelpointer.service.TerminalHandling.class));

            if (!isMyServiceRunning(TerminalHandling.class)) {
                startService(
                        new Intent(TerminalHandling.this,
                                com.naqelexpress.naqelpointer.service.TerminalHandling.class));
            }
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.SaveSuccessfully), GlobalVar.AlertType.Info);


            finish();
        }

    }*/

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.checkpointsfirstfragment, container, false);
        }
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (firstFragment == null) {
                        firstFragment = new FirstFragment();
                        return firstFragment;
                    } else
                        return firstFragment;
//                case 1:
//                    if (secondFragment == null) {
//                        secondFragment = new SecondFragment();
//                        return secondFragment;
//                    } else {
//                        return secondFragment;
//                    }
                case 1:
                    if (thirdFragment == null) {
                        thirdFragment = new ThirdFragment();
                        return thirdFragment;
                    } else {
                        return thirdFragment;
                    }
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return group;
//                case 1:
//                    return getResources().getString(R.string.Waybill);
                case 1:
                    return getResources().getString(R.string.Pieces);
            }
            return null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "firstFragment", firstFragment);
//        getSupportFragmentManager().putFragment(outState, "secondFragment", secondFragment);
        if (thirdFragment != null)
            getSupportFragmentManager().putFragment(outState, "thirdFragment", thirdFragment);
        outState.putDouble("Latitude", Latitude);
        outState.putDouble("Longitude", Longitude);
        outState.putInt("EmployID", GlobalVar.GV().EmployID);
        outState.putInt("UserID", GlobalVar.GV().UserID);
        outState.putInt("StationID", GlobalVar.GV().StationID);
        outState.putString("EmployMobileNo", GlobalVar.GV().EmployMobileNo);
        outState.putString("EmployName", GlobalVar.GV().EmployName);
        outState.putString("EmployStation", GlobalVar.GV().EmployStation);
        outState.putString("group", group);
        outState.putSerializable("status", status);
        outState.putSerializable("reason", reason);
        outState.putSerializable("city", city);
        outState.putSerializable("operationalcity", operationalcity);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            firstFragment = (FirstFragment) getSupportFragmentManager().getFragment(savedInstanceState, "firstFragment");
//            secondFragment = (SecondFragment) getSupportFragmentManager().getFragment(savedInstanceState, "secondFragment");
            thirdFragment = (ThirdFragment) getSupportFragmentManager().getFragment(savedInstanceState, "thirdFragment");
            GlobalVar.GV().EmployID = savedInstanceState.getInt("EmployID");
            GlobalVar.GV().UserID = savedInstanceState.getInt("UserID");
            GlobalVar.GV().StationID = savedInstanceState.getInt("StationID");
            GlobalVar.GV().EmployMobileNo = savedInstanceState.getString("EmployMobileNo");
            GlobalVar.GV().EmployName = savedInstanceState.getString("EmployName");
            GlobalVar.GV().EmployStation = savedInstanceState.getString("EmployStation");
            group = savedInstanceState.getString("group");
            status = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("status");
            reason = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("reason");
            city = savedInstanceState.getStringArrayList("city");
            operationalcity = savedInstanceState.getStringArrayList("operationalcity");
            Latitude = savedInstanceState.getDouble("Latitude");
            Longitude = savedInstanceState.getDouble("Longitude");


        }
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
                        TerminalHandling.super.onBackPressed();
                    }
                }).setNegativeButton("Cancel", null).setCancelable(false);
        AlertDialog alertDialog = builder.create();
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

    ProgressDialog progressDialog;

    private class BringNCLData extends AsyncTask<String, Integer, String> {
        StringBuffer buffer;

        @Override
        protected void onPreExecute() {

            if (progressDialog == null)
                progressDialog = ProgressDialog.show(TerminalHandling.this,
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
                for (int i = 0; i < deliveryReq.length(); i++) {
                    JSONObject jsonObject1 = deliveryReq.getJSONObject(i);
                    isdeliveryReq.add(jsonObject1.getString("BarCode"));
                }
            }

            JSONArray rtoreq = jsonObject.getJSONArray("RTOReq");

            if (rtoreq.length() > 0) {
                for (int i = 0; i < rtoreq.length(); i++) {
                    JSONObject jsonObject1 = rtoreq.getJSONObject(i);
                    isrtoReq.add(jsonObject1.getString("BarCode"));
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void LoadDivisionError() {
        AlertDialog alertDialog = new AlertDialog.Builder(TerminalHandling.this).create();
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

//   private long startTime = 30 * 60 * 1000; // 15 MINS IDLE TIME
//    private final long interval = 1 * 1000;
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
//            ActivityCompat.finishAffinity(TerminalHandling.this);
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
        // countDownTimer.cancel();
        //countDownTimer.start();
    }

    private void insertManual1() {

        stopService(
                new Intent(TerminalHandling.this,
                        com.naqelexpress.naqelpointer.service.TerminalHandling.class));

        try {
            DBConnections db = new DBConnections(getApplicationContext(), null);


            Cursor ts = db.Fill("select SUM(Count) As totalRecord  from CheckPoint", getApplicationContext());
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

    int uploaddatacount = 0, totalsize = 0;

    private class SaveAtTerminalHandlingbyManual extends AsyncTask<String, Integer, String> {
        String returnresult = "";
        StringBuffer buffer;
        int moveddata = 0;

        @Override
        protected void onPreExecute() {

            uploaddatacount = 0;
            if (progressDialog == null) {

                progressDialog = new ProgressDialog(TerminalHandling.this);
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
            Cursor result = db.Fill("select * from CheckPoint order by ID", getApplicationContext());
            result.moveToFirst();
            do {
                int jsonlegth = result.getInt(result.getColumnIndex("Count"));
                returnresult = "";
                buffer = new StringBuffer();
                buffer.setLength(0);


                final com.naqelexpress.naqelpointer.DB.DBObjects.TerminalHandling checkPoint = new com.naqelexpress.naqelpointer.DB.DBObjects.TerminalHandling();
                checkPoint.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                checkPoint.TerminalHandlingScanStatusID = Integer.parseInt(result.getString(result.getColumnIndex("CheckPointTypeID")));
                checkPoint.Date = DateTime.parse(result.getString(result.getColumnIndex("Date")));
                checkPoint.EmployID = Integer.parseInt(result.getString(result.getColumnIndex("EmployID")));
                checkPoint.IsSync = Boolean.parseBoolean(result.getString(result.getColumnIndex("IsSync")));
                checkPoint.Latitude = result.getString(result.getColumnIndex("Latitude"));
                checkPoint.Longitude = result.getString(result.getColumnIndex("Longitude"));
                checkPoint.TerminalHandlingScanStatusReasonID = Integer.parseInt(result.getString(result.getColumnIndex("CheckPointTypeDetailID")));
                checkPoint.Reference = result.getString(result.getColumnIndex("Ref"));


                Cursor resultDetail = db.Fill("select * from CheckPointBarCodeDetails where CheckPointID = " + checkPoint.ID, getApplicationContext());
                if (resultDetail.getCount() > 0) {
                    resultDetail.moveToFirst();
                    int index = 0;
                    resultDetail.moveToFirst();
                    do {
                        checkPoint.TerminalHandlingBarCodeDetails.add(index, new CheckPointBarCodeDetails(resultDetail.getString(resultDetail.getColumnIndex("BarCode")), checkPoint.ID));
                        index++;
                    }
                    while (resultDetail.moveToNext());
                }


                String jsonData = JsonSerializerDeserializer.serialize(checkPoint, true);
                jsonData = jsonData.replace("Date(-", "Date(");

                HttpURLConnection httpURLConnection = null;
                OutputStream dos = null;
                InputStream ist = null;

                try {
                    URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "InsertTerminalHandlingByPiece"); //LoadtoDestination
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
                    returnresult = String.valueOf(buffer);

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
                    returnresult = String.valueOf(buffer);
                }


                if (returnresult.contains("Created")) {
                    moveddata = moveddata + jsonlegth;
                    db.deleteCheckPointID(checkPoint.ID, getApplicationContext());
                    db.deleteCheckPointWayBill(checkPoint.ID, getApplicationContext());
                    db.deleteCheckPointBarcode(checkPoint.ID, getApplicationContext());
                }
                try {
                    uploaddatacount = uploaddatacount + jsonlegth;
                } catch (Exception e) {

                }
                publishProgress((int) ((uploaddatacount * 100) / totalsize));

            } while (result.moveToNext());

            result.close();
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
                Cursor ts = db.Fill("select SUM(Count) As totalRecord  from CheckPoint", getApplicationContext());
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
                            new Intent(TerminalHandling.this,
                                    com.naqelexpress.naqelpointer.service.TerminalHandling.class));
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

    private void ErrorAlert(final String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(TerminalHandling.this).create();
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
}