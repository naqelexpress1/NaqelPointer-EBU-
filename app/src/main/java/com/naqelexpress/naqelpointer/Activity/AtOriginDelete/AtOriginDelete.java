package com.naqelexpress.naqelpointer.Activity.AtOriginDelete;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

public class AtOriginDelete
        extends AppCompatActivity {

    AtOriginDeleteFirstFrgament firstFragment;
    AtOriginDeleteSecondFrgament secondFragment;

    DateTime TimeIn;
    private Bundle bundle;

    public double Latitude = 0;
    public double Longitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.notdelivered);
        bundle = getIntent().getExtras();

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
                if (GlobalVar.ValidateAutomacticDate(getApplicationContext()))
                    ValidateWayBillWithPiece();
                else
                    GlobalVar.RedirectSettings(AtOriginDelete.this);
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void ValidateWayBillWithPiece() {
        if (AtOriginDeleteFirstFrgament.Selectedwaybilldetails.size() > 0 &&
                AtOriginDeleteSecondFrgament.Selectedbarcodedetails.size() > 0) {

            boolean validData = true;
            String WaybillNo = "";
            for (int i = 0; i < AtOriginDeleteFirstFrgament.Selectedwaybilldetails.size(); i++) {
                WaybillNo = AtOriginDeleteFirstFrgament.Selectedwaybilldetails.get(i).get("WaybillNo");
                for (int j = 0; j < AtOriginDeleteSecondFrgament.Selectedbarcodedetails.size(); j++) {
                    String SecWaybillNo = AtOriginDeleteSecondFrgament.Selectedbarcodedetails.get(j).get("WaybillNo");
                    if (WaybillNo.equals(SecWaybillNo)) {
                        break;
                    }
                    if (j == AtOriginDeleteSecondFrgament.Selectedbarcodedetails.size() - 1) {
                        validData = false;
                        break;
                    }
                }
                if (!validData)
                    break;
            }

            if (validData)
                SavetoLocal();
            else
                ShowAlertMessage(WaybillNo);
        } else
            ShowAlertMessage();


    }

    private void ShowAlertMessage(String WaybillNo) {
        AlertDialog alertDialog = new AlertDialog.Builder(AtOriginDelete.this).create();
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

    private void ShowAlertMessage() {
        AlertDialog alertDialog = new AlertDialog.Builder(AtOriginDelete.this).create();
        alertDialog.setTitle("Error");
        alertDialog.setMessage("Kindly Please Save Valid Data");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }


    private void SavetoLocal() {

        JSONObject header = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONArray jsonArray1 = new JSONArray();

        try {
            header.put("AppVersion", "1.0");
            header.put("CourierID", GlobalVar.GV().EmployID);
            header.put("UserID", GlobalVar.GV().UserID);
            header.put("IDs", GlobalVar.GV().UserID);
            header.put("IsSync", false);
            header.put("WaybillCount", AtOriginDeleteFirstFrgament.Selectedwaybilldetails.size());
            header.put("StationID", GlobalVar.GV().StationID);
            header.put("PieceCount", AtOriginDeleteSecondFrgament.Selectedbarcodedetails.size());
            header.put("CTime", DateTime.now().toString());

            int insertpartial = 0;
            for (int i = 0; i < AtOriginDeleteFirstFrgament.Selectedwaybilldetails.size(); i++) {

                if (insertpartial == 5) {
                    header.put("AtOriginWaybillDetails", jsonArray);
                    insertpartial = 0;
                    savetopartialy(header.toString());
                    jsonArray = new JSONArray();
                }
                insertpartial++;

                JSONObject waybill = new JSONObject();
                waybill.put("WaybillNo", AtOriginDeleteFirstFrgament.Selectedwaybilldetails.get(i).get("WaybillNo"));
                waybill.put("IsSync", false);
                jsonArray.put(waybill);

            }
            insertpartial = 0;
            for (int i = 0; i < AtOriginDeleteSecondFrgament.Selectedbarcodedetails.size(); i++) {

                if (insertpartial == 5) {
                    header.put("AtOriginDetails", jsonArray1);
                    insertpartial = 0;
                    savetopartialy(header.toString());
                    jsonArray1 = new JSONArray();
                }
                insertpartial++;

                JSONObject waybill = new JSONObject();
                waybill.put("BarCode", AtOriginDeleteSecondFrgament.Selectedbarcodedetails.get(i).get("BarCode"));
                waybill.put("IsSync", false);
                jsonArray1.put(waybill);

            }
            if (jsonArray.length() > 0 || jsonArray1.length() > 0) {
                header.put("AtOriginWaybillDetails", jsonArray);
                header.put("AtOriginDetails", jsonArray1);
                savetopartialy(header.toString());
            }

            stopService(
                    new Intent(AtOriginDelete.this,
                            com.naqelexpress.naqelpointer.service.AtOrigin.class));
            if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.AtOrigin.class)) {
                startService(
                        new Intent(AtOriginDelete.this,
                                com.naqelexpress.naqelpointer.service.AtOrigin.class));
            }

            Intent intent = new Intent();
            intent.putExtra("Save", "Save");
            setResult(RESULT_OK, intent);

            finish();
            System.out.println(header);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void savetopartialy(String header) {

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        boolean valid = true;
        valid = dbConnections.InsertAtOrigin(header, getApplicationContext());
        dbConnections.close();
        if (!valid)
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Your data not saved,please try again", GlobalVar.AlertType.Error);


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
                        firstFragment = new AtOriginDeleteFirstFrgament();
                        if (bundle != null)
                            firstFragment.setArguments(bundle);
                        return firstFragment;
                    } else
                        return firstFragment;
                case 1:
                    if (secondFragment == null) {
                        secondFragment = new AtOriginDeleteSecondFrgament();
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
                        AtOriginDelete.super.onBackPressed();
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
            firstFragment = (AtOriginDeleteFirstFrgament) getSupportFragmentManager().getFragment(savedInstanceState, "firstFragment");
            secondFragment = (AtOriginDeleteSecondFrgament) getSupportFragmentManager().getFragment(savedInstanceState, "secondFragment");
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
