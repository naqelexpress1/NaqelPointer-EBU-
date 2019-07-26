package com.naqelexpress.naqelpointer.Activity.Login;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.ImageView;

import com.naqelexpress.naqelpointer.Activity.MainPage.MainPageActivity;
import com.naqelexpress.naqelpointer.AppController;
import com.naqelexpress.naqelpointer.BuildConfig;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.UserMeLogin;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.Receiver.LocationupdateInterval;
import com.naqelexpress.naqelpointer.service.LocationService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SplashScreenActivity
        extends AppCompatActivity {

    double Latitude = 0.0;
    double Longitude = 0.0;

    int redirctcalss = 0;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        dbConnections.DeleteFacilityLoggedIn(getApplicationContext());
        dbConnections.DeleteExsistingLogin(getApplicationContext());
        dbConnections.close();


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);


        if (GlobalVar.GV().IsEnglish())
            imageView.setImageResource(R.drawable.naqellogowhite);
        else
            imageView.setImageResource(R.drawable.naqellogowhitear);


        Thread myThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(2000);

                    loginPage();


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();

        if (savedInstanceState != null)
            setSavedInstance(savedInstanceState);


    }


    void loginPage() {

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        if (dbConnections.isColumnExist("UserMeLogin", "LogedOut", getApplicationContext())) {
            Cursor result = dbConnections.Fill("select * from UserMeLogin where LogedOut is NULL or LogedOut = 0", getApplicationContext());

            if (result == null) {


                ActivityCompat.requestPermissions(
                        SplashScreenActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG},
                        6
                );

                //OpenLoginPage();

                return;
            }

            if (result.getCount() > 1) {
                result.moveToFirst();
                do {
                    int ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                    UserMeLogin userMeLogin = new UserMeLogin(ID);
                    dbConnections.UpdateUserMeLogout(userMeLogin, getApplicationContext());
                }
                while (result.moveToNext());

                ActivityCompat.requestPermissions(
                        SplashScreenActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG},
                        6
                );

//                OpenLoginPage();

            } else if (result.getCount() == 1) {
                result.moveToFirst();
                try {
                    if (result.getPosition() != -1) {
                        int ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                        int EmployID = Integer.parseInt(result.getString(result.getColumnIndex("EmployID")));

                        GlobalVar.GV().EmployID = EmployID;


                        if (GetDivision()) {
                            if (dbConnections.isFacilityLoggedIn(getApplicationContext(), EmployID))
                                redirctcalss = 1;
                            else
                                redirctcalss = 0;
                        } else
                            redirctcalss = 1;


                        Cursor cursor = dbConnections.Fill("select * from UserME where EmployID = " + EmployID, getApplicationContext());
                        int uid = 0;
                        if (cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            do {
                                GlobalVar.GV().UserID = Integer.parseInt(cursor.getString(cursor.getColumnIndex("ID")));
                                GlobalVar.GV().StationID = Integer.parseInt(cursor.getString(cursor.getColumnIndex("StationID")));
                                GlobalVar.GV().EmployMobileNo = cursor.getString(cursor.getColumnIndex("MobileNo"));
                                uid = cursor.getInt(cursor.getColumnIndex("UserTypeID"));
                                if (GlobalVar.GV().IsEnglish()) {
                                    GlobalVar.GV().EmployName = cursor.getString(cursor.getColumnIndex("EmployName"));
                                    GlobalVar.GV().EmployStation = cursor.getString(cursor.getColumnIndex("StationName"));
                                } else {
                                    GlobalVar.GV().EmployName = cursor.getString(cursor.getColumnIndex("EmployFName"));
                                    GlobalVar.GV().EmployStation = cursor.getString(cursor.getColumnIndex("StationFName"));
                                }
                            }
                            while (cursor.moveToNext());

                            dbConnections.UpdateLastLogin(EmployID, getApplicationContext(), uid);
                            cursor.close();

                            ActivityCompat.requestPermissions(
                                    SplashScreenActivity.this,
                                    new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG},
                                    6
                            );


                        } else
//                                OpenLoginPage();
                            ActivityCompat.requestPermissions(
                                    SplashScreenActivity.this,
                                    new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG},
                                    6
                            );
//                        }
                    } else
//                        OpenLoginPage();
                        ActivityCompat.requestPermissions(
                                SplashScreenActivity.this,
                                new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG},
                                6
                        );
                } catch (Exception e) {
                    e.printStackTrace();
                }
                result.close();
            } else
//                OpenLoginPage();
                ActivityCompat.requestPermissions(
                        SplashScreenActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG},
                        6
                );
            dbConnections.close();
        } else
//            OpenLoginPage();
            ActivityCompat.requestPermissions(
                    SplashScreenActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG},
                    6
            );
    }

    private void OpenLoginPage() {

        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 111:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    loginPage();

                } else {
                    //  GlobalVar.AskPermission_Location(MainPageActivity.this);
                    try {
                        Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                        startActivity(i);
                    } catch (Exception e) {
                        GlobalVar.ShowDialog(SplashScreenActivity.this, "Contacts Permission necessary", "Kindly please contact our Admin", true);
                    }
                }
                break;
            case 2:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

//                    Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
//                    intent.putExtra("getMaster", 0);
//                    startActivity(intent);
//                    finish();
                    ActivityCompat.requestPermissions(
                            SplashScreenActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            4
                    );

                } else {
                    if (ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_DENIED) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                            try {
                                Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                startActivity(i);
                            } catch (Exception e) {
                                GlobalVar.ShowDialog(SplashScreenActivity.this, "Permission necessary", "Kindly please contact our Admin", true);
                            }
                            // finish();
                        } else {
                            ActivityCompat.requestPermissions(
                                    SplashScreenActivity.this,
                                    new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG},
                                    2
                            );
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                        intent.putExtra("getMaster", 0);
                        startActivity(intent);
                        finish();
                    }
                }

                break;
            case 3:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            SplashScreenActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            4
                    );
                } else {
                    if (ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_DENIED) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                            try {
                                Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                startActivity(i);
                            } catch (Exception e) {
                                GlobalVar.ShowDialog(SplashScreenActivity.this, "Permission necessary", "Kindly please contact our Admin", true);
                            }
                        } else {
                            ActivityCompat.requestPermissions(
                                    SplashScreenActivity.this,
                                    new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG},
                                    3
                            );
                        }
                    } else {
                        ActivityCompat.requestPermissions(
                                SplashScreenActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                4
                        );
                    }
                }

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

//                    if (!GlobalVar.isMyServiceRunning(com.naqelexpress.naqelpointer.service.Radios200Service.class, getApplicationContext())) {
//                        startService(
//                                new Intent(SplashScreenActivity.this,
//                                        com.naqelexpress.naqelpointer.service.Radios200Service.class));
//                    }
//                    loginPage();

                        Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                        intent.putExtra("getMaster", 0);
                        startActivity(intent);

//                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                    intent.putExtra("getMaster", 0);
//                    startActivity(intent);
                        finish();
                    } else {
                        GlobalVar.enableLocationSettings(SplashScreenActivity.this);
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_DENIED) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                            try {
                                Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                startActivity(i);
                            } catch (Exception e) {
                                GlobalVar.ShowDialog(SplashScreenActivity.this, "Permission necessary", "Kindly please contact our Admin", true);
                            }
                            // finish();
                        } else {
                            ActivityCompat.requestPermissions(
                                    SplashScreenActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                    4
                            );
                        }
                    }
// else {
//                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                        intent.putExtra("getMaster", 0);
//                        startActivity(intent);
//                        finish();
//                    }
                }

                break;

            case 5:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    loginPage();

                } else {
                    if (ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_DENIED) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                            try {
                                Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                startActivity(i);
                            } catch (Exception e) {
                                GlobalVar.ShowDialog(SplashScreenActivity.this, "Storage Permission necessary", "Kindly please contact our Admin", true);
                            }

                        } else {

                            ActivityCompat.requestPermissions(
                                    SplashScreenActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    5
                            );
                        }
                    }

                }


                break;
            case 6:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(
                            SplashScreenActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            7
                    );

                } else {
                    if (ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_DENIED) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                            try {
                                Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                startActivity(i);
                            } catch (Exception e) {
                                GlobalVar.ShowDialog(SplashScreenActivity.this, "Permission necessary", "Kindly please contact our Admin", true);
                            }
                            // finish();
                        } else {
                            ActivityCompat.requestPermissions(
                                    SplashScreenActivity.this,
                                    new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG},
                                    6
                            );
                        }
                    }
//                    else {
//                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }
                }

                break;
            case 7:
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


//                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                        intent.putExtra("getMaster", 0);
//                        startActivity(intent);
                        ActivityCompat.requestPermissions(
                                SplashScreenActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                8
                        );

//                        finish();
                    } else {
                        GlobalVar.enableLocationSettings(SplashScreenActivity.this);
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_DENIED) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                            try {
                                Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                startActivity(i);
                            } catch (Exception e) {
                                GlobalVar.ShowDialog(SplashScreenActivity.this, "Permission necessary", "Kindly please contact our Admin", true);
                            }
                            // finish();
                        } else {
                            ActivityCompat.requestPermissions(
                                    SplashScreenActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                    7
                            );
                        }
                    }
                }

                break;

            case 8:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(
                            SplashScreenActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            9
                    );

//                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                    intent.putExtra("getMaster", 0);
//                    startActivity(intent);


                } else {
                    if (ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_DENIED) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                            try {
                                Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                startActivity(i);
                            } catch (Exception e) {
                                GlobalVar.ShowDialog(SplashScreenActivity.this, "Permission necessary", "Kindly please contact our Admin", true);
                            }
                            // finish();
                        } else {
                            ActivityCompat.requestPermissions(
                                    SplashScreenActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                    8
                            );
                        }
                    }
                }
                break;
            case 9:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(
                            SplashScreenActivity.this,
                            new String[]{Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS},
                            10
                    );

//                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                    intent.putExtra("getMaster", 0);
//                    startActivity(intent);


                } else {
                    if (ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_DENIED) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                            try {
                                Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                startActivity(i);
                            } catch (Exception e) {
                                GlobalVar.ShowDialog(SplashScreenActivity.this, "Permission necessary", "Kindly please contact our Admin", true);
                            }
                            // finish();
                        } else {
                            ActivityCompat.requestPermissions(
                                    SplashScreenActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    9
                            );
                        }
                    }
                }
                break;
            case 10:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (redirctcalss == 0) {

                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.putExtra("getMaster", 0);
                        startActivity(intent);
                    } else if (redirctcalss == 1) {
                        Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                        intent.putExtra("getMaster", 0);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.putExtra("getMaster", 0);
                        startActivity(intent);
                    }
                    finish();

                } else {
                    if (ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_DENIED) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                            try {
                                Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                startActivity(i);
                            } catch (Exception e) {
                                GlobalVar.ShowDialog(SplashScreenActivity.this, "Permission necessary", "Kindly please contact our Admin", true);
                            }
                            // finish();
                        } else {
                            ActivityCompat.requestPermissions(
                                    SplashScreenActivity.this,
                                    new String[]{Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS},
                                    10
                            );
                        }
                    }
                }
                break;
        }

    }

    private boolean GetDivision() {
        String division = GlobalVar.GV().getDivisionID(getApplicationContext(), GlobalVar.GV().EmployID);

        if (division.equals("Express") || division.equals("IRS"))
            return false;
        else
            return true;

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putInt("EmployID", GlobalVar.GV().EmployID);
        outState.putInt("UserID", GlobalVar.GV().UserID);
        outState.putInt("StationID", GlobalVar.GV().StationID);
        outState.putString("EmployMobileNo", GlobalVar.GV().EmployMobileNo);
        outState.putString("EmployName", GlobalVar.GV().EmployName);
        outState.putString("EmployStation", GlobalVar.GV().EmployStation);


        super.onSaveInstanceState(outState, outPersistentState);
    }

    private void setSavedInstance(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            GlobalVar.GV().EmployID = savedInstanceState.getInt("EmployID");
            GlobalVar.GV().UserID = savedInstanceState.getInt("UserID");
            GlobalVar.GV().StationID = savedInstanceState.getInt("StationID");
            GlobalVar.GV().EmployMobileNo = savedInstanceState.getString("EmployMobileNo");
            GlobalVar.GV().EmployName = savedInstanceState.getString("EmployName");
            GlobalVar.GV().EmployStation = savedInstanceState.getString("EmployStation");


        }
    }
}