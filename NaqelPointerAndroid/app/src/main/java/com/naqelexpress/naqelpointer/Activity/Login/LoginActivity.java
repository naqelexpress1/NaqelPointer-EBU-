package com.naqelexpress.naqelpointer.Activity.Login;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.vision.barcode.Barcode;
import com.naqelexpress.naqelpointer.Activity.MainPage.MainPageActivity;
import com.naqelexpress.naqelpointer.BuildConfig;
import com.naqelexpress.naqelpointer.Classes.BarcodeScan;
import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.UserME;
import com.naqelexpress.naqelpointer.DB.DBObjects.UserMeLogin;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.JSON.ProjectAsyncTask;
import com.naqelexpress.naqelpointer.JSON.Request.GetUserMEDataRequest;
import com.naqelexpress.naqelpointer.JSON.Results.CheckNewVersionResult;
import com.naqelexpress.naqelpointer.JSON.Results.GetUserMEDataResult;
import com.naqelexpress.naqelpointer.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;

public class LoginActivity
        extends AppCompatActivity {
    // DBConnections dbConnections;
    //Context context;
    TextView lbVersion;
    Button btnLogin, btnForgotPassword, btnScan;
    EditText txtEmployID, txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);

        //GlobalVar.GV().rootViewMainPage = mainRootView = findViewById(android.R.id.content);

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

        //context = this;
        lbVersion = (TextView) findViewById(R.id.lbVersion);
        String Version = getString(R.string.lbVersion) + GlobalVar.GV().AppVersion;
        lbVersion.setText(Version);

        // this.dbConnections = new DBConnections(this, mainRootView);

        txtEmployID = (EditText) findViewById(R.id.txtEmployID);
        txtPassword = (EditText) findViewById(R.id.txtPassword);

//        if (savedInstanceState != null)
//            setSavedInstance(savedInstanceState);


        //TODO remove user name and password
        //txtEmployID.setText("17693");
        //txtPassword.setText("1989");

//        txtEmployID.setText("15304");
//        txtPassword.setText("123456");

        //Commented by Ismail
//        JSONObject jsonObject = new JSONObject();
//        try
//        {
//            if (GlobalVar.GV().HasInternetAccess)
//            {
//                CheckNewVersionRequest checkNewVersionRequest = new CheckNewVersionRequest();
//                jsonObject.put("AppSystemSettingID",checkNewVersionRequest.AppSystemSettingID);
//                jsonObject.put("CurrentVersion",checkNewVersionRequest.CurrentVersion);
//                jsonObject.put("AppTypeID",checkNewVersionRequest.AppTypeID);
//                jsonObject.put("AppVersion",checkNewVersionRequest.AppVersion);
//                jsonObject.put("LanguageID",checkNewVersionRequest.LanguageID);
//                String jsonData = jsonObject.toString();
//
//                new CheckNewVersion().execute(jsonData);
//            }
//            else
//                GlobalVar.GV().ShowSnackbar(mainRootView,getString(R.string.NoInternetConnection), GlobalVar.AlertType.Warning);
//        }
//        catch (JSONException e)
//        {
//            e.printStackTrace();
//        }

        txtEmployID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
//                //#4#-1#Admin#RUH#Yemen#Correct#
//                int UserID = 0, EmployID = -5;
//                if (txtEmployID.getText().toString().startsWith("#") &&
//                        txtEmployID.getText().toString().endsWith("#Correct#"))
//                {
//                    String[] txt = txtEmployID.getText().toString().split("#");
//                    UserID = Integer.parseInt(txt[1]);
//                    EmployID = Integer.parseInt(txt[2]);
//
//                    if (EmployID >= -1 && UserID > 0)
//                    {
//                        Cursor result = dbConnections.Fill("select * from UserME where StatusID <> 3 and EmployID = " + String.valueOf(EmployID) );//+ " and ID =" + String.valueOf(UserID));
//                        if (result.getCount() > 0)
//                        {
//                            result.moveToFirst();
//                            GlobalVar.GV().UserID = Integer.valueOf(result.getString(result.getColumnIndex("ID")));
//                            GlobalVar.GV().EmployID = Integer.valueOf(result.getString(result.getColumnIndex("EmployID")));
//                            GlobalVar.GV().StationID = Integer.valueOf(result.getString(result.getColumnIndex("StationID")));
//
//                            UserMeLogin userMeLogin = new UserMeLogin(GlobalVar.GV().EmployID,1);
//                            dbConnections.InsertUserMeLogin(userMeLogin);
//                            dataSync.SendUserMeLoginsData();
//
//                           OpenMainPage();
//                        }
//                        else
//                            {
//                                dataSync.GetUserMEData(EmployID,"NoPass");
//                                GlobalVar.GV().ShowSnackbar(mainRootView,"Please check your barcode", GlobalVar.AlertType.Error);
//                            }
//                    }
//                    else
//                    {
//                        dataSync.GetUserMEData(EmployID,"NoPass");
//                        GlobalVar.GV().ShowSnackbar(mainRootView,"Please check your barcode", GlobalVar.AlertType.Error);
//                    }
//                }
//                else
//                {
//                    dataSync.GetUserMEData(Integer.parseInt(txtEmployID.getText().toString()),txtPassword.getText().toString());
//                    GlobalVar.GV().ShowSnackbar(mainRootView, "Please Check Your Employ ID and Password", GlobalVar.AlertType.Error);
//                }
            }
        });

//        ActivityCompat.requestPermissions(
//                LoginActivity.this,
//                new String[]{Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG, Manifest.permission.WRITE_CONTACTS
//                        , Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE
//                        , Manifest.permission.CAMERA, Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                1
//        );
    }

    public void HideKeyBoard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        //SyncData();
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
//        if (txtPassword.getText().toString().equals("123456")) {
//            Intent intent = new Intent(this, UpdatePassword.class);
//            txtPassword.setText("");
//            intent.putExtra("EmployID", txtEmployID.getText().toString());
//            startActivity(intent);
//            return;
//        }

        LoginIntoOpenMainPage();
    }

    private void LoginIntoOpenMainPage() {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        String empID = txtEmployID.getText().toString();
        String Password = txtPassword.getText().toString();

        Cursor result = dbConnections.Fill("select * from UserME where StatusID <> 3 and EmployID = " + empID + " and Password ='" + Password + "'"
                , getApplicationContext());
        if (result.getCount() > 0) {
            result.moveToFirst();
//            int x1 = Integer.valueOf(result.getString(result.getColumnIndex("ID")));
            GlobalVar.GV().UserID = Integer.valueOf(result.getString(result.getColumnIndex("ID")));
            GlobalVar.GV().EmployID = Integer.valueOf(result.getString(result.getColumnIndex("EmployID")));
            GlobalVar.GV().StationID = Integer.valueOf(result.getString(result.getColumnIndex("StationID")));

//            try
//            {
//                String x = result.getString(result.getColumnIndex("MobileNo"));
//            }
//            catch (Exception e)
//            {
//                e.printStackTrace();
//            }

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
            LoginToMainPage();
        } else {
            // if ( GlobalVar.GV().HasInternetAccess )
            //  {
            //DataSync dataSync = new DataSync();
            GetUserMEData(Integer.parseInt(txtEmployID.getText().toString()), txtPassword.getText().toString());

            //     GlobalVar.GV().ShowSnackbar(mainRootView, "Please Check Your Employ ID and Password", GlobalVar.AlertType.Error);
            // }
            // else
            //      GlobalVar.GV().ShowSnackbar(mainRootView,"Please check the internet connection", GlobalVar.AlertType.Warning);
        }
    }

    private void LoginToMainPage() {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        UserMeLogin userMeLogin = new UserMeLogin(GlobalVar.GV().EmployID, 1);
        dbConnections.InsertUserMeLogin(userMeLogin, getApplicationContext());
        dbConnections.close();
        OpenMainPage(1);
    }

    int getMaster;

    private void OpenMainPage(int getMaster) {
        this.getMaster = getMaster;
        if (GlobalVar.GV().ThereIsMandtoryVersion)
            GlobalVar.GV().ShowDialog(getApplicationContext(), "New Version", "There is a new version, Please update the system, or cordiante with IT department for updating your system.", true);
        else {
            //  SetDeviceId();

//            Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
//            intent.putExtra("getMaster", getMaster);
//            startActivity(intent);
//            finish();

            ActivityCompat.requestPermissions(
                    LoginActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    2
            );

//            ActivityCompat.requestPermissions(
//                    LoginActivity.this,
//                    new String[]{Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG, Manifest.permission.WRITE_CONTACTS
//                            , Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE
//                            , Manifest.permission.CAMERA, Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS,
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_NUMBERS},
//                    1
//            );
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GlobalVar.GV().CAMERA_PERMISSION_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                final Barcode barcode = data.getParcelableExtra("barcode");
                txtEmployID.post(new Runnable() {
                    @Override
                    public void run() {
                        String resultBarcode = barcode.displayValue;
                        GlobalVar.GV().MakeSound(getApplicationContext(), R.raw.barcodescanned);

                        //#4#-1#Admin#RUH#Yemen#Correct#
                        int UserID, EmployID;
                        if (resultBarcode.startsWith("#") &&
                                resultBarcode.endsWith("#Correct#")) {
                            String[] txt = resultBarcode.split("#");
                            UserID = Integer.parseInt(txt[1]);
                            EmployID = Integer.parseInt(txt[2]);
                            DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                            if (EmployID >= -1 && UserID > 0) {
                                if (GlobalVar.GV().ThereIsMandtoryVersion) {
                                    GlobalVar.GV().ShowDialog(getApplicationContext(), "New Version", "There is a new version, Please update the system, or cordiante with IT department for updating your system.", false);
                                    return;
                                }

                                Cursor result = dbConnections.Fill("select * from UserME where StatusID <> 3 and EmployID = " + String.valueOf(EmployID), getApplicationContext());//+ " and ID =" + String.valueOf(UserID));
                                if (result.getCount() > 0) {
                                    result.moveToFirst();
                                    GlobalVar.GV().UserID = Integer.valueOf(result.getString(result.getColumnIndex("ID")));
                                    GlobalVar.GV().EmployID = Integer.valueOf(result.getString(result.getColumnIndex("EmployID")));
                                    GlobalVar.GV().StationID = Integer.valueOf(result.getString(result.getColumnIndex("StationID")));
                                    GlobalVar.GV().EmployMobileNo = result.getString(result.getColumnIndex("MobileNo"));
                                    if (GlobalVar.GV().IsEnglish()) {
                                        GlobalVar.GV().EmployName = result.getString(result.getColumnIndex("EmployName"));
                                        GlobalVar.GV().EmployStation = result.getString(result.getColumnIndex("StationName"));
                                    } else {
                                        GlobalVar.GV().EmployName = result.getString(result.getColumnIndex("EmployFName"));
                                        GlobalVar.GV().EmployStation = result.getString(result.getColumnIndex("StationFName"));
                                    }

                                    LoginToMainPage();
                                } else {
                                    //DataSync dataSync = new DataSync();
                                    GetUserMEData(EmployID, "NoPass");
                                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Please check your barcode", GlobalVar.AlertType.Error);
                                }
                            } else {
                                //DataSync dataSync = new DataSync();
                                GetUserMEData(EmployID, "NoPass");
                                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Please check your barcode", GlobalVar.AlertType.Error);
                            }
                            dbConnections.close();
                        }

                    }
                });
            }
        }


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
                                    Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                    startActivity(i);
                                } catch (Exception e) {
                                    GlobalVar.ShowDialog(MainPageActivity.this, "Permission necessary", "Kindly please contact our Admin", true);
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
                    //  GlobalVar.AskPermission_Location(MainPageActivity.this);
                    for (int i = 0; i < permissions.length; i++) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                            try {
                                Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                startActivity(i);
                            } catch (Exception e) {
                                GlobalVar.ShowDialog(MainPageActivity.this, "Permission necessary", "Kindly please contact our Admin", true);
                            }
                            break;
                        }
                    }
                }
                break;

            case 2:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                    intent.putExtra("getMaster", getMaster);
                    startActivity(intent);
                    finish();
                } else {
                    if (ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_DENIED) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                            try {
                                Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                startActivity(i);
                            } catch (Exception e) {
                                GlobalVar.ShowDialog(MainPageActivity.this, "Call Permission necessary", "Kindly please contact our Admin", true);
                            }
                            // finish();
                        }
                        ActivityCompat.requestPermissions(
                                LoginActivity.this,
                                new String[]{Manifest.permission.CALL_PHONE},
                                2
                        );
                    } else {
                        Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                        intent.putExtra("getMaster", getMaster);
                        startActivity(intent);
                        finish();
                    }
                }

                break;
        }

    }

    public void ScanEmployBarCode() {
        if (!GlobalVar.GV().checkPermission(this, GlobalVar.PermissionType.Camera))
            GlobalVar.GV().askPermission(this, GlobalVar.PermissionType.Camera);
        else {
            Intent intent = new Intent(getApplicationContext(), BarcodeScan.class);
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
                    GlobalVar.GV().ShowDialog(getApplicationContext(), "New Version", "There is a new version, Please update the system, or cordiante with IT department for updating your system.", true);

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
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("txtEmployID", txtEmployID.getText().toString());
        outState.putString("txtPassword", txtPassword.getText().toString());

        super.onSaveInstanceState(outState);
    }

    private void setSavedInstance(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            txtEmployID.setText(savedInstanceState.getString("txtEmployID"));
            txtPassword.setText(savedInstanceState.getString("txtPassword"));
        }
    }

    public void GetUserMEData(int EmployID, String Password) {
        //  if (!GlobalVar.GV().HasInternetAccess)
        //      return;
        GlobalVar.hideKeyboardFrom(getApplicationContext(), getWindow().getDecorView().getRootView());
        GetUserMEDataRequest getUserMEDataRequest = new GetUserMEDataRequest();
        getUserMEDataRequest.EmployID = EmployID;
        getUserMEDataRequest.Passowrd = Password;

        String jsonData = JsonSerializerDeserializer.serialize(getUserMEDataRequest, true);
        //System.out.println(jsonData);

        new GetUserMEDataFromServer().execute(jsonData);
    }

    private class GetUserMEDataFromServer extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;
        String result = "";
        StringBuffer buffer;

        @Override
        protected void onPreExecute() {

            progressDialog = ProgressDialog.show(LoginActivity.this, "Please wait.", "Bringing User Details.", true);
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "GetUserMEData");
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
                result = String.valueOf(buffer);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String finalJson) {

            DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
            if (finalJson != null) {

                GetUserMEDataResult getUserMEDataResult = new GetUserMEDataResult(finalJson);
                if (!getUserMEDataResult.HasError) {
                    // if (GlobalVar.GV().dbConnections != null) {
                    UserME instance = new UserME();
                    instance.ID = getUserMEDataResult.ID;
                    instance.EmployID = getUserMEDataResult.EmployID;
                    instance.Password = getUserMEDataResult.Password;
                    instance.RoleMEID = getUserMEDataResult.RoleMEID;
                    instance.StationID = getUserMEDataResult.StationID;
                    instance.StatusID = getUserMEDataResult.StatusID;

                    instance.EmployName = getUserMEDataResult.EmployName;
                    instance.EmployFName = getUserMEDataResult.EmployFName;
                    instance.MobileNo = getUserMEDataResult.MobileNo;
                    instance.StationCode = getUserMEDataResult.StationCode;
                    instance.StationName = getUserMEDataResult.StationName;
                    instance.StationFName = getUserMEDataResult.StationFName;


                    GlobalVar.GV().UserID = instance.ID;
                    GlobalVar.GV().EmployID = instance.EmployID;
                    GlobalVar.GV().StationID = instance.StationID;
                    GlobalVar.GV().EmployMobileNo = instance.MobileNo;
                    GlobalVar.GV().EmployName = instance.EmployName;
                    GlobalVar.GV().EmployStation = instance.StationFName;


                    dbConnections.deleteUserME(instance, getApplicationContext(), getWindow().getDecorView().getRootView());
                    dbConnections.InsertUserME(instance, getApplicationContext());
                    OpenMainPage(1);
                    //   }
                } else
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Data Not Sync Because :" + getUserMEDataResult.ErrorMessage, GlobalVar.AlertType.Error);
            } else
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.wentwrong), GlobalVar.AlertType.Error);

            progressDialog.dismiss();
            super.onPostExecute(String.valueOf(finalJson));
        }
    }
}