package com.naqelexpress.naqelpointer.Activity.PickUp;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.AsyncTask;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.naqelexpress.naqelpointer.Activity.Booking.Booking;
import com.naqelexpress.naqelpointer.Activity.BookingCBU.BookingModel;
import com.naqelexpress.naqelpointer.Activity.WaybillMeasurments.WaybillMeasurementActivity;
import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.PickUp;
import com.naqelexpress.naqelpointer.DB.DBObjects.PickUpDetail;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.JSON.Request.BringPickUpDataRequest;
import com.naqelexpress.naqelpointer.JSON.Request.CheckWaybillAlreadyPickedUpRequest;
import com.naqelexpress.naqelpointer.JSON.Results.BringPickUpDataResult;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.service.UpdateLocation;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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

public class PickUpActivity extends AppCompatActivity {

    PickUpFirstFragment firstFragment;
    PickUpSecondFragment secondFragment;
    DateTime TimeIn;
    private Bundle bundle;
    ArrayList<Booking> bookinglist;

    int position;
    static String class_;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pickup);


        Bundle bundle = getIntent().getExtras();
        class_ = bundle.getString("class");

        if (class_.equals("BookingDetailAcyivity")) {
            position = bundle.getInt("position");
            bookinglist = getIntent().getParcelableArrayListExtra("value");
        } else if (class_.equals("BookingDetailAcyivityforCBU")) {
            position = bundle.getInt("position");
            ArrayList<BookingModel> bookinglist = (ArrayList<BookingModel>) getIntent().getSerializableExtra("value");
            setBookingData(bookinglist.get(position));
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        TimeIn = DateTime.now();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pickupmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    boolean FullyInserted = false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.mnuBringData:
                BringPickUpData();
                return true;
            case R.id.mnuSave:
                //SaveData();

                if (GlobalVar.ValidateAutomacticDate(getApplicationContext())) {
                    String waybillno = firstFragment.txtWaybillNo.getText().toString();
                    DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                    Cursor result = dbConnections.Fill("select * from PickUpAuto Where WaybillNo = '" + waybillno + "'",
                            getApplicationContext());
                    if (result.getCount() > 0) {

                        GlobalVar.ShowDialog(PickUpActivity.this, "Info", "Already Pickedup this Waybill", true);
                        return false;
                    }
                    String division = GlobalVar.getDivision(getApplicationContext());
                    if (division.equals("Express"))
                        actualLocation();//Cross Validation
                    else
                        SaveData();

                } else
                    GlobalVar.RedirectSettings(PickUpActivity.this);
                return true;

            case R.id.getdistrict:
                firstFragment.FetchDistricData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void actualLocation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PickUpActivity.this);
        builder.setTitle("Info")
                .setMessage("Kindly Please update actual Location?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        PickUpFirstFragment.al = 1;
                        SaveData();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PickUpFirstFragment.al = 0;
                SaveData();
            }
        }).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void BringPickUpData() {
        GlobalVar.hideKeyboardFrom(getApplicationContext(), getWindow().getDecorView().getRootView());
        if (firstFragment != null && !firstFragment.txtWaybillNo.getText().toString().equals("")) {
            BringPickUpDataRequest bringPickUpDataRequest = new BringPickUpDataRequest();
            bringPickUpDataRequest.WaybillNo = Integer.parseInt(firstFragment.txtWaybillNo.getText().toString());
            BringPickUpData(bringPickUpDataRequest);
        } else
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Please enter correct Waybill Number", GlobalVar.AlertType.Warning);
    }

    private void SaveData() {
        GlobalVar.hideKeyboardFrom(getApplicationContext(), getWindow().getDecorView().getRootView());
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

            requestLocation();
        } else {
            ActivityCompat.requestPermissions(PickUpActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            return;
        }

        if (IsValid()) {

            CheckWaybillAlreadyPickedUp();

        }
    }

    private void SaveData(String aftervalidduplicatewaybill) {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);

        boolean IsSaved = true;
        int ClientID = 0;
        if (firstFragment.txtClientID.getText().toString().length() > 0) {
            ClientID = Integer.parseInt(firstFragment.txtClientID.getText().toString());
        }
        final PickUp pickUp = new PickUp(Integer.parseInt(firstFragment.txtWaybillNo.getText().toString()),
                ClientID,
                firstFragment.OriginID, firstFragment.DestinationID,
                GlobalVar.GV().getIntegerFromString(firstFragment.txtPiecesCount.getText().toString()),
                GlobalVar.GV().getDoubleFromString(firstFragment.txtWeight.getText().toString()),
                DateTime.now(), DateTime.now(), firstFragment.txtRefNo.getText().toString(),
                String.valueOf(Latitude), String.valueOf(Longitude), firstFragment.districtID);

        int loadtypeid = 0;
        if (firstFragment.clientdetails.size() > 0) {
            int pos = firstFragment.Loadtype.getSelectedItemPosition();
            loadtypeid = Integer.parseInt(firstFragment.clientdetails.get(pos).get("LoadTypeID"));
        }

        updateLocation();

        String appendPiececode = "";
        for (int i = 0; i < secondFragment.PickUpBarCodeList.size(); i++) {
            if (i == 0)
                appendPiececode = secondFragment.PickUpBarCodeList.get(i);
            else
                appendPiececode = appendPiececode + "," + secondFragment.PickUpBarCodeList.get(i);

        }
        boolean isok = true;
        if (class_.equals("BookingDetailAcyivityforCBU")) {
            if (dbConnections.UpdatepickupsheetdetailsID(Integer.parseInt(firstFragment.txtWaybillNo.getText().toString()), 2)) {
                isok = dbConnections.InsertPickUp(pickUp, getApplicationContext(), loadtypeid, firstFragment.al, appendPiececode);
                if (!isok) {
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Something went wrong , please save again.", GlobalVar.AlertType.Error);
                    return;
                }
            } else {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Something went wrong , please save again.", GlobalVar.AlertType.Error);

                return;
            }
        } else
            isok = dbConnections.InsertPickUp(pickUp, getApplicationContext(), loadtypeid, firstFragment.al, appendPiececode);

        if (isok) {
            //     int PickUpID = dbConnections.getMaxID("PickUpAuto", getApplicationContext());
            //   for (int i = 0; i < secondFragment.PickUpBarCodeList.size(); i++) {
            //  PickUpDetail pickUpDetail = new PickUpDetail(secondFragment.PickUpBarCodeList.get(i), PickUpID);
//                if (!dbConnections.InsertPickUpDetail(pickUpDetail, getApplicationContext())) {
//                    FullyInserted = false;
//                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving), GlobalVar.AlertType.Error);
//                    IsSaved = false;
//                    break;
//                }
            //       }

            if (IsSaved) {
                //GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.SaveSuccessfully), GlobalVar.AlertType.Info);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Waybill Measurements")
                        .setMessage("Do you want to add the dimensions for this shipment?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                Intent intent = new Intent(PickUpActivity.this, WaybillMeasurementActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("WaybillNo", String.valueOf(pickUp.WaybillNo));
                                bundle.putString("PiecesCount", String.valueOf(pickUp.PieceCount));
                                intent.putExtras(bundle);
                                startActivityForResult(intent, 0);

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {

                                addwaybillagain();
                                //  finish();
                            }
                        }).setCancelable(false);

                //.setNegativeButton("Cancel",null).setCancelable(false);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                //finish();
            } else
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.NotSaved), GlobalVar.AlertType.Error);
        } else
            GlobalVar.GV().ShowDialog(PickUpActivity.this, "Error", "Pickup Data Not Saved Kindly try again.", true);
        //GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving), GlobalVar.AlertType.Error);

        dbConnections.close();

    }

    private void insertBarcodeDetails(PickUpDetail pickUpDetail) {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        boolean isInsert = dbConnections.InsertPickUpDetail(pickUpDetail, getApplicationContext());
        if (!isInsert)
            insertBarcodeDetails(pickUpDetail);
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

    double Latitude = 0, Longitude = 0;

    private void requestLocation() {
        Location location = GlobalVar.getLastKnownLocation(getApplicationContext());
        if (location != null) {
            Latitude = location.getLatitude();
            Longitude = location.getLongitude();
        }
    }

    private void addwaybillagain() {

        AlertDialog.Builder builder = new AlertDialog.Builder(PickUpActivity.this);
        builder.setTitle("Info")
                .setMessage("Do you want to add the Waybill again?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        firstFragment.txtWaybillNo.setText("");
                        firstFragment.txtPiecesCount.setText("");
                        secondFragment.PickUpBarCodeList.clear();
                        secondFragment.adapter.notifyDataSetChanged();
                        secondFragment.lbTotal.setText("Count : 0");
                        secondFragment.txtBarCode.setText("");
                        firstFragment.txtWeight.setText("");

                        startService();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        startService();
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result", 0);
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }
                }).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void startService() {

        stopService(
                new Intent(PickUpActivity.this,
                        com.naqelexpress.naqelpointer.service.PickUp.class));
        // if (GlobalVar.GV().GetDivision(getApplicationContext()))
        if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.PickUp.class)) {

            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.SaveSuccessfully), GlobalVar.AlertType.Info);
            startService(
                    new Intent(PickUpActivity.this,
                            com.naqelexpress.naqelpointer.service.PickUp.class));
        }

    }


    private boolean IsValid() {
        boolean isValid = true;
        if (firstFragment == null) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to enter the WaybillNo", GlobalVar.AlertType.Error);
            isValid = false;
            return false;
        }

        if (secondFragment == null) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to scan the piece barcodes", GlobalVar.AlertType.Error);
            isValid = false;
            return false;
        }

        if (firstFragment != null) {
            if (firstFragment.txtWaybillNo.getText().toString().equals("")) {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to enter the Waybill No", GlobalVar.AlertType.Error);
                isValid = false;
                return false;
            }

            if (firstFragment.OriginID == 0) {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to select the origin", GlobalVar.AlertType.Error);
                isValid = false;
                return false;
            }

            if (firstFragment.DestinationID == 0) {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to select the destination", GlobalVar.AlertType.Error);
                isValid = false;
                return false;
            }

            if (firstFragment.txtPiecesCount.getText().toString().equals("") ||
                    GlobalVar.GV().getIntegerFromString(firstFragment.txtPiecesCount.getText().toString()) <= 0) {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to enter the Pieces Count", GlobalVar.AlertType.Error);
                isValid = false;
                return false;
            }


            if (firstFragment.txtPiecesCount.getText().toString().equals("") ||
                    GlobalVar.GV().getIntegerFromString(firstFragment.txtPiecesCount.getText().toString()) <= 0) {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to enter the Pieces Count", GlobalVar.AlertType.Error);
                isValid = false;
                return false;
            }

            if (firstFragment.txtWeight.getText().toString().equals("")) //|| GlobalVar.GV().getDoubleFromString(firstFragment.txtWeight.getText().toString()) <= 0
            {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to enter the Weight of the shipment", GlobalVar.AlertType.Error);
                isValid = false;
                return false;
            }
            if (firstFragment.txtRefNo.getText().toString().equals("")) //|| GlobalVar.GV().getDoubleFromString(firstFragment.txtWeight.getText().toString()) <= 0
            {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to enter the RefNo", GlobalVar.AlertType.Error);
                isValid = false;
                return false;
            }
        }

        if (secondFragment != null) {
            if (secondFragment.PickUpBarCodeList.size() <= 0) {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to scan the piece barcodes", GlobalVar.AlertType.Error);
                isValid = false;
                return false;
            }

            int piecesCount = secondFragment.PickUpBarCodeList.size();
            if (GlobalVar.GV().getIntegerFromString(firstFragment.txtPiecesCount.getText().toString()) != piecesCount) {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Count of pieces is not matching with piece barcodes scanned.", GlobalVar.AlertType.Error);
                isValid = false;
                return false;
            }

            if (firstFragment.txtClientID.getText().toString() == null || firstFragment.txtClientID.getText().toString().length() == 0) {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(),
                        "Kindly enter ClientID ", GlobalVar.AlertType.Error);
                isValid = false;
                return false;
            }

        }

        return isValid;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                int result = data.getIntExtra("result", -1);
                if (result == 0) {
                    addwaybillagain();
                }
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


    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {

            Bundle bundle = new Bundle();
            if (class_.equals("BookingDetailAcyivity")) { //class_.equals("")
                bundle.putSerializable("value", bookinglist);
                bundle.putInt("position", position);
                bundle.putString("class", "BookingDetailAcyivity");
            } else if (class_.equals("BookingDetailAcyivityforCBU")) {
                bundle.putSerializable("value", bookinglist);
                bundle.putInt("position", position);
                bundle.putString("class", "BookingDetailAcyivityforCBU");
            } else
                bundle.putString("class", "Pickup");

            switch (pos) {
                case 0:
                    if (firstFragment == null) {
                        firstFragment = new PickUpFirstFragment();
                        firstFragment.setArguments(bundle);
                        return firstFragment;
                    } else
                        return firstFragment;
                case 1:
                    if (secondFragment == null) {
                        secondFragment = new PickUpSecondFragment();
                        secondFragment.setArguments(bundle);
                        return secondFragment;
                    } else {
                        return secondFragment;
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
                    return getResources().getString(R.string.PickUpFirstFragment);
                case 1:
                    return getResources().getString(R.string.PiecesFragment);
            }
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit PickUp")
                .setMessage("Are you sure you want to exit without saving?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        PickUpActivity.super.onBackPressed();
                    }
                }).setNegativeButton("Cancel", null).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //--------------Bring Data in PickUp -------------------------------
    public void BringPickUpData(BringPickUpDataRequest bringPickUpDataRequest) {
        String jsonData = JsonSerializerDeserializer.serialize(bringPickUpDataRequest, true);
        new BringPickUpData().execute(jsonData);
    }

    private class BringPickUpData extends AsyncTask<String, Void, String> {
        String result = "";
        StringBuffer buffer;
        ProgressDialog progressDialog;
        String DomainURL = "";
        String isInternetAvailable = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(PickUpActivity.this, "Please wait.", "Downloading your pickup request"
                    , true);
            DomainURL = GlobalVar.GV().GetDomainURL(getApplicationContext());
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(DomainURL + "BringPickUpDataForClient");
                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setReadTimeout(GlobalVar.GV().loadbalance_ConRedtimeout);
                httpURLConnection.setConnectTimeout(GlobalVar.GV().loadbalance_ConRedtimeout);
                httpURLConnection.connect();

                dos = httpURLConnection.getOutputStream();
                httpURLConnection.getOutputStream();
                dos.write(jsonData.getBytes());

                ist = httpURLConnection.getInputStream();
//                int byteCharacters;
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(ist));
                buffer = new StringBuffer();

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                return String.valueOf(buffer);
            } catch (Exception e) {
                isInternetAvailable = e.toString();
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

                if (firstFragment != null) {
                    BringPickUpDataResult bringPickUpDataResult = new BringPickUpDataResult(finalJson);
                    firstFragment.txtClientID.setText(String.valueOf(bringPickUpDataResult.ClientID));
                    int i = 0;
                    for (Integer station : firstFragment.StationList) {
                        if (station == bringPickUpDataResult.OriginStationID) {
                            firstFragment.OriginID = bringPickUpDataResult.OriginStationID;

                            if (GlobalVar.GV().IsEnglish())
                                firstFragment.txtOrigin.setText(firstFragment.StationNameList.get(i));
                            else
                                firstFragment.txtOrigin.setText(firstFragment.StationNameList.get(i));
                            break;
                        }
                        i++;
                    }
                    i = 0;
                    for (Integer station : firstFragment.StationList) {
                        if (station == bringPickUpDataResult.DestinationStationID) {
                            firstFragment.DestinationID = bringPickUpDataResult.DestinationStationID;

                            if (GlobalVar.GV().IsEnglish())
                                firstFragment.txtDestination.setText(firstFragment.StationNameList.get(i));
                            else
                                firstFragment.txtDestination.setText(firstFragment.StationNameList.get(i));
                            break;
                        }
                        i++;
                    }
                    firstFragment.txtPiecesCount.setText(String.valueOf(bringPickUpDataResult.PiecesCount));
                    firstFragment.txtWeight.setText(String.valueOf(bringPickUpDataResult.Weight));
                }
            } else {
                //GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Something went wrong,please try again later", GlobalVar.AlertType.Error);
                if (isInternetAvailable.contains("No address associated with hostname")) {
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly check your internet", GlobalVar.AlertType.Error);
                } else {
                    GlobalVar.GV().triedTimes = GlobalVar.GV().triedTimes + 1;
                    if (GlobalVar.GV().triedTimes == GlobalVar.GV().triedTimesCondition) {
                        GlobalVar.GV().SwitchoverDomain(getApplicationContext(), DomainURL);

                    }

                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.servererror), GlobalVar.AlertType.Error);
                }
            }
            progressDialog.dismiss();
        }
    }


    public void CheckWaybillAlreadyPickedUp() {

        String WaybillNo = firstFragment.txtWaybillNo.getText().toString();
        if (WaybillNo.length() > 7) {
            CheckWaybillAlreadyPickedUpRequest checkWaybillAlreadyPickedUpRequest = new CheckWaybillAlreadyPickedUpRequest();
            checkWaybillAlreadyPickedUpRequest.WaybillNo = Integer.parseInt(WaybillNo);
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("WaybillNo", Integer.parseInt(WaybillNo));
                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < secondFragment.PickUpBarCodeList.size(); i++) {

                    JSONObject temp = new JSONObject();
                    temp.put("BarCode", secondFragment.PickUpBarCodeList.get(i));
                    jsonArray.put(temp);

                }
                jsonObject.put("BarCode", jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String jsonData = jsonObject.toString();
            new CheckWaybillAlreadyPickedUpInSystem().execute(jsonData);
        }
    }

    private class CheckWaybillAlreadyPickedUpInSystem extends AsyncTask<String, Void, String> {
        String result = "";
        StringBuffer buffer;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GlobalVar.hideKeyboardFrom(getApplicationContext(), getWindow().getDecorView().getRootView());
            progressDialog = ProgressDialog.show(PickUpActivity.this, "Please wait.", "Cross check WaybillNo ", true);
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "CheckWaybillAlreadyPickedUp");
                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpURLConnection.setConnectTimeout(GlobalVar.GV().ConnandReadtimeout);
                httpURLConnection.setReadTimeout(GlobalVar.GV().ConnandReadtimeout);
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
                    if (Boolean.parseBoolean(jsonObject.getString("hasPickedUp"))) {

                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), jsonObject.getString("ErrorMessage"),
                                GlobalVar.AlertType.Error, true, getApplicationContext());
                        //             DuplicateWaybillNo("Info", "This WaybillNo Already Picked Up");
                    } else {
                        if (GlobalVar.ValidateAutomacticDate(getApplicationContext()))
                            SaveData("");
                        else
                            GlobalVar.RedirectSettings(PickUpActivity.this);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    if (GlobalVar.ValidateAutomacticDate(getApplicationContext()))
                        SaveData("");
                    else
                        GlobalVar.RedirectSettings(PickUpActivity.this);
                }
            } else {
                if (GlobalVar.ValidateAutomacticDate(getApplicationContext()))
                    SaveData("");
                else
                    GlobalVar.RedirectSettings(PickUpActivity.this);
            }
            progressDialog.dismiss();
        }
    }

    private void DuplicateWaybillNo(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PickUpActivity.this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        // SaveData();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "firstFragment", firstFragment);
        getSupportFragmentManager().putFragment(outState, "secondFragment", secondFragment);
        outState.putInt("EmployID", GlobalVar.GV().EmployID);
        outState.putInt("UserID", GlobalVar.GV().UserID);
        outState.putInt("StationID", GlobalVar.GV().StationID);
        outState.putString("EmployMobileNo", GlobalVar.GV().EmployMobileNo);
        outState.putString("EmployName", GlobalVar.GV().EmployName);
        outState.putString("EmployStation", GlobalVar.GV().EmployStation);
        outState.putParcelable("currentSettings", GlobalVar.GV().currentSettings);
        outState.putInt("currentSettingsID", GlobalVar.GV().currentSettings.ID);

        outState.putString("class_", class_);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            firstFragment = (PickUpFirstFragment) getSupportFragmentManager().getFragment(savedInstanceState, "firstFragment");
            secondFragment = (PickUpSecondFragment) getSupportFragmentManager().getFragment(savedInstanceState, "secondFragment");
            GlobalVar.GV().EmployID = savedInstanceState.getInt("EmployID");
            GlobalVar.GV().UserID = savedInstanceState.getInt("UserID");
            GlobalVar.GV().StationID = savedInstanceState.getInt("StationID");
            GlobalVar.GV().EmployMobileNo = savedInstanceState.getString("EmployMobileNo");
            GlobalVar.GV().EmployName = savedInstanceState.getString("EmployName");
            GlobalVar.GV().EmployStation = savedInstanceState.getString("EmployStation");
            GlobalVar.GV().currentSettings = savedInstanceState.getParcelable("currentSettings");
            GlobalVar.GV().currentSettings.ID = savedInstanceState.getInt("currentSettingsID");
            class_ = savedInstanceState.getString("class_");
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

    private void setBookingData(BookingModel bookingModel) {

        try {
            bookinglist = new ArrayList<>();
            Booking booking = new Booking();
            booking.RefNo = String.valueOf(bookingModel.getWaybillNo());
            booking.BillType = bookingModel.getCode();
            booking.Destination = bookingModel.getDestCode();
            booking.ContactNumber = bookingModel.getConsigneeName();
            booking.DestinationId = bookingModel.getToStationID();
            booking.EmployeeId = GlobalVar.GV().EmployID;
            booking.Orgin = bookingModel.getOrgCode();
            booking.OriginId = bookingModel.ToStationID;
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
            DateTime dt = formatter.parseDateTime(bookingModel.getDate());
            booking.BookingDate = dt;

            bookinglist.add(booking);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}