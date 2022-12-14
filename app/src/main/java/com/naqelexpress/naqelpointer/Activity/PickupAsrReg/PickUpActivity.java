package com.naqelexpress.naqelpointer.Activity.PickupAsrReg;

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
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
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

import com.naqelexpress.naqelpointer.Activity.BookingCBU.PickupSheetReasonModel;
import com.naqelexpress.naqelpointer.Activity.SPAsrRegularBooking.BookingList;
import com.naqelexpress.naqelpointer.Activity.SPAsrRegularBooking.BookingModel;
import com.naqelexpress.naqelpointer.Activity.SPbookingException.SpWaybillException;
import com.naqelexpress.naqelpointer.Activity.SPbookingGroup.SpWaybillGroup;
import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.PickUp;
import com.naqelexpress.naqelpointer.DB.DBObjects.PickUpDetail;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.JSON.Request.BringPickUpDataRequest;
import com.naqelexpress.naqelpointer.JSON.Request.CheckWaybillAlreadyPickedUpRequest;
import com.naqelexpress.naqelpointer.R;
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

public class PickUpActivity extends AppCompatActivity {

    PickUpFirstFragment firstFragment;
    //PickUpSecondFragment secondFragment;
    DateTime TimeIn;
    private Bundle bundle;
    ArrayList<BookingModel> bookinglist;

    int position;
    int class_;
    static String RefNo = "";
    ArrayList<String> name;
    ArrayList<Integer> IDs;
    ArrayList<PickupSheetReasonModel> pickupSheetReasonModelArrayList;
    ArrayList<String> waybilllist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pickup);


        Bundle bundle = getIntent().getExtras();

        RefNo = "";

        //class 0 - SP , 1 - BL

        position = bundle.getInt("position");

        bookinglist = (ArrayList<BookingModel>)
                getIntent().getSerializableExtra("value");
        class_ = getIntent().getIntExtra("class", 1);
        // IDs = getIntent().getIntegerArrayListExtra("IDs");
        pickupSheetReasonModelArrayList = (ArrayList<PickupSheetReasonModel>)
                getIntent().getSerializableExtra("PRMA");
        if (pickupSheetReasonModelArrayList.size() > 0)
            fetchPickupsheetReasons();

        if (class_ == 0) {
            waybilllist =
                    getIntent().getStringArrayListExtra("waybilllist");
        }
        //bookinglist. add (bl.get(position));

//        setBookingData(bookinglist.get(position));


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(1);

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
                //BringPickUpData();
                return true;
            case R.id.mnuSave:
                //SaveData();

                if (GlobalVar.ValidateAutomacticDate(getApplicationContext())) {
                    if (class_ != 0) {
                        String waybillno = firstFragment.txtWaybillNo.getText().toString();
                        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                        Cursor result = dbConnections.Fill("select * from PickUpAuto Where WaybillNo = '" + waybillno + "'",
                                getApplicationContext());
                        if (result.getCount() > 0) {

                            GlobalVar.ShowDialog(PickUpActivity.this, "Info", "Already Pickedup this Waybill", true);
                            return false;
                        }
                    }
//                    String division = GlobalVar.getDivision(getApplicationContext());
//                    if (division.equals("Express"))
//                        actualLocation();//Cross Validation
                    if (class_ == 0)
                        SaveData_SP();
                    else
                        SaveData();

//                    SaveData("");

                } else
                    GlobalVar.RedirectSettings(PickUpActivity.this);
                return true;

            case R.id.getdistrict:
                //firstFragment.FetchDistricData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.mnuBringData);
        item.setVisible(false);
        MenuItem getdistrict = menu.findItem(R.id.getdistrict);
        getdistrict.setVisible(false);

        return super.onPrepareOptionsMenu(menu);
    }

    private void actualLocation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PickUpActivity.this);
        builder.setTitle("Info")
                .setMessage("Kindly Please update actual Location?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        PickUpFirstFragment.al = 1;
                        if (class_ == 0)
                            SaveData_SP();
                        else
                            SaveData();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PickUpFirstFragment.al = 0;
                if (class_ == 0)
                    SaveData_SP();
                else
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void SaveData_SP() {
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

            if (GlobalVar.ValidateAutomacticDate(getApplicationContext())) {
                ArrayList<String> empty = new ArrayList<>();
                SaveData_SP("", true, empty);
            } else
                GlobalVar.RedirectSettings(PickUpActivity.this);

        }
    }

    private void SaveData_SP(String aftervalidduplicatewaybill, boolean isFinish, ArrayList<String> notpickedWaybills) {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);

        boolean IsSaved = true;

        ArrayList<String> wlist = new ArrayList<>();
        ArrayList<BookingModel> blist = new ArrayList<>();

        for (BookingModel bookingList : bookinglist) {

            int ClientID = 0, OriginID = 0, DestinationID = 0, PiecesCount = 0, Weight = 0, districtID = 0;
            String RefNo = "";

            int Waybillno = bookingList.getWaybillNo();
            ClientID = bookingList.getClientID();
            OriginID = bookingList.getFromStationID();
            DestinationID = bookingList.getToStationID();

            if (notpickedWaybills.contains(String.valueOf(Waybillno)))
                continue;
            //PiecesCount = firstFragment.PickUpBarCodeList.size();
            ArrayList<String> PickUpBarCodeList = new ArrayList<>();
            for (int i = 0; i < firstFragment.PickUpBarCodeList.size(); i++) {
                if (firstFragment.PickUpBarCodeList.get(i).contains(String.valueOf(Waybillno))) {
                    PickUpBarCodeList.add(firstFragment.PickUpBarCodeList.get(i));
                }
            }


            if (PickUpBarCodeList.size() == 0 && !notpickedWaybills.contains(String.valueOf(Waybillno))) { //!notpickedWaybills.contains(String.valueOf(Waybillno))
                blist.add(bookingList);
                wlist.add(String.valueOf(Waybillno));
                continue;
            }

//            else if (!notpickedWaybills.contains(String.valueOf(Waybillno))) {
//                blist.add(bookingList);
//                wlist.add(String.valueOf(Waybillno));
//                continue;
//            }

//        Weight = bookinglist.get(position).getW
            RefNo = bookinglist.get(position).getRefNo();

            final PickUp pickUp = new PickUp(Waybillno,
                    ClientID,
                    OriginID, DestinationID,
                    PiecesCount,
                    Weight,
                    DateTime.now(), DateTime.now(), RefNo,
                    String.valueOf(Latitude), String.valueOf(Longitude), districtID
                    , bookinglist.get(position).getSPLOfficesID(), 0);

            int loadtypeid = 0;


            updateLocation();

            String appendPiececode = "";
            for (int i = 0; i < PickUpBarCodeList.size(); i++) {
                if (i == 0)
                    appendPiececode = PickUpBarCodeList.get(i);
                else
                    appendPiececode = appendPiececode + "," + PickUpBarCodeList.get(i);

            }

            boolean isok = true;

            if (dbConnections.UpdatepickupsheetdetailsID(Waybillno, 2)) {
                isok = dbConnections.InsertPickUp(pickUp, getApplicationContext(), loadtypeid, firstFragment.al, appendPiececode);
                if (!isok) {
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Something went wrong , please save again.", GlobalVar.AlertType.Error);
                    return;
                }
            } else {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Something went wrong , please save again.", GlobalVar.AlertType.Error);

                return;
            }


            dbConnections.close();

        }

        startService();

        if (isFinish) {

            isFinish();
        } else {
            // wlist.removeAll();
            Intent intent = new Intent(PickUpActivity.this, SpWaybillException.class);
            intent.putExtra("PRMA", pickupSheetReasonModelArrayList);
            intent.putExtra("waybilllist", wlist);
            intent.putExtra("blist", blist);
            intent.putExtra("value", bookinglist);
            startActivityForResult(intent, 100);
        }
//        } else
//            GlobalVar.GV().ShowDialog(PickUpActivity.this, "Error", "Pickup Data Not Saved Kindly try again.", true);

    }

    private void isFinish() {
        BookingList.isFinish = true;
        SpWaybillGroup.isFinish = true;
        finish();
    }

    private void SaveData(String aftervalidduplicatewaybill) {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);

        boolean IsSaved = true;
        int ClientID = 0, OriginID = 0, DestinationID = 0, PiecesCount = 0, Weight = 0, districtID = 0;
        String RefNo = "";

        int Waybillno = bookinglist.get(position).getWaybillNo();
        ClientID = bookinglist.get(position).getClientID();
        OriginID = bookinglist.get(position).getFromStationID();
        DestinationID = bookinglist.get(position).getToStationID();
        PiecesCount = firstFragment.PickUpBarCodeList.size();
//        Weight = bookinglist.get(position).getW
        RefNo = bookinglist.get(position).getRefNo();

        final PickUp pickUp = new PickUp(Waybillno,
                ClientID,
                OriginID, DestinationID,
                PiecesCount,
                Weight,
                DateTime.now(), DateTime.now(), RefNo,
                String.valueOf(Latitude), String.valueOf(Longitude), districtID, 0,
                Integer.parseInt(firstFragment.txtCollectedPiece.getText().toString()));

        int loadtypeid = 0;


        updateLocation();

        String appendPiececode = "";
        for (int i = 0; i < firstFragment.PickUpBarCodeList.size(); i++) {
            if (i == 0)
                appendPiececode = firstFragment.PickUpBarCodeList.get(i);
            else
                appendPiececode = appendPiececode + "," + firstFragment.PickUpBarCodeList.get(i);

        }

        boolean isok = true;

        if (dbConnections.UpdatepickupsheetdetailsID(Waybillno, 2)) {
            isok = dbConnections.InsertPickUp(pickUp, getApplicationContext(), loadtypeid, firstFragment.al, appendPiececode);
            if (!isok) {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Something went wrong , please save again.", GlobalVar.AlertType.Error);
                return;
            }
        } else {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Something went wrong , please save again.", GlobalVar.AlertType.Error);

            return;
        }


        if (isok) {
            startService();
            BookingList.isFinish = true;
            finish();

        } else
            GlobalVar.GV().ShowDialog(PickUpActivity.this, "Error", "Pickup Data Not Saved Kindly try again.", true);


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

/*    private void addwaybillagain() {

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
    }*/

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


    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean IsValid() {
        // boolean isValid = true;

        if (firstFragment != null) {
            if (firstFragment.PickUpBarCodeList.size() <= 0) {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to scan the piece barcodes", GlobalVar.AlertType.Error);
                //  isValid = false;
                return false;
            }


            if (class_ == 0) {
                if (firstFragment.txtCollectedPiece.getText().toString().length() == 0
                        || Integer.parseInt(firstFragment.txtCollectedPiece.getText().toString()) <= 0) {
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to enter Collected Piece ", GlobalVar.AlertType.Error);
                    return false;
                }

                String wnos = waybilllist.toString();
                wnos
                        = wnos.replace("[", "")
                        .replace("]", "")
                        .replace(" ", "");

                DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                ArrayList<String> WaybillList = dbConnections.getNotPickedupList(wnos, getApplicationContext());
//                ArrayList<String> wlist = new ArrayList<>();
//                if (WaybillList.size() == 0) {
//                    // ArrayList<String> rwlist = new ArrayList<>();
//                    for (String wno : waybilllist) {
//                        if (!firstFragment.PickUpBarCodeList.contains(wno + "00001"))
//                            wlist.add(wno);
//                    }
//                    //wlist.addAll(waybilllist);
//
//                } else {
//                    for (String wno : waybilllist) {
//                        if (!firstFragment.PickUpBarCodeList.contains(wno + "00001"))
//                            wlist.add(wno);
//                    }
//                    wlist.removeAll(WaybillList);
//                    //wlist.removeAll(WaybillList);
//                }
                if (waybilllist.size() > (WaybillList.size() + firstFragment.PickUpBarCodeList.size())) {
                    pcikupwaybillcountMismatch(WaybillList);
                    return false;
                }

            }
            /*int piecesCount = secondFragment.PickUpBarCodeList.size();
            if (GlobalVar.GV().getIntegerFromString(firstFragment.txtPiecesCount.getText().toString()) != piecesCount) {
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Count of pieces is not matching with piece barcodes scanned.", GlobalVar.AlertType.Error);
                isValid = false;
                return false;
            }*/
        }

       /* if (secondFragment == null) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to scan the piece barcodes", GlobalVar.AlertType.Error);
            isValid = false;
            return false;
        }

        */


        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                int result = data.getIntExtra("result", -1);
                if (result == 0) {
//                    addwaybillagain();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        } else if (requestCode == 100 && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    if (extras.containsKey("isFinish")) {
                        boolean finish = extras.getBoolean("isFinish");
                        if (finish) {
                            isFinish();
                        }
                    }
                }

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

            bundle.putSerializable("value", bookinglist);
            bundle.putInt("position", position);

            bundle.putStringArrayList("name", name);
            bundle.putIntegerArrayList("IDs", IDs);
            bundle.putInt("class", class_);

            if (class_ == 0)
                bundle.putStringArrayList("waybilllist", waybilllist);


            switch (pos) {
                case 0:
                    if (firstFragment == null) {
                        firstFragment = new PickUpFirstFragment();
                        firstFragment.setArguments(bundle);
                        return firstFragment;
                    } else
                        return firstFragment;
                /*case 1:
                    if (secondFragment == null) {
                        secondFragment = new PickUpSecondFragment();
                        secondFragment.setArguments(bundle);
                        return secondFragment;
                    } else {
                        return secondFragment;
                    }*/
            }
            return null;
        }

        @Override
        public int getCount() {
            return 1;
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
        //new BringPickUpData().execute(jsonData);
    }


    public void CheckWaybillAlreadyPickedUp() {

        String WaybillNo = String.valueOf(bookinglist.get(position).getWaybillNo());

        if (WaybillNo.length() > 7) {
            CheckWaybillAlreadyPickedUpRequest checkWaybillAlreadyPickedUpRequest = new CheckWaybillAlreadyPickedUpRequest();
            checkWaybillAlreadyPickedUpRequest.WaybillNo = Integer.parseInt(WaybillNo);
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("WaybillNo", Integer.parseInt(WaybillNo));
                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < firstFragment.PickUpBarCodeList.size(); i++) {

                    JSONObject temp = new JSONObject();
                    temp.put("BarCode", firstFragment.PickUpBarCodeList.get(i));
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
//        getSupportFragmentManager().putFragment(outState, "secondFragment", secondFragment);
        outState.putInt("EmployID", GlobalVar.GV().EmployID);
        outState.putInt("UserID", GlobalVar.GV().UserID);
        outState.putInt("StationID", GlobalVar.GV().StationID);
        outState.putString("EmployMobileNo", GlobalVar.GV().EmployMobileNo);
        outState.putString("EmployName", GlobalVar.GV().EmployName);
        outState.putString("EmployStation", GlobalVar.GV().EmployStation);
        outState.putParcelable("currentSettings", GlobalVar.GV().currentSettings);
        outState.putInt("currentSettingsID", GlobalVar.GV().currentSettings.ID);

        outState.putInt("class_", class_);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            firstFragment = (PickUpFirstFragment) getSupportFragmentManager().getFragment(savedInstanceState, "firstFragment");
//            secondFragment = (PickUpSecondFragment) getSupportFragmentManager().getFragment(savedInstanceState, "secondFragment");
            GlobalVar.GV().EmployID = savedInstanceState.getInt("EmployID");
            GlobalVar.GV().UserID = savedInstanceState.getInt("UserID");
            GlobalVar.GV().StationID = savedInstanceState.getInt("StationID");
            GlobalVar.GV().EmployMobileNo = savedInstanceState.getString("EmployMobileNo");
            GlobalVar.GV().EmployName = savedInstanceState.getString("EmployName");
            GlobalVar.GV().EmployStation = savedInstanceState.getString("EmployStation");
            GlobalVar.GV().currentSettings = savedInstanceState.getParcelable("currentSettings");
            GlobalVar.GV().currentSettings.ID = savedInstanceState.getInt("currentSettingsID");
            class_ = savedInstanceState.getInt("class_");
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

//    private void setBookingData(BookingModel bookingModel) {
//
//        try {
//            bookinglist = new ArrayList<>();
//            Booking booking = new Booking();
//            booking.RefNo = String.valueOf(bookingModel.getWaybillNo());
//            booking.BillType = bookingModel.getCode();
//            booking.Destination = bookingModel.getDestCode();
//            booking.ContactNumber = bookingModel.getConsigneeName();
//            booking.DestinationId = bookingModel.getToStationID();
//            booking.EmployeeId = GlobalVar.GV().EmployID;
//            booking.Orgin = bookingModel.getOrgCode();
//            booking.OriginId = bookingModel.ToStationID;
//            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
//            DateTime dt = formatter.parseDateTime(bookingModel.getDate());
//            booking.BookingDate = dt;
//
//            bookinglist.add(booking);
//
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//    }

    private void fetchPickupsheetReasons() {
        name = new ArrayList<>();
        IDs = new ArrayList<>();

        for (PickupSheetReasonModel pickupSheetReasonModel : pickupSheetReasonModelArrayList) {
            name.add(pickupSheetReasonModel.getName());
            IDs.add(pickupSheetReasonModel.getID());
        }

    }


    private void pcikupwaybillcountMismatch(final ArrayList<String> notpickedWNo) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(PickUpActivity.this);
        builder1.setTitle("Info");
        builder1.setMessage("You collected less shipments than it was booked.Are you sure to continue with saving  carrent data?");
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (GlobalVar.ValidateAutomacticDate(getApplicationContext()))
                            SaveData_SP("", false, notpickedWNo);
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


}