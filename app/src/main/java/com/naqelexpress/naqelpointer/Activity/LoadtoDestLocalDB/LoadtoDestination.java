package com.naqelexpress.naqelpointer.Activity.LoadtoDestLocalDB;

import static com.naqelexpress.naqelpointer.R.id.container;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoadtoDestination extends AppCompatActivity {

    WayBillDetails courierdetails;
    //    BuildPallet secondFragment;
    SingleLoad thirdFragment;
    DateTime TimeIn;

    static ArrayList<HashMap<String, String>> summeryList = new ArrayList<>();
    static String triplanID = "";

    static HashMap<String, String> tripDetails = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.delivery);
        Bundle bundle = getIntent().getExtras();
        triplanID = bundle.getString("triplanID");

        TimeIn = DateTime.now();
        summeryList.clear();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == 0) {

                    WayBillDetails.adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        courierdetails.validatewaybilldetails.clear();
//        secondFragment.Createpallertlist.clear();
//        secondFragment.validateBarcodeetails.clear();
        thirdFragment.ValidateBarCodeList.clear();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        JSONObject jo = new JSONObject();
        try {
            jo.put("TripPlanID", triplanID);
            getTripDetails(jo.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.loadtodest, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                int result = data.getIntExtra("result", -1);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.summery:
                Intent intent = new Intent(LoadtoDestination.this, Summery.class);
                intent.putExtra("WayBills", WayBillDetails.validatewaybilldetails);
                intent.putExtra("BarCode", SingleLoad.ValidateBarCodeList);

                startActivity(intent);

                return true;
            case R.id.tripdetails:
                Intent intent1 = new Intent(this, TripDetails.class);
                intent1.putExtra("tripDetails", tripDetails);
                startActivity(intent1);
                return true;

            case R.id.save:
                if (GlobalVar.ValidateAutomacticDate(getApplicationContext()))
                    SaveData();
                else
                    GlobalVar.RedirectSettings(LoadtoDestination.this);
                return true;
            case R.id.clear:
                if (GlobalVar.ValidateAutomacticDate(getApplicationContext())) {
                    ErrorAlertForDeleteLocalData();
                } else
                    GlobalVar.RedirectSettings(LoadtoDestination.this);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void SaveData() {

        JSONArray jsarray = new JSONArray();
        JSONObject jsobject = new JSONObject();
        JSONArray waybillarray = new JSONArray();
        JSONArray barcodearray = new JSONArray();
        JSONArray tripddetailsArray = new JSONArray();


        if (summeryList.size() > 0) {
            int waybillcount = 0;
            int pcecount = 0;

            try {

                JSONObject jo = new JSONObject();

                jo.put("CTime", DateTime.now());
                jo.put("StationID", GlobalVar.GV().StationID);
                jo.put("UserID", GlobalVar.GV().UserID);
                jo.put("TrailerNo", triplanID);

                int partialsave = 0;
                for (int i = 0; i < summeryList.size(); i++) {

                    if (partialsave == 5) {
                        jo.put("PieceCount", pcecount);
                        jo.put("WaybillCount", waybillcount);
                        jsarray.put(jo);
                        jsobject.put("OnLoading", jsarray);
                        jsobject.put("WayBill", waybillarray);
                        SavePartially(jsobject.toString(), tripddetailsArray.toString());
                        pcecount = 0;
                        waybillcount = 0;
                        tripddetailsArray = new JSONArray();
                        jsarray = new JSONArray();
                        waybillarray = new JSONArray();
                        partialsave = 0;
                    }


                    JSONObject jo1 = new JSONObject();
                    JSONObject jo2 = new JSONObject();
                    HashMap<String, String> temp = new HashMap<>();
                    temp.putAll(summeryList.get(i));
                    if (temp.get("SW").equals("1")) {
                        waybillcount = waybillcount + 1;
                        jo1.put("WaybillNo", summeryList.get(i).get("WaybillNo"));
                        waybillarray.put(jo1);
                    }
                    String remarks = temp.get("Remarks").replace(" ", ""); //remarks.length() == 0 ||
                    if ((temp.get("SW").equals("0") || !temp.get("pending").equals("0")) && remarks.length() == 0) {
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Please select the Remarks for " + temp.get("WaybillNo"), GlobalVar.AlertType.Error);
                        return;
                    }

                    jo2.put("Remark", remarks);
                    jo2.put("WaybillNo", summeryList.get(i).get("WaybillNo"));
                    jo2.put("Pieces", summeryList.get(i).get("ScannedPC"));
                    jo2.put("TripPlanID", Integer.parseInt(triplanID));
                    tripddetailsArray.put(jo2);

                    pcecount = pcecount + Integer.parseInt(temp.get("ScannedPC"));
                    partialsave++;
                }
                partialsave = 0;
                for (int i = 0; i < SingleLoad.ValidateBarCodeList.size(); i++) {
                    if (partialsave == 5) {

                        JSONArray dummy = new JSONArray();
                        jo.put("PieceCount", 0);
                        jo.put("WaybillCount", 0);
                        jsarray.put(jo);
                        jsobject.put("OnLoading", jsarray);
                        jsobject.put("WayBill", dummy);
                        jsobject.put("Barcode", barcodearray);
                        SavePartially(jsobject.toString(), "no need");
                        partialsave = 0;
                        barcodearray = new JSONArray();
                        jsarray = new JSONArray();
                    }
                    JSONObject jo1 = new JSONObject();
                    jo1.put("BarCode", SingleLoad.ValidateBarCodeList.get(i));
                    barcodearray.put(jo1);
                    partialsave++;
                }
                if (jsarray.length() > 0 || waybillarray.length() > 0 || barcodearray.length() > 0) {
                    jo.put("PieceCount", pcecount);
                    jo.put("WaybillCount", waybillcount);
                    jsobject.put("Barcode", barcodearray);
                    jsarray.put(jo);

                    jsobject.put("OnLoading", jsarray);
                    jsobject.put("WayBill", waybillarray);
                    SavePartially(jsobject.toString(), tripddetailsArray.toString());
                }


                stopService(
                        new Intent(LoadtoDestination.this,
                                com.naqelexpress.naqelpointer.service.TripPlanDetails.class));
                if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.TripPlanDetails.class)) {

                    startService(
                            new Intent(LoadtoDestination.this,
                                    com.naqelexpress.naqelpointer.service.TripPlanDetails.class));
                }

                DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                dbConnections.deleteLoadtoBytrailerNo(triplanID, getApplicationContext());
                dbConnections.close();

                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            // GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Please review summery page.", GlobalVar.AlertType.Error);
            Intent intent = new Intent(LoadtoDestination.this, Summery.class);
            intent.putExtra("WayBills", WayBillDetails.validatewaybilldetails);
            intent.putExtra("BarCode", SingleLoad.ValidateBarCodeList);

            startActivity(intent);
            return;
        }


    }

    private void SavePartially(String jsobject, String tripddetailsArray) {

        try {

            DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
            boolean valid1 = dbConnections.InsertTripPlanDetails(jsobject, getApplicationContext());
            boolean valid2 = true;
            if (!tripddetailsArray.contains("no need"))
                valid2 = dbConnections.InsertTripDDetails(tripddetailsArray, getApplicationContext(), triplanID);
            if (!valid1 || !valid2)
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Your data not saved,please try again", GlobalVar.AlertType.Error);
            dbConnections.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

//        if (secondFragment instanceof IOnFocusListenable) {
//            ((IOnFocusListenable) secondFragment).onWindowFocusChanged(hasFocus);
//        }
    }


    private void ErrorAlert(String message, String title, final int notrip) {
        AlertDialog alertDialog = new AlertDialog.Builder(LoadtoDestination.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (notrip == 0)
                            finish();
                        else {
                            JSONObject jo = new JSONObject();
                            try {
                                jo.put("TripPlanID", triplanID);
                                getTripDetails(jo.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        finish();
                    }
                });
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


    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (courierdetails == null) {
                        courierdetails = new WayBillDetails();
                        return courierdetails;
                    } else {
                        return courierdetails;
                    }

                case 1:
                    if (thirdFragment == null) {
                        thirdFragment = new SingleLoad();
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
                    return "Waybill";
                case 1:
                    return "Pieces";
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
                        LoadtoDestination.super.onBackPressed();
                    }
                }).setNegativeButton("Cancel", null).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, "courierdetails", courierdetails);
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
        outState.putSerializable("tripDetails", tripDetails);
        outState.putSerializable("summeryList", summeryList);
        outState.putString("tripDetails", triplanID);


    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            courierdetails = (WayBillDetails) getSupportFragmentManager().getFragment(savedInstanceState, "courierdetails");
            thirdFragment = (SingleLoad) getSupportFragmentManager().getFragment(savedInstanceState, "thirdFragment");
            GlobalVar.GV().EmployID = savedInstanceState.getInt("EmployID");
            GlobalVar.GV().UserID = savedInstanceState.getInt("UserID");
            GlobalVar.GV().StationID = savedInstanceState.getInt("StationID");
            GlobalVar.GV().EmployMobileNo = savedInstanceState.getString("EmployMobileNo");
            GlobalVar.GV().EmployName = savedInstanceState.getString("EmployName");
            GlobalVar.GV().EmployStation = savedInstanceState.getString("EmployStation");
            GlobalVar.GV().currentSettings = savedInstanceState.getParcelable("currentSettings");
            GlobalVar.GV().currentSettings.ID = savedInstanceState.getInt("currentSettingsID");
            tripDetails = (HashMap<String, String>) savedInstanceState.getSerializable("tripDetails");
            summeryList = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("summeryList");
            triplanID = savedInstanceState.getString("triplanID");

        }
    }


    public void getTripDetails(final String input) {

        final ProgressDialog progressDialog = ProgressDialog.show(this, "Please wait.", "Your request is being processed.", true);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = GlobalVar.GV().NaqelPointerAPILink + "GetTripPlanDetails";


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    boolean HasError = Boolean.parseBoolean(response.getString("HasError"));
                    String error = response.getString("ErrorMessage");
                    if (!HasError) {

                        JSONArray details = response.getJSONArray("TripPlanDetails");
                        for (int i = 0; i < details.length(); i++) {
                            JSONObject temp = details.getJSONObject(i);
                            tripDetails.put("TruckID", temp.getString("TruckID"));
                            tripDetails.put("PlateNumber", temp.getString("PlateNumber"));
                            tripDetails.put("TrailerNo", temp.getString("TrailerNo"));
                            tripDetails.put("Driver1", temp.getString("Driver1"));
                            tripDetails.put("Driver2", temp.getString("Driver2"));
                            tripDetails.put("ETD", temp.getString("ETD"));
                            tripDetails.put("TripPlanID", temp.getString("TripPlanID"));
                            tripDetails.put("TrailerID", temp.getString("TrailerID"));
                            tripDetails.put("TripCodeDetailID", temp.getString("TripCodeDetailID"));
                            tripDetails.put("TripCodeName", temp.getString("TripCodeName"));
                            tripDetails.put("OriginStationID", temp.getString("OriginStationID"));
                            tripDetails.put("DestinationStationID", temp.getString("DestinationStationID"));
                            tripDetails.put("Distance", temp.getString("Distance"));

                            WayBillDetails.tripname.setText(tripDetails.get("TripCodeName"));
                            WayBillDetails.tripcode.setText(String.valueOf(tripDetails.get("TripPlanID")));

                        }
                        progressDialog.dismiss();

                    } else {
                        ErrorAlert("Kindly please enter valid TripID", "No Trip :-" + triplanID, 0);
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    ErrorAlert(e.toString(), "Plese try again", 1);

                    progressDialog.dismiss();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorAlert(error.toString(), "Plese try again", 1);
                progressDialog.dismiss();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                try {
                    return input == null ? null : input.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", input, "utf-8");
                    return null;
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=utf-8");
                return params;
            }

        };
        jsonObjectRequest.setShouldCache(false);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjectRequest);
        requestQueue.getCache().remove(URL);

    }

    private void ErrorAlertForDeleteLocalData() {

        LoadtoDestination.this.runOnUiThread(new Runnable() {
            public void run() {
                try {
                    AlertDialog alertDialog = new AlertDialog.Builder(LoadtoDestination.this).create();
                    alertDialog.setTitle("Info");
                    alertDialog.setMessage("Do you want to Delete all Local AtDest Data?");
                    alertDialog.setCancelable(false);
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                                    dbConnections.deleteLoadtoDestLastScans(getApplicationContext());
                                    dbConnections.close();
                                    finish();
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });

    }
}