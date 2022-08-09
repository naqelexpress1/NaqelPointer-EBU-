package com.naqelexpress.naqelpointer.Activity.ArrivedatDestNoValidation;

import static com.naqelexpress.naqelpointer.R.id.container;

import android.app.Activity;
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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ArrivedatDestination extends AppCompatActivity {

    Waybill courierdetails;
    SingleItem thirdFragment;
    TakePicture takePicture;
    DateTime TimeIn;
    static String tripPlanID = "";

    static HashMap<String, String> headers = new HashMap<>();
    ArrayList<HashMap<String, String>> single = new ArrayList<>();
    ArrayList<HashMap<String, String>> waybills = new ArrayList<>();
    ArrayList<HashMap<String, String>> Pieces = new ArrayList<>();

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
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        Waybill.validatewaybillist.clear();
        SingleItem.ValidateBarCodeList.clear();


        JSONObject jo = new JSONObject();
        try {
            jo.put("TripPlanID", tripPlanID);
            getTripDetails(jo.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                int result = data.getIntExtra("result", -1);
                if (result == 0) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.atdestination, menu);
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
            case R.id.clear:
                if (GlobalVar.ValidateAutomacticDate(getApplicationContext())) {
                    ErrorAlertForDeleteLocalData();
                } else
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

        if (SingleItem.ValidateBarCodeList.size() == 0) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly Scan atleast one Piece Barcode",
                    GlobalVar.AlertType.Error);
            return false;
        }
        if (Waybill.validatewaybillist.size() == 0) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly Scan atleast one Waybill",
                    GlobalVar.AlertType.Error);
            return false;
        }


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
                httpURLConnection.setConnectTimeout(60000);
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

            piececount = piececount + SingleItem.ValidateBarCodeList.size();


            header.put("UserID", GlobalVar.GV().UserID);
            header.put("CTime", DateTime.now());
            header.put("PieceCount", piececount);
            header.put("TrailerNo", tripPlanID);
            header.put("WaybillCount", Waybill.validatewaybillist.size());
            header.put("StationID", GlobalVar.GV().StationID);


            int insertpartial = 0;
            for (int i = 0; i < SingleItem.ValidateBarCodeList.size(); i++) {

                if (insertpartial == 5) {
                    header.put("Pallets", palletarray);
                    insertpartial = 0;
                    savetopartialy(header.toString());
                    palletarray = new JSONArray();
                }
                insertpartial++;
                JSONObject jo1 = new JSONObject();
                jo1.put("PalletNo", SingleItem.ValidateBarCodeList.get(i)); //PalletNo considered PieceCode
                palletarray.put(jo1);
            }

            insertpartial = 0;
            for (int i = 0; i < Waybill.validatewaybillist.size(); i++) {

                if (insertpartial == 5) {
                    JSONArray dummy = new JSONArray();
                    header.put("Pallets", dummy);
                    header.put("WayBills", waybillarray);
                    insertpartial = 0;
                    savetopartialy(header.toString());
                    waybillarray = new JSONArray();
                }
                insertpartial++;
                JSONObject jo1 = new JSONObject();
                jo1.put("WayBillNo", Waybill.validatewaybillist.get(i));
                waybillarray.put(jo1);
            }

            if (palletarray.length() > 0 || waybillarray.length() > 0) {
                header.put("Pallets", palletarray);
                header.put("WayBills", waybillarray);
                savetopartialy(header.toString());
            }
            DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
            dbConnections.deleteAtDestbyTrailerNo(tripPlanID, getApplicationContext());
            dbConnections.close();
            if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.ArrivedatDest.class)) {
                stopService(
                        new Intent(ArrivedatDestination.this,
                                com.naqelexpress.naqelpointer.service.ArrivedatDest.class));
                startService(
                        new Intent(ArrivedatDestination.this,
                                com.naqelexpress.naqelpointer.service.ArrivedatDest.class));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void savetopartialy(String header) {

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        boolean valid = true;
        valid = dbConnections.InsertArrivedAtDest(header, getApplicationContext());
        if (!valid)
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Your data not saved,please try again", GlobalVar.AlertType.Error);


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

    private void ErrorAlertFortripDetails(final String message, final String title, final int notrip) {
        ArrivedatDestination.this.runOnUiThread(new Runnable() {
            public void run() {
                try {
                    AlertDialog alertDialog = new AlertDialog.Builder(ArrivedatDestination.this).create();
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
                                            jo.put("TripPlanID", tripPlanID);
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
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });

    }


    private void ErrorAlertForDeleteLocalData() {

        ArrivedatDestination.this.runOnUiThread(new Runnable() {
            public void run() {
                try {
                    AlertDialog alertDialog = new AlertDialog.Builder(ArrivedatDestination.this).create();
                    alertDialog.setTitle("Info");
                    alertDialog.setMessage("Do you want to Delete all Local AtDest Data?");
                    alertDialog.setCancelable(false);
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                                    dbConnections.deleteAtDestLastScans(getApplicationContext());
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

                case 1:
                    if (thirdFragment == null) {
                        thirdFragment = new SingleItem();
                        return thirdFragment;
                    } else {
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

                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink_UploadImage + "upload");
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

                            Waybill.tripname.setText(temp.getString("TripCodeName"));
                            Waybill.tripcode.setText(String.valueOf(temp.getInt("TripPlanID")));

                        }
                        progressDialog.dismiss();

                    } else {
                        Waybill.tripcode.setText("");
                        Waybill.tripname.setText("");

                        ErrorAlertFortripDetails("Kindly please enter valid TripID", "No Trip :-" + tripPlanID, 0);
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    ErrorAlertFortripDetails(e.toString(), "Plese try again", 1);

                    progressDialog.dismiss();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorAlertFortripDetails(error.toString(), "Plese try again", 1);
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

}
