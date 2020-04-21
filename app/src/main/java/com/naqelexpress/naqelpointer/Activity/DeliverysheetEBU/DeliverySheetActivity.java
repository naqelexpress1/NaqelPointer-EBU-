package com.naqelexpress.naqelpointer.Activity.DeliverysheetEBU;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.naqelexpress.naqelpointer.Activity.DeliverySheetPartial.DeliverySheet;
import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.OnCLoadingForDDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.OnCLoadingForDWaybill;
import com.naqelexpress.naqelpointer.DB.DBObjects.OnCloadingForD;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.JSON.Request.OptimizedOutOfDeliveryShipmentRequest;
import com.naqelexpress.naqelpointer.JSON.Results.OptimizedOutOfDeliveryShipmentResult;
import com.naqelexpress.naqelpointer.R;

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
import java.util.HashMap;

public class DeliverySheetActivity
        extends AppCompatActivity {
    DeliverySheetFirstFragment firstFragment;
    DeliverySheetSecondFragment secondFragment;
    DeliverySheetThirdFragment thirdFragment;
    DateTime TimeIn;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.deliverysheet);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        barcodelist.clear();
        barcode.clear();
        waybillno.clear();

        TimeIn = DateTime.now();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.atorigin, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.mnuSave:
                if (GlobalVar.ValidateAutomacticDate(getApplicationContext())) {
                    if (validateWaybillbyBarcode()) {
                        SaveData();
                    }
                } else
                    GlobalVar.RedirectSettings(DeliverySheetActivity.this);
                return true;
            case R.id.mnuGetMyRouteShipments:
                GetMyRouteShipments();
                return true;
            case R.id.PartialSave:
                if (GlobalVar.ValidateAutomacticDate(getApplicationContext())) {

                    if (secondFragment.WaybillList.size() > 0 && thirdFragment.PieceBarCodeList.size() > 0) {
                        showPopup(DeliverySheetActivity.this, p);
                    } else
                        ShowAlertMessage();
                } else
                    GlobalVar.RedirectSettings(DeliverySheetActivity.this);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void ShowAlertMessage() {
        AlertDialog alertDialog = new AlertDialog.Builder(DeliverySheetActivity.this).create();
        alertDialog.setTitle("Info");
        alertDialog.setMessage("Kindly scan at least one Waybill and Piece Barcode");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    PopupWindow popup;

    private void showPopup(final Activity context, Point p) {


        LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = layoutInflater.inflate(R.layout.tripplanpopup, viewGroup);

        final EditText tripplanbarcode = (EditText) layout.findViewById(R.id.palletbarcode);
        tripplanbarcode.setHint("");
        tripplanbarcode.setInputType(InputType.TYPE_CLASS_TEXT);

        tripplanbarcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (tripplanbarcode != null && tripplanbarcode.getText().length() == 12) {
                    if (tripplanbarcode.getText().toString().equals("D#liverys#et")) {
                        Intent intent = new Intent(DeliverySheetActivity.this, DeliverySheet.class);
                        intent.putExtra("Waybills", secondFragment.WaybillList);
                        intent.putExtra("BarCode", thirdFragment.PieceBarCodeList);
                        intent.putExtra("txtcourierID", firstFragment.txtCourierID.getText().toString());
                        intent.putExtra("truckID", firstFragment.txtTruckID.getText().toString());
                        startActivityForResult(intent, 1);
                    }
                    popup.dismiss();
                }
            }
        });

        popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setFocusable(true);
        popup.update();
        popup.setOutsideTouchable(false);
        int OFFSET_X = 30;
        int OFFSET_Y = 30;
        // Clear the default translucent background
        popup.setBackgroundDrawable(new BitmapDrawable());
        popup.showAtLocation(layout, Gravity.CENTER, p.x + OFFSET_X, p.y + OFFSET_Y);
    }

    Point p;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        int[] location = new int[2];
        p = new Point();
        p.x = location[0];
        p.y = location[1];
    }

    boolean isValid = false;
    boolean isvalidate = false;
    ProgressDialog progressDialog;

    private boolean validateWaybillbyBarcode() {
        isValid = false;
        isvalidate = false;
        if (secondFragment.WaybillList.size() == 0) {
            ErrorAlert("Info", "Kindly Scan at least One Waybill");
            return isValid;
        }

        String WayBillNo = "";

        int ammend = 0;
        for (int i = 0; i < secondFragment.WaybillList.size(); i++) {
            if (!waybillno.contains(secondFragment.WaybillList.get(i))) {
                if (ammend == 0) {
                    WayBillNo = secondFragment.WaybillList.get(i);
                    ammend++;
                } else
                    WayBillNo = WayBillNo + "," + secondFragment.WaybillList.get(i);
                isvalidate = true;
            }
        }

        final JSONObject input = new JSONObject();
        try {
            input.put("WaybilNos", WayBillNo);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        DeliverySheetActivity.this.runOnUiThread(new Runnable() {
            public void run() {


                progressDialog = ProgressDialog.show(DeliverySheetActivity.this,
                        "Info", "Validating OFD pieces with Server...", true);
                progressDialog.setCancelable(false);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (isvalidate) {

                                String result = new IsValidatePiece().execute(input.toString()).get();
                                try {
                                    if (result != null) {
                                        JSONObject response = new JSONObject(result);
                                        boolean HasError = Boolean.parseBoolean(response.getString("HasError"));
                                        if (!HasError) {
                                            isValid = fetchDate(response);
                                            if (isValid)
                                                SaveData();

                                        } else {
                                            ErrorAlert("Info", response.getString("ErrorMessage"));

                                        }
                                    } else
                                        ErrorAlert("Error", "No Internet / No Pieces / Server is Busy , kindly please try.");
                                } catch (JSONException e) {
                                    ErrorAlert("Info", e.toString());
                                    e.printStackTrace();
                                }
                            } else {
                                if (isValidatePiece())
                                    SaveData();
                                dismissprogress();
                            }
                        } catch (Exception e) {

                        }

                        dismissprogress();
                    }
                }).start();

            }
        });


        return isValid;

    }

    public static ArrayList<HashMap<String, String>> barcode = new ArrayList<>();
    public static ArrayList<String> waybillno = new ArrayList<>();
    public static ArrayList<String> barcodelist = new ArrayList<>();

    boolean fetchDate(JSONObject response) {


        try {
            JSONArray jsonArray = response.getJSONArray("BarCode");

            if (jsonArray.length() == 0)
                return false;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> temp = new HashMap<>();
                temp.put("WaybillNo", jsonObject.getString("WaybillNo"));
                temp.put("PieceCount", jsonObject.getString("PieceCount"));
                temp.put("BarCode", jsonObject.getString("BarCode"));
                if (!waybillno.contains(jsonObject.getString("WaybillNo")))
                    waybillno.add(jsonObject.getString("WaybillNo"));
                if (!barcodelist.contains(jsonObject.getString("BarCode")))
                    barcodelist.add(jsonObject.getString("BarCode"));
                barcode.add(temp);
            }


            for (int i = 0; i < barcode.size(); i++) {
                String piececode = barcode.get(i).get("BarCode");
                String waybillno = barcode.get(i).get("WaybillNo");
                if (!thirdFragment.PieceBarCodeList.contains(piececode)) {
                    ErrorAlert("Piece Barcode is Missing", waybillno + " : " + piececode);
                    //break;
                    dismissprogress();
                    return false;
                }
            }

            for (int i = 0; i < thirdFragment.PieceBarCodeList.size(); i++) {

                if (!barcodelist.contains(thirdFragment.PieceBarCodeList.get(i))) {
                    ErrorAlert("Info", "Kindly scan this piece (" + thirdFragment.PieceBarCodeList.get(i) + ")");
                    //break;
                    dismissprogress();
                    return false;
                }


            }


        } catch (JSONException e) {
            e.printStackTrace();
            dismissprogress();
            return false;
        }
        dismissprogress();
        return true;

    }

    private boolean isValidatePiece() {
        for (int i = 0; i < barcode.size(); i++) {
            String piececode = barcode.get(i).get("BarCode");
            String waybillno = barcode.get(i).get("WaybillNo");
            if (!thirdFragment.PieceBarCodeList.contains(piececode)) {
                ErrorAlert("Piece Barcode is Missing", waybillno + " : " + piececode);
                //break;
                return false;
            }
        }

        for (int i = 0; i < thirdFragment.PieceBarCodeList.size(); i++) {

            if (!barcodelist.contains(thirdFragment.PieceBarCodeList.get(i))) {
                ErrorAlert("Info", "Kindly scan this piece (" + thirdFragment.PieceBarCodeList.get(i) + ") WayBillNo");
                //break;
                return false;
            }


        }
        return true;
    }

    private void dismissprogress() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        progressDialog = null;
    }

    private void SaveData() {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        if (IsValid()) {
            boolean IsSaved = true;
            OnCloadingForD onCloadingForD = new OnCloadingForD(GlobalVar.GV().getIntegerFromString
                    (firstFragment.txtCourierID.getText().toString()),
                    thirdFragment.PieceBarCodeList.size(), secondFragment.WaybillList.size(),
                    firstFragment.txtTruckID.getText().toString());

            if (dbConnections.InsertOnCloadingForD(onCloadingForD, getApplicationContext())) {
                int OnCloadingForDID = dbConnections.getMaxID("OnCloadingForD", getApplicationContext());
                for (int i = 0; i < secondFragment.WaybillList.size(); i++) {
                    OnCLoadingForDWaybill onCLoadingForDWaybill = new OnCLoadingForDWaybill(secondFragment.WaybillList.get(i), OnCloadingForDID);
                    if (!dbConnections.InsertOnCLoadingForDWaybill(onCLoadingForDWaybill, getApplicationContext())) {
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving), GlobalVar.AlertType.Error);
                        IsSaved = false;
                        break;
                    }
                }

                for (int i = 0; i < thirdFragment.PieceBarCodeList.size(); i++) {
                    OnCLoadingForDDetail onCLoadingForDDetail = new OnCLoadingForDDetail(thirdFragment.PieceBarCodeList.get(i), OnCloadingForDID);
                    if (!dbConnections.InsertOnCLoadingForDDetail(onCLoadingForDDetail, getApplicationContext())) {
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving), GlobalVar.AlertType.Error);
                        IsSaved = false;
                        break;
                    }
                }

                if (IsSaved) {

                    if (!isMyServiceRunning(DeliverySheetActivity.class)) {
                        startService(
                                new Intent(DeliverySheetActivity.this,
                                        com.naqelexpress.naqelpointer.service.OnLoading.class));
                    }
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
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to enter the Courier ID", GlobalVar.AlertType.Error);
            isValid = false;
        }

        if (secondFragment == null) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to scan the waybill list", GlobalVar.AlertType.Error);
            isValid = false;
        }

        if (thirdFragment == null) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to scan the pieces list", GlobalVar.AlertType.Error);
            isValid = false;
        }

        if (firstFragment != null) {
            if (firstFragment.txtCourierID.getText().toString().equals("")) {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to enter the Courier ID", GlobalVar.AlertType.Error);
                isValid = false;
            }

            if (firstFragment.txtTruckID.getText().toString().equals("")) {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to enter the Truck ID", GlobalVar.AlertType.Error);
                isValid = false;
            }
        }

        if (secondFragment != null) {
            if (secondFragment.WaybillList.size() <= 0) {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to scan the Waybills", GlobalVar.AlertType.Error);
                isValid = false;
            }
        }

        if (thirdFragment != null) {
            if (thirdFragment.PieceBarCodeList.size() <= 0) {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to scan the Pieces", GlobalVar.AlertType.Error);
                isValid = false;
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
            return inflater.inflate(R.layout.deliverysheetfirstfragment, container, false);
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
                        firstFragment = new DeliverySheetFirstFragment();
                        return firstFragment;
                    } else
                        return firstFragment;
                case 1:
                    if (secondFragment == null) {
                        secondFragment = new DeliverySheetSecondFragment();
                        return secondFragment;
                    } else {
                        return secondFragment;
                    }
                case 2:
                    if (thirdFragment == null) {
                        thirdFragment = new DeliverySheetThirdFragment();
                        return thirdFragment;
                    } else {
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
                    return getResources().getString(R.string.DeliverySheetFirstFragement);
                case 1:
                    return getResources().getString(R.string.DeliverySheetSecondFragment);
                case 2:
                    return getResources().getString(R.string.DeliverySheetThirdFragment);
            }
            return null;
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
                        DeliverySheetActivity.super.onBackPressed();
                    }
                }).setNegativeButton("Cancel", null).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //--------------Get My Route Shipments -------------------------------
    public void GetMyRouteShipments() {
        GlobalVar.GV().optimizedOutOfDeliveryShipmentList = new ArrayList<>();
//        if (!GlobalVar.GV().HasInternetAccess)
//            return;

        OptimizedOutOfDeliveryShipmentRequest optimizedOutOfDeliveryShipmentRequest = new OptimizedOutOfDeliveryShipmentRequest();

        String jsonData = JsonSerializerDeserializer.serialize(optimizedOutOfDeliveryShipmentRequest, true);
        new GetMyRouteShipmentsData().execute(jsonData);
    }

    private class GetMyRouteShipmentsData extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;
        String result = "";
        StringBuffer buffer;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(DeliverySheetActivity.this, "Please wait.", "Downloading My Route Shipments.", true);
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "OptimizedOutOfDeliveryShipment");
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
            progressDialog.dismiss();
            super.onPostExecute(String.valueOf(finalJson));
            if (finalJson != null)
                new OptimizedOutOfDeliveryShipmentResult(finalJson, getWindow().getDecorView().getRootView());
        }
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            GlobalVar.GV().EmployID = savedInstanceState.getInt("EmployID");
            GlobalVar.GV().UserID = savedInstanceState.getInt("UserID");
            GlobalVar.GV().StationID = savedInstanceState.getInt("StationID");
            GlobalVar.GV().EmployMobileNo = savedInstanceState.getString("EmployMobileNo");
            GlobalVar.GV().EmployName = savedInstanceState.getString("EmployName");
            GlobalVar.GV().EmployStation = savedInstanceState.getString("EmployStation");
            barcode = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("barcode");
            barcodelist = savedInstanceState.getStringArrayList("barcodelist");
            waybillno = savedInstanceState.getStringArrayList("waybillno");

            firstFragment = (DeliverySheetFirstFragment) getSupportFragmentManager().getFragment(savedInstanceState, "firstFragment");
//            if(firstFragment.rootView == null)
//
            secondFragment = (DeliverySheetSecondFragment) getSupportFragmentManager().getFragment(savedInstanceState, "secondFragment");
            //if(thirdFragment !=null)
            thirdFragment = (DeliverySheetThirdFragment) getSupportFragmentManager().getFragment(savedInstanceState, "thirdFragment");
            GlobalVar.GV().optimizedOutOfDeliveryShipmentList = savedInstanceState.getStringArrayList("optimizedOutOfDeliveryShipmentList");

        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("EmployID", GlobalVar.GV().EmployID);
        outState.putInt("UserID", GlobalVar.GV().UserID);
        outState.putInt("StationID", GlobalVar.GV().StationID);
        outState.putString("EmployMobileNo", GlobalVar.GV().EmployMobileNo);
        outState.putString("EmployName", GlobalVar.GV().EmployName);
        outState.putString("EmployStation", GlobalVar.GV().EmployStation);
        outState.putStringArrayList("optimizedOutOfDeliveryShipmentList", GlobalVar.GV().optimizedOutOfDeliveryShipmentList);
        outState.putStringArrayList("waybillno", waybillno);
        outState.putStringArrayList("barcodelist", barcodelist);
        outState.putSerializable("barcode", barcode);

        getSupportFragmentManager().putFragment(outState, "firstFragment", firstFragment);
        getSupportFragmentManager().putFragment(outState, "secondFragment", secondFragment);
        if (thirdFragment != null)
            getSupportFragmentManager().putFragment(outState, "thirdFragment", thirdFragment);


        super.onSaveInstanceState(outState);
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

    private void ErrorAlert(final String title, final String message) {

        DeliverySheetActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                try {
                    AlertDialog alertDialog = new AlertDialog.Builder(DeliverySheetActivity.this).create();
                    alertDialog.setTitle(title);
                    alertDialog.setMessage(message);
                    alertDialog.setCancelable(false);
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
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


    private class IsValidatePiece extends AsyncTask<String, Integer, String> {
        StringBuffer buffer;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

        }

        protected String doInBackground(String... params) {

            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;
            try {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "IsValidateDeliverysheet");
                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpURLConnection.setConnectTimeout(60000);
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
            }
            return null;
//


        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute("");
            if (result != null) {


                System.out.println(result);
            }


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