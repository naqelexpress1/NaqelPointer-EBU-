package com.naqelexpress.naqelpointer.Activity.Login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.naqelexpress.naqelpointer.Activity.MainPage.MainPageActivity;
import com.naqelexpress.naqelpointer.BuildConfig;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.UserMeLogin;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.Receiver.LocationupdateInterval;
import com.naqelexpress.naqelpointer.service.LocationService;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.util.ArrayList;


public class SplashScreenActivity
        extends AppCompatActivity {


    double Latitude = 0.0;
    double Longitude = 0.0;

    int redirctcalss = 0;

    // static final Uri CallLog_URI = CallLog.Calls.CONTENT_URI;
    FirebaseAuth mAuth;
    FirebaseAnalytics firebaseAnalytics;
    String key = "-MORKAgIlD5uuqZWtgCT";
    public static ArrayList<Location> places = new ArrayList<>();//96346
    public  String AreaData = "";
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);


        //#startRegion
//        Intent intent = new Intent(SplashScreenActivity.this, RouteMap.class);
//        intent.putParcelableArrayListExtra("myroute", GlobalVar.GV().myRouteShipmentList);
//        intent.putParcelableArrayListExtra("places", places);
//        intent.putExtra("AreaData", AreaData);
//        startActivityForResult(intent, 1);
        //#EndRegion

//        try {
        //  mAuth = FirebaseAuth.getInstance();
        //mAuth.signOut();
////
//            String uid = FirebaseAuth.getInstance().getUid();
////            if (uid == null || uid.length() == 0)
//
//
//            //  Write a message to the database
//
//            FirebaseDatabase database = FirebaseDatabase.getInstance();
//
//            final DatabaseReference myRef = database.getReference("LiveTracking");
//
//            final CourierDetailsFirebase user = new CourierDetailsFirebase("19128", "24.428261,39.606400", "", "", "", "", "", 0, "", "", "");
//
//          //  String
////            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
////                @Override
////                public void onDataChange(DataSnapshot snapshot) {
////                    if (snapshot.hasChild("EmpID")) {
////                        System.out.println(snapshot);
////                    }
////                }
////
////                @Override
////                public void onCancelled(DatabaseError databaseError) {
////
////                }
////            });
//
//
//
//            myRef.orderByChild("EmpID").equalTo("19128").addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.getValue() == null) {
//                        String userid = myRef.push().getKey();
//                        System.out.println(userid);
//                        myRef.child(userid).setValue(user);
//                        return;
//                    }else
//                    {
//
//                        String asd = dataSnapshot.getKey();
//                        key = dataSnapshot.getChildren().iterator().next().getKey();
//                        System.out.println(asd);
//                    }
//
//                    Map newUserData = new HashMap();
//                    newUserData.put("LatLng", "0,0");
//                    newUserData.put("NextWaybillNo", "123");
//                    newUserData.put("EmpID", "19128");
//                    newUserData.put("MobileNo", "Mno");
//                    newUserData.put("ConsLocation", "ConsLocation");
//                    newUserData.put("ConsigneeName", "ConsigneeName");
//                    newUserData.put("Speed","12");
//                    newUserData.put("isnotify", "1");
//                    newUserData.put("BillingType", "BillingType");
//                    newUserData.put("CollectedAmount", "CollectedAmount");
//                    //  newUserData.put("ConsLocation", ConsLocation);
//                    myRef.child(key).updateChildren(newUserData);
//
//
////                    CourierDetailsFirebase fetchuser = dataSnapshot.getChildren().iterator().next().getValue(CourierDetailsFirebase.class);
////                    if (fetchuser.EmpID != null)
////                        fetchuser.ID = dataSnapshot.getChildren().iterator().next().getKey();
////
////                    if (fetchuser.ID != null) {
////                        myRef.child(fetchuser.ID).child("LatLng").setValue("24.428261,39.606400");
////                    } else {
////                        String userid = myRef.push().getKey();
////                        myRef.child(userid).setValue(user);
////                    }
//                    //  Log.d("User", "");
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                    Log.d("User", "");
//                }
//
//
//            });
//        } catch (Exception e) {
//            System.out.println(e.toString());
//        }

//        Query queryRef = myRef
//                .orderByChild("EmpID")
//                .equalTo("19128");
//
//        queryRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                //TODO auto generated
//                System.out.println(s);
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                //TODO auto generated;
//                System.out.println(s);
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                //TODO auto generated;
//                System.out.println(dataSnapshot);
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//                //TODO auto generated
//                System.out.println(s);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                //TODO auto generated
//                System.out.println(databaseError);
//            }
//        });


        //getCallLog();

        //DBConnections dbConnections1 = new DBConnections(getApplicationContext(), null);
        // dbConnections1.deleteDeliveryDeliveyDetails(getApplicationContext());
//        dbConnections1.DeleteAllSuggestLocation(getApplicationContext());
//        dbConnections1.close();

//        startService(
//                new Intent(this,
//                        com.naqelexpress.naqelpointer.service.PlannedRoute_MyRouteComp.class));
        // sendNotification("cty3khWlR8Kka_O6UpyIyy:APA91bEDE7g-xvSSEW4OOh0E_dhG2pKRnrOYP7nVKwvD79wE6eMFCLl79j_Vh58mfMC8P_Zqfw_8pSecMDveB8AZWocFsvgt5lxlFuLTD_pGYQ4_g5cy_M4djaHmsk32rTwwuWAkT3ff");
        // sendNotification("et7zm9gyRaWEszgjT_zwGp:APA91bFgieA2s2HPcqbBt5By_2TmvVx34tB80adMkXtbXytL19Cjsp3jLYAbeIrxgZHRqGyjlG_GMBwQN0sEGGRrcCZd5S4mSrvH7aPN5Vm7nNNHcs-KIAMI3XOf_hwWGXJ68O213siQ");
//        FirebaseMessaging fm = FirebaseMessaging.getInstance();
//        fm.send(new RemoteMessage.Builder("cty3khWlR8Kka_O6UpyIyy:APA91bEDE7g-xvSSEW4OOh0E_dhG2pKRnrOYP7nVKwvD79wE6eMFCLl79j_Vh58mfMC8P_Zqfw_8pSecMDveB8AZWocFsvgt5lxlFuLTD_pGYQ4_g5cy_M4djaHmsk32rTwwuWAkT3ff" + "@fcm.googleapis.com")
//
//                .setMessageId(Integer.toString(123))
//
//                .addData("waybillno", "123456789")
//                .addData("EmpID","19127")
//                .addData("Lat","23.4582")
//                .addData("Lng","45.155")
//                .build());

        //  String time = GlobalVar.GV().getCurrentDateTime();
//        DBConnections dbConnections1 = new DBConnections(getApplicationContext(), null);
//        dbConnections1.DeleteAllSuggestLocation(getApplicationContext());
//        dbConnections1.DeleteAllPlannedLocation(getApplicationContext());
//        dbConnections1.close();
//

//        DBConnections db = new DBConnections(getApplicationContext(), null);
//        db.deleteAllLocation(getApplicationContext());
//        db.close();


//        startService(
//                new Intent(SplashScreenActivity.this,
//                        com.naqelexpress.naqelpointer.service.DeviceActivity.class));


        if (GlobalVar.ValidateAutomacticDate(getApplicationContext())) { //DateTime Validate

            DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
            dbConnections.InsertDomain_ForDelService(getApplicationContext());
            dbConnections.InsertDomain_ForNotDeliveredService(getApplicationContext());
            dbConnections.InsertDomain_ForDelSheetService(getApplicationContext());
            dbConnections.InsertDomain(getApplicationContext());
            dbConnections.DeleteFacilityLoggedIn(getApplicationContext());
            dbConnections.DeleteExsistingLogin(getApplicationContext());
            dbConnections.DeleteAllSyncData(getApplicationContext());
            dbConnections.InsertDomain_ForDelSheetServicebyNCL(getApplicationContext());
            dbConnections.InsertDomain_ForArrivedatDest(getApplicationContext());
            dbConnections.InsertDomain_ForAtorigin(getApplicationContext());
            dbConnections.InsertDomain_ForPickup(getApplicationContext());
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

        } else

            GlobalVar.RedirectSettings(SplashScreenActivity.this);
    }


    void loginPage() {

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        if (dbConnections.isColumnExist("UserMeLogin", "LogedOut", getApplicationContext())) {
            Cursor result = dbConnections.Fill("select * from UserMeLogin where LogedOut is NULL or LogedOut = 0",
                    getApplicationContext());

            if (result == null) {


                ActivityCompat.requestPermissions(
                        SplashScreenActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG},
                        6
                );

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
                            ActivityCompat.requestPermissions(
                                    SplashScreenActivity.this,
                                    new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG},
                                    6
                            );
                    } else
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
                ActivityCompat.requestPermissions(
                        SplashScreenActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG},
                        6
                );
            dbConnections.close();
        } else
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
                        String division = GlobalVar.GV().getDivisionID(getApplicationContext(), GlobalVar.GV().EmployID);
                        if (GlobalVar.GV().IsTerminalApp || division.equals("IRS") || division.equals("Express")) {
                            Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                            intent.putExtra("getMaster", 0);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(getApplicationContext(), VerifyMobileNo.class);
                            startActivity(intent);
                            finish();
                        }
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

//                        Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
//                        intent.putExtra("getMaster", 0);
//                        startActivity(intent);
//
////                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
////                    intent.putExtra("getMaster", 0);
////                    startActivity(intent);
//                        finish();

                        String division = GlobalVar.GV().getDivisionID(getApplicationContext(), GlobalVar.GV().EmployID);
                        if (GlobalVar.GV().IsTerminalApp || division.equals("IRS") || division.equals("Express")) {
                            Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                            intent.putExtra("getMaster", 0);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(getApplicationContext(), VerifyMobileNo.class);
                            startActivity(intent);
                            finish();
                        }

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
                            new String[]{
                                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
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
                                    new String[]{
                                            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
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

                    ActivityCompat.requestPermissions(
                            SplashScreenActivity.this,
                            new String[]{Manifest.permission.READ_PHONE_STATE},
                            11
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
                                    new String[]{Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS},
                                    10
                            );
                        }
                    }
                }
                break;
            case 11:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (redirctcalss == 0) {

                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.putExtra("getMaster", 0);
                        startActivity(intent);
                    } else if (redirctcalss == 1) {

                        String division = GlobalVar.GV().getDivisionID(getApplicationContext(), GlobalVar.GV().EmployID);
                        if (GlobalVar.GV().IsTerminalApp || division.equals("IRS") || division.equals("Express")) {
                            Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                            intent.putExtra("getMaster", 0);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(getApplicationContext(), VerifyMobileNo.class);
                            startActivity(intent);
                            finish();
                        }

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
                                    new String[]{Manifest.permission.READ_PHONE_STATE},
                                    11
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

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private void sendNotification(final String regToken) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json = new JSONObject();
                    JSONObject dataJson = new JSONObject();
                    dataJson.put("title", "Our Courier will be ready to Deliver(40000464),kindly be ready ");
                    dataJson.put("waybillno", "41790089");
                    dataJson.put("EmpID", "93283");
                    dataJson.put("Lat", "24.214574");
                    dataJson.put("Lng", "47.23162");
                    json.put("notification", dataJson);
                    json.put("to", regToken);
                    RequestBody body = RequestBody.create(JSON, json.toString());
                    Request request = new Request.Builder()
                            .header("Authorization", "key=" + "AAAAiNWIXO8:APA91bHXz33puOphUeOiAviFjDmkbehmBEL-ycu-1Adan90aYX8DQiQ03U_njrX-9ySnA-GWF0imJWBCXErfo-9r0rV8XW4lnG4G1-OtqbnlBfqvfsGv8o6REq95bQlqWAPrjVF72oVY")
                            .url("https://fcm.googleapis.com/fcm/send")
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    String finalResponse = response.body().string();
                } catch (Exception e) {
                    //Log.d(TAG,e+"");
                }
                return null;
            }
        }.execute();

    }


}