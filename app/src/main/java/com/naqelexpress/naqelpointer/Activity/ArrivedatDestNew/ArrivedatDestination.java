package com.naqelexpress.naqelpointer.Activity.ArrivedatDestNew;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static com.naqelexpress.naqelpointer.R.id.container;

public class ArrivedatDestination extends AppCompatActivity {

    Waybill courierdetails;
    OpenPallet secondFragment;
    SingleItem thirdFragment;
    TakePicture takePicture;
    DateTime TimeIn;
    String tripPlanID = "";

    static HashMap<String, String> headers = new HashMap<>();
    ArrayList<HashMap<String, String>> pallet = new ArrayList<>();
    ArrayList<HashMap<String, String>> single = new ArrayList<>();
    ArrayList<HashMap<String, String>> waybills = new ArrayList<>();
    ArrayList<HashMap<String, String>> Pieces = new ArrayList<>();
    // static ArrayList<String> Pieces = new ArrayList<>();

    static ArrayList<HashMap<String, String>> arrivedatdest = new ArrayList<>();

    public void sendData() {
        try {

            String tag = "android:switcher:" + R.id.container + ":" + 0;
            Waybill f = (Waybill) getSupportFragmentManager().findFragmentByTag(tag);
            f.displayReceivedData(tripPlanID, headers, waybills);


            String tag2 = "android:switcher:" + R.id.container + ":" + 1;
            SingleItem si = (SingleItem) getSupportFragmentManager().findFragmentByTag(tag2);
            si.displayReceivedData(tripPlanID, Pieces);

//            String tag1 = "android:switcher:" + R.id.container + ":" + 1;
//            OpenPallet op = (OpenPallet) getSupportFragmentManager().findFragmentByTag(tag1);
//            op.displayReceivedData(tripPlanID, pallet);


        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.delivery);
        Bundle bundle = getIntent().getExtras();
        tripPlanID = bundle.getString("triplanID");
        TimeIn = DateTime.now();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == 0) {
                    // Waybill.adapter.updateData(Waybill.waybilldetails);
//                    ArrayList<HashMap<String, String>> temp = new ArrayList<>();
//                    temp.addAll(Waybill.waybilldetails);
//                    Waybill.waybilldetails.clear();
//                    Waybill.waybilldetails.addAll(temp);
//                    temp.clear();
//                    Waybill.adapter.updateData();
                    Waybill.adapter.notifyDataSetChanged();
                } else if (i == 1) {
                }
                //OpenPallet.adapter.notifyDataSetChanged();

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        waybills.clear();
        pallet.clear();
        single.clear();
        headers.clear();
        Pieces.clear();

        Waybill.Selectedwaybill.clear();
        Waybill.validatewaybillist.clear();
        OpenPallet.SelectedPallet.clear();
        SingleItem.SelectedSingleLoad.clear();
        SingleItem.SingleLoad.clear();
        SingleItem.ValidateBarCodeList.clear();

        new GetSummery().execute("");


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
                    UploadAllImages();
                else
                    GlobalVar.RedirectSettings(ArrivedatDestination.this);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void UploadAllImages() {

        // LinkedList ll = new LinkedList();
        final ArrayList<Integer> imagepositions = new ArrayList<>();
        Iterator<Integer> keyIterator = takePicture.sendimages.keySet().iterator();
        while (keyIterator.hasNext()) {
            Integer key = keyIterator.next();
            imagepositions.add(key);

            //System.out.println("Code=" + key + "  Country=" + hashMap.get(key));
        }
        if (isValid())
            SaveData();
        else
            return;

        if (imagepositions.size() == 0) {
            ErrorAlert("Info", "Successfully Inserted ", 1);
            return;
        }
        final ProgressDialog progresRing = ProgressDialog.show(ArrivedatDestination.this,
                "Info", "please Wait uploading images...", true);
        progresRing.setCancelable(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < imagepositions.size(); i++) {
                        try {
                            boolean upload = false;
                            while (!upload) {
                                String result = new uploadImages().execute(String.valueOf(imagepositions.get(i))).get();
                                if (result != null && result.contains("Created Successfully")) {
                                    deleteimages(imagepositions.get(i));
                                    upload = true;
                                }
                            }
                        } catch (Exception e) {
                            System.out.println(e.toString());
                        }

                        if (imagepositions.size() - 1 == i) {
                            String result = alertMail();

                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                if (!jsonObject.getBoolean("HasError")) {

                                    GlobalVar.hideKeyboardFrom(getApplicationContext(), getWindow().getDecorView().getRootView());
                                    ErrorAlert("Info", "Successfully Inserted ", 1);
                                } else {

                                    ErrorAlert("Info", "Something Error , kindly please update again ", 0);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            // finish();
                        }
                    }

                } catch (Exception e) {

                }
                progresRing.dismiss();

            }
        }).start();


    }

    private boolean isValid() {
        boolean valid = true;

        if (SingleItem.SelectedSingleLoad.size() == 0) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly Scan atleast one Piece Barcode",
                    GlobalVar.AlertType.Error);
            return false;
        }
        if (Waybill.Selectedwaybill.size() == 0) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly Scan atleast one Waybill",
                    GlobalVar.AlertType.Error);
            return false;
        }

       /* for (int i = 0; i < Waybill.Selectedwaybill.size(); i++) {
            String waybillno = Waybill.Selectedwaybill.get(i).get("WaybillNo");

            boolean scanned = false;
            for (int j = 0; j < SingleItem.SelectedSingleLoad.size(); j++) {
                String pieceWaybill = SingleItem.SelectedSingleLoad.get(j).get("WaybillNo");
                if (waybillno.equals(pieceWaybill)) {
                    scanned = true;
                    break;
                }
            }
            if (!scanned) {
                ErrorAlert("Info", "Kindly please scan(" + waybillno + ") WaybillNo Piece Barcode", 0);
                valid = false;
                break;
            }
        }
        if (!valid)
            return false;

        for (int i = 0; i < SingleItem.SelectedSingleLoad.size(); i++) {
            String pieceWaybill = SingleItem.SelectedSingleLoad.get(i).get("WaybillNo");

            boolean scanned = false;
            for (int j = 0; j < Waybill.Selectedwaybill.size(); j++) {
                String waybillno = Waybill.Selectedwaybill.get(j).get("WaybillNo");
                if (waybillno.equals(pieceWaybill)) {
                    scanned = true;
                    break;
                }
            }
            if (!scanned) {
                ErrorAlert("Info", "Kindly please scan(" + SingleItem.SelectedSingleLoad.get(i).get("BarCode") + ") Piece Barcode WayBillNo", 0);
                valid = false;
                break;
            }
        }
*/

        return valid;
    }

    private String alertMail() {
        String incidentresult = "";
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        try {

            Iterator<Integer> keyIterator = takePicture.sendimages.keySet().iterator();
            String imagenames = "";
            int i = 1;
            while (keyIterator.hasNext()) {
                Integer key = keyIterator.next();
                jsonObject.put("image" + String.valueOf(i), takePicture.sendimages.get(key));
                String splitimagename[] = takePicture.sendimages.get(key).split("\\_");
                String splitpng[] = splitimagename[2].split("\\.");
                if (i == 1)
                    imagenames = splitpng[0];
                else
                    imagenames = imagenames + "," + splitpng[0];
                i++;
                //System.out.println("Code=" + key + "  Country=" + hashMap.get(key));
            }
            jsonObject.put("Requestby", GlobalVar.GV().EmployID);
            jsonObject.put("CreatedDate", DateTime.now());
            jsonObject.put("Imagnames", imagenames);
            jsonObject.put("Region", GlobalVar.GV().EmployStation);
            jsonObject.put("TripNumber", tripPlanID);
            // jsonArray.put(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            incidentresult = new InsertIncident().execute(jsonObject.toString()).get();
        } catch (Exception e) {
            System.out.println(e);
        }
        return incidentresult;
    }


    private class InsertIncident extends AsyncTask<String, Integer, String> {
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

                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "ArrivedatDestDamageImages");
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
        }
    }

    private void SaveData() {
        int piececount = 0;
        JSONObject header = new JSONObject();
        JSONArray palletarray = new JSONArray();
        JSONArray waybillarray = new JSONArray();
        try {
            for (int i = 0; i < OpenPallet.SelectedPallet.size(); i++) {
                JSONObject jo1 = new JSONObject();
                jo1.put("PalletNo", OpenPallet.SelectedPallet.get(i).get("PalletNo"));
                piececount = piececount + Integer.parseInt(OpenPallet.SelectedPallet.get(i).get("PieceCount"));
                palletarray.put(jo1);

            }
            piececount = piececount + SingleItem.SelectedSingleLoad.size();
            for (int i = 0; i < SingleItem.SelectedSingleLoad.size(); i++) {
                JSONObject jo1 = new JSONObject();
                jo1.put("PalletNo", SingleItem.SelectedSingleLoad.get(i).get("BarCode")); //PalletNo considered PieceCode
                palletarray.put(jo1);
            }

            for (int i = 0; i < Waybill.Selectedwaybill.size(); i++) {
                JSONObject jo1 = new JSONObject();
                jo1.put("WayBillNo", Waybill.Selectedwaybill.get(i).get("WaybillNo"));
                waybillarray.put(jo1);
            }

            header.put("UserID", GlobalVar.GV().UserID);
            header.put("CTime", DateTime.now());
            header.put("PieceCount", piececount);
            header.put("TrailerNo", tripPlanID);
            header.put("WaybillCount", Waybill.Selectedwaybill.size());
            header.put("StationID", GlobalVar.GV().StationID);

            header.put("Pallets", palletarray);
            header.put("WayBills", waybillarray);

            DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
            boolean valid = true;
            valid = dbConnections.InsertArrivedAtDest(header.toString(), getApplicationContext());
            if (!valid)
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Your data not saved,please try again", GlobalVar.AlertType.Error);


//            JSONObject images = new JSONObject();
//            JSONArray imagearray = new JSONArray();
//            for (int i = 0; i < takePicture.sendimages.size(); i++) {
//                JSONObject jo1 = new JSONObject();
//                jo1.put("Image", takePicture.sendimages.get(i));
//                imagearray.put(jo1);
//            }
//            images.put("Image", imagearray);
//
//            if (takePicture.sendimages.size() > 0) {
//                boolean valid1 = dbConnections.InsertArrivedAtDestImages(images.toString(), getApplicationContext());
//                if (!valid1)
//                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Your data not saved,please try again", GlobalVar.AlertType.Error);
//            }
//            dbConnections.close();

            if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.ArrivedatDest.class)) {
                stopService(
                        new Intent(ArrivedatDestination.this,
                                com.naqelexpress.naqelpointer.service.ArrivedatDest.class));
                startService(
                        new Intent(ArrivedatDestination.this,
                                com.naqelexpress.naqelpointer.service.ArrivedatDest.class));
            }

//            if (takePicture.sendimages.size() > 0) {
//                if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.ArrivedatDest.class)) {
//                    stopService(
//                            new Intent(ArrivedatDestination.this,
//                                    com.naqelexpress.naqelpointer.service.ArrivedatDest.class));
//                    startService(
//                            new Intent(ArrivedatDestination.this,
//                                    com.naqelexpress.naqelpointer.service.ArrivedatDest.class));
//                }
//            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void ErrorAlert(final String title, final String message, final int close) {

        ArrivedatDestination.this.runOnUiThread(new Runnable() {
            public void run() {
                try {
                    AlertDialog alertDialog = new AlertDialog.Builder(ArrivedatDestination.this).create();
                    alertDialog.setTitle(title);
                    alertDialog.setMessage(message);
                    alertDialog.setCancelable(false);
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    if (close == 1)
                                        finish();
                                }
                            });
                    alertDialog.show();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });

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
                        courierdetails = new Waybill();
                        return courierdetails;
                    } else {
                        return courierdetails;
                    }
//                case 1:
//                    if (secondFragment == null) {
//                        secondFragment = new OpenPallet();
//                        return secondFragment;
//                    } else {
//                        return secondFragment;
//                    }
                case 1:
                    if (thirdFragment == null) {
                        thirdFragment = new SingleItem();
                        // thirdFragment.ShipmentBarCodeList = firstFragment.ShipmentBarCodeList;
                        return thirdFragment;
                    } else {
                        //   thirdFragment.ShipmentBarCodeList = firstFragment.ShipmentBarCodeList;
                        return thirdFragment;
                    }
                case 2:
                    if (takePicture == null) {
                        takePicture = new TakePicture();
                        return takePicture;
                    } else {
                        return takePicture;
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
                    return "Waybill";
//                case 1:
//                    return "Open Pallet";
                case 1:
                    return "Pieces";
                case 2:
                    return "Pictures";
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
                        ArrivedatDestination.super.onBackPressed();
                    }
                }).setNegativeButton("Cancel", null).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private class GetSummery extends AsyncTask<String, Void, String> {
        String result = "";
        StringBuffer buffer;
        ProgressDialog progressDialog;
        JSONObject jsonObject;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ArrivedatDestination.this);
            //progressDialog.setMax(100);
            progressDialog.setMessage("Kindly please wait.");
            progressDialog.setTitle("Please wait");
            progressDialog.show();
            progressDialog.setCancelable(false);

            jsonObject = new JSONObject();
            try {

                jsonObject.put("TripPlanID", Integer.parseInt(tripPlanID));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = jsonObject.toString();
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;
            String error = "";

            try {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "GetArrivedatDestbyTripID");
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
                error = e.toString();
                e.printStackTrace();
            } finally {
                try {
                    if (ist != null)
                        ist.close();
                } catch (IOException e) {
                    error = e.toString();
                    e.printStackTrace();
                }
                try {
                    if (dos != null)
                        dos.close();
                } catch (IOException e) {
                    error = e.toString();
                    e.printStackTrace();
                }
                if (httpURLConnection != null)
                    httpURLConnection.disconnect();
                result = String.valueOf(buffer);
            }
            return error;
        }

        @Override
        protected void onPostExecute(String finalJson) {
            if (finalJson != null) {
                try {
                    JSONObject jsonObject = new JSONObject(finalJson);
                    JSONArray header = jsonObject.getJSONArray("Headers");
                    for (int i = 0; i < header.length(); i++) {
                        JSONObject th = header.getJSONObject(i);
                        headers.put("PalletCount", String.valueOf(th.getInt("PalletCount")));
                        headers.put("WaybillCount", String.valueOf(th.getInt("WaybillCount")));
                        headers.put("PieceCount", String.valueOf(th.getInt("PieceCount")));
                        headers.put("SingleCount", String.valueOf(th.getInt("SingleCount")));
                    }

                    JSONArray singleLoadArray = jsonObject.getJSONArray("SingleLoad");
                    for (int i = 0; i < singleLoadArray.length(); i++) {
                        JSONObject sl = singleLoadArray.getJSONObject(i);
                        HashMap<String, String> temp = new HashMap<>();
                        temp.put("PalletNo", String.valueOf(sl.getString("PalletNo")));
                        temp.put("bgcolor", "0");
                        temp.put("LoadType", String.valueOf(sl.getInt("LoadType")));
                        single.add(temp);
                    }

                    JSONArray palletize = jsonObject.getJSONArray("Palletize");
                    for (int i = 0; i < palletize.length(); i++) {
                        JSONObject pz = palletize.getJSONObject(i);
                        HashMap<String, String> temp = new HashMap<>();
                        temp.put("PalletNo", String.valueOf(pz.getString("PalletNo")));
                        temp.put("bgcolor", "0");
                        temp.put("PieceCount", String.valueOf(pz.getInt("PieceCount")));
                        pallet.add(temp);
                    }

                    JSONArray waybil = jsonObject.getJSONArray("WayBills");
                    for (int i = 0; i < waybil.length(); i++) {
                        JSONObject pz = waybil.getJSONObject(i);
                        HashMap<String, String> temp = new HashMap<>();
                        temp.put("WaybillNo", String.valueOf(pz.getInt("WaybillNo")));
                        temp.put("bgcolor", "0");
                        temp.put("TrailerNo", String.valueOf(pz.getString("TrailerNo")));
                        waybills.add(temp);
                    }

                    JSONArray piece = jsonObject.getJSONArray("PieceCode");
                    for (int i = 0; i < piece.length(); i++) {
                        HashMap<String, String> temp = new HashMap<>();
                        JSONObject pz = piece.getJSONObject(i);
                        temp.put("BarCode", pz.getString("BarCode"));
                        temp.put("bgcolor", "0");
                        temp.put("WaybillNo", String.valueOf(pz.getInt("WaybillNo")));
                        Pieces.add(temp);
                    }

                    sendData();
                } catch (JSONException e) {
                    if (finalJson.contains("java.net.ConnectException"))
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly please check your internet connection", GlobalVar.AlertType.Error);
                    e.printStackTrace();
                }


            } else {

                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "No data with these Data's", GlobalVar.AlertType.Error);
            }

            progressDialog.dismiss();
            super.onPostExecute(String.valueOf(finalJson));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, "courierdetails", courierdetails);
        getSupportFragmentManager().putFragment(outState, "takePicture", takePicture);
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
        outState.putSerializable("headers", headers);
        outState.putSerializable("pallet", pallet);
        outState.putSerializable("single", single);
        outState.putSerializable("waybills", waybills);
        outState.putSerializable("Pieces", Pieces);


    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            courierdetails = (Waybill) getSupportFragmentManager().getFragment(savedInstanceState, "courierdetails");
            takePicture = (TakePicture) getSupportFragmentManager().getFragment(savedInstanceState, "takePicture");
            thirdFragment = (SingleItem) getSupportFragmentManager().getFragment(savedInstanceState, "thirdFragment");
            GlobalVar.GV().EmployID = savedInstanceState.getInt("EmployID");
            GlobalVar.GV().UserID = savedInstanceState.getInt("UserID");
            GlobalVar.GV().StationID = savedInstanceState.getInt("StationID");
            GlobalVar.GV().EmployMobileNo = savedInstanceState.getString("EmployMobileNo");
            GlobalVar.GV().EmployName = savedInstanceState.getString("EmployName");
            GlobalVar.GV().EmployStation = savedInstanceState.getString("EmployStation");
            GlobalVar.GV().currentSettings = savedInstanceState.getParcelable("currentSettings");
            GlobalVar.GV().currentSettings.ID = savedInstanceState.getInt("currentSettingsID");
            headers = (HashMap<String, String>) savedInstanceState.getSerializable("headers");
            pallet = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("pallet");
            single = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("single");
            waybills = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("waybills");
            Pieces = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("Pieces");

        }
    }

    private class uploadImages extends AsyncTask<String, Integer, String> {
        StringBuffer buffer;
        int position = 0;

        @Override
        protected void onPreExecute() {

            // if (progressDialog == null)
            //   progressDialog = ProgressDialog.show(ArrivedatDestination.this,
            //         "Please wait.", "Uploading Images to Server.", true);
            super.onPreExecute();

        }

        protected String doInBackground(String... params) {
            position = Integer.parseInt(params[0]);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("ImageName", takePicture.sendimages.get(position));
                String image = convertimage(position);
                if (!image.equals("error"))
                    jsonObject.put("FileName", image);
                else
                    return "201 - Created Successfully";
            } catch (JSONException e) {
                e.printStackTrace();

            }

            String jsonData = jsonObject.toString();
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {

                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "upload");
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

    private void deleteimages(int position) {
        try {

            File sourceFile = new File(GlobalVar.naqelvehicleimagepath + "/" +
                    takePicture.sendimages.get(position));

            if (sourceFile.exists()) {
                deletefile(position);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String convertimage(int position) {
        String image = "error";
        String imagename = GlobalVar.naqelvehicleimagepath + "/" + takePicture.sendimages.get(position);

        File imgfile = new File(imagename);


        Uri outputFileUri = null;
        if (Build.VERSION.SDK_INT > 23)
            outputFileUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".fileprovider",
                    imgfile);
        else
            outputFileUri = Uri.fromFile(imgfile);

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), outputFileUri);
            Bitmap lastBitmap = null;
            lastBitmap = bitmap;

            image = getStringImage(lastBitmap);
            Log.d("image", image);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;

    }

    private void deletefile(int position) {
        try {
            File deletefile = new File(GlobalVar.naqelvehicleimagepath + "/"
                    + takePicture.sendimages.get(position));
            deletefile.delete();
        } catch (Exception e) {
            System.out.println(e);
        }

    }


}
