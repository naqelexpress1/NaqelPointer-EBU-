package com.naqelexpress.naqelpointer.Activity.AtOrigin;

import static com.naqelexpress.naqelpointer.R.id.container;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AtOrigin extends AppCompatActivity {

    FirstFragment secondFragment;
    SecondFragment thirdFragment;
    DateTime TimeIn;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.delivery);
        bundle = getIntent().getExtras();
        TimeIn = DateTime.now();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(container);
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
                SaveData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void SaveData() {

        for (int i = 0; i < FirstFragment.Selectedwaybilldetails.size(); i++) {
            int piececount = Integer.parseInt(FirstFragment.Selectedwaybilldetails.get(i).get("PieceCount"));
            int barcodeCout = 0;
            String Waybill = "", BarCode = "";
            Waybill = FirstFragment.Selectedwaybilldetails.get(i).get("WaybillNo");
            for (int j = 0; j < SecondFragment.SelectedwaybillBardetails.size(); j++) {
                BarCode = SecondFragment.SelectedwaybillBardetails.get(j).get("BarCode");
                if (FirstFragment.Selectedwaybilldetails.get(i).get("WaybillNo").
                        equals(SecondFragment.SelectedwaybillBardetails.get(j).get("WaybillNo"))) {
                    barcodeCout = barcodeCout + 1;

                }

            }
            if (barcodeCout == 0) {
                //GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Please Scan BarCode Correctly", GlobalVar.AlertType.Error);
                BarCode = "";
                int count = 0;
                for (int k = 0; k < FirstFragment.waybillBardetails.size(); k++) {
                    if (Waybill.equals(FirstFragment.waybillBardetails.get(k).get("WaybillNo"))) {
                        if (k == 0)
                            BarCode = FirstFragment.waybillBardetails.get(k).get("BarCode");
                        else
                            BarCode = BarCode + "\n" + FirstFragment.waybillBardetails.get(k).get("BarCode");
                        count++;
                    }

                }
                ErrorAlert(Waybill, BarCode, count);

                break;
            } else if (piececount != barcodeCout) {
                BarCode = "";
                int count = 0;
                for (int k = 0; k < FirstFragment.waybillBardetails.size(); k++) {
                    boolean add = false;
                    if (Waybill.equals(FirstFragment.waybillBardetails.get(k).get("WaybillNo"))) {

                        for (int j = 0; j < SecondFragment.SelectedwaybillBardetails.size(); j++) {

                            if (FirstFragment.waybillBardetails.get(k).get("BarCode").
                                    equals(SecondFragment.SelectedwaybillBardetails.get(j).get("BarCode"))) {

                                break;
                            }

                            if (j == SecondFragment.SelectedwaybillBardetails.size() - 1) {
                                if (count == 0)
                                    BarCode = FirstFragment.waybillBardetails.get(k).get("BarCode");
                                else
                                    BarCode = BarCode + "\n" + FirstFragment.waybillBardetails.get(k).get("BarCode");
                                count++;
                            }
                        }

                    }
                }
                ErrorAlert(Waybill, BarCode, count);
                break;
            } else {
                if (FirstFragment.Selectedwaybilldetails.size() - 1 == i)
                    SavetoLocal(FirstFragment.Selectedwaybilldetails.get(i).get("EmployID"));
            }
        }
    }

    private void ErrorAlert(String Waybill, String BarCode, int Count) {
        AlertDialog alertDialog = new AlertDialog.Builder(AtOrigin.this).create();
        alertDialog.setTitle("WB:" + Waybill + "-" + String.valueOf(Count) + " Missing");
        alertDialog.setMessage(BarCode);
        alertDialog.setCancelable(false);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void SavetoLocal(String employid) {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        JSONObject header = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONArray jsonArray1 = new JSONArray();

        try {
            header.put("AppVersion", "1.0");
            header.put("CourierID", Integer.parseInt(employid));
            header.put("UserID", GlobalVar.GV().UserID);
            header.put("IDs", GlobalVar.GV().UserID);
            header.put("IsSync", false);
            header.put("WaybillCount", FirstFragment.Selectedwaybilldetails.size());
            header.put("StationID", GlobalVar.GV().StationID);
            header.put("PieceCount", SecondFragment.SelectedwaybillBardetails.size());
            header.put("CTime", DateTime.now().toString());
            for (int i = 0; i < FirstFragment.Selectedwaybilldetails.size(); i++) {
                JSONObject waybill = new JSONObject();
                waybill.put("WaybillNo", FirstFragment.Selectedwaybilldetails.get(i).get("WaybillNo"));
                waybill.put("IsSync", false);
                jsonArray.put(waybill);

            }

            for (int i = 0; i < SecondFragment.SelectedwaybillBardetails.size(); i++) {
                JSONObject waybill = new JSONObject();
                waybill.put("BarCode", SecondFragment.SelectedwaybillBardetails.get(i).get("BarCode"));
                waybill.put("IsSync", false);
                jsonArray1.put(waybill);

            }
            header.put("AtOriginWaybillDetails", jsonArray);
            header.put("AtOriginDetails", jsonArray1);

            dbConnections.InsertAtOrigin(header.toString(), getApplicationContext());
            dbConnections.close();


            if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.AtOrigin.class)) {
                startService(
                        new Intent(AtOrigin.this,
                                com.naqelexpress.naqelpointer.service.AtOrigin.class));
            }
            finish();
            System.out.println(header);

        } catch (JSONException e) {
            e.printStackTrace();
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

    public void GetPickupDatas(View view) {
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (secondFragment == null) {
                        secondFragment = new FirstFragment();
                        return secondFragment;
                    } else {
                        return secondFragment;
                    }
                case 1:
                    if (thirdFragment == null) {
                        thirdFragment = new SecondFragment();
                        // thirdFragment.ShipmentBarCodeList = firstFragment.ShipmentBarCodeList;
                        return thirdFragment;
                    } else {
                        //   thirdFragment.ShipmentBarCodeList = firstFragment.ShipmentBarCodeList;
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
                    return getResources().getString(R.string.waybilldetails);
                case 1:
                    return getResources().getString(R.string.barcodedetails);
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
                        AtOrigin.super.onBackPressed();
                    }
                }).setNegativeButton("Cancel", null).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, "secondFragment", secondFragment);
        if (thirdFragment != null)
            getSupportFragmentManager().putFragment(outState, "thirdFragment", thirdFragment);
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
            secondFragment = (FirstFragment) getSupportFragmentManager().getFragment(savedInstanceState, "secondFragment");
            thirdFragment = (SecondFragment) getSupportFragmentManager().getFragment(savedInstanceState, "thirdFragment");
            GlobalVar.GV().EmployID = savedInstanceState.getInt("EmployID");
            GlobalVar.GV().UserID = savedInstanceState.getInt("UserID");
            GlobalVar.GV().StationID = savedInstanceState.getInt("StationID");
            GlobalVar.GV().EmployMobileNo = savedInstanceState.getString("EmployMobileNo");
            GlobalVar.GV().EmployName = savedInstanceState.getString("EmployName");
            GlobalVar.GV().EmployStation = savedInstanceState.getString("EmployStation");
            GlobalVar.GV().currentSettings = savedInstanceState.getParcelable("currentSettings");
            GlobalVar.GV().currentSettings.ID = savedInstanceState.getInt("currentSettingsID");

        }
    }
}