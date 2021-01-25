package com.naqelexpress.naqelpointer.Activity.NotDeliveredSinglePiece;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.android.volley.BuildConfig;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.NotDelivered;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.service.UpdateLocation;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import Error.ErrorReporter;
public class NotDeliveredActivity
        extends AppCompatActivity {

    NotDeliveredFirstFragment firstFragment;

    DateTime TimeIn;
    private Bundle bundle;

    FusedLocationProviderClient mFusedLocationClient;
    public double Latitude = 0;
    public double Longitude = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ErrorReporter());
        setContentView(R.layout.notdeliveredsingle);
        bundle = getIntent().getExtras();
        TimeIn = DateTime.now();


        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(1);

//        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
//        tabLayout.setupWithViewPager(mViewPager);


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


        Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GlobalVar.ValidateAutomacticDate(getApplicationContext())) {
                    if (firstFragment.txtWaybillNo.getText().toString().length() == 0) {
                        GlobalVar.ShowDialog(NotDeliveredActivity.this, "Info", "Please Enter Waybill Correctly", true);
                        return;
                    }
                    String waybillno = firstFragment.txtWaybillNo.getText().toString().substring(0, 8);
                    DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                    Cursor result = dbConnections.Fill("select * from MyRouteShipments Where ItemNo = '" + waybillno + "'",
                            getApplicationContext());
                    if (result.getCount() > 0) {
                        result.moveToFirst();
                        boolean isdelivered = result.getInt(result.getColumnIndex("IsDelivered")) > 0;
                        if (!isdelivered) {
                            boolean isupdatedelivered = result.getInt(result.getColumnIndex("UpdateDeliverScan")) > 0;
                            if (!isupdatedelivered)
                                SaveData();
                            else
                                GlobalVar.ShowDialog(NotDeliveredActivity.this, "You cannot scan again", "Delivery Scan Updated Against this waybill", true);
                        } else
                            GlobalVar.ShowDialog(NotDeliveredActivity.this, "Info", "Already Delivered this Waybill", true);
                    } else
                        SaveData();

                    result.close();
                    dbConnections.close();
                } else
                    GlobalVar.RedirectSettings(NotDeliveredActivity.this);
            }
        });


    }

    private void requestLocation() {
        Location location = GlobalVar.getLastKnownLocation(getApplicationContext());
        if (location != null) {
            Latitude = location.getLatitude();
            Longitude = location.getLongitude();
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

    private boolean IsDelivered(String pieceno) {
        boolean isdeliver = false;
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        Cursor result = dbConnections.Fill("select * from BarCode Where BarCode = '" + pieceno + "'",
                getApplicationContext());
        if (result.getCount() > 0) {
            result.moveToFirst();
            isdeliver = result.getInt(result.getColumnIndex("IsDelivered")) > 0;
        }
        return isdeliver;

    }

    private void SaveData() {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        if (IsValid() && dbConnections.UpdateMyRouteActionActivitySeqNo(getApplicationContext(), firstFragment.txtReason.getText().toString(),
                firstFragment.txtWaybillNo.getText().toString(), bundle.getInt("SeqNo"), bundle.getBoolean("isupdate"))) {
            dbConnections.UpdateProductivity_Exceptions(GlobalVar.getDate(), getApplicationContext());

            String Barcode = "";

            for (int i = 0; i < firstFragment.ShipmentBarCodeList.size(); i++) {
                if (!IsDelivered(firstFragment.ShipmentBarCodeList.get(i))) {
                    if (i == 0)
                        Barcode = firstFragment.ShipmentBarCodeList.get(i);
                    else
                        Barcode = Barcode + "," + firstFragment.ShipmentBarCodeList.get(i);
                } else {
                    GlobalVar.GV().MakeSound(this.getApplicationContext(), R.raw.wrongbarcodescan);
                    AlertDialog.Builder builder = new AlertDialog.Builder(NotDeliveredActivity.this);
                    builder.setMessage("This piece(" + firstFragment.ShipmentBarCodeList.get(i) + ") is " +
                            "already delivered cannot scan again")

                            .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {

                                }
                            })
                            .setCancelable(true);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    return;
                }

            }

            boolean IsSaved = true;
            NotDelivered notDelivered = new NotDelivered(firstFragment.txtWaybillNo.getText().toString(), 0,
                    TimeIn, DateTime.now(), String.valueOf(Latitude), String.valueOf(Longitude), firstFragment.ReasonID,
                    firstFragment.txtNotes.getText().toString(), firstFragment.subReasonId, Barcode);

            Cursor result = dbConnections.Fill("select * from MyRouteShipments where ItemNo = '" + firstFragment.txtWaybillNo.getText().toString()
                    + "' and HasComplaint = 1", getApplicationContext());
            if (result.getCount() > 0)
                dbConnections.UpdateComplaint_Exceptions(GlobalVar.getDate(), getApplicationContext());

            updateLocation();


            if (dbConnections.InsertNotDelivered(notDelivered, getApplicationContext())) {

                //we are facing the problem change the concept of old one
               /* int NotDeeliveredID = dbConnections.getMaxID("NotDelivered", getApplicationContext());
                for (int i = 0; i < firstFragment.ShipmentBarCodeList.size(); i++) {
                    NotDeliveredDetail notDeliveredDetail = new NotDeliveredDetail(firstFragment.ShipmentBarCodeList.get(i),
                            NotDeeliveredID);
                    if (!dbConnections.InsertNotDeliveredDetail(notDeliveredDetail, getApplicationContext())) {
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving), GlobalVar.AlertType.Error);
                        IsSaved = false;
                        break;
                    }
                }*/

                if (IsSaved) {
                    if (!GlobalVar.GV().isFortesting)
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

        GlobalVar.hideKeyboardFrom(getApplicationContext(), getWindow().getDecorView().getRootView());
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
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to select the reason",
                        GlobalVar.AlertType.Error);
                isValid = false;
            } else if (firstFragment.DeliveryStatussubReason.size() > 0 && firstFragment.subReasonId == 0) {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to select the Sub reason",
                        GlobalVar.AlertType.Error);
                isValid = false;
            } else if (firstFragment.txtsubReason.getHint().toString().equals("Choose Date") &&
                    firstFragment.txtNotes.getText().toString().length() == 0) {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to select the Date",
                        GlobalVar.AlertType.Error);
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
                    DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                    Cursor result = dbConnections.Fill("select * from MyRouteShipments Where ItemNo = '" + waybillno + "'",
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
                } else
                    GlobalVar.RedirectSettings(NotDeliveredActivity.this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
                        firstFragment = new NotDeliveredFirstFragment();
                        if (bundle != null)
                            firstFragment.setArguments(bundle);
                        return firstFragment;
                    } else
                        return firstFragment;

            }
            return null;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.NotDeliveredActivity);

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
}
