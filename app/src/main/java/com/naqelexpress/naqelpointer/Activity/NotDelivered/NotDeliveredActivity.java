package com.naqelexpress.naqelpointer.Activity.NotDelivered;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.material.tabs.TabLayout;
import com.naqelexpress.naqelpointer.BuildConfig;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.NotDelivered;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.service.UpdateLocation;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NotDeliveredActivity
        extends AppCompatActivity {

    NotDeliveredFirstFragment firstFragment;
    NotDeliveredSecondFragment secondFragment;

    DateTime TimeIn;
    private Bundle bundle;

    FusedLocationProviderClient mFusedLocationClient;
    public double Latitude = 0;
    public double Longitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.notdelivered);
        bundle = getIntent().getExtras();
        TimeIn = DateTime.now();

//        Toast.makeText(this, "Not Delivery", Toast.LENGTH_SHORT).show();

        //GlobalVar.GV().activity = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

            requestLocation();


        } else {
            ActivityCompat.requestPermissions(NotDeliveredActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            finish();

        }


    }

    private void requestLocation() {
        Location location = GlobalVar.getLastKnownLocation(getApplicationContext());
        if (location != null) {
            Latitude = location.getLatitude();
            Longitude = location.getLongitude();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                int result = data.getIntExtra("result", -1);
                if (result == 0) {
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        } else {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLocation();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    boolean deniedPermission = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0]);
                    if (!deniedPermission) {
                        try {
                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                            startActivity(intent);
                        } catch (Exception e) {
                            GlobalVar.ShowDialog(NotDeliveredActivity.this, "Location Permission necessary", "Kindly please contact our Admin", true);
                        }
                    }
                }
                return;
            }
        }
    }

    private void SaveData() {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        if (IsValid()) {
            dbConnections.UpdateProductivity_Exceptions(GlobalVar.getDate(), getApplicationContext());


            String Barcode = "";

            for (int i = 0; i < secondFragment.NotDeliveredBarCodeList.size(); i++) {
                if (i == 0)
                    Barcode = secondFragment.NotDeliveredBarCodeList.get(i);
                else
                    Barcode = Barcode + "," + secondFragment.NotDeliveredBarCodeList.get(i);
            }


            boolean IsSaved = true;
            NotDelivered notDelivered = new NotDelivered(firstFragment.txtWaybillNo.getText().toString(), 0,
                    TimeIn, DateTime.now(), String.valueOf(Latitude), String.valueOf(Longitude), firstFragment.ReasonID,
                    firstFragment.txtNotes.getText().toString(), 0, Barcode);

            Cursor result = dbConnections.Fill("select * from MyRouteShipments where ItemNo = '" + firstFragment.txtWaybillNo.getText().toString() + "' and HasComplaint = 1", getApplicationContext());
            if (result.getCount() > 0)
                dbConnections.UpdateComplaint_Exceptions(GlobalVar.getDate(), getApplicationContext());

            updateLocation();
            if (dbConnections.InsertNotDelivered(notDelivered, getApplicationContext())) {

                /*int NotDeeliveredID = dbConnections.getMaxID("NotDelivered", getApplicationContext());
                for (int i = 0; i < secondFragment.NotDeliveredBarCodeList.size(); i++) {
                    NotDeliveredDetail notDeliveredDetail = new NotDeliveredDetail(secondFragment.NotDeliveredBarCodeList.get(i),
                            NotDeeliveredID);
                    if (!dbConnections.InsertNotDeliveredDetail(notDeliveredDetail, getApplicationContext())) {
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving), GlobalVar.AlertType.Error);
                        IsSaved = false;
                        break;
                    }
                }*/

                if (IsSaved) {
                    if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.NotDelivery.class)) {
                        startService(
                                new Intent(NotDeliveredActivity.this,
                                        com.naqelexpress.naqelpointer.service.NotDelivery.class));
                    }
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.SaveSuccessfully), GlobalVar.AlertType.Info);
                    finish();
                } else
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.NotSaved), GlobalVar.AlertType.Error);
            } else
                GlobalVar.GV().ShowDialog(NotDeliveredActivity.this, "Error", "Your data not sucessfully registered" +
                        ",kindly try again to save.", true);
        }

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

    private boolean IsValid() {
        boolean isValid = true;
        if (firstFragment == null) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to enter the WaybillNo", GlobalVar.AlertType.Error);
            isValid = false;
        }

        if (firstFragment != null) {
            if (firstFragment.txtWaybillNo.getText().toString().equals("")) {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to enter the Waybill No", GlobalVar.AlertType.Error);
                isValid = false;
            }

            if (firstFragment.ReasonID == 0) {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to select the reason", GlobalVar.AlertType.Error);
                isValid = false;
            }
        }

        return isValid;
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
                    if (firstFragment.txtWaybillNo.getText().toString().length() == 0) {
                        GlobalVar.ShowDialog(NotDeliveredActivity.this, "Info", "Please Enter Waybill Correctly", true);
                        return false;
                    }

                    String waybillno = firstFragment.txtWaybillNo.getText().toString().substring(0, 8);
                    GlobalVar.hideKeyboardFrom(getApplicationContext(), getWindow().getDecorView().getRootView());
                    String reason = firstFragment.txtReason.getText().toString();

                    String division = Division();
                    if (division.equals("Courier")) {
                        if (reason.contains("Bad Address") || reason.contains("No Answer") || reason.contains("Future Delivery") ||
                                reason.contains("Refused") || reason.contains("Wrong Telephone No") || reason.contains("No Response")) {
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("WayBillNo", waybillno);
                                jsonObject.put("TimeIn", DateTime.now());

                                new CrossValidationException().execute(jsonObject.toString());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else
                            DeliverException();
                    } else
                        DeliverException();
                } else
                    GlobalVar.RedirectSettings(NotDeliveredActivity.this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String Division() {
        String devision = "Courier";
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        Cursor result = dbConnections.Fill("select * from UserME where StatusID <> 3 and EmployID = " +
                GlobalVar.GV().EmployID, getApplicationContext());
        if (result.getCount() > 0) {
            result.moveToFirst();
            devision = result.getString(result.getColumnIndex("Division"));

        }
        return devision;
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
                        firstFragment = new NotDeliveredFirstFragment();
                        if (bundle != null)
                            firstFragment.setArguments(bundle);
                        return firstFragment;
                    } else
                        return firstFragment;
                case 1:
                    if (secondFragment == null) {
                        secondFragment = new NotDeliveredSecondFragment();
                        return secondFragment;
                    } else
                        return secondFragment;
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
                    return getResources().getString(R.string.NotDeliveredActivity);
                case 1:
                    return getResources().getString(R.string.PiecesFragment);
            }
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit Not Delivery")
                .setMessage("Are you sure you want to exit without saving?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        NotDeliveredActivity.super.onBackPressed();
                    }
                }).setNegativeButton("Cancel", null).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "firstFragment", firstFragment);
        getSupportFragmentManager().putFragment(outState, "secondFragment", secondFragment);
        outState.putInt("EmployID", GlobalVar.GV().EmployID);
        outState.putInt("UserID", GlobalVar.GV().UserID);
        outState.putInt("StationID", GlobalVar.GV().StationID);
        outState.putString("EmployMobileNo", GlobalVar.GV().EmployMobileNo);
        outState.putString("EmployName", GlobalVar.GV().EmployName);
        outState.putString("EmployStation", GlobalVar.GV().EmployStation);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            firstFragment = (NotDeliveredFirstFragment) getSupportFragmentManager().getFragment(savedInstanceState, "firstFragment");
            secondFragment = (NotDeliveredSecondFragment) getSupportFragmentManager().getFragment(savedInstanceState, "secondFragment");
            GlobalVar.GV().EmployID = savedInstanceState.getInt("EmployID");
            GlobalVar.GV().UserID = savedInstanceState.getInt("UserID");
            GlobalVar.GV().StationID = savedInstanceState.getInt("StationID");
            GlobalVar.GV().EmployMobileNo = savedInstanceState.getString("EmployMobileNo");
            GlobalVar.GV().EmployName = savedInstanceState.getString("EmployName");
            GlobalVar.GV().EmployStation = savedInstanceState.getString("EmployStation");

        }
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

    private class CrossValidationException extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;
        String result = "";
        StringBuffer buffer;

        @Override
        protected void onPreExecute() {

            progressDialog = ProgressDialog.show(NotDeliveredActivity.this,
                    "Please wait.", "Call tracking validation is being process.", true);
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "TraceDeliveryException");
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
                result = String.valueOf(buffer);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String finalJson) {

            if (finalJson != null) {

                try {
                    JSONObject jsonObject = new JSONObject(finalJson);
                    boolean haserror = jsonObject.getBoolean("HasError");
                    String errormsg = jsonObject.getString("ErrorMessage");
                    if (haserror) {
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(),
                                errormsg, GlobalVar.AlertType.Error);
                    } else {
                        DeliverException();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.wentwrong), GlobalVar.AlertType.Error);

            if (NotDeliveredActivity.this.isDestroyed()) { // or call isFinishing() if min sdk version < 17
                return;
            }

            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();

            super.onPostExecute(String.valueOf(finalJson));
        }
    }

    private void DeliverException() {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        Cursor result = dbConnections.Fill("select * from MyRouteShipments Where ItemNo = '" +
                        firstFragment.txtWaybillNo.getText().toString().substring(0, 8) + "'",
                getApplicationContext());
        if (result.getCount() > 0) {
            result.moveToFirst();
            boolean isdelivered = result.getInt(result.getColumnIndex("IsDelivered")) > 0;
            if (!isdelivered) {
                SaveData();
            } else
                GlobalVar.ShowDialog(NotDeliveredActivity.this, "Info", "Already Delivered this Waybill", true);
        } else
            SaveData();
    }

}
