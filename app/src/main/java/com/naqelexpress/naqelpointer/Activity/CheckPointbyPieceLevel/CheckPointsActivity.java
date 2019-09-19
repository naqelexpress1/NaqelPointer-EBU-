package com.naqelexpress.naqelpointer.Activity.CheckPointbyPieceLevel;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.CheckPoint;
import com.naqelexpress.naqelpointer.DB.DBObjects.CheckPointBarCodeDetails;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import org.joda.time.DateTime;

// Created by Ismail on 21/03/2018.

public class CheckPointsActivity extends AppCompatActivity {
    CheckPointsFirstFragment firstFragment;
    CheckPointsSecondFragment secondFragment;
    CheckPointsThirdFragment thirdFragment;
    DateTime TimeIn;
    public double Latitude = 0;
    public double Longitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.checkpoints);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        TimeIn = DateTime.now();

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

            requestLocation();
        } else {
            ActivityCompat.requestPermissions(CheckPointsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            finish();
        }

        thirdFragment.BarCodeList.clear();

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
        inflater.inflate(R.menu.checkpointmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.mnuSave:
                if (GlobalVar.ValidateAutomacticDate(getApplicationContext()))
                    SaveData();
                else
                    GlobalVar.RedirectSettings(CheckPointsActivity.this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void SaveData() {

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        if (IsValid()) {
            boolean IsSaved = true;

            String ref = "";
            if (firstFragment.CheckPointTypeID == 9) {
                ref = firstFragment.txtCheckPointTypeDetail.getText().toString();
            } else if (firstFragment.CheckPointTypeID == 7) {
                ref = firstFragment.txtCheckPointTypeDDetail.getText().toString();
            }
            CheckPoint checkPoint = new CheckPoint(firstFragment.CheckPointTypeID, String.valueOf(Latitude),
                    String.valueOf(Longitude), firstFragment.CheckPointTypeDetailID, firstFragment.CheckPointTypeDDetailID, ref);

            if (dbConnections.InsertCheckPoint(checkPoint, getApplicationContext())) {
                int ID = dbConnections.getMaxID("CheckPoint", getApplicationContext());
//                for (int i = 0; i < secondFragment.WaybillList.size(); i++) {
//                    CheckPointWaybillDetails waybills = new CheckPointWaybillDetails(secondFragment.WaybillList.get(i), ID);
//                    if (!dbConnections.InsertCheckPointWaybillDetails(waybills, getApplicationContext())) {
//                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving),
//                                GlobalVar.AlertType.Error);
//                        IsSaved = false;
//                        break;
//                    }
//                }

                for (int i = 0; i < thirdFragment.BarCodeList.size(); i++) {
                    CheckPointBarCodeDetails checkPointBarCodeDetails = new CheckPointBarCodeDetails(thirdFragment.BarCodeList.get(i), ID);
                    if (!dbConnections.InsertCheckPointBarCodeDetails(checkPointBarCodeDetails, getApplicationContext())) {
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving),
                                GlobalVar.AlertType.Error);
                        IsSaved = false;
                        break;
                    }
                }


                if (IsSaved) {
//                    stopService(
//                            new Intent(CheckPointsActivity.this,
//                                    com.naqelexpress.naqelpointer.service.CheckPoint.class));

                    if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.CheckPoint.class)) {
                        startService(
                                new Intent(CheckPointsActivity.this,
                                        com.naqelexpress.naqelpointer.service.CheckPoint.class));
                    }
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.SaveSuccessfully), GlobalVar.AlertType.Info);
                    //finish();

                    resetAllFields(1);

                } else
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.NotSaved),
                            GlobalVar.AlertType.Error);
            } else
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving),
                        GlobalVar.AlertType.Error);
        }
        dbConnections.close();
    }

    private void resetAllFields(int close) {

        if (close == 0)
            finish();
        else {
            thirdFragment.BarCodeList.clear();
            thirdFragment.lbTotal.setText(getString(R.string.lbCount) + " 0");
            thirdFragment.adapter.notifyDataSetChanged();
        }
    }

    private boolean IsValid() {
        boolean isValid = true;
        if (firstFragment == null || firstFragment.CheckPointTypeID <= 0) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to select the Check Point Type",
                    GlobalVar.AlertType.Error);
            return false;
        } else if ((firstFragment.txtCheckPointTypeDetail.getVisibility() == View.VISIBLE &&
                firstFragment.CheckPointTypeDetailID == 0) && firstFragment.CheckPointTypeID != 9) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to select the reason",
                    GlobalVar.AlertType.Error);
            return false;
        } else if ((firstFragment.txtCheckPointTypeDDetail.getVisibility() == View.VISIBLE &&
                firstFragment.CheckPointTypeDDetailID == 0) && firstFragment.CheckPointTypeID != 7) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to select the reason",
                    GlobalVar.AlertType.Error);
            return false;
        }

//        if (secondFragment == null) {
//            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to scan the waybill list",
//                    GlobalVar.AlertType.Error);
//            return false;
//        }

        if (thirdFragment == null) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to scan the pieces list",
                    GlobalVar.AlertType.Error);
            return false;

        }


//        if (secondFragment != null) {
//            if (secondFragment.WaybillList.size() <= 0) {
//                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to scan the Waybills",
//                        GlobalVar.AlertType.Error);
//                return false;
//            }
//        }

        if (thirdFragment != null) {
            if (thirdFragment.BarCodeList.size() <= 0 && (firstFragment.txtCheckPointTypeDDetail.getText().toString().length() > 0 &&
                    (!firstFragment.txtCheckPointTypeDDetail.getText().toString().contains("Batha") &&

                            !firstFragment.txtCheckPointTypeDDetail.getText().toString().contains("Khafji")) &&

                    !firstFragment.txtCheckPointTypeDDetail.getText().toString().contains("KFIA"))) {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to scan the pieces list",
                        GlobalVar.AlertType.Error);
                return false;


            }
        }

        if (firstFragment.CheckPointTypeID == 9) {
            if (firstFragment.txtCheckPointTypeDetail.getText().toString().replace(" ", "").length() == 0) {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to enter Master WayBill",
                        GlobalVar.AlertType.Error);
                return false;
            }
        }
        if (firstFragment.CheckPointTypeID == 7) {
            if (firstFragment.txtCheckPointTypeDDetail.getText().toString().replace(" ", "").length() == 0) {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to enter Reason",
                        GlobalVar.AlertType.Error);
                return false;
            }
        }


        return isValid;
    }

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
                        firstFragment = new CheckPointsFirstFragment();
                        return firstFragment;
                    } else
                        return firstFragment;
//                case 1:
//                    if (secondFragment == null) {
//                        secondFragment = new CheckPointsSecondFragment();
//                        return secondFragment;
//                    } else {
//                        return secondFragment;
//                    }
                case 1:
                    if (thirdFragment == null) {
                        thirdFragment = new CheckPointsThirdFragment();
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
                    return getResources().getString(R.string.CheckPointFirstFragement);
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
        getSupportFragmentManager().putFragment(outState, "secondFragment", secondFragment);
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
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            firstFragment = (CheckPointsFirstFragment) getSupportFragmentManager().getFragment(savedInstanceState, "firstFragment");
            secondFragment = (CheckPointsSecondFragment) getSupportFragmentManager().getFragment(savedInstanceState, "secondFragment");
            thirdFragment = (CheckPointsThirdFragment) getSupportFragmentManager().getFragment(savedInstanceState, "thirdFragment");
            GlobalVar.GV().EmployID = savedInstanceState.getInt("EmployID");
            GlobalVar.GV().UserID = savedInstanceState.getInt("UserID");
            GlobalVar.GV().StationID = savedInstanceState.getInt("StationID");
            GlobalVar.GV().EmployMobileNo = savedInstanceState.getString("EmployMobileNo");
            GlobalVar.GV().EmployName = savedInstanceState.getString("EmployName");
            GlobalVar.GV().EmployStation = savedInstanceState.getString("EmployStation");


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
                        CheckPointsActivity.super.onBackPressed();
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
}