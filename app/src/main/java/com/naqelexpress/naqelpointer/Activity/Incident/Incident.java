package com.naqelexpress.naqelpointer.Activity.Incident;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;
import com.naqelexpress.naqelpointer.Activity.Login.FindVehicle;
import com.naqelexpress.naqelpointer.Activity.routeMap.DirectionsJSONParser;
import com.naqelexpress.naqelpointer.Classes.OnSpinerItemClick;
import com.naqelexpress.naqelpointer.Classes.SpinnerDialog;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.FindVehilceObject;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// Created by Ismail on 19/02/2019.

public class Incident extends AppCompatActivity implements View.OnClickListener {
    DateTime TimeIn;
    public double Latitude = 0;
    public double Longitude = 0;


    public EditText txtCheckPointType, txtCheckPointTypeDetail, txtCheckPointTypeDDetail, vehicleno, pendingwaybill, remarks;
    SpinnerDialog checkPointTypeSpinnerDialog, checkPointTypeDetailSpinnerDialog, checkPointTypeDDetailSpinnerDialog,
            remarksspinnerdialog;
    public int CheckPointTypeID = 0, CheckPointTypeDetailID = 0, CheckPointTypeDDetailID = 0;

    public ArrayList<Integer> CheckPointTypeList = new ArrayList<>();
    public ArrayList<String> CheckPointTypeNameList = new ArrayList<>();
    public ArrayList<String> CheckPointTypeFNameList = new ArrayList<>();

    public ArrayList<Integer> CheckPointTypeDetailList = new ArrayList<>();
    public ArrayList<String> CheckPointTypeDetailNameList = new ArrayList<>();
    public ArrayList<String> CheckPointTypeDetailFNameList = new ArrayList<>();

    public ArrayList<Integer> CheckPointTypeDDetailList = new ArrayList<>();
    public ArrayList<String> CheckPointTypeDDetailNameList = new ArrayList<>();
    public ArrayList<String> CheckPointTypeDDetailFNameList = new ArrayList<>();
    public ArrayList<String> Remarks = new ArrayList<>();

    String imagenames[] = {"plateno.png", "image1.png", "image2.png", "image3.png"};

    List<AsyncTask<Integer, Integer, String>> asyncTasks = new ArrayList<AsyncTask<Integer, Integer, String>>();

    ImageView image1, image2, image3, image4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.incident);


        image1 = (ImageView) findViewById(R.id.image1);
        image1.setOnClickListener(this);

        image2 = (ImageView) findViewById(R.id.image2);
        image2.setOnClickListener(this);

        image3 = (ImageView) findViewById(R.id.image3);
        image3.setOnClickListener(this);

        image4 = (ImageView) findViewById(R.id.image4);
        image4.setOnClickListener(this);

        sendimages = new HashMap<Integer, String>();
        tempimages = new HashMap<Integer, String>();

        final EditText empid = (EditText) findViewById(R.id.empid);
        empid.setText(String.valueOf(GlobalVar.GV().EmployID));

        vehicleno = (EditText) findViewById(R.id.vehicleno);
        pendingwaybill = (EditText) findViewById(R.id.pendingwaybillno);
        remarks = (EditText) findViewById(R.id.remarks);


        vehicleno.setKeyListener(null);
        ReadFromLocal();
        vehicleno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vehicles.size() > 0)
                    RedirectVechicleClass();


            }
        });

        Button emergency = (Button) findViewById(R.id.emergency);
        emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("EmployID", Integer.parseInt(empid.getText().toString()));
                    jsonObject.put("StationID", GlobalVar.GV().StationID);
                    new Emergency().execute(jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Button close = (Button) findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("EmployID", Integer.parseInt(empid.getText().toString()));
                    jsonObject.put("StationID", GlobalVar.GV().StationID);
                    new Close().execute(jsonObject.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        txtCheckPointType = (EditText) findViewById(R.id.txtCheckPointType);
        txtCheckPointTypeDetail = (EditText) findViewById(R.id.txtCheckPointTypeDetail);
        txtCheckPointTypeDDetail = (EditText) findViewById(R.id.txtCheckPointTypeDDetail);

        txtCheckPointType.setInputType(InputType.TYPE_NULL);
        txtCheckPointTypeDetail.setInputType(InputType.TYPE_NULL);
        txtCheckPointTypeDDetail.setInputType(InputType.TYPE_NULL);
        remarks.setInputType(InputType.TYPE_NULL);

        // txtCheckPointTypeDetail.setVisibility(View.INVISIBLE);
        // txtCheckPointTypeDDetail.setVisibility(View.INVISIBLE);

        txtCheckPointType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPointTypeSpinnerDialog.showSpinerDialog(false);
            }
        });

        txtCheckPointTypeDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPointTypeDetailSpinnerDialog.showSpinerDialog(false);
            }
        });

        txtCheckPointTypeDDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPointTypeDDetailSpinnerDialog.showSpinerDialog(false);
            }
        });

        remarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remarksspinnerdialog.showSpinerDialog(false);
            }
        });


        if (GlobalVar.GV().IsEnglish())
            checkPointTypeSpinnerDialog = new SpinnerDialog(Incident.this, CheckPointTypeNameList,
                    "Select Reason", R.style.DialogAnimations_SmileWindow);
        else
            checkPointTypeSpinnerDialog = new SpinnerDialog(Incident.this, CheckPointTypeFNameList,
                    "Select Reason", R.style.DialogAnimations_SmileWindow);

        if (GlobalVar.GV().IsEnglish())
            checkPointTypeDDetailSpinnerDialog = new SpinnerDialog(Incident.this, CheckPointTypeDDetailNameList,
                    "Select Request Action", R.style.DialogAnimations_SmileWindow);
        else
            checkPointTypeDDetailSpinnerDialog = new SpinnerDialog(Incident.this, CheckPointTypeDDetailNameList,
                    "Select Request Action", R.style.DialogAnimations_SmileWindow);


        remarksspinnerdialog = new SpinnerDialog(Incident.this, Remarks,
                "Select Remarks", R.style.DialogAnimations_SmileWindow);

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("EmployID", 0);
            new BringIncident().execute(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        checkPointTypeSpinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {

                if (GlobalVar.GV().IsEnglish())
                    txtCheckPointType.setText(CheckPointTypeNameList.get(position));
                else
                    txtCheckPointType.setText(CheckPointTypeFNameList.get(position));

                CheckPointTypeID = CheckPointTypeList.get(position);

                GetSubReason(CheckPointTypeID);

                txtCheckPointTypeDetail.setText("");
                txtCheckPointTypeDDetail.setText("");

                CheckPointTypeDetailID = 0;

//                if (CheckPointTypeDetailList.size() > 0) {
////                    txtCheckPointTypeDetail.setVisibility(View.VISIBLE);
//                } else {
//                    txtCheckPointTypeDetail.setVisibility(View.INVISIBLE);
//                    CheckPointTypeDetailID = 0;
//                }
            }
        });

        if (GlobalVar.GV().IsEnglish())
            checkPointTypeDetailSpinnerDialog = new SpinnerDialog(Incident.this, CheckPointTypeDetailNameList,
                    "Select Reason", R.style.DialogAnimations_SmileWindow);
        else
            checkPointTypeDetailSpinnerDialog = new SpinnerDialog(Incident.this, CheckPointTypeDetailFNameList,
                    "Select Reason", R.style.DialogAnimations_SmileWindow);

        checkPointTypeDetailSpinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                if (GlobalVar.GV().IsEnglish())
                    txtCheckPointTypeDetail.setText(CheckPointTypeDetailNameList.get(position));
                else
                    txtCheckPointTypeDetail.setText(CheckPointTypeDetailFNameList.get(position));
                CheckPointTypeDetailID = CheckPointTypeDetailList.get(position);

//                GetCheckPointTypeDetailList();

//                txtCheckPointTypeDDetail.setText("");
//                CheckPointTypeDDetailID = 0;


//                if (CheckPointTypeDDetailList.size() > 0) {
//                    txtCheckPointTypeDDetail.setVisibility(View.VISIBLE);
//                } else {
//                    txtCheckPointTypeDDetail.setVisibility(View.INVISIBLE);
//                    CheckPointTypeDDetailID = 0;
//                }
            }
        });


        checkPointTypeDDetailSpinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                if (GlobalVar.GV().IsEnglish())
                    txtCheckPointTypeDDetail.setText(CheckPointTypeDDetailNameList.get(position));
                else
                    txtCheckPointTypeDDetail.setText(CheckPointTypeDDetailFNameList.get(position));

                CheckPointTypeDDetailID = CheckPointTypeDDetailList.get(position);

            }
        });

        remarksspinnerdialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                remarks.setText(Remarks.get(position));

            }
        });


//        requestLocation();
//        LatLng origin = new LatLng(24.5536940493677, 46.86225289318732);
//        LatLng dest = new LatLng(Latitude, Longitude);
//
//        String url = getDirectionsUrl(origin, dest);
//
//        DownloadTask downloadTask = new DownloadTask();
//        // Start downloading json data from Google Directions API
//        downloadTask.execute(url);

//        String imagename = Environment.getExternalStorageDirectory()
//                + File.separator + "test.png";
//
//        File imgfile = new File(imagename);
//
//
//        Uri outputFileUri = null;
//        if (Build.VERSION.SDK_INT > 23)
//            outputFileUri = FileProvider.getUriForFile(Incident.this, getPackageName() + ".fileprovider",
//                    imgfile);
//        else
//            outputFileUri = Uri.fromFile(imgfile);
//
//        try {
//            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), outputFileUri);
//            Bitmap lastBitmap = null;
//            lastBitmap = bitmap;
//            //encoding image to string
//            String image = getStringImage(lastBitmap);
//            Log.d("image", image);
//            //passing the image to volley
//            SendImage(image);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    public void GetCheckPointTypeList() {

        CheckPointTypeList.clear();// = new ArrayList<>();
        CheckPointTypeNameList.clear();// = new ArrayList<>();
        CheckPointTypeFNameList.clear();// = new ArrayList<>();

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);

        Cursor result = dbConnections.Fill("select * from CheckPointType", getApplicationContext());
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {
                int ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                String Name = result.getString(result.getColumnIndex("Name"));
                String FName = result.getString(result.getColumnIndex("FName"));


                CheckPointTypeList.add(ID);
                CheckPointTypeNameList.add(Name);
                CheckPointTypeFNameList.add(FName);
            }
            while (result.moveToNext());

        }
        dbConnections.close();

    }

    public void GetSubReason(int reasonid) {

        CheckPointTypeDetailNameList.clear();// = new ArrayList<>();
        CheckPointTypeDetailList.clear();// = new ArrayList<>();
        CheckPointTypeFNameList.clear();// = new ArrayList<>();


        for (int i = 0; i < sreasondata.size(); i++) {
            if (reasonid == Integer.parseInt(sreasondata.get(i).get("ReasonID"))) {

                CheckPointTypeDetailNameList.add(sreasondata.get(i).get("Name"));
                CheckPointTypeDetailList.add(Integer.parseInt(sreasondata.get(i).get("ID")));
            }

        }

    }

    public void GetCheckPointTypeDetailList() {
        CheckPointTypeDetailList.clear();// = new ArrayList<>();
        CheckPointTypeDetailNameList.clear();// = new ArrayList<>();
        CheckPointTypeDetailFNameList.clear();// = new ArrayList<>();

        String selectCommand = "select * from CheckPointTypeDetail";
        if (CheckPointTypeID > 0)
            selectCommand += " where CheckPointTypeID=" + CheckPointTypeID;

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);

        Cursor result = dbConnections.Fill(selectCommand, getApplicationContext());
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {
                int ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                String Name = result.getString(result.getColumnIndex("Name"));
                String FName = result.getString(result.getColumnIndex("FName"));
                int CheckPointTypeID = Integer.parseInt(result.getString(result.getColumnIndex("CheckPointTypeID")));

                CheckPointTypeDetailList.add(ID);
                CheckPointTypeDetailNameList.add(Name);
                CheckPointTypeDetailFNameList.add(FName);
            }
            while (result.moveToNext());

        }

        dbConnections.close();

    }

    private void requestLocation() {
        Location location = GlobalVar.getLastKnownLocation(getApplicationContext());
        if (location != null) {
            Latitude = location.getLatitude();
            Longitude = location.getLongitude();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.checkpointmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.mnuSave:

                requestLocation();
                if (toKm.equals("0") && Address.equals("0")) {

                    LatLng origin = new LatLng(24.55369404936778, 46.86225289318732);
                    LatLng dest = new LatLng(Latitude, Longitude);

                    String url = getDirectionsUrl(origin, dest);

                    DownloadTask downloadTask = new DownloadTask();
                    downloadTask.execute(url);
                } else
                    SaveData();


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void SaveData() {


        if (CheckPointTypeID == 0 || CheckPointTypeDetailID == 0 || CheckPointTypeDDetailID == 0) {
            GlobalVar.hideKeyboardFrom(getApplicationContext(), getWindow().getDecorView().getRootView());
            GlobalVar.ShowDialog(Incident.this, "Please Select Mandatory Field", "Kindly choose all fields", true);
            return;
        }

        if (vehicleno.getText().toString().replace(" ", "").length() > 0 && vehicleno.getText().toString().equals("0")) {
            GlobalVar.hideKeyboardFrom(getApplicationContext(), getWindow().getDecorView().getRootView());
            GlobalVar.ShowDialog(Incident.this, "Please Select Mandatory Field", "Kindly Enter Vehicle No", true);
            return;
        }

        if (remarks.getText().toString().replace(" ", "").length() == 0) {
            GlobalVar.hideKeyboardFrom(getApplicationContext(), getWindow().getDecorView().getRootView());
            GlobalVar.ShowDialog(Incident.this, "Please Select Mandatory Field",
                    "Kindly choose Remark from a list", true);
            return;
        }

//        if (pendingwaybill.getText().toString().length() > 0) {
//            GlobalVar.hideKeyboardFrom(getApplicationContext(), getWindow().getDecorView().getRootView());
//            GlobalVar.ShowDialog(Incident.this, "Please Select Mandatory Field", "Kindly Enter Pending Waybill Number or 0", true);
//            return;
//        }

        if (toKm.equals("0") && Address.equals("0")) {
            GlobalVar.ShowDialog(Incident.this, "Info", "Cannot get Address,Kindly please try again", true);
            GlobalVar.hideKeyboardFrom(getApplicationContext(), getWindow().getDecorView().getRootView());
            return;
        }


        if (sendimages.size() == 0) {
            GlobalVar.ShowDialog(Incident.this, "Info", "Kindly please Capture Vehicle Plate Number Image", true);
            GlobalVar.hideKeyboardFrom(getApplicationContext(), getWindow().getDecorView().getRootView());
            return;
        }

        if (!isFlag_image1) {
            Image1 image1 = new Image1();
            StartAsyncTaskInParallel(image1);
//            new Image1().execute("");
        }

        if (!isFlag_image2 && sendimages.size() > 1) {
            Image2 image2 = new Image2();
            StartAsyncTaskInParallel(image2);
//            new Image2().execute("");

        }


        if (!isFlag_image3 && sendimages.size() > 2) {
            Image3 image3 = new Image3();
            StartAsyncTaskInParallel(image3);
//            new Image3().execute("");

        }

        if (!isFlag_image4 && sendimages.size() > 3) {
            Image4 image4 = new Image4();
            StartAsyncTaskInParallel(image4);
//            new Image4().execute("");
        }


    }


    private void insertDatatoServer() {
        JSONObject jsonObject = new JSONObject();
        JSONObject incident = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        try {
            jsonObject.put("Reason", txtCheckPointType.getText().toString());

            jsonObject.put("SReason", txtCheckPointTypeDetail.getText().toString());
            jsonObject.put("RequestAction", txtCheckPointTypeDDetail.getText().toString());
            jsonObject.put("Latitude", String.valueOf(Latitude));
            jsonObject.put("Longitude", String.valueOf(Longitude));
            jsonObject.put("CreatedDate", DateTime.now());
            jsonObject.put("Requestby", GlobalVar.GV().EmployID);
            jsonObject.put("DeliverysheetID", 0);
            jsonObject.put("VehicleNo", vehicleno.getText().toString());
            jsonObject.put("image1", sendimages.get(0));
            jsonObject.put("image2", 0);
            jsonObject.put("image3", 0);
            jsonObject.put("image4", 0);
            if (sendimages.size() == 2)
                jsonObject.put("image2", sendimages.get(1));
            if (sendimages.size() == 3) {
                jsonObject.put("image2", sendimages.get(1));
                jsonObject.put("image3", sendimages.get(2));
            }
            if (sendimages.size() == 4) {
                jsonObject.put("image2", sendimages.get(1));
                jsonObject.put("image3", sendimages.get(2));
                jsonObject.put("image4", sendimages.get(3));
            }

            jsonArray.put(jsonObject);


            incident.put("Incident", jsonArray);

            incident.put("Region", GlobalVar.GV().EmployStation);
            incident.put("PendingWayBills", 0);
            incident.put("EmpName", GlobalVar.GV().EmployName);
            incident.put("Address", Address);
            incident.put("Distance", toKm);
            incident.put("Remarks", remarks.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new InsertIncident().execute(incident.toString());
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(Incident.this, "Please wait.", "Fetching Current Location"
                    , true);
        }

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);

            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (!result.equals("")) {
                ParserTask parserTask = new ParserTask();
                parserTask.execute(result);
            }

        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, JSONObject> {
        // Parsing the data in non-ui thread
        @Override
        protected JSONObject doInBackground(String... jsonData) {

            JSONObject jObject = null;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);

                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jObject);

//                parseaddress(jObject);
//                jsonroute.add(jObject.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jObject;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (result.length() > 0 && result != null) {
                System.out.println("");
                parseaddress(result);
            }
        }
    }

    String toKm = "0", Elapsetime = "0", Address = "0";

    private void parseaddress(JSONObject jObject) {

        JSONArray jRoutes = null;
        JSONArray jLegs = null;

        try {

            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {

                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                toKm = (String) ((JSONObject) ((JSONObject) jLegs.get(i)).get("distance")).get("text");
                String duration = "";
                duration = (String) ((JSONObject) ((JSONObject) jLegs.get(i)).get("duration")).get("text");

                Elapsetime = duration;
                Address = ((JSONObject) jLegs.get(i)).getString("end_address");

                progressDialog.dismiss();
                progressDialog = null;

                SaveData();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }

    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String key = "key=" + getString(R.string.google_maps_key_forrute);
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + key;

        // Output format
        String output = "json";


        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }


//    private void SaveData() {
//
//        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
//        if (IsValid()) {
//            boolean IsSaved = true;
//
//            CheckPoint checkPoint = new CheckPoint(firstFragment.CheckPointTypeID, String.valueOf(Latitude),
//                    String.valueOf(Longitude), firstFragment.CheckPointTypeDetailID, firstFragment.CheckPointTypeDDetailID);
//
//            if (dbConnections.InsertCheckPoint(checkPoint, getApplicationContext())) {
//                int ID = dbConnections.getMaxID("CheckPoint", getApplicationContext());
//                for (int i = 0; i < secondFragment.WaybillList.size(); i++) {
//                    CheckPointWaybillDetails waybills = new CheckPointWaybillDetails(secondFragment.WaybillList.get(i), ID);
//                    if (!dbConnections.InsertCheckPointWaybillDetails(waybills, getApplicationContext())) {
//                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving),
//                                GlobalVar.AlertType.Error);
//                        IsSaved = false;
//                        break;
//                    }
//                }
//
//                for (int i = 0; i < thirdFragment.BarCodeList.size(); i++) {
//                    CheckPointBarCodeDetails checkPointBarCodeDetails = new CheckPointBarCodeDetails(thirdFragment.BarCodeList.get(i), ID);
//                    if (!dbConnections.InsertCheckPointBarCodeDetails(checkPointBarCodeDetails, getApplicationContext())) {
//                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving),
//                                GlobalVar.AlertType.Error);
//                        IsSaved = false;
//                        break;
//                    }
//                }
//
//
//                if (IsSaved) {
//                    if (!isMyServiceRunning(Incident.class)) {
//                        startService(
//                                new Intent(Incident.this,
//                                        com.naqelexpress.naqelpointer.service.CheckPoint.class));
//                    }
//                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.SaveSuccessfully), GlobalVar.AlertType.Info);
//                    finish();
//                } else
//                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.NotSaved),
//                            GlobalVar.AlertType.Error);
//            } else
//                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving),
//                        GlobalVar.AlertType.Error);
//        }
//        dbConnections.close();
//    }
//
//    private boolean IsValid() {
//        boolean isValid = true;
//        if (firstFragment == null || firstFragment.CheckPointTypeID <= 0) {
//            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to select the Check Point Type",
//                    GlobalVar.AlertType.Error);
//            return false;
//        } else if (firstFragment.txtCheckPointTypeDetail.getVisibility() == View.VISIBLE &&
//                firstFragment.CheckPointTypeDetailID == 0) {
//            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to select the reason",
//                    GlobalVar.AlertType.Error);
//            return false;
//        } else if (firstFragment.txtCheckPointTypeDDetail.getVisibility() == View.VISIBLE &&
//                firstFragment.CheckPointTypeDDetailID == 0) {
//            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to select the reason",
//                    GlobalVar.AlertType.Error);
//            return false;
//        }
//
//        if (secondFragment == null) {
//            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to scan the waybill list",
//                    GlobalVar.AlertType.Error);
//            return false;
//        }
//
//        if (thirdFragment == null) {
//            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to scan the pieces list",
//                    GlobalVar.AlertType.Error);
//            return false;
//
//        }
//
//
//        if (secondFragment != null) {
//            if (secondFragment.WaybillList.size() <= 0) {
//                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to scan the Waybills",
//                        GlobalVar.AlertType.Error);
//                return false;
//            }
//        }
//
//        if (thirdFragment != null) {
//            if (thirdFragment.BarCodeList.size() <= 0 && (firstFragment.txtCheckPointTypeDDetail.getText().toString().length() > 0 &&
//                    (!firstFragment.txtCheckPointTypeDDetail.getText().toString().contains("Batha") &&
//
//                            !firstFragment.txtCheckPointTypeDDetail.getText().toString().contains("Khafji")) &&
//
//                    !firstFragment.txtCheckPointTypeDDetail.getText().toString().contains("KFIA"))) {
//                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to scan the pieces list",
//                        GlobalVar.AlertType.Error);
//                return false;
//
//
//            }
//        }
//
//        return isValid;
//    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble("Latitude", Latitude);
        outState.putDouble("Longitude", Longitude);
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

            GlobalVar.GV().EmployID = savedInstanceState.getInt("EmployID");
            GlobalVar.GV().UserID = savedInstanceState.getInt("UserID");
            GlobalVar.GV().StationID = savedInstanceState.getInt("StationID");
            GlobalVar.GV().EmployMobileNo = savedInstanceState.getString("EmployMobileNo");
            GlobalVar.GV().EmployName = savedInstanceState.getString("EmployName");
            GlobalVar.GV().EmployStation = savedInstanceState.getString("EmployStation");


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
                        finish();
                    }
                }).setNegativeButton("Cancel", null).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    int flag_insert = 1;
    String filename;

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

    ProgressDialog progressDialog;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image1:
                if (flag_insert != 0) {
                    filename = createfilename(0);
                    if (filename.length() > 0) {
                        tempimages.put(0, filename);
                        callcameraIntent(filename, 0);
                    }
                } else

                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly capture vehicle plate Number ", GlobalVar.AlertType.Warning);
                break;
            case R.id.image2:
                if (flag_insert != 0) {
                    filename = createfilename(1);
                    if (filename.length() > 0) {
                        tempimages.put(1, filename);
                        callcameraIntent(filename, 1);
                    }
                } else

                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly capture vehicle plate Number ", GlobalVar.AlertType.Warning);
                break;
            case R.id.image3:
                if (flag_insert != 0) {
                    filename = createfilename(2);
                    if (filename.length() > 0) {
                        tempimages.put(2, filename);
                        callcameraIntent(filename, 2);
                    }
                } else

                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly capture vehicle plate Number ", GlobalVar.AlertType.Warning);
                break;
            case R.id.image4:
                if (flag_insert != 0) {
                    filename = createfilename(3);
                    if (filename.length() > 0) {
                        tempimages.put(3, filename);
                        callcameraIntent(filename, 3);
                    }
                } else

                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly capture vehicle plate Number ", GlobalVar.AlertType.Warning);
                break;
        }
    }

    private void StartAsyncTaskInParallel(Image1 asynthread) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asynthread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            asynthread.execute();

    }

    private void StartAsyncTaskInParallel(Image2 asynthread) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asynthread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            asynthread.execute();

    }

    private void StartAsyncTaskInParallel(Image3 asynthread) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asynthread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            asynthread.execute();

    }

    private void StartAsyncTaskInParallel(Image4 asynthread) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asynthread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            asynthread.execute();

    }


    private String createfilename(int position) {
        Long timpstamp = System.currentTimeMillis() / 1000;
        String filename = "";

        String id = String.valueOf(GlobalVar.GV().EmployID);

        if (id.length() > 0) {
            filename = id + "_" + timpstamp.toString() + "_" + imagenames[position];
        }
        return filename;
    }

    protected void callcameraIntent(String imagename, int camerareqid) {

        if (ContextCompat.checkSelfPermission(Incident.this,
                android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(Incident.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            Intent cameraIntent = new Intent(
                    android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            File newfile = new File(GlobalVar.naqelvehicleimagepath);
            File imgfile = new File(GlobalVar.naqelvehicleimagepath + "/" + imagename);
            if (!newfile.exists())
                newfile.mkdirs();

            Uri outputFileUri = null;
            if (Build.VERSION.SDK_INT > 23)
                outputFileUri = FileProvider.getUriForFile(Incident.this, getPackageName() + ".fileprovider",
                        imgfile);
            else
                outputFileUri = Uri.fromFile(imgfile);

            // Uri outputFileUri = Uri.fromFile(imgfile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(cameraIntent, camerareqid);

        } else {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.ErrorWhileSaving),
                    GlobalVar.AlertType.Error);
            try {
                Intent intent = new Intent(
                        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(),
                        null);
                intent.setData(uri);
                startActivity(intent);
            } catch (Exception e) {

            }
        }
    }

    HashMap<Integer, String> sendimages;
    HashMap<Integer, String> tempimages;
    int flag_custid = 1;

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case 0:

                if (requestCode == 0
                        && resultCode == Activity.RESULT_OK) {

                    File image = new File(GlobalVar.naqelvehicleimagepath + "/"
                            + tempimages.get(0));

                    int file_size = Integer
                            .parseInt(String.valueOf(image.length() / 1024));
                    if (file_size > 0) {
                        boolean ci = true;
                        while (ci) {
                            long kb = image.length() / 1024;
                            if (kb > 300)
                                compressimage(image, 0);
                            else
                                ci = false;
                        }
                        image1.setImageBitmap(BitmapFactory.decodeFile(image
                                .getAbsolutePath()));

                        if (sendimages.containsKey(0)) {
                            deletefile(0);
                            sendimages.remove(0);
                            sendimages.put(0, tempimages.get(0));
                        } else
                            sendimages.put(0, tempimages.get(0));
                        isFlag_image1 = false;
                        isFlag_image1_Compl = false;
                        flag_custid = 0;

                    } else
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.prp),
                                GlobalVar.AlertType.Error);

                }
                break;
            case 1:

                if (requestCode == 1
                        && resultCode == Activity.RESULT_OK) {

                    File image = new File(GlobalVar.naqelvehicleimagepath + "/"
                            + tempimages.get(1));

                    int file_size = Integer
                            .parseInt(String.valueOf(image.length() / 1024));
                    if (file_size > 0) {
                        boolean ci = true;
                        while (ci) {
                            long kb = image.length() / 1024;
                            if (kb > 300)
                                compressimage(image, 0);
                            else
                                ci = false;
                        }
                        image2.setImageBitmap(BitmapFactory.decodeFile(image
                                .getAbsolutePath()));

                        if (sendimages.containsKey(1)) {
                            deletefile(1);
                            sendimages.remove(1);
                            sendimages.put(1, tempimages.get(1));
                        } else
                            sendimages.put(1, tempimages.get(1));
                        flag_custid = 0;
                        isFlag_image2 = false;
                        isFlag_image2_Compl = false;

                    } else
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.prp),
                                GlobalVar.AlertType.Error);

                }
                break;

            case 2:

                if (requestCode == 2
                        && resultCode == Activity.RESULT_OK) {

                    File image = new File(GlobalVar.naqelvehicleimagepath + "/"
                            + tempimages.get(2));

                    int file_size = Integer
                            .parseInt(String.valueOf(image.length() / 1024));
                    if (file_size > 0) {
                        boolean ci = true;
                        while (ci) {
                            long kb = image.length() / 1024;
                            if (kb > 300)
                                compressimage(image, 0);
                            else
                                ci = false;
                        }
                        image3.setImageBitmap(BitmapFactory.decodeFile(image
                                .getAbsolutePath()));

                        if (sendimages.containsKey(2)) {
                            deletefile(2);
                            sendimages.remove(2);
                            sendimages.put(2, tempimages.get(2));
                        } else
                            sendimages.put(2, tempimages.get(2));
                        flag_custid = 0;
                        isFlag_image3 = false;
                        isFlag_image3_Compl = false;
                    } else
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.prp),
                                GlobalVar.AlertType.Error);

                }
                break;
            case 3:

                if (requestCode == 3
                        && resultCode == Activity.RESULT_OK) {

                    File image = new File(GlobalVar.naqelvehicleimagepath + "/"
                            + tempimages.get(3));

                    int file_size = Integer
                            .parseInt(String.valueOf(image.length() / 1024));
                    if (file_size > 0) {
                        boolean ci = true;
                        while (ci) {
                            long kb = image.length() / 1024;
                            if (kb > 300)
                                compressimage(image, 0);
                            else
                                ci = false;
                        }
                        image4.setImageBitmap(BitmapFactory.decodeFile(image
                                .getAbsolutePath()));

                        if (sendimages.containsKey(3)) {
                            deletefile(3);
                            sendimages.remove(3);
                            sendimages.put(3, tempimages.get(3));
                        } else
                            sendimages.put(3, tempimages.get(3));
                        flag_custid = 0;
                        isFlag_image4 = false;
                        isFlag_image4_Compl = false;

                    } else
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.prp),
                                GlobalVar.AlertType.Error);

                }
                break;

            case 99: {
                if (resultCode == Activity.RESULT_OK) {

                    vehicleno.setText(data.getStringExtra("name"));
                    //truckID = data.getIntExtra("truckid", 0);
                }
                break;
            }


        }

    }

    private void deletefile(int position) {
        try {
            File deletefile = new File(GlobalVar.naqelvehicleimagepath + "/"
                    + sendimages.get(position));
            deletefile.delete();
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void compressimage(File imageDir, int size) {
        Bitmap bm = null;
        try {
            Uri outputFileUri = Uri.fromFile(imageDir);
            BitmapFactory.Options options = new BitmapFactory.Options();
            // options.inJustDecodeBounds = true;
            if (size == 0)
                options.inSampleSize = 2;
            else
                options.inSampleSize = 2;

            // bm = Media.getBitmap(mContext.getContentResolver(), imageLoc);
            bm = BitmapFactory.decodeStream(Incident.this.getContentResolver()
                    .openInputStream(outputFileUri), null, options);
            FileOutputStream out = new FileOutputStream(imageDir);
            bm.compress(Bitmap.CompressFormat.JPEG, 60, out);

            bm.recycle();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class BringIncident extends AsyncTask<String, Integer, String> {
        StringBuffer buffer;

        @Override
        protected void onPreExecute() {

            if (progressDialog == null)
                progressDialog = ProgressDialog.show(Incident.this,
                        "Please wait.", "Bringing Incident Data.", true);
            super.onPreExecute();

        }

        protected String doInBackground(String... params) {

            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {

                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "BringIncidentReasons");
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

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (!jsonObject.getBoolean("HasError")) {
                        fetchData(jsonObject);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                System.out.println(result);
            } else {

                LoadDivisionError();
            }
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }

        }
    }


    private class InsertIncident extends AsyncTask<String, Integer, String> {
        StringBuffer buffer;

        @Override
        protected void onPreExecute() {

            if (progressDialog == null)
                progressDialog = ProgressDialog.show(Incident.this,
                        "Please wait.", "your Incident Request is being process.", true);
            super.onPreExecute();

        }

        protected String doInBackground(String... params) {

            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {

                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "InsertIncident");
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

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (!jsonObject.getBoolean("HasError")) {

                        GlobalVar.hideKeyboardFrom(getApplicationContext(), getWindow().getDecorView().getRootView());
//                        GlobalVar.ShowDialog(Incident.this, "Info", "Your Request Sucessfully Inserted", true);
                        SucessfullyInsert();
                    } else {
                        GlobalVar.hideKeyboardFrom(getApplicationContext(), getWindow().getDecorView().getRootView());
                        GlobalVar.ShowDialog(Incident.this, "Error", jsonObject.getString("ErrorMessage"), true);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                System.out.println(result);
            } else {

                GlobalVar.ShowDialog(Incident.this, "Error", "Your Request Not Insert, please try again later", true);
            }
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }

        }
    }

    private class Emergency extends AsyncTask<String, Integer, String> {
        StringBuffer buffer;

        @Override
        protected void onPreExecute() {

            if (progressDialog == null)
                progressDialog = ProgressDialog.show(Incident.this,
                        "Please wait.", "Send Emergency Request", true);
            super.onPreExecute();

        }

        protected String doInBackground(String... params) {

            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {

                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "Emergency");
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

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (!jsonObject.getBoolean("HasError")) {

                        GlobalVar.hideKeyboardFrom(getApplicationContext(), getWindow().getDecorView().getRootView());
                        GlobalVar.ShowDialog(Incident.this, "Info", "Your Request Sucessfully Inserted", true);
//                        SucessfullyInsert();
                    } else {
                        GlobalVar.hideKeyboardFrom(getApplicationContext(), getWindow().getDecorView().getRootView());
                        GlobalVar.ShowDialog(Incident.this, "Error", "Your Request Not Insert, please try again later", true);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                System.out.println(result);
            } else {

                GlobalVar.ShowDialog(Incident.this, "Error", "Your Request Not Insert, please try again later", true);
            }
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }

        }
    }

    private class Close extends AsyncTask<String, Integer, String> {
        StringBuffer buffer;

        @Override
        protected void onPreExecute() {

            if (progressDialog == null)
                progressDialog = ProgressDialog.show(Incident.this,
                        "Please wait.", "your Request is being process", true);
            super.onPreExecute();

        }

        protected String doInBackground(String... params) {

            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {

                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "CloseIncident");
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

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (!jsonObject.getBoolean("HasError")) {

                        GlobalVar.hideKeyboardFrom(getApplicationContext(), getWindow().getDecorView().getRootView());
                        GlobalVar.ShowDialog(Incident.this, "Info", "Your Last Request Closed Sucessfully", true);
//                        SucessfullyInsert();
                    } else {
                        GlobalVar.hideKeyboardFrom(getApplicationContext(), getWindow().getDecorView().getRootView());
                        GlobalVar.ShowDialog(Incident.this, "Error", "Your Request Not Closed, please try again later", true);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                System.out.println(result);
            } else {

                GlobalVar.ShowDialog(Incident.this, "Error", "Your Request Not Closed, please try again later", true);
            }
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }

        }
    }

    ArrayList<HashMap<String, String>> sreasondata = new ArrayList<>();

    private void fetchData(JSONObject jsonObject) {

        CheckPointTypeNameList.clear();
        CheckPointTypeList.clear();
        CheckPointTypeDetailNameList.clear();
        CheckPointTypeDetailList.clear();
        Remarks.clear();

        try {

            JSONArray sreason = jsonObject.getJSONArray("SReason");
            JSONArray reason = jsonObject.getJSONArray("Reason");
            JSONArray action = jsonObject.getJSONArray("Action");
            JSONArray remarks = jsonObject.getJSONArray("Remarks");

            if (reason.length() > 0) {
                for (int i = 0; i < reason.length(); i++) {
                    JSONObject jsonObject1 = reason.getJSONObject(i);
                    HashMap<String, String> temp = new HashMap<>();
                    temp.put("ID", jsonObject1.getString("ID"));
                    temp.put("Name", jsonObject1.getString("Name"));

                    CheckPointTypeNameList.add(jsonObject1.getString("Name"));
                    CheckPointTypeList.add(jsonObject1.getInt("ID"));

                }

            }

            if (sreason.length() > 0) {
                for (int i = 0; i < sreason.length(); i++) {
                    JSONObject jsonObject1 = sreason.getJSONObject(i);
                    HashMap<String, String> temp = new HashMap<>();
                    temp.put("ID", jsonObject1.getString("ID"));
                    temp.put("Name", jsonObject1.getString("Name"));
                    temp.put("ReasonID", jsonObject1.getString("IncidentReasonID"));

                    sreasondata.add(temp);
                }

            }


            if (action.length() > 0) {
                for (int i = 0; i < action.length(); i++) {
                    JSONObject jsonObject1 = action.getJSONObject(i);
                    HashMap<String, String> temp = new HashMap<>();
                    temp.put("ID", jsonObject1.getString("ID"));
                    temp.put("Name", jsonObject1.getString("Name"));

                    CheckPointTypeDDetailNameList.add(jsonObject1.getString("Name"));
                    CheckPointTypeDDetailList.add(jsonObject1.getInt("ID"));

                }

            }

            if (remarks.length() > 0) {
                for (int i = 0; i < remarks.length(); i++) {
                    JSONObject jsonObject1 = remarks.getJSONObject(i);
                    HashMap<String, String> temp = new HashMap<>();
                    temp.put("ID", jsonObject1.getString("ID"));
                    temp.put("Name", jsonObject1.getString("Name"));

                    Remarks.add(jsonObject1.getString("Name"));


                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void LoadDivisionError() {
        AlertDialog alertDialog = new AlertDialog.Builder(Incident.this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Info.");
        alertDialog.setMessage("Kindly Check your Internet Connection,please try again");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        new BringIncident().execute("");
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Close",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void SucessfullyInsert() {
        AlertDialog alertDialog = new AlertDialog.Builder(Incident.this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Info.");
        alertDialog.setMessage("your request sucessfully inserted ?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        txtCheckPointType.setText("");
                        txtCheckPointTypeDetail.setText("");
                        txtCheckPointTypeDDetail.setText("");
                        remarks.setText("");
                        pendingwaybill.setText("");
                        CheckPointTypeID = 0;
                        CheckPointTypeDetailID = 0;
                        CheckPointTypeDDetailID = 0;
                        isFlag_image1 = true;
                        isFlag_image2 = true;
                        isFlag_image3 = true;
                        isFlag_image4 = true;
                        sendimages.clear();
                        tempimages.clear();

                        image1.setBackgroundResource(R.drawable.capture);

                        image2.setBackgroundResource(R.drawable.capture);

                        image3.setBackgroundResource(R.drawable.capture);

                        image4.setBackgroundResource(R.drawable.capture);

                        dialog.dismiss();
                        finish();
                    }
                });
//        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "no",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        finish();
//                    }
//                });
        alertDialog.show();
    }


    boolean isFlag_image1 = true, isFlag_image2 = true, isFlag_image3 = true, isFlag_image4 = true;
    boolean isFlag_image1_Compl = true, isFlag_image2_Compl = true, isFlag_image3_Compl = true, isFlag_image4_Compl = true;


    private void checkuploadimages() {

        if (isFlag_image1 && isFlag_image2 && isFlag_image3 && isFlag_image4) {
            progressDialog.dismiss();
            progressDialog = null;
            insertDatatoServer();
            return;
        }

        if (isFlag_image1_Compl && isFlag_image2_Compl && isFlag_image3_Compl && isFlag_image4_Compl) {

            SaveData();
        }


    }

    /*private class Image1 extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            if (progressDialog == null)
                progressDialog = ProgressDialog.show(Incident.this, "Please wait.", "Uploading Images to Server"
                        , true);

            super.onPreExecute();
        }

        @SuppressWarnings("deprecation")
        protected String doInBackground(String... params) {

            boolean delete = false;
            int position = 0;
            try {

                delete = CloudStorage.uploadFile("naqeldocuments", GlobalVar.naqelvehicleimagepath + "/" + sendimages.get(position), getApplicationContext());

            } catch (Exception e2) {
                e2.printStackTrace();
                return "FALSE";

            }

            File sourceFile = new File(GlobalVar.naqelvehicleimagepath + "/" + sendimages.get(position));

            if (!sourceFile.exists()) {
                return "FALSE";

            } else if (sourceFile.exists() && delete) {
                deletefile(position);
            }


            return "TRUE";

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals("TRUE"))
                isFlag_image1 = true;
            else
                isFlag_image1 = false;

            isFlag_image1_Compl = true;
            checkuploadimages();

        }
    }*/
/*
    private class Image2 extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @SuppressWarnings("deprecation")
        protected String doInBackground(String... params) {

            boolean delete = false;
            int position = 1;
            try {

                delete = CloudStorage.uploadFile("naqeldocuments", GlobalVar.naqelvehicleimagepath + "/" + sendimages.get(position), getApplicationContext());

            } catch (Exception e2) {
                e2.printStackTrace();
                return "FALSE";

            }

            File sourceFile = new File(GlobalVar.naqelvehicleimagepath + "/" + sendimages.get(position));

            if (!sourceFile.exists()) {
                return "FALSE";

            } else if (sourceFile.exists() && delete) {
                deletefile(position);
            }


            return "TRUE";

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals("TRUE"))
                isFlag_image2 = true;
            else
                isFlag_image2 = false;

            isFlag_image2_Compl = true;

            checkuploadimages();
        }
    }
*/

    private class Image2 extends AsyncTask<String, Integer, String> {
        StringBuffer buffer;
        int position = 0;

        @Override
        protected void onPreExecute() {


            super.onPreExecute();

        }

        protected String doInBackground(String... params) {

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("ImageName", sendimages.get(1));
                jsonObject.put("FileName", convertimage(1));
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

                try {
                    boolean delete = false;

                    if (result.contains("Created Successfully")) {

                        delete = true;
                    }

                    File sourceFile = new File(GlobalVar.naqelvehicleimagepath + "/" + sendimages.get(1));

                    if (sourceFile.exists() && delete) {
                        deletefile(position);
                    }

                    if (result.contains("Created Successfully"))
                        isFlag_image2 = true;
                    else
                        isFlag_image2 = false;

                    isFlag_image1_Compl = true;
                    checkuploadimages();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println(result);
            } else {

                LoadDivisionError();
            }

        }
    }


   /* private class Image3 extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @SuppressWarnings("deprecation")
        protected String doInBackground(String... params) {

            boolean delete = false;
            int position = 2;
            try {

                delete = CloudStorage.uploadFile("naqeldocuments", GlobalVar.naqelvehicleimagepath + "/" + sendimages.get(position), getApplicationContext());

            } catch (Exception e2) {
                e2.printStackTrace();
                return "FALSE";

            }

            File sourceFile = new File(GlobalVar.naqelvehicleimagepath + "/" + sendimages.get(position));

            if (!sourceFile.exists()) {
                return "FALSE";

            } else if (sourceFile.exists() && delete) {
                deletefile(position);
            }


            return "TRUE";

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals("TRUE"))
                isFlag_image3 = true;
            else
                isFlag_image3 = false;

            isFlag_image3_Compl = true;
            checkuploadimages();
        }
    }*/

    private class Image3 extends AsyncTask<String, Integer, String> {
        StringBuffer buffer;
        int position = 0;

        @Override
        protected void onPreExecute() {


            super.onPreExecute();

        }

        protected String doInBackground(String... params) {

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("ImageName", sendimages.get(2));
                jsonObject.put("FileName", convertimage(2));
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

                try {
                    boolean delete = false;

                    if (result.contains("Created Successfully")) {

                        delete = true;
                    }

                    File sourceFile = new File(GlobalVar.naqelvehicleimagepath + "/" + sendimages.get(2));

                    if (sourceFile.exists() && delete) {
                        deletefile(position);
                    }

                    if (result.contains("Created Successfully"))
                        isFlag_image3 = true;
                    else
                        isFlag_image3 = false;

                    isFlag_image1_Compl = true;
                    checkuploadimages();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println(result);
            } else {

                LoadDivisionError();
            }


        }
    }

   /* private class Image4 extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @SuppressWarnings("deprecation")
        protected String doInBackground(String... params) {

            boolean delete = false;
            int position = 3;
            try {

                delete = CloudStorage.uploadFile("naqeldocuments", GlobalVar.naqelvehicleimagepath + "/" + sendimages.get(position), getApplicationContext());

            } catch (Exception e2) {
                e2.printStackTrace();
                return "FALSE";

            }

            File sourceFile = new File(GlobalVar.naqelvehicleimagepath + "/" + sendimages.get(position));

            if (!sourceFile.exists()) {
                return "FALSE";

            } else if (sourceFile.exists() && delete) {
                deletefile(position);
            }


            return "TRUE";

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals("TRUE"))
                isFlag_image4 = true;
            else
                isFlag_image4 = false;

            isFlag_image4_Compl = true;
            checkuploadimages();
        }
    }*/

    private class Image4 extends AsyncTask<String, Integer, String> {
        StringBuffer buffer;
        int position = 0;

        @Override
        protected void onPreExecute() {


            super.onPreExecute();

        }

        protected String doInBackground(String... params) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("ImageName", sendimages.get(3));
                jsonObject.put("FileName", convertimage(3));
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

                try {
                    boolean delete = false;

                    if (result.contains("Created Successfully")) {

                        delete = true;
                    }

                    File sourceFile = new File(GlobalVar.naqelvehicleimagepath + "/" + sendimages.get(3));

                    if (sourceFile.exists() && delete) {
                        deletefile(position);
                    }

                    if (result.contains("Created Successfully"))
                        isFlag_image4 = true;
                    else
                        isFlag_image4 = false;

                    isFlag_image1_Compl = true;
                    checkuploadimages();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println(result);
            } else {

                LoadDivisionError();
            }
        }
    }

    /* private class Image1 extends AsyncTask<String, Integer, String> {


         @Override
         protected void onPreExecute() {
             if (progressDialog == null)
                 progressDialog = ProgressDialog.show(Incident.this, "Please wait.", "Uploading Images to Server"
                         , true);

             super.onPreExecute();
         }

         @SuppressWarnings("deprecation")
         protected String doInBackground(String... params) {

             boolean delete = false;
             int position = 0;
             try {

                 //delete = CloudStorage.uploadFile("naqeldocuments", GlobalVar.naqelvehicleimagepath + "/" + sendimages.get(position), getApplicationContext());


             } catch (Exception e2) {
                 e2.printStackTrace();
                 return "FALSE";

             }

             File sourceFile = new File(GlobalVar.naqelvehicleimagepath + "/" + sendimages.get(position));

             if (!sourceFile.exists()) {
                 return "FALSE";

             } else if (sourceFile.exists() && delete) {
                 deletefile(position);
             }


             return "TRUE";

         }

         @Override
         protected void onPostExecute(String result) {
             super.onPostExecute(result);
             if (result.equals("TRUE"))
                 isFlag_image1 = true;
             else
                 isFlag_image1 = false;

             isFlag_image1_Compl = true;
             checkuploadimages();

         }
     }*/
    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;

    }


   /* private void SendImage(final String image) {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalVar.GV().NaqelPointerAPILink + "upload",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("uploade", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if()
                            isFlag_image1 = true;

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Incident.this, "No internet connection", Toast.LENGTH_LONG).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new Hashtable<String, String>();
                params.put("ImageName", "welcome.png");
                params.put("FileName", image);
                return params;
            }
        };
        {
            int socketTimeout = 30000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }
    }*/

    private class Image1 extends AsyncTask<String, Integer, String> {
        StringBuffer buffer;
        int position = 0;

        @Override
        protected void onPreExecute() {

            if (progressDialog == null)
                progressDialog = ProgressDialog.show(Incident.this,
                        "Please wait.", "Uploading Images to Server.", true);
            super.onPreExecute();

        }

        protected String doInBackground(String... params) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("ImageName", sendimages.get(0));
                jsonObject.put("FileName", convertimage(0));
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

                try {
                    boolean delete = false;

                    if (result.contains("Created Successfully")) {

                        delete = true;
                    }

                    File sourceFile = new File(GlobalVar.naqelvehicleimagepath + "/" + sendimages.get(0));

                    if (sourceFile.exists() && delete) {
                        deletefile(position);
                    }

                    if (result.contains("Created Successfully"))
                        isFlag_image1 = true;
                    else
                        isFlag_image1 = false;

                    isFlag_image1_Compl = true;
                    checkuploadimages();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println(result);
            }


        }
    }

    private String convertimage(int position) {
        String image = "";
        String imagename = GlobalVar.naqelvehicleimagepath + "/" + sendimages.get(position);

        File imgfile = new File(imagename);


        Uri outputFileUri = null;
        if (Build.VERSION.SDK_INT > 23)
            outputFileUri = FileProvider.getUriForFile(Incident.this, getPackageName() + ".fileprovider",
                    imgfile);
        else
            outputFileUri = Uri.fromFile(imgfile);

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), outputFileUri);
            Bitmap lastBitmap = null;
            lastBitmap = bitmap;
            //encoding image to string
            image = getStringImage(lastBitmap);
            Log.d("image", image);
            //passing the image to volley


        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    ArrayList<FindVehilceObject> vehicles;

    private void RedirectVechicleClass() {
        Intent intent = new Intent(this, FindVehicle.class);
        intent.putExtra("Vehicles", vehicles);
        startActivityForResult(intent, 99);
    }

    private void ReadFromLocal() {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        Cursor result = dbConnections.Fill("select * from Truck", getApplicationContext());

        result.moveToFirst();
        vehicles = new ArrayList<FindVehilceObject>();
        vehicles.clear();
        setDefault();
        try {
            if (result.getCount() > 0) {
                result.moveToFirst();
                do {
                    FindVehilceObject fvo = new FindVehilceObject();
                    fvo.ID = result.getInt(result.getColumnIndex("TruckID"));
                    fvo.Name = result.getString(result.getColumnIndex("Name"));
                    vehicles.add(fvo);
                } while (result.moveToNext());
            } else
                GlobalVar.GV().Logout(getApplicationContext());

            result.close();
            dbConnections.close();
            // RedirectVechicleClass();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDefault() {
        FindVehilceObject fvo = new FindVehilceObject();
        fvo.ID = 0;
        fvo.Name = "At Yard";
        vehicles.add(fvo);
    }
}