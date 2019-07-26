package com.naqelexpress.naqelpointer.Activity.DeliverySheetPartial;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
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
import com.naqelexpress.naqelpointer.Activity.TerminalHandling.FirstFragment;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.OnCLoadingForDDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.OnCLoadingForDWaybill;
import com.naqelexpress.naqelpointer.DB.DBObjects.OnCloadingForD;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class DeliverySheet
        extends AppCompatActivity {

    FirstFrgament firstFragment;
    SecondFrgament secondFragment;

    DateTime TimeIn;
    Bundle bundle;

    public double Latitude = 0;
    public double Longitude = 0;

    String txtCourierID = "", txtTruckID = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.notdelivered);
        bundle = getIntent().getExtras();


        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        txtCourierID = bundle.getString("txtcourierID");
        txtTruckID = bundle.getString("truckID");
        TimeIn = DateTime.now();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


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
                    SaveData();
                } else
                    GlobalVar.RedirectSettings(DeliverySheet.this);
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void SaveData() {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);

        boolean IsSaved = true;
        OnCloadingForD onCloadingForD = new OnCloadingForD(Integer.parseInt(txtCourierID),
                secondFragment.barcode.size(), firstFragment.waybill.size(), txtTruckID);

        if (dbConnections.InsertOnCloadingForD(onCloadingForD, getApplicationContext())) {
            int OnCloadingForDID = dbConnections.getMaxID("OnCloadingForD", getApplicationContext());
            for (int i = 0; i < firstFragment.waybill.size(); i++) {
                OnCLoadingForDWaybill onCLoadingForDWaybill = new OnCLoadingForDWaybill(firstFragment.waybill.get(i), OnCloadingForDID);
                if (!dbConnections.InsertOnCLoadingForDWaybill(onCLoadingForDWaybill, getApplicationContext())) {
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving), GlobalVar.AlertType.Error);
                    IsSaved = false;
                    break;
                }
            }

            for (int i = 0; i < secondFragment.barcode.size(); i++) {
                OnCLoadingForDDetail onCLoadingForDDetail = new OnCLoadingForDDetail(secondFragment.barcode.get(i), OnCloadingForDID);
                if (!dbConnections.InsertOnCLoadingForDDetail(onCLoadingForDDetail, getApplicationContext())) {
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving), GlobalVar.AlertType.Error);
                    IsSaved = false;
                    break;
                }
            }

            if (IsSaved) {

                if (!isMyServiceRunning(DeliverySheet.class)) {
                    startService(
                            new Intent(DeliverySheet.this,
                                    com.naqelexpress.naqelpointer.service.OnLoading.class));
                }
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.SaveSuccessfully), GlobalVar.AlertType.Info);

//                Intent intent = new Intent(this, CustomerRatings.class);
//                intent.putStringArrayListExtra("waybill", firstFragment.waybill);
//                startActivityForResult(intent, 1);

                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", "done");
                setResult(Activity.RESULT_OK, returnIntent);
                finish();

            } else
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.NotSaved), GlobalVar.AlertType.Error);
        } else
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving), GlobalVar.AlertType.Error);

        dbConnections.close();
    }

    private void ShowAlertMessage(String WaybillNo) {
        AlertDialog alertDialog = new AlertDialog.Builder(DeliverySheet.this).create();
        alertDialog.setTitle("Info");
        alertDialog.setMessage(WaybillNo + " -  No Scanned Pieces , Kindly delete this waybillno");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
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
                        firstFragment = new FirstFrgament();
                        if (bundle != null)
                            firstFragment.setArguments(bundle);
                        return firstFragment;
                    } else
                        return firstFragment;
                case 1:
                    if (secondFragment == null) {
                        secondFragment = new SecondFrgament();
                        if (bundle != null)
                            secondFragment.setArguments(bundle);
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
                    return "WayBill";
                case 1:
                    return getResources().getString(R.string.PiecesFragment);
            }
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit AtOrigin")
                .setMessage("Are you sure you want to exit without saving?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        DeliverySheet.super.onBackPressed();
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
            firstFragment = (FirstFrgament) getSupportFragmentManager().getFragment(savedInstanceState, "firstFragment");
            secondFragment = (SecondFrgament) getSupportFragmentManager().getFragment(savedInstanceState, "secondFragment");
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
