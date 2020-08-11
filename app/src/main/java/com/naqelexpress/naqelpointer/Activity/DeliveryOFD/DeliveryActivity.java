package com.naqelexpress.naqelpointer.Activity.DeliveryOFD;

import android.Manifest;
import android.app.ActivityManager;
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
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.service.ActualLocation;
import com.naqelexpress.naqelpointer.service.LocationService;
import com.naqelexpress.naqelpointer.service.UpdateLocation;

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

import static com.naqelexpress.naqelpointer.R.id.container;

public class DeliveryActivity extends AppCompatActivity {

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

            if (!GlobalVar.isMyServiceRunning(LocationService.class, getApplicationContext())) {
                startService(new Intent(getBaseContext(),
                        LocationService.class));
            }
//            if (!GlobalVar.isMyServiceRunning(com.naqelexpress.naqelpointer.service.Radios200Service.class, getApplicationContext())) {
//                startService(
//                        new Intent(DeliveryActivity.this,
//                                com.naqelexpress.naqelpointer.service.Radios200Service.class));
//            }

        } else {
            ActivityCompat.requestPermissions(DeliveryActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            finish();
        }

        firstFragment.IsCODtextboxEnable = 0;
        firstFragment.Billingtype = "";

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
                    Toast.makeText(DeliveryActivity.this, "Our app need the Location Permission,please kindly allow me", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ondeliverypayment, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.mnuSave:
                if (firstFragment.signrequired)
                    if (secondFragment.signmand == 1) {
                        if (GlobalVar.ValidateAutomacticDate(getApplicationContext())) {
                            if (IsValid()) {
                                String division = GlobalVar.getDivision(getApplicationContext());
                                if (division.equals("Express")) {
//                                    if (GlobalVar.find200Radios(Double.parseDouble(firstFragment.Lat), Double.parseDouble(firstFragment.Longi), 200)) {
//                                        SaveData();
//                                    } else
                                    actualLocation();
                                } else
                                    SaveData();
                            }
                        } else
                            GlobalVar.RedirectSettings(DeliveryActivity.this);
                    } else
                        Toast.makeText(DeliveryActivity.this, "Kindly get Signature from Customer.", Toast.LENGTH_SHORT).show();
                else {
                    if (GlobalVar.ValidateAutomacticDate(getApplicationContext())) {
                        if (IsValid()) {
                            String division = GlobalVar.getDivision(getApplicationContext());
                            if (division.equals("Express"))
                                actualLocation();
                            else
                                SaveData();
                        }
                    } else
                        GlobalVar.RedirectSettings(DeliveryActivity.this);
                }

                return true;
          /*  case R.id.stcPay:

                if (bundle.getString("BT").equals("COD")) {
                    Intent mIntent = new Intent(this, StcPaymentGateway.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putString("WaybillNo", bundle.getString("WaybillNo"));
                    mBundle.putDouble("COD", bundle.getDouble("COD"));
                    mIntent.putExtras(mBundle);
                    startActivity(mIntent);
                } else {
                    new SweetAlertDialog(DeliveryActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Info")
                            .setContentText("This is not COD waybill,kindly try with COD waybill")
                            .show();
                }
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void crossvalidation() {

        String WaybillNo = firstFragment.txtWaybillNo.getText().toString();
        if (WaybillNo.length() > 7) {
            DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
            ArrayList<String> barcode = dbConnections.BarCode(WaybillNo, getApplicationContext());
            if (barcode.size() == 0) {
                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put("WaybillNo", Integer.parseInt(WaybillNo));
                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < thirdFragment.DeliveryBarCodeList.size(); i++) {

                        JSONObject temp = new JSONObject();
                        temp.put("BarCode", thirdFragment.DeliveryBarCodeList.get(i));
                        jsonArray.put(temp);

                    }
                    jsonObject.put("BarCode", jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String jsonData = jsonObject.toString();
                new CheckWaybillPieceCode().execute(jsonData);
            } else {
                boolean save = true;
                for (int i = 0; i < thirdFragment.DeliveryBarCodeList.size(); i++) {
                    if (!barcode.contains(thirdFragment.DeliveryBarCodeList.get(i))) {
                        save = false;
                        GlobalVar.GV().ShowDialog(DeliveryActivity.this, "Info", "This Piececode " +
                                thirdFragment.DeliveryBarCodeList.get(i) + " Not belongs to this " + WaybillNo, true);
//                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "This Piececode " +
//                                        thirdFragment.DeliveryBarCodeList.get(i) + " Not belongs to this " + WaybillNo,
//                                GlobalVar.AlertType.Error, true, getApplicationContext());
                        break;
                    }
                }
                if (save) {
                    if (GlobalVar.ValidateAutomacticDate(getApplicationContext()))
                        SaveData();
                    else
                        GlobalVar.RedirectSettings(DeliveryActivity.this);
                }
            }
        }
    }

    boolean isPartial = false;

    private void SaveData() {
        if (IsValid()) {
            isSaved();
        }
    }

    private void isSaved() {

        String WaybillNo = firstFragment.txtWaybillNo.getText().toString();
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);


        Cursor result = dbConnections.Fill("select * from MyRouteShipments Where ItemNo = '" + WaybillNo + "'",
                getApplicationContext());
        if (result.getCount() > 0) {
            result.moveToFirst();
            boolean isdelivered = result.getInt(result.getColumnIndex("IsDelivered")) > 0;
            if (!isdelivered) {
                boolean isupdatedelivered = result.getInt(result.getColumnIndex("UpdateDeliverScan")) > 0;
                if (isupdatedelivered) {
                    GlobalVar.ShowDialog(DeliveryActivity.this, "You cannot scan again",
                            "Delivery Scan Updated Against this waybill", true);
                    return;
                }
            } else {
                GlobalVar.ShowDialog(DeliveryActivity.this, "Info", "Already Delivered this Waybill", true);
                return;
            }
        }

        boolean IsSaved = true;

        String ReceiverName = String.valueOf(secondFragment.txtReceiverName.getText().toString());

        double POSAmount = 0;
        double CashAmount = 0;
        double TotalAmount;

        String Barcode = "";
        for (int i = 0; i < thirdFragment.DeliveryBarCodeList.size(); i++) {
            if (i == 0)
                Barcode = thirdFragment.DeliveryBarCodeList.get(i);
            else
                Barcode = Barcode + "," + thirdFragment.DeliveryBarCodeList.get(i);
        }


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
                TotalAmount, CashAmount, POSAmount, Barcode);

        if (DeliveryFirstFragment.al == 1) {
            dbConnections.InsertActualLocation(WaybillNo, String.valueOf(Latitude), String.valueOf(Longitude), getApplicationContext());
            ActualLocation.start(getApplicationContext());
        }

        dbConnections.UpdateProductivity_Delivered(GlobalVar.getDate(), getApplicationContext());
        result = dbConnections.Fill("select * from MyRouteShipments where ItemNo = '" + WaybillNo + "' and HasComplaint = 1", getApplicationContext());


        if (result.getCount() > 0)
            dbConnections.UpdateComplaint_Delivered(GlobalVar.getDate(), getApplicationContext());

        updateLocation();

        String iqamaid = "", phoneno = "", rname = "";
        if (secondFragment.Isnootp) {
            iqamaid = secondFragment.iqamaid.getText().toString();
            phoneno = secondFragment.phoneno.getText().toString();
            rname = secondFragment.receivername.getText().toString();
        }
        if (dbConnections.InsertOnDelivery(onDelivery, getApplicationContext(), firstFragment.al,
                iqamaid, phoneno, rname)) {

//            int DeliveryID = dbConnections.getMaxID("OnDelivery", getApplicationContext());
//            for (int i = 0; i < thirdFragment.DeliveryBarCodeList.size(); i++) {
//                OnDeliveryDetail onDeliveryDetail = new OnDeliveryDetail(thirdFragment.DeliveryBarCodeList.get(i), DeliveryID);
//                if (!dbConnections.InsertOnDeliveryDetail(onDeliveryDetail, getApplicationContext())) {
//                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving), GlobalVar.AlertType.Error);
//                    IsSaved = false;
//                    break;
//                }
//            }

            if (isPartial) {

//                stopService(
//                        new Intent(DeliveryActivity.this,
//                                com.naqelexpress.naqelpointer.service.PartialDelivery.class));

                if (IsSaved) {
                    if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.PartialDelivery.class)) {
                        startService(
                                new Intent(DeliveryActivity.this,
                                        com.naqelexpress.naqelpointer.service.PartialDelivery.class));
                    }

                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.SaveSuccessfully), GlobalVar.AlertType.Info);
//                    UpdateMyRouteShipments();
                    finish();

                } else
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.NotSaved), GlobalVar.AlertType.Error);
            } else {
//                stopService(
//                        new Intent(DeliveryActivity.this,
//                                com.naqelexpress.naqelpointer.service.PartialDelivery.class)); //OnDelivery

                if (IsSaved) {
                    if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.PartialDelivery.class)) {
                        startService(
                                new Intent(DeliveryActivity.this,
                                        com.naqelexpress.naqelpointer.service.PartialDelivery.class)); // OnDelivery
                    }

                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.SaveSuccessfully), GlobalVar.AlertType.Info);
//                    UpdateMyRouteShipments();
                    finish();

                } else
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.NotSaved), GlobalVar.AlertType.Error);
            }


        } else
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving), GlobalVar.AlertType.Error);

        result.close();
        dbConnections.close();
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

            String division = GlobalVar.getDivision(getApplicationContext());

            if (GlobalVar.GV().isneedOtp) {
                if (division.equals("Courier") && !secondFragment.Isnootp) {
                    if (secondFragment.txtotpno.getText().toString().equals("")) {
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to enter the OTPNo", GlobalVar.AlertType.Error);
//                GlobalVar.GV().ShowMessage(this,"You have to enter the Receiver Name", GlobalVar.AlertType.Error);
                        isValid = false;
                        return isValid;
                    } else {
                        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                        Cursor result = dbConnections.Fill("select * from MyRouteShipments Where ItemNo = '" + DeliveryFirstFragment.txtWaybillNo.getText().toString() + "'",
                                getApplicationContext());

                        if (result.getCount() > 0) {
                            result.moveToFirst();
                            int otpno = result.getInt(result.getColumnIndex("OTPNo"));
                            if (otpno != Integer.parseInt(secondFragment.txtotpno.getText().toString())) {
                                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Entered OTPNo is wrong , kindly contact Supervisor", GlobalVar.AlertType.Error);
                                isValid = false;
                                return isValid;

                            }
                        }
                    }
                } else if (secondFragment.Isnootp) {
                    if (secondFragment.iqamaid.getText().toString().length() == 0) {
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly enter Iqama No", GlobalVar.AlertType.Error);
                        isValid = false;
                        return isValid;
                    } else if (secondFragment.phoneno.getText().toString().length() == 0) {
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly enter Mobile No", GlobalVar.AlertType.Error);
                        isValid = false;
                        return isValid;
                    } else if (secondFragment.receivername.getText().toString().length() == 0) {
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly enter Name", GlobalVar.AlertType.Error);
                        isValid = false;
                        return isValid;
                    }
                }
            }

        }

        if (thirdFragment != null)
            if (thirdFragment.DeliveryBarCodeList.size() <= 0) {
//                GlobalVar.GV().ShowMessage(this,"You have to scan the piece barcodes", GlobalVar.AlertType.Error);
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to scan the piece barcodes", GlobalVar.AlertType.Error);
                isValid = false;
                return isValid;
            }

        if (firstFragment.PieceCount != thirdFragment.DeliveryBarCodeList.size()) {
            ShowAlertMessage("Total Pieces(" + String.valueOf(firstFragment.PieceCount) + ") and Scanned Pieces(" + String.valueOf(thirdFragment.DeliveryBarCodeList.size()) + ")" +
                    " Do you want to Save with Partial Delivery Click Continue or Scan Full Pieces?");
            isValid = false;
        }

        return isValid;
    }


    private void ShowAlertMessage(String Message) {
        AlertDialog alertDialog = new AlertDialog.Builder(DeliveryActivity.this).create();
        alertDialog.setTitle("Info");
        alertDialog.setMessage(Message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Continue",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        isPartial = true;
                        isSaved();
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        isPartial = false;
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
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
                        if (bundle != null)
                            firstFragment.setArguments(bundle);
                        return secondFragment;
                    } else {
                        return secondFragment;
                    }
                case 2:
                    if (thirdFragment == null) {
                        thirdFragment = new DeliveryThirdFragment();
                        //  thirdFragment.ShipmentBarCodeList = firstFragment.ShipmentBarCodeList;
                        return thirdFragment;
                    } else {
                        // thirdFragment.ShipmentBarCodeList = firstFragment.ShipmentBarCodeList;
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


    private class CheckWaybillPieceCode extends AsyncTask<String, Void, String> {
        String result = "";
        StringBuffer buffer;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GlobalVar.hideKeyboardFrom(getApplicationContext(), getWindow().getDecorView().getRootView());
            progressDialog = ProgressDialog.show(DeliveryActivity.this, "Please wait.", "Cross check Piececode ", true);
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "CheckWaybillPieceCode");
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
            super.onPostExecute(String.valueOf(finalJson));

            if (finalJson != null) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(finalJson);
                    if (Boolean.parseBoolean(jsonObject.getString("HasError"))) {

                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), jsonObject.getString("ErrorMessage"),
                                GlobalVar.AlertType.Error, true, getApplicationContext());
                        //             DuplicateWaybillNo("Info", "This WaybillNo Already Picked Up");
                    } else {
                        if (GlobalVar.ValidateAutomacticDate(getApplicationContext()))
                            SaveData();
                        else
                            GlobalVar.RedirectSettings(DeliveryActivity.this);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    if (GlobalVar.ValidateAutomacticDate(getApplicationContext()))
                        SaveData();
                    else
                        GlobalVar.RedirectSettings(DeliveryActivity.this);
                }
            } else {
                if (GlobalVar.ValidateAutomacticDate(getApplicationContext()))
                    SaveData();
                else
                    GlobalVar.RedirectSettings(DeliveryActivity.this);
            }
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
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
            thirdFragment.DeliveryBarCodeList = savedInstanceState.getStringArrayList("BarCodeList");
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
        if (thirdFragment != null) {
            getSupportFragmentManager().putFragment(outState, "thirdFragment", thirdFragment);
            outState.putStringArrayList("BarCodeList", thirdFragment.DeliveryBarCodeList);
        }
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

    private void actualLocation() {

        double lat = 0;
        double longi = 0;
        try {
            lat = Double.parseDouble(firstFragment.Lat);
            longi = Double.parseDouble(firstFragment.Longi);
        } catch (NumberFormatException nf) {

        }
        if (lat != 0)
            if (GlobalVar.find200Radios(lat, longi, 200)) {
                DeliveryFirstFragment.al = 0;
                crossvalidation();
                return;
            }

        AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryActivity.this);
        builder.setTitle("Info")
                .setMessage("Kindly Please update actual Location?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        DeliveryFirstFragment.al = 1;
                        crossvalidation();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DeliveryFirstFragment.al = 0;
                crossvalidation();
            }
        }).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}