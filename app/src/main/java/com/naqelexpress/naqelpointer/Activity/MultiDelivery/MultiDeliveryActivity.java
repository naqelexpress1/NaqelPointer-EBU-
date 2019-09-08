package com.naqelexpress.naqelpointer.Activity.MultiDelivery;

import android.Manifest;
import android.app.Activity;
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
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.naqelexpress.naqelpointer.Activity.CustomerRating.CustomerRatings;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.MultiDelivery;
import com.naqelexpress.naqelpointer.DB.DBObjects.MultiDeliveryDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.MultiDeliveryWaybillDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.MyRouteShipments;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.service.ActualLocation;
import com.naqelexpress.naqelpointer.service.LocationService;
import com.naqelexpress.naqelpointer.service.UpdateLocation;

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

import static com.naqelexpress.naqelpointer.R.id.container;

public class MultiDeliveryActivity extends AppCompatActivity {
    MultiDeliveryFirstFragment firstFragment;
    MultiDeliverySecondFragment secondFragment;
    MultiDeliveryThirdFragment thirdFragment;
    DateTime TimeIn;
    private Bundle bundle;
    double Latitude = 0;
    double Longitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.multidelivery);
        bundle = getIntent().getExtras();
        TimeIn = DateTime.now();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        com.naqelexpress.naqelpointer.Activity.MultiDelivery.MultiDeliveryActivity.SectionsPagerAdapter mSectionsPagerAdapter
                = new com.naqelexpress.naqelpointer.Activity.MultiDelivery.MultiDeliveryActivity.SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

            requestLocation();

            if (!GlobalVar.isMyServiceRunning(LocationService.class, getApplicationContext())) {
                startService(new Intent(getBaseContext(),
                        LocationService.class));
            }

//            if (!GlobalVar.isMyServiceRunning(com.naqelexpress.naqelpointer.service.Radios200Service.class, getApplicationContext())) {
//                startService(
//                        new Intent(MultiDeliveryActivity.this,
//                                com.naqelexpress.naqelpointer.service.Radios200Service.class));
//            }


        } else {
            ActivityCompat.requestPermissions(MultiDeliveryActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            finish();
        }

        secondFragment.WaybillList.clear();
        thirdFragment.PieceBarCodeList.clear();
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
        inflater.inflate(R.menu.notdeliveredmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.mnuSave:
                if (GlobalVar.ValidateAutomacticDate(getApplicationContext())) {
                    if (IsValid()) {
                        String division = GlobalVar.getDivision(getApplicationContext());
                        if (division.equals("Express"))
                            crossvalidation();
                        else
                            SaveData();
                    }
                } else
                    GlobalVar.RedirectSettings(MultiDeliveryActivity.this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void crossvalidation() {
        try {
            DBConnections db = new DBConnections(getApplicationContext(), null);

            JSONObject headerJson = new JSONObject();
            JSONArray waybilljsonarray = new JSONArray();
            for (int j = 0; j < secondFragment.WaybillList.size(); j++) {
                String WaybillNo = secondFragment.WaybillList.get(j);
                JSONObject temp = new JSONObject();
                temp.put("WaybillNo", WaybillNo);
                Location location = db.GetLocationByWaybill(getApplicationContext(), WaybillNo);
                actualLocation(WaybillNo);
                temp.put("al", al);
                waybilljsonarray.put(temp);

            }
            headerJson.put("WaybillNo", waybilljsonarray);

            JSONArray jsonArray = new JSONArray();

            for (int i = 0; i < thirdFragment.PieceBarCodeList.size(); i++) {


                JSONObject temp = new JSONObject();
                temp.put("BarCode", thirdFragment.PieceBarCodeList.get(i));
                jsonArray.put(temp);


            }
            headerJson.put("BarCode", jsonArray);
            new CheckWaybillPieceCode().execute(headerJson.toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void SaveData() {

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        if (IsValid()) {
            boolean IsSaved = true;
            String txtReceivedBy = firstFragment.txtReceiverName.getText().toString();

            MultiDelivery multiDelivery = new MultiDelivery(txtReceivedBy, thirdFragment.PieceBarCodeList.size(), TimeIn,
                    DateTime.now(), secondFragment.WaybillList.size(),
                    String.valueOf(Latitude), String.valueOf(Longitude), 0, "0", 0);

            updateLocation();

            if (al == 1) {
                ActualLocation.start(getApplicationContext());
            }
            if (dbConnections.InsertMultiDelivery(multiDelivery, getApplicationContext(), firstFragment.al)) {
                int multiDeliveryID = dbConnections.getMaxID("MultiDelivery", getApplicationContext());
                for (int i = 0; i < secondFragment.WaybillList.size(); i++) {
                    MultiDeliveryWaybillDetail multiDeliveryWaybillDetail =
                            new MultiDeliveryWaybillDetail(secondFragment.WaybillList.get(i), multiDeliveryID);
                    if (!dbConnections.InsertMultiDeliveryWaybillDetail(multiDeliveryWaybillDetail, getApplicationContext())) {
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving),
                                GlobalVar.AlertType.Error);
                        IsSaved = false;
                        break;
                    }
                }

                for (int i = 0; i < thirdFragment.PieceBarCodeList.size(); i++) {
                    MultiDeliveryDetail multiDeliveryDetail = new MultiDeliveryDetail(thirdFragment.PieceBarCodeList.get(i),
                            multiDeliveryID);
                    if (!dbConnections.InsertMultiDeliveryDetail(multiDeliveryDetail, getApplicationContext())) {
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving),
                                GlobalVar.AlertType.Error);
                        IsSaved = false;
                        break;
                    }
                }

                if (IsSaved) {
                    if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.MultiDelivery.class)) {
                        startService(
                                new Intent(MultiDeliveryActivity.this,
                                        com.naqelexpress.naqelpointer.service.MultiDelivery.class));
                    }
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.SaveSuccessfully), GlobalVar.AlertType.Info);
                    UpdateMyRouteShipments();

                    Intent intent = new Intent(this, CustomerRatings.class);

                    intent.putStringArrayListExtra("waybill", secondFragment.WaybillList);
                    startActivityForResult(intent, 1);

                    //  finish();


                } else
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.NotSaved), GlobalVar.AlertType.Error);
            } else
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving), GlobalVar.AlertType.Error);
        }
        dbConnections.close();
    }

    private void updateLocation() {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("EmployID", GlobalVar.GV().EmployID);
            jsonObject.put("Latitude", String.valueOf(Latitude));
            jsonObject.put("Longitude", String.valueOf(Longitude));

            dbConnections.UpdateLocation(jsonObject.toString(), getApplicationContext());
            dbConnections.close();
            UpdateLocation.start(getApplicationContext());


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public static String Lat = "0", Longi = "0";

    private boolean IsValid() {
        boolean isValid = true;
        if (firstFragment == null) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to enter the Receiver Name", GlobalVar.AlertType.Error);
            isValid = false;
        } else {
            if (firstFragment.txtReceiverName.getText().toString().equals("") || firstFragment.txtReceiverName.getText().toString().length() < 3) {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to enter the Receiver Name", GlobalVar.AlertType.Error);
                isValid = false;
            }
        }

        if (secondFragment == null) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to enter the Waybills", GlobalVar.AlertType.Error);
            isValid = false;
        }

        if (thirdFragment == null) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to scan the piece barcodes", GlobalVar.AlertType.Error);
            isValid = false;
        }

        if (secondFragment != null && secondFragment.WaybillList.size() <= 0) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to scan the Waybill List", GlobalVar.AlertType.Error);
            isValid = false;

        }

        if (thirdFragment != null && thirdFragment.PieceBarCodeList.size() <= 0) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to scan the piece barcodes", GlobalVar.AlertType.Error);
            isValid = false;
        }
        return isValid;
    }

    private void UpdateMyRouteShipments() {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        if (GlobalVar.GV().CourierDailyRouteID > 0) {
            for (int j = 0; j < secondFragment.WaybillList.size(); j++) {
                String WaybillNo = secondFragment.WaybillList.get(j).toString();
                Cursor resultDetail = dbConnections.Fill("select * from MyRouteShipments where ItemNo = " + WaybillNo + " " +
                        "and CourierDailyRouteID = " + GlobalVar.GV().CourierDailyRouteID, getApplicationContext());

                if (resultDetail.getCount() > 0) {
                    resultDetail.moveToLast();
                    dbConnections.UpdateMyRouteShipmentsWithDelivery(Integer.parseInt(resultDetail.getString(resultDetail.getColumnIndex("ID")))
                            , getWindow().getDecorView().getRootView(), getApplicationContext());
                    if (GlobalVar.GV().myRouteShipmentList.size() > 0) {
                        for (int i = 0; i < GlobalVar.GV().myRouteShipmentList.size(); i++) {
                            MyRouteShipments item = GlobalVar.GV().myRouteShipmentList.get(i);
                            if (item.TypeID == 1 && item.ItemNo == WaybillNo)
                                GlobalVar.GV().myRouteShipmentList.remove(i);
                        }
                    }
                }
            }
        }
        dbConnections.close();
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (firstFragment == null) {
                        firstFragment = new MultiDeliveryFirstFragment();
                        if (bundle != null)
                            firstFragment.setArguments(bundle);
                        return firstFragment;
                    } else
                        return firstFragment;
                case 1:
                    if (secondFragment == null) {
                        secondFragment = new MultiDeliverySecondFragment();
                        return secondFragment;
                    } else {
                        return secondFragment;
                    }
                case 2:
                    if (thirdFragment == null) {
                        thirdFragment = new MultiDeliveryThirdFragment();
                        //thirdFragment.ShipmentBarCodeList = firstFragment.ShipmentBarCodeList;
                        return thirdFragment;
                    } else {
                        //thirdFragment.ShipmentBarCodeList = firstFragment.ShipmentBarCodeList;
                        return thirdFragment;
                    }
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.MultiDeliveryFirstFragment);
                case 1:
                    return getResources().getString(R.string.MultiDeliverySecondFragment);
                case 2:
                    return getResources().getString(R.string.MultiDeliveryThirdFragment);
            }
            return null;
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, "firstFragment", firstFragment);
        getSupportFragmentManager().putFragment(outState, "secondFragment", secondFragment);
        if (thirdFragment != null)
            getSupportFragmentManager().putFragment(outState, "thirdFragment", thirdFragment);
        outState.putInt("CourierDailyRouteID", GlobalVar.GV().CourierDailyRouteID);
        outState.putDouble("Latitude", Latitude);
        outState.putDouble("Longitude", Longitude);
        outState.putBundle("bundle", bundle);
        outState.putInt("EmployID", GlobalVar.GV().EmployID);
        outState.putInt("UserID", GlobalVar.GV().UserID);
        outState.putInt("StationID", GlobalVar.GV().StationID);
        outState.putString("EmployMobileNo", GlobalVar.GV().EmployMobileNo);
        outState.putString("EmployName", GlobalVar.GV().EmployName);
        outState.putString("EmployStation", GlobalVar.GV().EmployStation);
        outState.putParcelableArrayList("kpi", GlobalVar.GV().myRouteShipmentList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            GlobalVar.GV().CourierDailyRouteID = savedInstanceState.getInt("CourierDailyRouteID");
            GlobalVar.GV().myRouteShipmentList = savedInstanceState.getParcelableArrayList("kpi");
            firstFragment = (MultiDeliveryFirstFragment) getSupportFragmentManager().getFragment(savedInstanceState, "firstFragment");
            secondFragment = (MultiDeliverySecondFragment) getSupportFragmentManager().getFragment(savedInstanceState, "secondFragment");
            thirdFragment = (MultiDeliveryThirdFragment) getSupportFragmentManager().getFragment(savedInstanceState, "thirdFragment");
            GlobalVar.GV().EmployID = savedInstanceState.getInt("EmployID");
            GlobalVar.GV().UserID = savedInstanceState.getInt("UserID");
            GlobalVar.GV().StationID = savedInstanceState.getInt("StationID");
            GlobalVar.GV().EmployMobileNo = savedInstanceState.getString("EmployMobileNo");
            GlobalVar.GV().EmployName = savedInstanceState.getString("EmployName");
            GlobalVar.GV().EmployStation = savedInstanceState.getString("EmployStation");


        }
    }

    private class CheckWaybillPieceCode extends AsyncTask<String, Void, String> {
        String result = "";
        StringBuffer buffer;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GlobalVar.hideKeyboardFrom(getApplicationContext(), getWindow().getDecorView().getRootView());
            progressDialog = ProgressDialog.show(MultiDeliveryActivity.this, "Please wait.", "Cross check Piececode ", true);
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "CheckWaybillPieceCodeForMulti");
                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpURLConnection.setConnectTimeout(GlobalVar.GV().ConnandReadtimeout);
                httpURLConnection.setReadTimeout(GlobalVar.GV().ConnandReadtimeout);
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
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(finalJson);
                    if (Boolean.parseBoolean(jsonObject.getString("HasError"))) {
                        ShowAlertMessage(jsonObject.getString("ErrorMessage"));
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), jsonObject.getString("ErrorMessage"),
                                GlobalVar.AlertType.Error, true, getApplicationContext());
                        //             DuplicateWaybillNo("Info", "This WaybillNo Already Picked Up");
                    } else {
                        if (GlobalVar.ValidateAutomacticDate(getApplicationContext()))
                            SaveData();
                        else
                            GlobalVar.RedirectSettings(MultiDeliveryActivity.this);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    if (GlobalVar.ValidateAutomacticDate(getApplicationContext()))
                        SaveData();
                    else
                        GlobalVar.RedirectSettings(MultiDeliveryActivity.this);
                }
            } else {
                if (GlobalVar.ValidateAutomacticDate(getApplicationContext()))
                    SaveData();
                else
                    GlobalVar.RedirectSettings(MultiDeliveryActivity.this);
            }
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }

    private void ShowAlertMessage(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(MultiDeliveryActivity.this).create();
        alertDialog.setTitle("Warinig");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit Multi Delivery")
                .setMessage("Are you sure you want to exit without saving?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        com.naqelexpress.naqelpointer.Activity.MultiDelivery.MultiDeliveryActivity.super.onBackPressed();
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

    int al = 0;

    private void actualLocation(String WaybillNo) {

        al = 0;
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        double lat = 0;
        double longi = 0;

        try {

            Cursor result = dbConnections.Fill("select * from MyRouteShipments Where ItemNo = '"
                            + WaybillNo + "'",
                    getApplicationContext());

            if (result.getCount() > 0) {
                if (result.getString(result.getColumnIndex("Latitude")) != null &&
                        result.getString(result.getColumnIndex("Latitude")).length() > 3) {
                    Lat = result.getString(result.getColumnIndex("Latitude"));
                    Longi = result.getString(result.getColumnIndex("Longitude"));
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            lat = Double.parseDouble(Lat);
            longi = Double.parseDouble(Longi);

        } catch (NumberFormatException e) {

        }

        if (lat != 0) {
            if (GlobalVar.find200Radios(lat, longi, 200)) {
            } else {
                al = 1;
                dbConnections.InsertActualLocation(WaybillNo, String.valueOf(Latitude), String.valueOf(Longitude), getApplicationContext());
            }
        } else {
            al = 1;
            dbConnections.InsertActualLocation(WaybillNo, String.valueOf(Latitude), String.valueOf(Longitude), getApplicationContext());
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1: {
                if (resultCode == Activity.RESULT_OK) {
                    // TODO Extract the data returned from the child Activity.
                    String returnValue = data.getStringExtra("result");
                    if (returnValue.equals("done"))
                        finish();
                }
                break;
            }
        }
    }
}