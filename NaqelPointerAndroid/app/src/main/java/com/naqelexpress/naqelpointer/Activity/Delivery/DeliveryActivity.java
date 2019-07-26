package com.naqelexpress.naqelpointer.Activity.Delivery;

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
import android.widget.Toast;

import com.naqelexpress.naqelpointer.BuildConfig;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.MyRouteShipments;
import com.naqelexpress.naqelpointer.DB.DBObjects.OnDelivery;
import com.naqelexpress.naqelpointer.DB.DBObjects.OnDeliveryDetail;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import org.joda.time.DateTime;

import static com.naqelexpress.naqelpointer.R.id.container;

public class DeliveryActivity
        extends AppCompatActivity {
    DeliveryFirstFragment firstFragment;
    DeliverySecondFragment secondFragment;
    DeliveryThirdFragment thirdFragment;
    DateTime TimeIn;
    private Bundle bundle;
    public double Latitude = 0;
    public double Longitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.delivery);
        bundle = getIntent().getExtras();
        TimeIn = DateTime.now();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

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
        } else {
            ActivityCompat.requestPermissions(DeliveryActivity.this,
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
                            Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                            startActivity(i);
                        } catch (Exception e) {
                            GlobalVar.ShowDialog(DeliveryActivity.this, "Location Permission necessary", "Kindly please contact our Admin", true);
                        }
                    }
                }
                return;
            }
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
                if (firstFragment.signrequired)
                    if (secondFragment.signmand == 1)
                        SaveData();
                    else
                        Toast.makeText(DeliveryActivity.this, "Kindly get Signature from Customer.", Toast.LENGTH_SHORT).show();
                else
                    SaveData();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void SaveData() {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        if (IsValid()) {
            boolean IsSaved = true;
            String WaybillNo = firstFragment.txtWaybillNo.getText().toString();
            String ReceiverName = String.valueOf(secondFragment.txtReceiverName.getText().toString());

            double POSAmount = 0;
            double CashAmount = 0;
            double TotalAmount;

            if (!secondFragment.txtPOS.getText().toString().equals(""))
                POSAmount = GlobalVar.GV().getDoubleFromString(secondFragment.txtPOS.getText().toString());
            if (!secondFragment.txtCash.getText().toString().equals(("")))
                CashAmount = GlobalVar.GV().getDoubleFromString(secondFragment.txtCash.getText().toString());
            TotalAmount = CashAmount + POSAmount;

            OnDelivery onDelivery = new OnDelivery(GlobalVar.GV().getIntegerFromString(WaybillNo),
                    ReceiverName, thirdFragment.DeliveryBarCodeList.size(),
                    TimeIn,
                    DateTime.now(),
                    String.valueOf(Latitude), String.valueOf(Longitude),
                    TotalAmount, CashAmount, POSAmount);

            dbConnections.UpdateProductivity_Delivered(GlobalVar.getDate(), getApplicationContext());
            Cursor result = dbConnections.Fill("select * from MyRouteShipments where ItemNo = '" + WaybillNo + "' and HasComplaint = 1", getApplicationContext());
            if (result.getCount() > 0)
                dbConnections.UpdateComplaint_Delivered(GlobalVar.getDate(), getApplicationContext());

            if (dbConnections.InsertOnDelivery(onDelivery, getApplicationContext())) {
                int DeliveryID = dbConnections.getMaxID("OnDelivery", getApplicationContext());
                for (int i = 0; i < thirdFragment.DeliveryBarCodeList.size(); i++) {
                    OnDeliveryDetail onDeliveryDetail = new OnDeliveryDetail(thirdFragment.DeliveryBarCodeList.get(i), DeliveryID);
                    if (!dbConnections.InsertOnDeliveryDetail(onDeliveryDetail, getApplicationContext())) {
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving), GlobalVar.AlertType.Error);
                        IsSaved = false;
                        break;
                    }
                }

                if (IsSaved) {
                    if (!isMyServiceRunning(DeliveryActivity.class)) {
                        startService(
                                new Intent(DeliveryActivity.this,
                                        com.naqelexpress.naqelpointer.service.OnDelivery.class));
                    }

                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.SaveSuccessfully), GlobalVar.AlertType.Info);
                    UpdateMyRouteShipments();
                    finish();
                } else
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.NotSaved), GlobalVar.AlertType.Error);
            } else
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving), GlobalVar.AlertType.Error);
        }
        dbConnections.close();
    }

    private boolean IsValid() {
        boolean isValid = true;
        if (firstFragment == null) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to enter the Waybill No", GlobalVar.AlertType.Error);
            isValid = false;
            return isValid;
        } else {
            if (firstFragment.txtWaybillNo.getText().toString().equals("") || firstFragment.txtWaybillNo.getText().toString().length() < 8) {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to enter the Waybill No", GlobalVar.AlertType.Error);
                isValid = false;
                return isValid;
            }
        }

        if (firstFragment.txtBillingType.getText().toString().equals(getResources().getString(R.string.txtBillingType))) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Please Save with valid Data", GlobalVar.AlertType.Error);
            isValid = false;
            return isValid;
        }

        if (secondFragment == null) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to enter the Receiver Name", GlobalVar.AlertType.Error);
            isValid = false;
            return isValid;
        }

        if (thirdFragment == null) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to scan the piece barcodes", GlobalVar.AlertType.Error);
            isValid = false;
            return isValid;
        }

        if (secondFragment != null) {
            if (secondFragment.txtReceiverName.getText().toString().equals("")) {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to enter the Receiver Name", GlobalVar.AlertType.Error);
//                GlobalVar.GV().ShowMessage(this,"You have to enter the Receiver Name", GlobalVar.AlertType.Error);
                isValid = false;
                return isValid;
            }
        }

        if (thirdFragment != null)
            if (thirdFragment.DeliveryBarCodeList.size() <= 0) {
//                GlobalVar.GV().ShowMessage(this,"You have to scan the piece barcodes", GlobalVar.AlertType.Error);
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to scan the piece barcodes", GlobalVar.AlertType.Error);
                isValid = false;
                return isValid;
            }
        return isValid;
    }

    private void UpdateMyRouteShipments() {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        if (GlobalVar.GV().CourierDailyRouteID > 0) {
            String WaybillNo = firstFragment.txtWaybillNo.getText().toString();
            Cursor resultDetail = dbConnections.Fill("select * from MyRouteShipments where ItemNo = " + WaybillNo + " " +
                    "and CourierDailyRouteID = " + GlobalVar.GV().CourierDailyRouteID, getApplicationContext());

            if (resultDetail.getCount() > 0) {
                resultDetail.moveToLast();
                dbConnections.UpdateMyRouteShipmentsWithDelivery(Integer.parseInt(resultDetail.getString(resultDetail.getColumnIndex("ID"))),
                        getWindow().getDecorView().getRootView(), getApplicationContext());
                if (GlobalVar.GV().myRouteShipmentList.size() > 0) {
                    for (int i = 0; i < GlobalVar.GV().myRouteShipmentList.size(); i++) {
                        MyRouteShipments item = GlobalVar.GV().myRouteShipmentList.get(i);
                        if (item.TypeID == 1 && item.ItemNo == WaybillNo)
                            GlobalVar.GV().myRouteShipmentList.remove(i);
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
                        firstFragment = new DeliveryFirstFragment();
                        if (bundle != null)
                            firstFragment.setArguments(bundle);
                        return firstFragment;
                    } else
                        return firstFragment;
                case 1:
                    if (secondFragment == null) {
                        secondFragment = new DeliverySecondFragment();
                        return secondFragment;
                    } else {
                        return secondFragment;
                    }
                case 2:
                    if (thirdFragment == null) {
                        thirdFragment = new DeliveryThirdFragment();
                        thirdFragment.ShipmentBarCodeList = firstFragment.ShipmentBarCodeList;
                        return thirdFragment;
                    } else {
                        thirdFragment.ShipmentBarCodeList = firstFragment.ShipmentBarCodeList;
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
                    return getResources().getString(R.string.DeliveryFirstFragment);
                case 1:
                    return getResources().getString(R.string.DeliverySecondFragment);
                case 2:
                    return getResources().getString(R.string.PiecesFragment);
            }
            return null;
        }
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit Delivery")
                .setMessage("Are you sure you want to exit without saving?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        DeliveryActivity.super.onBackPressed();
                    }
                }).setNegativeButton("Cancel", null).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            firstFragment = (DeliveryFirstFragment) getSupportFragmentManager().getFragment(savedInstanceState, "firstFragment");
            secondFragment = (DeliverySecondFragment) getSupportFragmentManager().getFragment(savedInstanceState, "secondFragment");
            thirdFragment = (DeliveryThirdFragment) getSupportFragmentManager().getFragment(savedInstanceState, "thirdFragment");
            GlobalVar.GV().EmployID = savedInstanceState.getInt("EmployID");
            GlobalVar.GV().UserID = savedInstanceState.getInt("UserID");
            GlobalVar.GV().StationID = savedInstanceState.getInt("StationID");
            GlobalVar.GV().EmployMobileNo = savedInstanceState.getString("EmployMobileNo");
            GlobalVar.GV().EmployName = savedInstanceState.getString("EmployName");
            GlobalVar.GV().EmployStation = savedInstanceState.getString("EmployStation");


        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, "firstFragment", firstFragment);
        getSupportFragmentManager().putFragment(outState, "secondFragment", secondFragment);
        if (thirdFragment != null)
            getSupportFragmentManager().putFragment(outState, "thirdFragment", thirdFragment);
        outState.putDouble("Latitude", Latitude);
        outState.putDouble("Longitude", Longitude);
        outState.putBundle("bundle", bundle);
        outState.putInt("EmployID", GlobalVar.GV().EmployID);
        outState.putInt("UserID", GlobalVar.GV().UserID);
        outState.putInt("StationID", GlobalVar.GV().StationID);
        outState.putString("EmployMobileNo", GlobalVar.GV().EmployMobileNo);
        outState.putString("EmployName", GlobalVar.GV().EmployName);
        outState.putString("EmployStation", GlobalVar.GV().EmployStation);
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