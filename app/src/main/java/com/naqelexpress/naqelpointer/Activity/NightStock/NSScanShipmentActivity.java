package com.naqelexpress.naqelpointer.Activity.NightStock;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.NightStock;
import com.naqelexpress.naqelpointer.DB.DBObjects.NightStockDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.NightStockWaybillDetail;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import org.joda.time.DateTime;

public class NSScanShipmentActivity extends AppCompatActivity {

    ScanShipmentFragment firstFragment;
    ScanShipmentSecondFragment secondFragment;
    private Bundle bundle;
    DateTime TimeIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nsscan_shipment);
        TimeIn = DateTime.now();
        bundle = getIntent().getExtras();
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

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
                SaveData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void ErrorAlert(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(NSScanShipmentActivity.this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Info");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }

    private void SaveData() {


        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        if (IsValid()) {
            if (firstFragment.txtbinlocation.getText().toString().replace(" ", "").length() == 0) {
                ErrorAlert("Kindly Scan the Bin Location - Go to Waybill Scanning Section");
                firstFragment.txtbinlocation.setVisibility(View.VISIBLE);
                return;
            }

            boolean IsSaved = true;


            NightStock nightStockD = new NightStock(firstFragment.WaybillList.size(), secondFragment.PieceCodeList.size(), TimeIn,
                    firstFragment.txtbinlocation.getText().toString());

            if (dbConnections.InsertNightStock(nightStockD, getApplicationContext())) {
                int nightStockDID = dbConnections.getMaxID("NightStock", getApplicationContext());
                for (int i = 0; i < firstFragment.WaybillList.size(); i++) {
                    NightStockWaybillDetail nightstockWaybillDetail =
                            new NightStockWaybillDetail(firstFragment.WaybillList.get(i), nightStockDID);
                    if (!dbConnections.InsertNightStockWaybillDetail(nightstockWaybillDetail, getApplicationContext())) {
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving),
                                GlobalVar.AlertType.Error);
                        IsSaved = false;
                        break;
                    }
                }

                for (int i = 0; i < secondFragment.PieceCodeList.size(); i++) {
                    NightStockDetail nightStockDetail = new NightStockDetail(secondFragment.PieceCodeList.get(i),
                            nightStockDID);
                    if (!dbConnections.InsertNightStockDetail(nightStockDetail, getApplicationContext())) {
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving),
                                GlobalVar.AlertType.Error);
                        IsSaved = false;
                        break;
                    }
                }

                if (IsSaved) {
                    startService(
                            new Intent(NSScanShipmentActivity.this,
                                    com.naqelexpress.naqelpointer.service.NightStock.class));
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.SaveSuccessfully), GlobalVar.AlertType.Info);
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
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to enter the Waybills", GlobalVar.AlertType.Error);
            isValid = false;
        }

        if (secondFragment == null) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to scan the piece barcodes", GlobalVar.AlertType.Error);
            isValid = false;
        }

        if (firstFragment != null && firstFragment.WaybillList.size() <= 0) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to scan the Waybill List", GlobalVar.AlertType.Error);
            isValid = false;

        }

        if (secondFragment != null && secondFragment.PieceCodeList.size() <= 0) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to scan the piece barcodes", GlobalVar.AlertType.Error);
            isValid = false;
        }
        return isValid;
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {
                case 0:
                    if (firstFragment == null) {
                        firstFragment = new ScanShipmentFragment();
                        firstFragment.setArguments(bundle);
                        //return firstFragment;
                    }
                    return firstFragment;
                case 1:
                    if (secondFragment == null) {
                        secondFragment = new ScanShipmentSecondFragment();
                        secondFragment.setArguments(bundle);
                    }
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
                    return getResources().getString(R.string.ns_Waybill);
                case 1:
                    return getResources().getString(R.string.ns_Peices);
            }
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit Night stock")
                .setMessage("Are you sure you want to exit without saving?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        NSScanShipmentActivity.super.onBackPressed();
                    }
                }).setNegativeButton("Cancel", null).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}


