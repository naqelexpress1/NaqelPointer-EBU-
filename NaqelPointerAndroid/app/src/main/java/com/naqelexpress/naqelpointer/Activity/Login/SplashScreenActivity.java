package com.naqelexpress.naqelpointer.Activity.Login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ImageView;

import com.naqelexpress.naqelpointer.Activity.MainPage.MainPageActivity;
import com.naqelexpress.naqelpointer.BuildConfig;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.UserMeLogin;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import org.joda.time.DateTime;

public class SplashScreenActivity
        extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splashscreen);

        //String token = FirebaseInstanceId.getInstance().getToken();

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        //dbConnections.deleteAllMeasurement();
//        dbConnections.deleteAllOrigin();
        dbConnections.deleteAllTrip();
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
                    //GlobalVar.AskPermission_Reboot(SplashScreenActivity.this);

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
            if (result.getCount() > 1) {
                result.moveToFirst();
                do {
                    int ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                    UserMeLogin userMeLogin = new UserMeLogin(ID);
                    dbConnections.UpdateUserMeLogout(userMeLogin, getApplicationContext());
                }
                while (result.moveToNext());

                OpenLoginPage();
            } else if (result.getCount() == 1) {
                result.moveToFirst();
                try {
                    if (result.getPosition() != -1) {
                        int ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                        int EmployID = Integer.parseInt(result.getString(result.getColumnIndex("EmployID")));
                        DateTime Date = DateTime.parse(result.getString(result.getColumnIndex("Date")));
                        int counts = DateTime.now().getDayOfMonth() - Date.getDayOfMonth();
                        if (counts >= 1) {
                            UserMeLogin userMeLogin = new UserMeLogin(ID);
                            dbConnections.UpdateUserMeLogout(userMeLogin, getApplicationContext());
                            OpenLoginPage();
                        } else {
                            GlobalVar.GV().EmployID = EmployID;

                            Cursor cursor = dbConnections.Fill("select * from UserME where EmployID = " + EmployID, getApplicationContext());
                            if (cursor.getCount() > 0) {
                                cursor.moveToFirst();
                                do {
                                    GlobalVar.GV().UserID = Integer.parseInt(cursor.getString(cursor.getColumnIndex("ID")));
                                    GlobalVar.GV().StationID = Integer.parseInt(cursor.getString(cursor.getColumnIndex("StationID")));
                                    GlobalVar.GV().EmployMobileNo = cursor.getString(cursor.getColumnIndex("MobileNo"));

                                    if (GlobalVar.GV().IsEnglish()) {
                                        GlobalVar.GV().EmployName = cursor.getString(cursor.getColumnIndex("EmployName"));
                                        GlobalVar.GV().EmployStation = cursor.getString(cursor.getColumnIndex("StationName"));
                                    } else {
                                        GlobalVar.GV().EmployName = cursor.getString(cursor.getColumnIndex("EmployFName"));
                                        GlobalVar.GV().EmployStation = cursor.getString(cursor.getColumnIndex("StationFName"));
                                    }
                                }
                                while (cursor.moveToNext());

                                ActivityCompat.requestPermissions(
                                        SplashScreenActivity.this,
                                        new String[]{Manifest.permission.CALL_PHONE},
                                        2
                                );

                                //String pwd = GlobalVar.GV().GetUserPassword(EmployID, getApplicationContext());
                                //Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                                //intent.putExtra("getMaster", 0);

                                //startActivity(intent);
                                //finish();

                            } else
                                OpenLoginPage();
                        }
                    } else
                        OpenLoginPage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else
                OpenLoginPage();
        } else
            OpenLoginPage();
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
                    Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                    startActivity(i);
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Our app need the Contacts Permission,please kindly allow me", GlobalVar.AlertType.Error);
                }
                break;
            case 2:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                    intent.putExtra("getMaster", 0);
                    startActivity(intent);
                    finish();
                } else {
                    if (ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_DENIED) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                            startActivity(intent);
                            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Our app need the Phone call Permission,please kindly allow me", GlobalVar.AlertType.Error);
                            // finish();
                        }
                        ActivityCompat.requestPermissions(
                                SplashScreenActivity.this,
                                new String[]{Manifest.permission.CALL_PHONE},
                                2
                        );
                    } else {
                        Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                        intent.putExtra("getMaster", 0);
                        startActivity(intent);
                        finish();
                    }
                }

                break;

        }

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