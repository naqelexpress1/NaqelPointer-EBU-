package com.naqelexpress.naqelpointer.Activity.Login;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.naqelexpress.naqelpointer.Activity.MainPage.MainPageActivity;
import com.naqelexpress.naqelpointer.BuildConfig;
import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.Classes.NewBarCodeScanner;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.CheckPointType;
import com.naqelexpress.naqelpointer.DB.DBObjects.CheckPointTypeDDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.CheckPointTypeDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.Contacts;
import com.naqelexpress.naqelpointer.DB.DBObjects.DeliveryStatus;
import com.naqelexpress.naqelpointer.DB.DBObjects.FacilityStatus;
import com.naqelexpress.naqelpointer.DB.DBObjects.FindVehilceObject;
import com.naqelexpress.naqelpointer.DB.DBObjects.NoNeedVolumeReason;
import com.naqelexpress.naqelpointer.DB.DBObjects.Station;
import com.naqelexpress.naqelpointer.DB.DBObjects.UserME;
import com.naqelexpress.naqelpointer.DB.DBObjects.UserMeLogin;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.JSON.ProjectAsyncTask;
import com.naqelexpress.naqelpointer.JSON.Request.GetDeliveryStatusRequest;
import com.naqelexpress.naqelpointer.JSON.Request.GetUserMEDataRequest;
import com.naqelexpress.naqelpointer.JSON.Results.CheckNewVersionResult;
import com.naqelexpress.naqelpointer.JSON.Results.GetUserMEDataResult;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.Receiver.LocationupdateInterval;
import com.naqelexpress.naqelpointer.service.LocationService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {


    TextView lbVersion;
    Button btnLogin, btnForgotPassword, btnScan;
    EditText txtEmployID, txtPassword;

    EditText truck, odometer;
    ArrayList<FindVehilceObject> vehicles;
    int truckID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (GlobalVar.GV().LoginVariation)
            setContentView(R.layout.loginebu);
        else
            setContentView(R.layout.login);


        boolean asd = GlobalVar.GV().IsAllowtoScan("Upto : 2019-12-11 16.30".replace("Upto : ", ""));

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        btnLogin = (Button) findViewById(R.id.btnLogin);

        btnForgotPassword = (Button) findViewById(R.id.btnForgotPassword);
        btnScan = (Button) findViewById(R.id.btnScan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanEmployBarCode();
            }
        });

        lbVersion = (TextView) findViewById(R.id.lbVersion);
        String Version = getString(R.string.lbVersion) + GlobalVar.GV().AppVersion;
        lbVersion.setText(Version);


        txtEmployID = (EditText) findViewById(R.id.txtEmployID);
        txtPassword = (EditText) findViewById(R.id.txtPassword);

        GlobalVar.ResetTriedCount();


        txtEmployID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        if (GlobalVar.GV().LoginVariation) {
            vehicles = new ArrayList<FindVehilceObject>();


            truck = (EditText) findViewById(R.id.truckid);
            odometer = (EditText) findViewById(R.id.odometer);
            truck.setKeyListener(null);
            truck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (vehicles.size() > 0)
                        RedirectVechicleClass();
                    else
                        ShowAlertMessage("No Truck Data , Kindly contact concern person", 1);

                }
            });

            DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
            Cursor result = dbConnections.Fill("select * from Truck", getApplicationContext());
            if (result.getCount() == 0) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    new BringTruckData().execute(jsonObject.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else
                ReadFromLocal(result, dbConnections);

            dbConnections.close();
            result.close();
        }

    }

    private void ReadFromLocal(Cursor result, DBConnections dbConnections) {

        result.moveToFirst();
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
            }

            result.close();
            dbConnections.close();
            RedirectVechicleClass();


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void HideKeyBoard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }


    public void OpenForgotPasswordActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
        startActivity(intent);
    }

    public void Login(View view) throws ParseException {

        GlobalVar.GV().UserPassword = txtPassword.getText().toString();
        if (GlobalVar.GV().ThereIsMandtoryVersion) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "There is a new version, you have to install it first. Contact IT department if you need any support.", GlobalVar.AlertType.Warning);
            return;
        }

        if (txtEmployID.getText().toString().equals("")) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to enter the Employee ID", GlobalVar.AlertType.Warning);
            return;
        }

        if (txtPassword.getText().toString().equals("")) {

            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to enter your password", GlobalVar.AlertType.Warning);
            return;
        }
        if (GlobalVar.GV().LoginVariation && truck.getText().toString().equals("")) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to select Fleet Number", GlobalVar.AlertType.Warning);
            return;
        }
        if (GlobalVar.GV().LoginVariation && odometer.getText().toString().equals("")) {
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(),
                    "You have to enter Odometer", GlobalVar.AlertType.Warning);
            return;
        }

        LoginIntoOpenMainPage();
    }

    private void LoginIntoOpenMainPage() {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        String empID = txtEmployID.getText().toString();
        String Password = txtPassword.getText().toString();

        Cursor result = dbConnections.Fill("select * from UserME where StatusID <> 3 and EmployID = " + empID + " and Password ='" + Password + "'"
                , getApplicationContext());
        if (result != null && result.getCount() > 0) {

            result.moveToFirst();
            GlobalVar.GV().UserID = Integer.valueOf(result.getString(result.getColumnIndex("ID")));
            GlobalVar.GV().EmployID = Integer.valueOf(result.getString(result.getColumnIndex("EmployID")));
            GlobalVar.GV().StationID = Integer.valueOf(result.getString(result.getColumnIndex("StationID")));
            int uid = result.getInt(result.getColumnIndex("UserTypeID"));
            usertype = uid;

            dbConnections.UpdateLastLogin(GlobalVar.GV().EmployID, getApplicationContext(), uid);
            dbConnections.UpdateTruckID(GlobalVar.GV().EmployID, getApplicationContext(), truckID);


            try {
                GlobalVar.GV().EmployMobileNo = result.getString(result.getColumnIndex("MobileNo"));
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {

                if (GlobalVar.GV().IsEnglish()) {
                    GlobalVar.GV().EmployName = result.getString(result.getColumnIndex("EmployName"));
                    GlobalVar.GV().EmployStation = result.getString(result.getColumnIndex("StationName"));
                } else {
                    GlobalVar.GV().EmployName = result.getString(result.getColumnIndex("EmployFName"));
                    GlobalVar.GV().EmployStation = result.getString(result.getColumnIndex("StationFName"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            LoginToMainPage(1);
        } else {
            GetUserMEData(Integer.parseInt(txtEmployID.getText().toString()), txtPassword.getText().toString());
        }
    }

    private void LoginToMainPage(int load) {

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        UserMeLogin userMeLogin = new UserMeLogin(GlobalVar.GV().EmployID, 1);
        dbConnections.InsertUserMeLogin(userMeLogin, getApplicationContext());
        dbConnections.close();

        OpenMainPage(load);
    }

    int getMaster;

    private void OpenMainPage(int getMaster) {

        this.getMaster = getMaster;
        if (GlobalVar.GV().ThereIsMandtoryVersion)
            GlobalVar.GV().ShowDialog(LoginActivity.this, "New Version", "There is a new version, Please update the system, or cordiante with IT department for updating your system.", true);
        else {

            ActivityCompat.requestPermissions(
                    LoginActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG}, //Manifest.permission.CALL_PHONE,
                    2
            );
        }
    }


    class Optimization {
        public String EmployID;
        public String DeviceId;
    }

    private void SetDeviceId() {
        String DeviceId = "XXXX";
        //To Do from FCM

        Optimization optimization = new Optimization();

        optimization.EmployID = String.valueOf(GlobalVar.GV().EmployID);
        optimization.DeviceId = DeviceId;

        String jsonData = JsonSerializerDeserializer.serialize(optimization, true);
        ProjectAsyncTask task = new ProjectAsyncTask("Optimize", "Post", jsonData, "http://35.188.10.142/NaqelRouteApi/api/");
        task.execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 1:

                boolean redirect = true;
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    for (int i = 0; i < permissions.length; i++) {
                        int result = ContextCompat.checkSelfPermission(this, permissions[i]);
                        if (result == PackageManager.PERMISSION_DENIED) {
                            redirect = false;
                            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                                try {
                                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                    startActivity(intent);
                                } catch (Exception e) {
                                    GlobalVar.ShowDialog(LoginActivity.this, "Permission necessary", "Kindly please contact our Admin", true);
                                }
                                break;
                            } else {
                                ActivityCompat.requestPermissions(
                                        LoginActivity.this,
                                        new String[]{Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG, Manifest.permission.WRITE_CONTACTS
                                                , Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE
                                                , Manifest.permission.CAMERA, Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS,
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_NUMBERS},
                                        1
                                );
                                break;
                            }
                        }
                    }
                    if (redirect) {
                        Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                        intent.putExtra("getMaster", getMaster);
                        startActivity(intent);
                        finish();
                    }

                } else {
                    for (int i = 0; i < permissions.length; i++) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                            startActivity(intent);
                            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Our app need the those Permission,please kindly allow me", GlobalVar.AlertType.Error);
                            break;
                        }
                    }
                }
                break;

            case 2:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(
                            LoginActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            4
                    );

                } else {
                    if (ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_DENIED) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                            try {
                                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                startActivity(intent);
                            } catch (Exception e) {
                                GlobalVar.ShowDialog(LoginActivity.this, "Permission necessary", "Kindly please contact our Admin", true);
                            }
                        } else {
                            ActivityCompat.requestPermissions(
                                    LoginActivity.this,
                                    new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG},
                                    2
                            );
                        }
                    }
                }

                break;
            case 3:
                break;
            case 4:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (GlobalVar.locationEnabled(getApplicationContext())) {
                        if (!GlobalVar.isMyServiceRunning(LocationService.class, getApplicationContext())) {
                            startService(new Intent(getBaseContext(),
                                    LocationService.class));
                        }

                        if (!GlobalVar.isMyServiceRunning(com.naqelexpress.naqelpointer.Activity.GoogleApiFusedLocation.LocationService.class,
                                getApplicationContext())) {
                            startService(new Intent(getBaseContext(),
                                    com.naqelexpress.naqelpointer.Activity.GoogleApiFusedLocation.LocationService.class));
                        }

                        LocationupdateInterval.cancelAlarm(getApplicationContext());
                        LocationupdateInterval.setAlarm(true, getApplicationContext());

                        ActivityCompat.requestPermissions(
                                LoginActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                5
                        );

                    } else {
                        GlobalVar.enableLocationSettings(LoginActivity.this);
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_DENIED) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                            try {
                                Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                startActivity(i);
                            } catch (Exception e) {
                                GlobalVar.ShowDialog(LoginActivity.this, "Permission necessary", "Kindly please contact our Admin", true);
                            }
                        } else {
                            ActivityCompat.requestPermissions(
                                    LoginActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                    4
                            );
                        }
                    }

                }

                break;
            case 5:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    DBConnections dbConnections = new DBConnections(getApplicationContext(), null);


                    if (!dbConnections.isFacilityLoggedIn(getApplicationContext(), GlobalVar.GV().EmployID) && GetDivision()) {
                        Intent intent = new Intent(getApplicationContext(), FacilityLogin.class);
                        intent.putExtra("usertype", usertype);
                        intent.putExtra("getMaster", getMaster);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                        intent.putExtra("usertype", usertype);
                        intent.putExtra("getMaster", getMaster);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_DENIED) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                            try {
                                Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                startActivity(i);
                            } catch (Exception e) {
                                GlobalVar.ShowDialog(LoginActivity.this, "Storage Permission necessary", "Kindly please contact our Admin", true);
                            }

                        } else {
                            ActivityCompat.requestPermissions(
                                    LoginActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                    5
                            );
                        }
                    }

                }
                break;
            case 14:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new DownloadApk().execute("");

                } else {
                    if (ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_DENIED) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {

                            try {
                                Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                startActivity(i);
                            } catch (Exception e) {
                                GlobalVar.ShowDialog(LoginActivity.this, "Permission necessary", "Kindly please contact our Admin", true);
                            }
                            // finish();
                        } else {
                            ActivityCompat.requestPermissions(
                                    LoginActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                    14
                            );
                        }
                    }
                }
                break;
        }

    }

    String installaionfile = "";
    ProgressDialog mProgressDialog;
    String app_pkg_name = "com.naqelexpress.naqelpointer";
    int UNINSTALL_REQUEST_CODE = 123;

    private class DownloadApk extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;

            String DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/NaqelSignature/";
            File file = new File(DIRECTORY);
            if (!file.exists()) {
                file.mkdirs();
            }

            try {
                installaionfile = "naqelpointer.apk";
                if (GlobalVar.GV().IsTerminalApp)
                    installaionfile = "terminalapp.apk";
                else if (!GetDivision())
                    installaionfile = "naqelpointerEBU.apk";

                URL url = new URL(GlobalVar.GV().NaqelApk + installaionfile);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();


                output = new FileOutputStream(DIRECTORY + "/" + installaionfile);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                    return ignored.toString();
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            mProgressDialog = new ProgressDialog(LoginActivity.this);
            mProgressDialog.setMessage("File is downloading,kindly please wait ");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {

            mProgressDialog.dismiss();
            if (result != null)
                Toast.makeText(getApplicationContext(), "Download error: " + result, Toast.LENGTH_LONG).show();
            else {
                Toast.makeText(getApplicationContext(), "File downloaded", Toast.LENGTH_SHORT).show();

                try {

                    if (!isUnknownSourceEnable()) {
                        String DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/NaqelSignature/" + installaionfile;
                        File file = new File(DIRECTORY);
                        if (file.exists()) {

                            String DIRECTORY1 = Environment.getExternalStorageDirectory().getPath() + "/NaqelSignature";
                            File toInstall = new File(DIRECTORY1, installaionfile);


                            Uri contentUri = FileProvider.getUriForFile(
                                    getApplicationContext(),
                                    "com.naqelexpress.naqelpointer.fileprovider", toInstall);

                            Intent intent = new Intent(Intent.ACTION_VIEW, contentUri);
                            intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(intent);

                        }
                    } else {
                        try {
                            startActivity(new Intent(android.provider.Settings.ACTION_SECURITY_SETTINGS));
                        } catch (Exception e) {
                            GlobalVar.ShowDialog(LoginActivity.this, "Need Unknown Source Permission", "Kindly please Enable Unknown Source", true);
                        }
                    }
                } catch (Exception e) {

                }
            }
        }
    }

    private boolean isUnknownSourceEnable() {
        boolean unknownSource = false;
        try {
            if (Build.VERSION.SDK_INT < 17) {
                unknownSource = Settings.Secure.getInt(null, Settings.Secure.INSTALL_NON_MARKET_APPS, 0) == 1;
            } else {
                unknownSource = Settings.Global.getInt(null, Settings.Global.INSTALL_NON_MARKET_APPS, 0) == 1;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return unknownSource;
    }

    private boolean GetDivision() {
        String division = GlobalVar.GV().getDivisionID(getApplicationContext(), GlobalVar.GV().EmployID);

        if (division.equals("Express") || division.equals("IRS"))
            return false;
        else
            return true;

    }

    public void ScanEmployBarCode() {
        if (!GlobalVar.GV().checkPermission(this, GlobalVar.PermissionType.Camera))
            GlobalVar.GV().askPermission(this, GlobalVar.PermissionType.Camera);
        else {
            Intent intent = new Intent(getApplicationContext(), NewBarCodeScanner.class);
            startActivityForResult(intent, GlobalVar.GV().CAMERA_PERMISSION_REQUEST);
        }
    }

    private class CheckNewVersion extends AsyncTask<String, Void, String> {
        String result = "";
        StringBuffer buffer;

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "CheckNewVersion");
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
            if (finalJson != null) {
                CheckNewVersionResult checkNewVersionResult = new CheckNewVersionResult(finalJson);

                if (checkNewVersionResult.HasNewVersion)
                    GlobalVar.GV().ShowDialog(LoginActivity.this, "New Version", "There is a new version, Please update the system, or cordiante with IT department for updating your system.", true);

                if (checkNewVersionResult.IsMandatory)
                    GlobalVar.GV().ThereIsMandtoryVersion = true;

//                btnLogin.setVisibility(View.INVISIBLE);
//                btnScan.setVisibility(View.INVISIBLE);
//                txtEmployID.setVisibility(View.INVISIBLE);
//                txtPassword.setVisibility(View.INVISIBLE);
//                btnForgotPassword.setVisibility(View.INVISIBLE);
//            }
            } else
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.wentwrong), GlobalVar.AlertType.Error);
            super.onPostExecute(String.valueOf(finalJson));
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            txtEmployID.setText(savedInstanceState.getString("txtEmployID"));
            txtPassword.setText(savedInstanceState.getString("txtPassword"));
            //instance = (UserME) savedInstanceState.getSerializable("instance");
            if (truck != null) {
                truck.setText(savedInstanceState.getString("truck"));
                truckID = savedInstanceState.getInt("truckID");
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("txtEmployID", txtEmployID.getText().toString());
        outState.putString("txtPassword", txtPassword.getText().toString());
        //outState.putSerializable("instance", (Serializable) instance);

        if (truck != null) {
            outState.putString("truck", truck.getText().toString());
            outState.putInt("truckID", truckID);
        }

        super.onSaveInstanceState(outState);
    }


    public void GetUserMEData(int EmployID, String Password) {
        DBConnections dbConnections = new DBConnections(getApplicationContext() , null);

        GlobalVar.hideKeyboardFrom(getApplicationContext(), getWindow().getDecorView().getRootView());
        GetUserMEDataRequest getUserMEDataRequest = new GetUserMEDataRequest();
        getUserMEDataRequest.EmployID = EmployID;
        getUserMEDataRequest.Passowrd = Password;
        getUserMEDataRequest.AppTypeID = GlobalVar.VersionCode(getApplicationContext());
        getUserMEDataRequest.IsLastLoggingOutError = dbConnections.UpdateLoginStatusErrorCount(EmployID , getApplicationContext()) > 0 ? true : false;


        if (GlobalVar.GV().LoginVariation)
            getUserMEDataRequest.Odometer = Integer.parseInt(odometer.getText().toString());


        try {
            FirebaseApp.initializeApp(this);
            String token = FirebaseInstanceId.getInstance().getToken();
            getUserMEDataRequest.DeviceToken = token;
        } catch (Exception e) {
            getUserMEDataRequest.DeviceToken = "";
        }

//        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
//        dbConnections.UpdateLastLogin()

        String jsonData = JsonSerializerDeserializer.serialize(getUserMEDataRequest, true);
        //System.out.println(jsonData);

        new GetUserMEDataFromServer().execute(jsonData);
    }

    int usertype = 0;
    UserME instance;


    private class GetUserMEDataFromServer extends AsyncTask<String, Void, String> {
        //private ProgressDialog progressDialog;
        String result = "";
        StringBuffer buffer;
        String DomainURL = "";
        String isInternetAvailable = "";

        @Override
        protected void onPreExecute() {

            progressDialog = ProgressDialog.show(LoginActivity.this, "Please wait.",
                    "Bringing User Details.", true);

            DomainURL = GlobalVar.getTestAPIURL(getApplicationContext());

        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;


            try {

                URL url = new URL(DomainURL + "GetUserMEData_v2");
                httpURLConnection = (HttpURLConnection) url.openConnection();

                try {
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    httpURLConnection.setReadTimeout(GlobalVar.GV().loadbalance_ConRedtimeout);
                    httpURLConnection.setConnectTimeout(GlobalVar.GV().loadbalance_ConRedtimeout);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.connect();
                } catch (java.net.NoRouteToHostException se) {
                    System.out.println(se);
                }
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
                //buffer.append(ignored.toString());
                isInternetAvailable = ignored.toString();
                // System.out.println(ignored);
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

            DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
            if (finalJson != null) {

                GlobalVar.ResetTriedCount();
                GetUserMEDataResult getUserMEDataResult = new GetUserMEDataResult(finalJson);

                if (getUserMEDataResult.HasError) {
                    if (getUserMEDataResult.PwdNeedUpdate) {
                        updatePasswordDialog(getUserMEDataResult.ErrorMessage);
                    } else {
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Data Not Sync Because :" + getUserMEDataResult.ErrorMessage, GlobalVar.AlertType.Error);
                    }
                } else {
                    instance = new UserME();
                    instance.ID = getUserMEDataResult.ID;
                    instance.EmployID = getUserMEDataResult.EmployID;
                    instance.Password = txtPassword.getText().toString();//getUserMEDataResult.Password;
                    instance.RoleMEID = getUserMEDataResult.RoleMEID;
                    instance.StationID = getUserMEDataResult.StationID;
                    instance.StatusID = getUserMEDataResult.StatusID;

                    instance.EmployName = getUserMEDataResult.EmployName;
                    instance.EmployFName = getUserMEDataResult.EmployFName;
                    instance.MobileNo = getUserMEDataResult.MobileNo;
                    instance.StationCode = getUserMEDataResult.StationCode;
                    instance.StationName = getUserMEDataResult.StationName;
                    instance.StationFName = getUserMEDataResult.StationFName;
                    instance.Division = getUserMEDataResult.Division;
                    instance.UsertypeID = getUserMEDataResult.UsertypeId;
                    instance.Menu = getUserMEDataResult.Menu;
                    usertype = getUserMEDataResult.UsertypeId;
                    instance.CountryID = getUserMEDataResult.CountryID;
                    instance.CountryCode = getUserMEDataResult.CountryCode;

                    instance.DisableEnabletxtBox = getUserMEDataResult.DisableEnabletxtBox;
                    instance.TruckID = truckID;


                    GlobalVar.GV().UserID = instance.ID;
                    GlobalVar.GV().EmployID = instance.EmployID;
                    GlobalVar.GV().StationID = instance.StationID;
                    GlobalVar.GV().EmployMobileNo = instance.MobileNo;
                    GlobalVar.GV().EmployName = instance.EmployName;
                    GlobalVar.GV().EmployStation = instance.StationFName;

                    dbConnections.InsertAppVersion(getUserMEDataResult.Appversion, getApplicationContext());


                    String division = getUserMEDataResult.Division;
                    if (division.equals("IRS"))
                        division = "Courier";

                    String AppName = getUserMEDataResult.AppName;
                    if (AppName.equals("TH"))
                        AppName = "Courier";


                    if (!AppName.equals(division) && getUserMEDataResult.EmployID != 19127) {
                        ShowAlertMessage("This Application will support only for " + getUserMEDataResult.AppName + " Employees" +
                                " kindly contact concern person", 1);
                        dismissUserMeProgressdialog();
                        dbConnections.close();
                        return;
                    }

                    if (AppName.equals("Courier")) {
                        try {
                            JSONObject jsonObject = new JSONObject(finalJson);
                            dbConnections.deleteAllDenied(getApplicationContext());
                            dbConnections.insertDeniedBulk(jsonObject.getJSONArray("DeniedWaybills"), getApplicationContext());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }


                    if (GlobalVar.GV().CheckDataAvailability(getUserMEDataResult.Division, getApplicationContext(),
                            getUserMEDataResult.EmployID, getUserMEDataResult.Password, getUserMEDataResult.UpdateMenu)) {
                        dismissUserMeProgressdialog();
                        new GetMasterData().execute();
                        return;
                    } else {
                        if (AppName.equals(division) || getUserMEDataResult.EmployID == 19127) {
                            if (getUserMEDataResult.Appversion != GlobalVar.VersionCode(getApplicationContext())) {
                                dismissUserMeProgressdialog();
                                deleteApk();
                                updateApp();
                                return;
                            } else {
                                updateUserDetails();
                                LoginIntoOpenMainPage();
                            }
                        } else
                            ShowAlertMessage("This Application will support only for " + getUserMEDataResult.AppName + " Employees" +
                                    " kindly contact concern person", 1);
                    }
                }
            }
             else {
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

            dismissUserMeProgressdialog();
            super.onPostExecute(String.valueOf(finalJson));
        }
    }


    private void dismissUserMeProgressdialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void updateUserDetails() {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        dbConnections.UpdateLastLogin(instance.EmployID, getApplicationContext(), instance.UsertypeID);
        dbConnections.deleteUserME(instance, getApplicationContext(), getWindow().getDecorView().getRootView());
        dbConnections.InsertUserME(instance, getApplicationContext());
        dbConnections.close();

    }

    private class GetMasterData extends AsyncTask<String, Integer, String> {
        StringBuffer buffer;
        private ProgressDialog progressDialog;
        String DomainURL = "";
        String isInternetAvailable = "";

        @Override
        protected void onPreExecute() {

            if (progressDialog == null)
                progressDialog = ProgressDialog.show(LoginActivity.this, "Please wait.",
                        "Bringing Master Details.", true);
            DomainURL = GlobalVar.GV().GetDomainURL(getApplicationContext());
            //DomainURL = GlobalVar.GV().NaqelPointerAPILink;
            super.onPreExecute();

        }

        @SuppressWarnings("deprecation")
        protected String doInBackground(String... params) {

            GetDeliveryStatusRequest getDeliveryStatusRequest = new GetDeliveryStatusRequest();
            String jsonData = JsonSerializerDeserializer.serialize(getDeliveryStatusRequest, true);

            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(DomainURL + "GetMasterData");
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(GlobalVar.GV().loadbalance_ConRedtimeout);
                httpURLConnection.setConnectTimeout(GlobalVar.GV().loadbalance_Contimeout);
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
                isInternetAvailable = ignored.toString();
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
                // result = String.valueOf(buffer);
            }
            return null;
//


        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute("");
            if (result != null) {

                GlobalVar.ResetTriedCount();
                try {
                    Context context = getApplicationContext();
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray appversion = jsonObject.getJSONArray("AppVersion");
                    View view = getWindow().getDecorView().getRootView();
                    JSONObject jo = appversion.getJSONObject(0);
                    DBConnections dbConnections = new DBConnections(context, null);
                    dbConnections.InsertAppVersion(jo.getInt("VersionCode"), context);
                    int versioncode = GlobalVar.VersionCode(context);
                    int updatemenu = GlobalVar.VersionCode(context);

                    if (GlobalVar.GV().EmployID == 19127) //&& GlobalVar.GV().EmployID == 17099
                        versioncode = jo.getInt("VersionCode");

                    if (jo.getInt("VersionCode") == versioncode) {
                        // if (jo.getInt("ChangesMainMenu") == 1) {
                        JSONArray station = jsonObject.getJSONArray("Station");
                        if (station.length() > 0)
                            new Station(station.toString(), view, context);

                        JSONArray deliveryStatus = jsonObject.getJSONArray("DeliveyStatus");
                        if (deliveryStatus.length() > 0)
                            new DeliveryStatus(deliveryStatus.toString(), view, context);

                        JSONArray checkPointType = jsonObject.getJSONArray("CheckPointType");
                        if (checkPointType.length() > 0)
                            new CheckPointType(checkPointType.toString(), view, context);

                        JSONArray checkPointdetail = jsonObject.getJSONArray("CheckPointTypeDetail");
                        if (checkPointdetail.length() > 0)
                            new CheckPointTypeDetail(checkPointdetail.toString(), view, context);

                        JSONArray typeDDetails = jsonObject.getJSONArray("TypeDDetails");
                        if (typeDDetails.length() > 0)
                            new CheckPointTypeDDetail(typeDDetails.toString(), view, context);

                        JSONArray noNeedVolume = jsonObject.getJSONArray("NoNeedVolume");
                        if (noNeedVolume.length() > 0)
                            new NoNeedVolumeReason(noNeedVolume.toString(), view, context);

                        String devision = jsonObject.getString("Division");
                        dbConnections.UpdateUserDivision(devision, getWindow().getDecorView().getRootView()
                                , jo.getInt("ChangesMainMenu"));
                        dbConnections.UpdateMenu(devision, getWindow().getDecorView().getRootView()
                                , jo.getInt("ChangesMainMenu"));

                        JSONArray deliverysubstatus = jsonObject.getJSONArray("DeliveyStatusReason");
                        if (deliverysubstatus.length() > 0)
                            new DeliveryStatus(deliverysubstatus.toString(), view, context, 0);

                        JSONArray facility = jsonObject.getJSONArray("Facility");
                        if (facility.length() > 0)
                            new FacilityStatus(facility.toString(), view, context);

                        JSONArray contacts = jsonObject.getJSONArray("Contacts");
                        if (contacts.length() > 0)
                            new Contacts(contacts.toString(), view, context);

                        JSONArray CityLists = jsonObject.getJSONArray("CityLists");
                        if (CityLists.length() > 0)
                            dbConnections.insertCityBulk(CityLists, getApplicationContext());

                        try {

                            JSONArray binMasterList = jsonObject.getJSONArray("BinMastersList");

                            if (binMasterList.length() > 0)
                                dbConnections.insertBinMasterBulk(binMasterList, getApplicationContext());
                        } catch (Exception ex) {
                            Log.d("test" , "Insert Master Bin ex " + ex.toString());
                        }



                        updateUserDetails();
                        LoginIntoOpenMainPage();

                    } else {
                        deleteApk();
                        updateApp();
                    }
                    dbConnections.close();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                if (isInternetAvailable.contains("No address associated with hostname")) {
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly check your internet", GlobalVar.AlertType.Error);
                } else {
                    GlobalVar.GV().triedTimes = GlobalVar.GV().triedTimes + 1;
                    if (GlobalVar.GV().triedTimes == GlobalVar.GV().triedTimesCondition) {
                        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                        // dbConnections.UpdateDomaintriedTimes(GlobalVar.GV().triedTimes, DomainURL, getApplicationContext());
                        GlobalVar.GV().SwitchoverDomain(getApplicationContext(), DomainURL);
                    }
                    LoadDivisionError();
                }
            }
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }

        }
    }

    private void updateApp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Info")
                .setMessage("Kindly Please update our lastest version")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                        ActivityCompat.requestPermissions(
                                LoginActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                14
                        );

//                        new DownloadApk().execute("");

//                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.naqelexpress.naqelpointer"));
//                        startActivity(intent);
//                        final DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
//                        int id = dbConnections.getMaxID(" UserMeLogin where LogoutDate is NULL ", getApplicationContext());
//                        UserMeLogin userMeLogin = new UserMeLogin(id);
//                        dbConnections.UpdateUserMeLogout(userMeLogin, getApplicationContext());
//                        finish();
                    }
                }).setCancelable(false);//.setNegativeButton("Cancel", null).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void LoadDivisionError() {
        AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Info.");
        alertDialog.setMessage("Kindly Check your Internet Connection,please try again");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        GetMasterData asynthread = new GetMasterData();
                        StartAsyncTaskInParallel(asynthread);
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Close",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                        int id = dbConnections.getMaxID(" UserMeLogin where LogoutDate is NULL ", getApplicationContext());
                        UserMeLogin userMeLogin = new UserMeLogin(id);
                        dbConnections.UpdateUserMeLogout(userMeLogin, getApplicationContext());
                        finish();
                    }
                });
        alertDialog.show();
    }

    private void StartAsyncTaskInParallel(GetMasterData asynthread) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asynthread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            asynthread.execute("");

    }

    private void deleteApk() {

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        try {
            String DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/NaqelSignature/" + installaionfile;
            File file = new File(DIRECTORY);
            if (file.exists()) {
                file.delete();
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    ProgressDialog progressDialog;

    private class BringTruckData extends AsyncTask<String, Integer, String> {
        StringBuffer buffer;

        @Override
        protected void onPreExecute() {

            if (progressDialog == null)
                progressDialog = ProgressDialog.show(LoginActivity.this,
                        "Please wait.", "Bringing Truck Details...", true);
            super.onPreExecute();

        }

        protected String doInBackground(String... params) {

            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {

                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "BringTruck");
                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpURLConnection.setReadTimeout(30000);
                httpURLConnection.setConnectTimeout(30000);

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

                ShowAlertMessage("No Internet connection / Something went wrong ", 0);
            }

            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }

        }
    }

    private void setDefault() {
        FindVehilceObject fvo = new FindVehilceObject();
        fvo.ID = 0;
        fvo.Name = "At Yard";

        vehicles.add(fvo);
    }

    private void fetchData(JSONObject jsonObject) {
        vehicles.clear();
        setDefault();


        try {

            JSONArray status = jsonObject.getJSONArray("Truck");
            DBConnections dbConnections = new DBConnections(getApplicationContext(), null);

            if (status.length() > 0) {
                for (int i = 0; i < status.length(); i++) {
                    JSONObject jsonObject1 = status.getJSONObject(i);

                    FindVehilceObject fvo = new FindVehilceObject();
                    fvo.Name = jsonObject1.getString("Name");
                    fvo.ID = jsonObject1.getInt("ID");
                    vehicles.add(fvo);

                    dbConnections.InsertTruck(jsonObject1.getString("Name"), jsonObject1.getInt("ID"), getApplicationContext());

                }

                RedirectVechicleClass();
            } else
                ShowAlertMessage("No Vehicle Data, Kindly contact Admin", 1);

            dbConnections.close();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void RedirectVechicleClass() {
        Intent intent = new Intent(this, FindVehicle.class);
        intent.putExtra("Vehicles", vehicles);
        startActivityForResult(intent, 99);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 99: {
                if (resultCode == Activity.RESULT_OK) {

                    truck.setText(data.getStringExtra("name"));
                    truckID = data.getIntExtra("truckid", 0);
                }
                break;
            }
        }
    }

    private void ShowAlertMessage(String message, final int fun) {
        AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Info.");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (fun == 0) {
                            JSONObject jsonObject = new JSONObject();
                            //jsonObject.put("StationID", GlobalVar.GV().StationID);
                            //jsonObject.put("Function", function);
                            new BringTruckData().execute(jsonObject.toString());
                        }
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

    private void updatePasswordDialog(String message) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("Info")
                    .setMessage(message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            Intent i = new Intent(LoginActivity.this , UpdatePasswordActivity.class);
                            i.putExtra("emp_id" , Integer.parseInt(txtEmployID.getText().toString()));
                            startActivity(i);
                            Log.d("Login" , "Start");

                        }
                    }).setCancelable(false);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } catch (Exception e) {
            Log.d("Login" , "Login activity" + e.toString());
        }
    }

}