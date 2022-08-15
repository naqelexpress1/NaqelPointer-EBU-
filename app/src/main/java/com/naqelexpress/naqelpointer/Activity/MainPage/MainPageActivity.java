package com.naqelexpress.naqelpointer.Activity.MainPage;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.naqelexpress.naqelpointer.Activity.ArrivedatDestNoValidation.ArrivedatDestination;
import com.naqelexpress.naqelpointer.Activity.AtOriginusingLocalDB.AtOrigin;
import com.naqelexpress.naqelpointer.Activity.Booking.BookingList;
import com.naqelexpress.naqelpointer.Activity.CBM.CBM;
import com.naqelexpress.naqelpointer.Activity.CheckCOD.CODCheckingActivity;
import com.naqelexpress.naqelpointer.Activity.CheckPointbyPieceLevel.CheckPointsActivity;
import com.naqelexpress.naqelpointer.Activity.CourierKPIEBU.CourierKpi;
import com.naqelexpress.naqelpointer.Activity.Delivery.DeliveryActivity;
import com.naqelexpress.naqelpointer.Activity.DeliverysheetEBU.DeliverySheetActivity;
import com.naqelexpress.naqelpointer.Activity.EBURoute.DeliverySheet;
import com.naqelexpress.naqelpointer.Activity.FuelModelEBU.Fuel;
import com.naqelexpress.naqelpointer.Activity.History.History;
import com.naqelexpress.naqelpointer.Activity.IncabCheckList.IncCabChecklist;
import com.naqelexpress.naqelpointer.Activity.Incident.Incident;
import com.naqelexpress.naqelpointer.Activity.InterCity.TripAndVehicleDetail;
import com.naqelexpress.naqelpointer.Activity.LoadtoDestLocalDB.LoadtoDestination;
import com.naqelexpress.naqelpointer.Activity.LoadtoDestLocalDB.SyncTripDetails;
import com.naqelexpress.naqelpointer.Activity.Login.SplashScreenActivity;
import com.naqelexpress.naqelpointer.Activity.Login.UpdatePasswordModule;
import com.naqelexpress.naqelpointer.Activity.MultiDelivery.MultiDeliveryActivity;
import com.naqelexpress.naqelpointer.Activity.MyAccount.MyAccountActivity;
import com.naqelexpress.naqelpointer.Activity.MyRoute.MyRouteActivity;
import com.naqelexpress.naqelpointer.Activity.NightStock.NSScanShipmentActivity;
import com.naqelexpress.naqelpointer.Activity.NightStock.NightStockActivity;
import com.naqelexpress.naqelpointer.Activity.NotDelivered.NotDeliveredActivity;
import com.naqelexpress.naqelpointer.Activity.PendingMoney.PendingMoneyActivity;
import com.naqelexpress.naqelpointer.Activity.PickUp.PickUpActivity;
import com.naqelexpress.naqelpointer.Activity.Rating.CourierRating;
import com.naqelexpress.naqelpointer.Activity.ScanWaybill.ScanWaybill;
import com.naqelexpress.naqelpointer.Activity.Settings.SettingActivity;
import com.naqelexpress.naqelpointer.Activity.TerminalHandlingAutoSave.TerminalHandlingGroup;
import com.naqelexpress.naqelpointer.Activity.ValidationDS.ValidationDS;
import com.naqelexpress.naqelpointer.Activity.WaybillMeasurments.WaybillMeasurementActivity;
import com.naqelexpress.naqelpointer.BuildConfig;
import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.ContactNo.Contact;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.CheckPointType;
import com.naqelexpress.naqelpointer.DB.DBObjects.CheckPointTypeDDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.CheckPointTypeDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.Contacts;
import com.naqelexpress.naqelpointer.DB.DBObjects.DeliveryStatus;
import com.naqelexpress.naqelpointer.DB.DBObjects.NoNeedVolumeReason;
import com.naqelexpress.naqelpointer.DB.DBObjects.Station;
import com.naqelexpress.naqelpointer.DB.DBObjects.UserMeLogin;
import com.naqelexpress.naqelpointer.DB.DBObjects.UserSettings;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.JSON.DataSync;
import com.naqelexpress.naqelpointer.JSON.Request.GetDeliveryStatusRequest;
import com.naqelexpress.naqelpointer.MainActivity;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.service.LocationService;

import org.joda.time.DateTime;
import org.joda.time.DurationFieldType;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainPageActivity
        extends AppCompatActivity {

    protected List<AsyncTask<String, Integer, String>> asyncTasks = new ArrayList<AsyncTask<String, Integer, String>>();
    String[] cellTitle;
    int cellIcon[];
    String app_pkg_name = "com.naqelexpress.naqelpointer";
    int UNINSTALL_REQUEST_CODE = 123;
    int menu = 0;

    GridView gridView;

    String devision = "";
    FloatingActionButton btnSignOut;
    public DataSync dataSync;
    int progressBarStatus = 0;
    TextView ofd, attempted, delivered, exceptions, productivity, complaint, compDelvrd, ComRemain,
            tvValidationHeader, tvValidationDate, tvValidationCountHeader, tvValidationCountBody;

    TableLayout tl, tl1;
    TextView user, version, devicestatus;
    LinearLayout llValidation;
    float Rating = 0;
    RatingBar ratingBar;

    //TextView out
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GlobalVar.GV().SignedIn = true;

        try {
            setContentView(R.layout.mainpage);

            registerFirebase();
            user = (TextView) findViewById(R.id.user);
            version = (TextView) findViewById(R.id.version);
            devicestatus = (TextView) findViewById(R.id.devicestatus);
            tvValidationDate = findViewById(R.id.tv_validation_file_Date);
            tvValidationHeader = findViewById(R.id.tv_validation_file_Date_header);
            tvValidationCountHeader = findViewById(R.id.tv_validation_file_count_header);
            tvValidationCountBody = findViewById(R.id.tv_validation_file_count_body);
            llValidation = findViewById(R.id.ll_validation);

            ratingBar = (RatingBar) findViewById(R.id.ratingBar);
            ratingBar.setNumStars(3);
            //ratingBar.setVisibility(View.GONE);
            tl = (TableLayout) findViewById(R.id.tl);
            tl1 = (TableLayout) findViewById(R.id.tl1);


            float screenSize = getResources().getDisplayMetrics().density;
            ActionBar mActionBar = getSupportActionBar();
            mActionBar.setDisplayShowHomeEnabled(false);
            mActionBar.setDisplayShowTitleEnabled(false);
            LayoutInflater mInflater = LayoutInflater.from(this);
            //int size = (int) (50 * screenSize);
            ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams((int) (50 * screenSize),
                    (int) (50 * screenSize), Gravity.RIGHT);

            View mCustomView = mInflater.inflate(R.layout.circle, null);
            mActionBar.setCustomView(mCustomView, layoutParams);

            compDelvrd = (TextView) mCustomView.findViewById(R.id.Compdeliverd);

            ComRemain = (TextView) mCustomView.findViewById(R.id.remaining);

            mActionBar.setCustomView(mCustomView);
            mActionBar.setDisplayShowCustomEnabled(true);

            // isDeviceonline();

        } catch (Exception e) {
            System.out.println(e);
        }


        ofd = (TextView) findViewById(R.id.ofd);
        attempted = (TextView) findViewById(R.id.attemped);
        delivered = (TextView) findViewById(R.id.delivered);
        exceptions = (TextView) findViewById(R.id.excep);
        productivity = (TextView) findViewById(R.id.productivity);
        complaint = (TextView) findViewById(R.id.complaint);


        if (savedInstanceState != null)
            setSavedInstance(savedInstanceState);

        //setProductivitytext();

        if (!GlobalVar.isMyServiceRunning(com.naqelexpress.naqelpointer.Activity.GoogleApiFusedLocation.LocationService.class,
                getApplicationContext())) {
            startService(new Intent(getBaseContext(),
                    com.naqelexpress.naqelpointer.Activity.GoogleApiFusedLocation.LocationService.class));
        }

        SharedPreferences sharedpreferences = getSharedPreferences("naqelSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("mc", true);
        editor.commit();

        final DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        Rating = dbConnections.getCourierRating(GlobalVar.GV().EmployID, getApplicationContext());

        setValidationText();

        if (GlobalVar.ValidateAutomacticDate(getApplicationContext())) {
            dbConnections.DeleteFacilityLoggedIn(getApplicationContext());
            dbConnections.DeleteExsistingLogin(getApplicationContext());
            dbConnections.DeleteAllSyncData(getApplicationContext());
            dbConnections.deleteDenied(getApplicationContext());
        }
        Cursor resultOnlineValidationfile = dbConnections.Fill("select ValidDate from RtoReq Limit 1", getApplicationContext());
        if (resultOnlineValidationfile.getCount() > 0) {
            resultOnlineValidationfile.moveToFirst();
            String upto = "Upto : " + resultOnlineValidationfile.getString(resultOnlineValidationfile.getColumnIndex("ValidDate")) + " 15:00"; //16:30
            if (GlobalVar.GV().ValidateAutomacticDate(getApplicationContext())) {
                if (!GlobalVar.GV().IsAllowtoScan(upto.replace("Upto : ", ""))) { //validupto.getText().toString()
                    if (!GlobalVar.GV().isFortesting)
                        dbConnections.deleteOnlineValidationfile(getApplicationContext());
                }
            }
        }

        resultOnlineValidationfile.close();

        btnSignOut = (FloatingActionButton) findViewById(R.id.btnSignOut);

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainPageActivity.this);
                builder.setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {

                                Logout();
                            }
                        }).setNegativeButton("Cancel", null).setCancelable(false);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });


        LoadUserSettings();

        Cursor menuresult = dbConnections.Fill("select * from UserME where StatusID <> 3 and EmployID = " +
                GlobalVar.GV().EmployID, getApplicationContext());

        if (menuresult.getCount() > 0) {
            menuresult.moveToFirst();
            menu = menuresult.getInt(menuresult.getColumnIndex("Menu"));

        } else {
            menuresult.close();
            Logout();
        }
        menuresult.close();

        Cursor result = dbConnections.Fill("select * from UserME where StatusID <> 3 and EmployID = " +
                GlobalVar.GV().EmployID, getApplicationContext());
        if (result.getCount() > 0) {
            result.moveToFirst();
            devision = result.getString(result.getColumnIndex("Division"));
            menu = result.getInt(result.getColumnIndex("Menu"));

            if (devision.equals("0")) {

                // GetMasterData asynthread = new GetMasterData();
                //  StartAsyncTaskInParallel(asynthread);
                Logout();

            } else {
                int vc = dbConnections.GetAppVersion(getApplicationContext());
                int versioncode = GlobalVar.VersionCode(getApplicationContext());
                version.setText("Versioncode :-" + String.valueOf(versioncode));
                //if (devision.equals("Courier")) {
                if (vc != versioncode && GlobalVar.GV().EmployID != 19127) {
                    version.setText("Version :-" + " OLD ");
                    // GlobalVar.updateApp(MainPageActivity.this);
                    // GetMasterData asynthread = new GetMasterData();
                    // StartAsyncTaskInParallel(asynthread);
                    //deleteExsistinguser();
                    VersionMismatch("Info", "Version is mismatch , kindly Logout & Login again");
                } else {
                    LoadMenu();
                    version.setText("Version :-" + " NEW ");
                }

            }
        }

        Cursor EmployeeInfo = dbConnections.Fill("SELECT *  FROM  EmployInfo where EmpID = " + GlobalVar.GV().EmployID, getApplicationContext());

        if (EmployeeInfo.getCount() == 0 && GlobalVar.GV().EmployID != 19127 && GlobalVar.GV().EmployID != 17742) { // && GlobalVar.GV().EmployID != 17099

            //Intent intent = new Intent(MainPageActivity.this, EmployeeInformation.class);
            //startActivity(intent);

        }

        if (devision.equals("Express")) {
            try {
                boolean isvalid = dbConnections.isInCabCheckList(getApplicationContext(), GlobalVar.GV().EmployID);
                if (isvalid && GlobalVar.GV().EmployID != 19127) {
                    Intent chat = new Intent(getApplicationContext(), IncCabChecklist.class);
                    chat.putExtra("close", 0);
                    startActivityForResult(chat, 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        dbConnections.close();

        // updateApp();

        if (android.os.Build.VERSION.SDK_INT >= 29)
            if (GlobalVar.GV().isPermissionEnabled(Manifest.permission.ACCESS_BACKGROUND_LOCATION, MainPageActivity.this)
                    == PackageManager.PERMISSION_DENIED) {

                ActivityCompat.requestPermissions(
                        MainPageActivity.this,
                        new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        15
                );
            }

    }


    HashMap<Integer, Integer> itemposition = new HashMap<>();

    private void LoadMenu() {
        //total 28 cases for Menus

        user.setText("Welcome to Mr." + String.valueOf(GlobalVar.GV().EmployID));
        // menu = 1;
        if (devision.equals("Courier")) {
            if (menu == 1) {
                cellTitle = new String[15];
                cellIcon = new int[15];
            } else {
                cellTitle = new String[14];
                cellIcon = new int[14];
            }
        } else if (devision.equals("IRS")) {
            cellTitle = new String[5];
            cellIcon = new int[5];
        } else if (devision.equals("Express")) {
            if (menu == 0) {
                cellTitle = new String[21];
                cellIcon = new int[21];
            } else {
                cellTitle = new String[27];
                cellIcon = new int[27];
            }
        }

        if (devision.equals("Courier")) {
            cellTitle[0] = getResources().getString(R.string.DeliverySheetActivity);//CBU
            cellTitle[1] = getResources().getString(R.string.MyRouteActivity);//CBU

            cellTitle[2] = getResources().getString(R.string.PickUpActivity);//CBU 5
            cellTitle[3] = getResources().getString(R.string.WaybillMeasurementActivity); //6
            cellTitle[4] = getResources().getString(R.string.SettingsActivity);//CBU 7
            //cellTitle[8] = getResources().getString(R.string.DataSync);
            cellTitle[5] = getResources().getString(R.string.CODChecking);//CBU 8
            cellTitle[6] = getResources().getString(R.string.PendingCOD);//CBU 9
            cellTitle[7] = "Validate DS";//17
            cellTitle[8] = "Hisory";//13
            cellTitle[9] = "Terminal Handling";//13
            cellTitle[10] = "Booking List";//13
            cellTitle[11] = "Change Password";//13
            cellTitle[12] = "DeliverySheet by NCL";//13
            cellTitle[13] = "Rating";//13

            if (menu == 1)
                cellTitle[14] = "Skip Waybill in RL";//13 //Skip Waybill in RouteLine Seq

            itemposition.put(0, 0);
            itemposition.put(1, 1); // 1 is old screen
            itemposition.put(2, 5);
            itemposition.put(3, 6);
            itemposition.put(4, 7);
            itemposition.put(5, 8);
            itemposition.put(6, 9);
            itemposition.put(7, 17);
            itemposition.put(8, 13);
            itemposition.put(9, 23);
            itemposition.put(10, 11);
            itemposition.put(11, 24);
            itemposition.put(12, 28);
            itemposition.put(13, 30);
            if (menu == 1)
                itemposition.put(14, 29);
        }


        if (devision.equals("Express")) {
            if (menu == 1) {
                cellTitle[0] = getResources().getString(R.string.BookingList);//11
                cellTitle[1] = getResources().getString(R.string.PickUpActivity);//CBU 5
                cellTitle[2] = getResources().getString(R.string.AtOrigin);//12
                cellTitle[3] = getResources().getString(R.string.ld);//14
                cellTitle[4] = "Arrived Dest";//15
                cellTitle[5] = getResources().getString(R.string.DeliverySheetActivity);//CBU
                cellTitle[6] = getResources().getString(R.string.MyRouteActivity);//CBU
                cellTitle[7] = getResources().getString(R.string.DeliveryActivity);//CBU
                cellTitle[8] = getResources().getString(R.string.MultiDeliveryActivity);
                cellTitle[9] = getResources().getString(R.string.NotDeliveredActivity);//CBU 4
                cellTitle[10] = "Night Stock";//19
                cellTitle[11] = getResources().getString(R.string.WaybillMeasurementActivity); //6
                cellTitle[12] = "CBM";//24
                cellTitle[13] = "Sync Trip";//19
                cellTitle[14] = "Incident";//19
                cellTitle[15] = getResources().getString(R.string.CODChecking);//CBU 8
                cellTitle[16] = getResources().getString(R.string.PendingCOD);//CBU 9
                cellTitle[17] = "Contacts";//16
                cellTitle[18] = "History";//13
                cellTitle[19] = getResources().getString(R.string.SettingsActivity);//CBU 7
                cellTitle[20] = "Change Password";//CBU 7
                cellTitle[21] = "Chat";//CBU 7
                cellTitle[22] = "KPI";//CBU 7
                cellTitle[23] = "InCab CheckList";//CBU 7
                cellTitle[24] = "Fuel Model";//CBU 7
                cellTitle[25] = "Scan Waybills";//CBU 7
//                cellTitle[26] = "Inter City";//CBU 7

            } else {

                cellTitle[0] = getResources().getString(R.string.BookingList);//11
                cellTitle[1] = getResources().getString(R.string.PickUpActivity);//CBU 5
                cellTitle[2] = getResources().getString(R.string.DeliverySheetActivity);//CBU
                cellTitle[3] = getResources().getString(R.string.MyRouteActivity);//CBU
                cellTitle[4] = getResources().getString(R.string.DeliveryActivity);//CBU
                cellTitle[5] = getResources().getString(R.string.MultiDeliveryActivity);
                cellTitle[6] = getResources().getString(R.string.NotDeliveredActivity);//CBU 4
                cellTitle[7] = getResources().getString(R.string.WaybillMeasurementActivity); //6
                cellTitle[8] = "CBM";//CBU 7
                cellTitle[9] = "Incident";//19
                cellTitle[10] = getResources().getString(R.string.CODChecking);//CBU 8
                cellTitle[11] = getResources().getString(R.string.PendingCOD);//CBU 9
                cellTitle[12] = "Contacts";//16
                cellTitle[13] = "Hisory";//13
                cellTitle[14] = getResources().getString(R.string.SettingsActivity);//CBU 7
                cellTitle[15] = "Change Password";//CBU 7
                cellTitle[16] = "Chat";//CBU 7
                cellTitle[17] = "KPI";//CBU 7
                cellTitle[18] = "InCab CheckList";//CBU 7
                cellTitle[19] = "Fuel Model";//CBU 7
                cellTitle[20] = "Scan Waybills";//CBU 7
            }

            if (menu == 1) {
                itemposition.put(0, 11);
                itemposition.put(1, 5); //18
                itemposition.put(2, 12);
                itemposition.put(3, 14);
                itemposition.put(4, 15);
                itemposition.put(5, 0);
                itemposition.put(6, 1);
                itemposition.put(7, 2);
                itemposition.put(8, 3);
                itemposition.put(9, 4);
                itemposition.put(10, 21);
                itemposition.put(11, 6);
                itemposition.put(12, 32);
                itemposition.put(13, 22);
                itemposition.put(14, 20);
                itemposition.put(15, 8);
                itemposition.put(16, 9);
                itemposition.put(17, 16);
                itemposition.put(18, 13);
                itemposition.put(19, 7);
                itemposition.put(20, 24);
                itemposition.put(21, 25);
                itemposition.put(22, 26);
                itemposition.put(23, 27);
                itemposition.put(24, 31);
                itemposition.put(25, 33);
//                itemposition.put(26, 34);

            } else {
                itemposition.put(0, 11);
                itemposition.put(1, 5); //18
                itemposition.put(2, 0);
                itemposition.put(3, 1);
                itemposition.put(4, 2);
                itemposition.put(5, 3);
                itemposition.put(6, 4);
                itemposition.put(7, 6);
                itemposition.put(8, 20);
                itemposition.put(9, 8);
                itemposition.put(10, 9);
                itemposition.put(11, 16);
                itemposition.put(12, 32);
                itemposition.put(13, 13);
                itemposition.put(14, 7);
                itemposition.put(15, 24);
                itemposition.put(16, 25);
                itemposition.put(17, 26);
                itemposition.put(18, 27);
                itemposition.put(19, 31);
                itemposition.put(20, 33);
            }

        }

        if (devision.equals("IRS")) {
            cellTitle[0] = getResources().getString(R.string.CustomsClearance);
            cellTitle[1] = getResources().getString(R.string.PickUpActivity);//CBU 5;
            cellTitle[2] = "Change Password";//CBU 5;
            cellTitle[3] = "History";//13
            cellTitle[4] = "Terminal Handling";//13

            itemposition.put(0, 10);
            itemposition.put(1, 5);
            itemposition.put(2, 24);
            itemposition.put(3, 13);
            itemposition.put(4, 23);
        }

        if (devision.equals("Courier") || devision.equals("Express")) {
            cellIcon[0] = R.drawable.deliverysheet; //CBU
            cellIcon[1] = R.drawable.maplist; //CBU
            cellIcon[2] = R.drawable.pickup; //CBU
            cellIcon[3] = R.drawable.waybillmeasurement; //CBU
            cellIcon[4] = R.drawable.settings; //CBU
            cellIcon[5] = R.drawable.money; //CBU
            cellIcon[6] = R.drawable.pendingcod; //CBU
            cellIcon[7] = R.drawable.closetrip; //CBU
            cellIcon[8] = R.drawable.pendingdata; //CBU
            cellIcon[9] = R.drawable.terminalhandeling; //CBU
            cellIcon[10] = R.drawable.contacts; //CBU
            cellIcon[11] = R.drawable.money; //CBU
            cellIcon[12] = R.drawable.deliverysheet; //CBU
            cellIcon[13] = android.R.drawable.star_big_off; //CBU

            if (menu == 1)
                cellIcon[14] = R.drawable.recyclebin; //CBU
        }
        if (devision.equals("Express")) {

            if (menu == 1) {
                cellIcon[0] = R.drawable.contacts;
                cellIcon[1] = R.drawable.pickup; //CBU
                cellIcon[2] = R.drawable.deliverysheet; //CBU
                cellIcon[3] = R.drawable.ld; //CBU
                cellIcon[4] = R.drawable.importtruck; //CBU
                cellIcon[5] = R.drawable.deliverysheet; //CBU
                cellIcon[6] = R.drawable.maplist; //CBU
                cellIcon[7] = R.drawable.delivery; //CBU
                cellIcon[8] = R.drawable.deliverysheet; //CBU
                cellIcon[9] = R.drawable.notdelivered; //CBU
                cellIcon[10] = R.drawable.delivery; //CBU
                cellIcon[11] = R.drawable.waybillmeasurement; //CBU
                cellIcon[12] = R.drawable.bluebox; //CBU
                cellIcon[13] = R.drawable.datasync; //CBU
                cellIcon[14] = R.drawable.delivery; //CBU
                cellIcon[15] = R.drawable.money; //CBU
                cellIcon[16] = R.drawable.pendingcod; //CBU
                cellIcon[17] = R.drawable.contacts; //CBU
                cellIcon[18] = R.drawable.pendingdata; //CBU
                cellIcon[19] = R.drawable.settings; //CBU
                cellIcon[20] = R.drawable.contacts; //CBU
                cellIcon[21] = R.drawable.contacts; //CBU
                cellIcon[22] = R.drawable.customclearence; //CBU
                cellIcon[23] = R.drawable.car; //CBU
                cellIcon[24] = R.drawable.fuel; //CBU
                cellIcon[25] = R.drawable.scanwaybill; //CBU
//                cellIcon[26] = R.drawable.contacts; //CBU
            } else {
                cellIcon[0] = R.drawable.contacts;
                cellIcon[1] = R.drawable.pickup; //CBU
                cellIcon[2] = R.drawable.deliverysheet; //CBU
                cellIcon[3] = R.drawable.maplist; //CBU
                cellIcon[4] = R.drawable.delivery; //CBU
                cellIcon[5] = R.drawable.deliverysheet; //CBU
                cellIcon[6] = R.drawable.notdelivered; //CBU
                cellIcon[7] = R.drawable.waybillmeasurement; //CBU
                cellIcon[8] = R.drawable.delivery; //CBU
                cellIcon[9] = R.drawable.money; //CBU
                cellIcon[10] = R.drawable.pendingcod; //CBU
                cellIcon[11] = R.drawable.contacts; //CBU
                cellIcon[12] = R.drawable.bluebox; //CBU
                cellIcon[13] = R.drawable.pendingdata; //CBU
                cellIcon[14] = R.drawable.settings; //CBU
                cellIcon[15] = R.drawable.contacts; //CBU
                cellIcon[16] = R.drawable.contacts; //CBU
                cellIcon[17] = R.drawable.customclearence; //CBU
                cellIcon[18] = R.drawable.car; //CBU
                cellIcon[19] = R.drawable.fuel; //CBU
                cellIcon[20] = R.drawable.scanwaybill; //CBU
            }
        }
        if (devision.equals("IRS")) {
            cellIcon[0] = R.drawable.customclearence; //CBU
            cellIcon[1] = R.drawable.pickup; //CBU
            cellIcon[2] = R.drawable.contacts; //CBU
            cellIcon[3] = R.drawable.pendingdata; //CBU
            cellIcon[4] = R.drawable.terminalhandeling; //CBU
        }
        if (devision.length() > 0 && !devision.equals("0"))
            MainPageNavigation();

        if (devision.equals("Express") || devision.equals("IRS")) {
            hidevisibleproductivity();
        } else if (GlobalVar.GV().IsTerminalApp) {
            hidevisibleproductivity();
        } else if (devision.equals("Courier")) {
            enablevisibleproductivity();
        }
    }

    private void hidevisibleproductivity() {
        tl.setVisibility(View.GONE);
        tl1.setVisibility(View.GONE);
        ratingBar.setVisibility(View.GONE);
    }

    private void enablevisibleproductivity() {
        ratingBar.setRating(Rating);
        //ratingBar.setVisibility(View.VISIBLE);
        tl.setVisibility(View.VISIBLE);
        tl1.setVisibility(View.VISIBLE);
    }

    private void GetMasterData() {
        //new Thread(new Task()).start();


        for (int i = 0; i < 1; i++) {
            GetMasterData asynthread = new GetMasterData();
            StartAsyncTaskInParallel(asynthread);
            asyncTasks.add(asynthread);
        }


    }

//    class Task implements Runnable {
//
//        @Override
//
//        public void run() {
//            progressDialog = ProgressDialog.show(MainPageActivity.this, "Please wait.", "Bringing Master Details.", true);
//            boolean loop = false;
//            while (!loop) {
//                if (GlobalVar.gs && GlobalVar.dsl && GlobalVar.cptl && GlobalVar.cptdl && GlobalVar.cptddl && GlobalVar.nnvdl) {
//                    if (progressDialog != null)
//                        progressDialog.dismiss();
//
//                    break;
//                }
//                try {
//                    Thread.sleep(10000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//
//
//        }
//    }


    private void StartAsyncTaskInParallel(GetMasterData asynthread) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asynthread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            asynthread.execute("");

    }

    private void setProductivitytext() {

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        Cursor result = dbConnections.Fill("select * from Productivity where Date = '" + GlobalVar.getDate() + "' and Attempted = " + GlobalVar.GV().EmployID, getApplicationContext());
        double ofd_ = 0, attempt = 0, delivrd = 0, excep = 0, producvty = 0.0;

        if (result.getCount() > 0) {
            result.moveToFirst();
            do {
                ofd_ = result.getInt(result.getColumnIndex("OFD"));
                delivrd = result.getInt(result.getColumnIndex("Delivered"));
                excep = result.getInt(result.getColumnIndex("Exceptions"));
                attempt = delivrd + excep;
            }
            while (result.moveToNext());
        }

        ofd.setText(String.valueOf(ofd_));
        attempted.setText(String.valueOf(attempt));
        delivered.setText(String.valueOf(delivrd));
        exceptions.setText(String.valueOf(excep));

        if (ofd_ != 0 && delivrd != 0)
            producvty = (delivrd / ofd_) * 100;
        productivity.setText(String.valueOf(String.format("%.2f", producvty)));


        int comp = 0, attempt1 = 0, delivrd1 = 0, excep1 = 0, remain = 0;

        result = dbConnections.Fill("select * from Complaint where Date = '" + GlobalVar.getDate() + "' and Attempted = " + GlobalVar.GV().EmployID, getApplicationContext());
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {
                comp = result.getInt(result.getColumnIndex("TotalComp"));
                delivrd1 = result.getInt(result.getColumnIndex("Delivered"));
                excep1 = result.getInt(result.getColumnIndex("Exceptions"));
                remain = comp - (delivrd1 + excep1);
            }
            while (result.moveToNext());
        }

        complaint.setText("Req and Complaints : " + String.valueOf(comp) + " / " + excep1);
        ComRemain.setText(String.valueOf(remain));
        compDelvrd.setText(String.valueOf(delivrd1));
    }

    private void setValidationText() {
        //Riyam
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);

        if (GlobalVar.GV().IsTerminalApp || devision.equals("IRS")) {

            try {
                llValidation.setVisibility(View.VISIBLE);
                String uploadDate = "Not Downloaded";
                String count = "0";

                if (devision.equals("Courier") && !dbConnections.isValidationFileEmpty(getApplicationContext())) {
                    uploadDate = dbConnections.getOnlineValidationUploadDate(getApplicationContext());
                    count = String.valueOf(dbConnections.getValidationFileCount(false, getApplicationContext()));
                } else if (devision.equals("IRS") && !dbConnections.isOnlineValidationFileEmpty(getApplicationContext())) {
                    uploadDate = dbConnections.getOnlineValidationUploadDate(getApplicationContext());
                    count = String.valueOf(dbConnections.getValidationFileCount(true, getApplicationContext()));
                }
                tvValidationDate.setText(uploadDate);
                tvValidationCountBody.setText(count);

                dbConnections.close();
            } catch (Exception e) {

            }
        }

        dbConnections.close();
    }


    private void LoadUserSettings() {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);

        int Count = dbConnections.getCount("UserSettings", " EmployID = " + String.valueOf(GlobalVar.GV().EmployID), getApplicationContext());
        if (Count > 0) {
            Cursor result = dbConnections.Fill("select * from UserSettings where EmployID = " + String.valueOf(GlobalVar.GV().EmployID), getApplicationContext());
            if (result.getCount() > 0) {
                result.moveToFirst();
                do {
                    int ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                    int EmployID = Integer.parseInt(result.getString(result.getColumnIndex("EmployID")));
                    String IPAddress = result.getString(result.getColumnIndex("IPAddress"));
                    boolean ShowScaningCamera = Boolean.parseBoolean(result.getString(result.getColumnIndex("ShowScaningCamera")));
                    DateTime dateTime = DateTime.now().withFieldAdded(DurationFieldType.days(), -30);
                    dateTime = DateTime.parse(result.getString(result.getColumnIndex("LastBringMasterData")));

                    GlobalVar.GV().currentSettings = new UserSettings(ID, EmployID, IPAddress, ShowScaningCamera, dateTime);
                }
                while (result.moveToNext());
            }
        } else {
            GlobalVar.GV().currentSettings = new UserSettings("212.93.160.150", true);
            dbConnections.InsertSettings(GlobalVar.GV().currentSettings, getApplicationContext());
            GlobalVar.GV().currentSettings.ID = dbConnections.getMaxID("UserSettings", getApplicationContext());
        }
        dbConnections.close();
    }


    private boolean LoadDeliverysheet() {
        String division = GlobalVar.GV().getDivisionID(getApplicationContext(), GlobalVar.GV().EmployID);
        if (division.equals("Express") || GlobalVar.GV().EmployID == 19127)
            return true;

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        Cursor result = dbConnections.Fill("select * from MyRouteShipments Where DDate = '" +
                GlobalVar.getDate() + "' and EmpID = " + GlobalVar.GV().EmployID, getApplicationContext());
        if (result.getCount() > 0)
            return true;
        else
            return false;

    }

    private boolean GetDivision() {
//        if (GlobalVar.GV().EmployID == 19127)
//            return false;
        String division = GlobalVar.GV().getDivisionID(getApplicationContext(), GlobalVar.GV().EmployID);

        if (division.equals("Express"))
            return false;
        else
            return true;

    }

    public void MainPageNavigation() {
        gridView = (GridView) findViewById(R.id.gridView);
        MainPageCellAdapter adapter = new MainPageCellAdapter(MainPageActivity.this, cellIcon, cellTitle);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position = itemposition.get(position);
                switch (position) {
                    case 0:
                        if (GlobalVar.locationEnabled(getApplicationContext())) {
                            if (GlobalVar.AskPermission_Camera(MainPageActivity.this, 6)) {
                                if (!GetDivision()) {
                                    if (VersionMatct()) {
                                        Intent deliverySheet = new Intent(getApplicationContext(), DeliverySheetActivity.class);
                                        startActivity(deliverySheet);
                                    } else {
                                        GlobalVar.GV().ShowDialog(MainPageActivity.this, "Info.", "Kindly Update our Latest Version.(Logout and Login again)"
                                                , true);
                                    }
                                } else {
                                    if (VersionMatct()) {
                                        Intent deliverySheet = new Intent(getApplicationContext(),
                                                com.naqelexpress.naqelpointer.Activity.OFDPieceLevel.DeliverySheetActivity.class);
//                                    Intent deliverySheet = new Intent(getApplicationContext(), DeliverySheetActivity.class);
                                        startActivity(deliverySheet);
                                    } else {
                                        GlobalVar.GV().ShowDialog(MainPageActivity.this, "Info.", "Kindly Update our Latest Version.(Logout and Login again)"
                                                , true);
                                    }
                                }
                            }
                        } else
                            GlobalVar.enableLocationSettings(MainPageActivity.this);

                        break;
                    case 1:
                        if (GlobalVar.locationEnabled(getApplicationContext())) {
                            if (GlobalVar.AskPermission_Location(MainPageActivity.this, 1)) {
                                if (!GlobalVar.isMyServiceRunning(LocationService.class, getApplicationContext())) {
                                    startService(new Intent(getBaseContext(),
                                            LocationService.class));
                                }
                                if (GlobalVar.AskPermission_Contcatcs(MainPageActivity.this, 111)) {
                                    if (VersionMatct()) {
                                        if (!GetDivision()) {
                                            Intent mapList = new Intent(getApplicationContext(), MyRouteActivity.class);
                                            startActivity(mapList);
                                        } else {
//                                            Intent mapList = new Intent(getApplicationContext(), com.naqelexpress.naqelpointer.Activity.MyrouteCBU.MyRouteActivity.class);
                                            //Intent mapList = new Intent(getApplicationContext(), com.naqelexpress.naqelpointer.Activity.MyrouteCBU.MyRouteActivity_Complaince.class);
                                            //Active one
                                            Intent mapList = new Intent(getApplicationContext(), com.naqelexpress.naqelpointer.Activity.MyrouteCBU.MyRouteActivity_Complaince_GroupbyPhn.class);
                                            //Intent mapList = new Intent(getApplicationContext(), MyRouteActivity_PaperlessDS.class);
                                            startActivity(mapList);
                                        }
                                    } else
                                        GlobalVar.ShowDialog(MainPageActivity.this, "Info.", "Kindly Update our Latest Version.(Logout and Login again)"
                                                , true);

                                }
                            }
                        } else
                            GlobalVar.enableLocationSettings(MainPageActivity.this);
                        break;
                    case 2:
                        if (GlobalVar.locationEnabled(getApplicationContext())) {
                            if (GlobalVar.AskPermission_Camera(MainPageActivity.this, 2)) {

                                if (LoadDeliverysheet()) {
                                    if (VersionMatct()) {
                                        Intent delivery = new Intent(getApplicationContext(), DeliveryActivity.class);
                                        startActivity(delivery);
                                    } else {
                                        GlobalVar.GV().ShowDialog(MainPageActivity.this, "Info.", "Kindly Update our Latest Version.(Logout and Login again)"
                                                , true);
                                    }
                                } else {
                                    GlobalVar.GV().ShowDialog(MainPageActivity.this, "Info.", "Kindly Load DeliverySheet,Press MyRoute"
                                            , true);
                                }

                            }
                        } else
                            GlobalVar.enableLocationSettings(MainPageActivity.this);
                        break;
                    case 3:
                        if (GlobalVar.locationEnabled(getApplicationContext())) {
                            if (GlobalVar.AskPermission_Camera(MainPageActivity.this, 10)) {
                                if (LoadDeliverysheet()) {
                                    if (VersionMatct()) {
                                        Intent multiDelivery = new Intent(getApplicationContext(), MultiDeliveryActivity.class);
                                        startActivity(multiDelivery);
                                    } else {
                                        GlobalVar.GV().ShowDialog(MainPageActivity.this, "Info.", "Kindly Update our Latest Version.(Logout and Login again)"
                                                , true);
                                    }
                                } else {
                                    GlobalVar.GV().ShowDialog(MainPageActivity.this, "Info.", "Kindly Load DeliverySheet,Press MyRoute"
                                            , true);
                                }
                            }
                        } else
                            GlobalVar.enableLocationSettings(MainPageActivity.this);
                        break;
                    case 4:
                        if (GlobalVar.locationEnabled(getApplicationContext())) {
                            if (GlobalVar.AskPermission_Camera(MainPageActivity.this, 3)) {
                                if (LoadDeliverysheet()) {
                                    if (GetDivision()) {
//                                        Intent notDelivered = new Intent(getApplicationContext(),
//                                                com.naqelexpress.naqelpointer.Activity.NotDeliveredCBU.NotDeliveredActivity.class);
                                        Intent notDelivered = new Intent(getApplicationContext(), NotDeliveredActivity.class);
                                        startActivity(notDelivered);
                                    } else {
                                        if (VersionMatct()) {
                                            Intent notDelivered = new Intent(getApplicationContext(), NotDeliveredActivity.class);
                                            startActivity(notDelivered);
                                        } else {
                                            GlobalVar.GV().ShowDialog(MainPageActivity.this, "Info.", "Kindly Update our Latest Version.(Logout and Login again)"
                                                    , true);
                                        }
                                    }
                                } else {
                                    GlobalVar.GV().ShowDialog(MainPageActivity.this, "Info.", "Kindly Load DeliverySheet,Press MyRoute"
                                            , true);
                                }
                            }
                        } else
                            GlobalVar.enableLocationSettings(MainPageActivity.this);
                        break;
                    case 5:
                        if (GlobalVar.locationEnabled(getApplicationContext())) {
                            if (GlobalVar.AskPermission_Camera(MainPageActivity.this, 4)) {
                                if (GetDivision()) {
                                    if (VersionMatct()) {
                                        Intent pickup = new Intent(getApplicationContext(),
                                                com.naqelexpress.naqelpointer.Activity.PickupPieceLevel.PickUpActivity.class);
//                                    Intent pickup = new Intent(getApplicationContext(), PickUpActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("class", "MainPage");
                                        pickup.putExtras(bundle);
                                        startActivity(pickup);
                                    } else {
                                        GlobalVar.GV().ShowDialog(MainPageActivity.this, "Info.", "Kindly Update our Latest Version.(Logout and Login again)"
                                                , true);
                                    }
                                } else {
                                    if (VersionMatct()) {

                                        Intent pickup = new Intent(getApplicationContext(), PickUpActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("class", "MainPage");
                                        pickup.putExtras(bundle);
                                        startActivity(pickup);
                                    } else {
                                        GlobalVar.GV().ShowDialog(MainPageActivity.this, "Info.", "Kindly Update our Latest Version.(Logout and Login again)"
                                                , true);
                                    }
                                }
                            }
                        } else
                            GlobalVar.enableLocationSettings(MainPageActivity.this);
                        break;
                    case 6:
                        if (GlobalVar.locationEnabled(getApplicationContext())) {
                            if (VersionMatct()) {
                                Intent waybillMeasurement = new Intent(getApplicationContext(), WaybillMeasurementActivity.class);
                                startActivity(waybillMeasurement);
                            } else {
                                GlobalVar.GV().ShowDialog(MainPageActivity.this, "Info.", "Kindly Update our Latest Version.(Logout and Login again)"
                                        , true);
                            }
                        } else
                            GlobalVar.enableLocationSettings(MainPageActivity.this);
                        break;
                    case 7:
                        if (GlobalVar.locationEnabled(getApplicationContext())) {
                            Intent settings = new Intent(getApplicationContext(), SettingActivity.class);
                            startActivity(settings);
                        } else
                            GlobalVar.enableLocationSettings(MainPageActivity.this);
                        break;

                    case 8:
                        if (GlobalVar.locationEnabled(getApplicationContext())) {
                            Intent checkCOD = new Intent(getApplicationContext(), CODCheckingActivity.class);
                            startActivity(checkCOD);
                        } else
                            GlobalVar.enableLocationSettings(MainPageActivity.this);
                        break;
                    case 9:
                        if (GlobalVar.locationEnabled(getApplicationContext())) {
                            Intent pendingMoney = new Intent(getApplicationContext(), PendingMoneyActivity.class);
                            startActivity(pendingMoney);
                        } else
                            GlobalVar.enableLocationSettings(MainPageActivity.this);
                        break;
                    case 10:
                        if (GlobalVar.locationEnabled(getApplicationContext())) {
                            if (VersionMatct()) {
                                Intent checkPoint = new Intent(getApplicationContext(), CheckPointsActivity.class);
                                startActivity(checkPoint);
                            } else {
                                GlobalVar.GV().ShowDialog(MainPageActivity.this, "Info.", "Kindly Update our Latest Version.(Logout and Login again)"
                                        , true);
                            }
                        } else
                            GlobalVar.enableLocationSettings(MainPageActivity.this);
                        break;
                    case 11:
                        if (GlobalVar.locationEnabled(getApplicationContext())) {
                            if (VersionMatct()) {
                                if (!GetDivision()) {
                                    Intent bookingList = new Intent(getApplicationContext(), BookingList.class);
                                    startActivity(bookingList);
                                } else {
//                                    Intent bookingList = new Intent(getApplicationContext(), com.naqelexpress.naqelpointer.Activity.BookingCBU.BookingList.class);
                                    Intent bookingList = new Intent(getApplicationContext(), com.naqelexpress.naqelpointer.Activity.SPAsrRegularBooking.BookingList.class);
                                    startActivity(bookingList);
                                }
                            } else {
                                GlobalVar.GV().ShowDialog(MainPageActivity.this, "Info.", "Kindly Update our Latest Version.(Logout and Login again)"
                                        , true);
                            }
                        } else
                            GlobalVar.enableLocationSettings(MainPageActivity.this);
                        break;
                    case 12:
                        if (GlobalVar.locationEnabled(getApplicationContext())) {
                            if (GlobalVar.AskPermission_Camera(MainPageActivity.this, 5)) {
//                            Intent atorigin = new Intent(getApplicationContext(), ArrivedatDestination.class);
                                if (VersionMatct()) {
                                    Intent atorigin = new Intent(getApplicationContext(), AtOrigin.class);
                                    startActivity(atorigin);
                                } else {
                                    GlobalVar.GV().ShowDialog(MainPageActivity.this, "Info.", "Kindly Update our Latest Version.(Logout and Login again)"
                                            , true);
                                }
                            }
                        } else
                            GlobalVar.enableLocationSettings(MainPageActivity.this);
                        break;
                    case 13:
                        if (GlobalVar.locationEnabled(getApplicationContext())) {
                            Intent history = new Intent(getApplicationContext(), History.class);
                            startActivity(history);
                        } else
                            GlobalVar.enableLocationSettings(MainPageActivity.this);
                        break;
                    case 14:
                        if (GlobalVar.locationEnabled(getApplicationContext())) {
                            if (VersionMatct()) {
                                showPopup(MainPageActivity.this, p, 0);
                            } else {
                                GlobalVar.GV().ShowDialog(MainPageActivity.this, "Info.", "Kindly Update our Latest Version.(Logout and Login again)"
                                        , true);
                            }
                        } else
                            GlobalVar.enableLocationSettings(MainPageActivity.this);
                        break;
                    case 15:
                        if (GlobalVar.locationEnabled(getApplicationContext())) {
                            if (VersionMatct()) {
                                showPopup(MainPageActivity.this, p, 1);
                            } else {
                                GlobalVar.GV().ShowDialog(MainPageActivity.this, "Info.", "Kindly Update our Latest Version.(Logout and Login again)"
                                        , true);
                            }
                        } else
                            GlobalVar.enableLocationSettings(MainPageActivity.this);
                        break;
                    case 16:
                        if (GlobalVar.locationEnabled(getApplicationContext())) {
                            Intent contact = new Intent(getApplicationContext(), Contact.class);
                            startActivity(contact);
                        } else
                            GlobalVar.enableLocationSettings(MainPageActivity.this);
                        break;
                    case 17:
                        if (GlobalVar.locationEnabled(getApplicationContext())) {
                            Intent cvds = new Intent(getApplicationContext(), ValidationDS.class);
                            startActivity(cvds);
                        } else
                            GlobalVar.enableLocationSettings(MainPageActivity.this);
                        break;
                    case 18:
                        if (GlobalVar.locationEnabled(getApplicationContext())) {
                            if (GlobalVar.AskPermission_Location(MainPageActivity.this, 8)) {
                                if (!GlobalVar.isMyServiceRunning(LocationService.class, getApplicationContext())) {
                                    startService(new Intent(getBaseContext(),
                                            LocationService.class));
                                }
                                if (GlobalVar.AskPermission_Contcatcs(MainPageActivity.this, 9)) {
                                    if (VersionMatct()) {
                                        Intent eburoute = new Intent(getApplicationContext(), DeliverySheet.class);
                                        startActivity(eburoute);
                                    } else {
                                        GlobalVar.GV().ShowDialog(MainPageActivity.this, "Info.", "Kindly Update our Latest Version.(Logout and Login again)"
                                                , true);
                                    }
                                }
                            }
                        } else
                            GlobalVar.enableLocationSettings(MainPageActivity.this);
                        break;
                    case 19:
                        if (GlobalVar.locationEnabled(getApplicationContext())) {
                            if (GlobalVar.AskPermission_Location(MainPageActivity.this, 11)) {
                                if (!GlobalVar.isMyServiceRunning(LocationService.class, getApplicationContext())) {
                                    startService(new Intent(getBaseContext(),
                                            LocationService.class));
                                }
                                if (GlobalVar.AskPermission_Contcatcs(MainPageActivity.this, 12)) {
                                    Intent sds = new Intent(getApplicationContext(), com.naqelexpress.naqelpointer.Activity.SuggestDeliverysheet.MyRouteActivity.class);
                                    startActivity(sds);
                                }
                            }
                        } else
                            GlobalVar.enableLocationSettings(MainPageActivity.this);
                        break;
                    case 20:
                        if (GlobalVar.locationEnabled(getApplicationContext())) {
                            Intent checkPoint = new Intent(getApplicationContext(), Incident.class);
                            startActivity(checkPoint);
                        } else
                            GlobalVar.enableLocationSettings(MainPageActivity.this);
                        break;
                    case 21:
                        if (GlobalVar.locationEnabled(getApplicationContext())) {

                            if (GlobalVar.AskPermission_Location(MainPageActivity.this, 11)) {
                                if (!GlobalVar.isMyServiceRunning(LocationService.class, getApplicationContext())) {
                                    startService(new Intent(getBaseContext(),
                                            LocationService.class));
                                }
                            }
                            if (VersionMatct()) {

                                Intent checkPoint = new Intent(getApplicationContext(), NSScanShipmentActivity.class);
                                startActivity(checkPoint);
                            } else {
                                GlobalVar.GV().ShowDialog(MainPageActivity.this, "Info.", "Kindly Update our Latest Version.(Logout and Login again)"
                                        , true);
                            }
                        } else
                            GlobalVar.enableLocationSettings(MainPageActivity.this);
                        break;

                    case 22:
                        Intent checkPoint = new Intent(getApplicationContext(), SyncTripDetails.class);
                        startActivity(checkPoint);

                        break;
                    case 23:
                        if (VersionMatct()) {
                            if (GlobalVar.GV().IsTerminalApp || devision.equals("IRS") || GlobalVar.GV().isFortesting) {
                                Intent terminalhandling = new Intent(getApplicationContext(), TerminalHandlingGroup.class);
                                startActivity(terminalhandling);
                            } else
                                GlobalVar.ShowDialog(MainPageActivity.this, "Info.", "This Module only TH users"
                                        , true);
                        } else {
                            GlobalVar.GV().ShowDialog(MainPageActivity.this, "Info.", "Kindly Update our Latest Version.(Logout and Login again)"
                                    , true);
                        }
                        break;
                    case 24:
                        if (VersionMatct()) {
                            Intent changepwd = new Intent(getApplicationContext(), UpdatePasswordModule.class);
                            startActivity(changepwd);
                        } else {
                            GlobalVar.GV().ShowDialog(MainPageActivity.this, "Info.", "Kindly Update our Latest Version.(Logout and Login again)"
                                    , true);
                        }
                        break;

                    case 25:
                        if (VersionMatct()) {
                            Intent chat = new Intent(getApplicationContext(), MainActivity.class);
                            // startActivity(chat);
                        } else {
                            GlobalVar.GV().ShowDialog(MainPageActivity.this, "Info.", "Kindly Update our Latest Version.(Logout and Login again)"
                                    , true);
                        }
                        break;
                    case 26:
                        if (VersionMatct()) {
                            Intent chat = new Intent(getApplicationContext(), CourierKpi.class);
                            startActivity(chat);
                        } else {
                            GlobalVar.GV().ShowDialog(MainPageActivity.this, "Info.", "Kindly Update our Latest Version.(Logout and Login again)"
                                    , true);
                        }
                        break;
                    case 27:
                        if (VersionMatct()) {
                            Intent chat = new Intent(getApplicationContext(), IncCabChecklist.class);
                            chat.putExtra("close", 1);
                            startActivityForResult(chat, 2);
                        } else {
                            GlobalVar.GV().ShowDialog(MainPageActivity.this, "Info.", "Kindly Update our Latest Version.(Logout and Login again)"
                                    , true);
                        }
                        break;
                    case 28:

                        if (GlobalVar.locationEnabled(getApplicationContext())) {
                            if (GlobalVar.AskPermission_Camera(MainPageActivity.this, 6)) {

                                if (VersionMatct()) {
                                    Intent deliverySheet = new Intent(getApplicationContext(),
                                            com.naqelexpress.naqelpointer.Activity.OFDPiecebyNCL.DeliverySheetActivity.class);
//                                    Intent deliverySheet = new Intent(getApplicationContext(), DeliverySheetActivity.class);
                                    startActivity(deliverySheet);
                                } else {
                                    GlobalVar.GV().ShowDialog(MainPageActivity.this, "Info.",
                                            "Kindly Update our Latest Version.(Logout and Login again)"
                                            , true);
                                }

                            }
                        } else
                            GlobalVar.enableLocationSettings(MainPageActivity.this);

                        break;
                    case 29:
                        if (VersionMatct()) {
                            Intent deliverySheet = new Intent(getApplicationContext(),
                                    com.naqelexpress.naqelpointer.Activity.SkipWaybillNofromRouteLine.SkipWaybillNoinRouteLine.class);
//                                    Intent deliverySheet = new Intent(getApplicationContext(), DeliverySheetActivity.class);
                            startActivity(deliverySheet);
                        } else {
                            GlobalVar.GV().ShowDialog(MainPageActivity.this, "Info.",
                                    "Kindly Update our Latest Version.(Logout and Login again)"
                                    , true);
                        }

                        break;
                    case 30:
                        if (VersionMatct()) {
                            Intent deliverySheet = new Intent(getApplicationContext(),
                                    CourierRating.class);
//
                            startActivity(deliverySheet);
                        } else {
                            GlobalVar.GV().ShowDialog(MainPageActivity.this, "Info.",
                                    "Kindly Update our Latest Version.(Logout and Login again)"
                                    , true);
                        }

                        break;
                    case 31:
                        if (VersionMatct()) {
                            Intent fuel = new Intent(getApplicationContext(),
                                    Fuel.class);
//
                            startActivity(fuel);
                        } else {
                            GlobalVar.GV().ShowDialog(MainPageActivity.this, "Info.",
                                    "Kindly Update our Latest Version.(Logout and Login again)"
                                    , true);
                        }

                        break;
                    case 32:
                        if (VersionMatct()) {
                            Intent cbm = new Intent(getApplicationContext(),
                                    CBM.class);
//
                            startActivity(cbm);
                        } else {
                            GlobalVar.GV().ShowDialog(MainPageActivity.this, "Info.",
                                    "Kindly Update our Latest Version.(Logout and Login again)"
                                    , true);
                        }

                        break;
                    case 33:
                        if (VersionMatct()) {
                            Intent cbm = new Intent(getApplicationContext(),
                                    ScanWaybill.class);
                            startActivity(cbm);
                        } else {
                            GlobalVar.GV().ShowDialog(MainPageActivity.this, "Info.",
                                    "Kindly Update our Latest Version.(Logout and Login again)"
                                    , true);
                        }

                        break;
                    case 34:
                        if (VersionMatct()){
                            Intent cbm = new Intent(getApplicationContext(), TripAndVehicleDetail.class);
                            startActivity(cbm);
                        }else{
                            GlobalVar.GV().ShowDialog(MainPageActivity.this, "Info.",
                                    "Kindly Update our Latest Version.(Logout and Login again)"
                                    ,true);
                        }
                        break;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        if (devision.equals("Courier")) {
            MenuItem item = menu.findItem(R.id.ratingmeanu);
            item.setVisible(true);
            SpannableString s = new SpannableString("Rating " + String.valueOf(Rating));
            s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s.length(), 0);
            item.setTitle(s);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.myAccount:
                Intent intent = new Intent(getApplicationContext(), MyAccountActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void ShowAlertMessage() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainPageActivity.this).create();
        alertDialog.setTitle(getResources().getString(R.string.app_name));
        alertDialog.setMessage("Under Construction");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }


    @Override
    public void onBackPressed() {
        GlobalVar.CloseActivity(MainPageActivity.this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 111:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent mapList = new Intent(getApplicationContext(), MyRouteActivity.class);
                    startActivity(mapList);
                } else {
                    if (ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_DENIED) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                            try {
                                Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                startActivity(i);
                            } catch (Exception e) {
                                GlobalVar.ShowDialog(MainPageActivity.this, "Contacts Permission necessary", "Kindly please contact our Admin", true);
                            }
                        } else {
                            ActivityCompat.requestPermissions(
                                    MainPageActivity.this,
                                    new String[]{Manifest.permission.READ_CONTACTS,
                                            Manifest.permission.WRITE_CONTACTS},
                                    111);
                        }
                    }
                }
                break;

            case 1:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (!GlobalVar.isMyServiceRunning(LocationService.class, getApplicationContext())) {
                        startService(new Intent(getBaseContext(),
                                LocationService.class));
                    }
                    if (GlobalVar.AskPermission_Contcatcs(MainPageActivity.this, 111)) { // 1 irunthucji
                        Intent mapList = new Intent(getApplicationContext(), MyRouteActivity.class);
                        startActivity(mapList);
                    }

                } else {
                    if (ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_DENIED) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                            try {
                                Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                startActivity(i);
                            } catch (Exception e) {
                                GlobalVar.ShowDialog(MainPageActivity.this, "Contacts Permission necessary", "Kindly please contact our Admin", true);
                            }
                        } else {
                            ActivityCompat.requestPermissions(
                                    MainPageActivity.this,
                                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                            Manifest.permission.ACCESS_FINE_LOCATION},
                                    1);
                        }
                    }
                }
                break;

            case 2:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent delivery = new Intent(getApplicationContext(), DeliveryActivity.class);
                    startActivity(delivery);
                } else {
                    if (ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_DENIED) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                            try {
                                Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                startActivity(i);
                            } catch (Exception e) {
                                GlobalVar.ShowDialog(MainPageActivity.this, "Contacts Permission necessary", "Kindly please contact our Admin", true);
                            }
                        } else {
                            ActivityCompat.requestPermissions(
                                    MainPageActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    2);
                        }
                    }
                }
                break;
            case 3:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (GetDivision()) {
                        Intent notDelivered = new Intent(getApplicationContext(),
                                com.naqelexpress.naqelpointer.Activity.NotDeliveredCBU.NotDeliveredActivity.class);
                        startActivity(notDelivered);
                    } else {
                        Intent notDelivered = new Intent(getApplicationContext(), NotDeliveredActivity.class);
                        startActivity(notDelivered);
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_DENIED) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                            try {
                                Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" +
                                        BuildConfig.APPLICATION_ID));
                                startActivity(i);
                            } catch (Exception e) {
                                GlobalVar.ShowDialog(MainPageActivity.this, "Contacts Permission necessary", "Kindly please contact our Admin", true);
                            }
                        } else {
                            ActivityCompat.requestPermissions(
                                    MainPageActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    3);
                        }
                    }
                }
                break;
            case 4:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent pickup = new Intent(getApplicationContext(), PickUpActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("class", "MainPage");
                    pickup.putExtras(bundle);
                    startActivity(pickup);

                } else {
                    if (ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_DENIED) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                            try {
                                Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                startActivity(i);
                            } catch (Exception e) {
                                GlobalVar.ShowDialog(MainPageActivity.this, "Contacts Permission necessary", "Kindly please contact our Admin", true);
                            }
                        } else {
                            ActivityCompat.requestPermissions(
                                    MainPageActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    4);
                        }
                    }
                }
                break;
            case 5:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent atorigin = new Intent(getApplicationContext(), AtOrigin.class);
                    startActivity(atorigin);

                } else {
                    if (ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_DENIED) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                            try {
                                Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                startActivity(i);
                            } catch (Exception e) {
                                GlobalVar.ShowDialog(MainPageActivity.this, "Contacts Permission necessary", "Kindly please contact our Admin", true);
                            }
                        } else {
                            ActivityCompat.requestPermissions(
                                    MainPageActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    5);
                        }
                    }
                }
                break;
            case 6:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!GetDivision()) {
                        Intent deliverySheet = new Intent(getApplicationContext(), DeliverySheetActivity.class);
                        startActivity(deliverySheet);
                    } else {
                        Intent deliverySheet = new Intent(getApplicationContext(),
                                com.naqelexpress.naqelpointer.Activity.OFDPieceLevel.DeliverySheetActivity.class);
                        startActivity(deliverySheet);
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_DENIED) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                            try {
                                Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                startActivity(i);
                            } catch (Exception e) {
                                GlobalVar.ShowDialog(MainPageActivity.this, "Contacts Permission necessary", "Kindly please contact our Admin", true);
                            }
                        } else {
                            ActivityCompat.requestPermissions(
                                    MainPageActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    6);
                        }
                    }
                }

                break;

            case 7:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    boolean redirect = true;
                    if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        for (int i = 0; i < permissions.length; i++) {
                            int result = ContextCompat.checkSelfPermission(this, permissions[i]);
                            if(result == PackageManager.PERMISSION_DENIED){
                                redirect = false;
                                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                                    try {
                                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                        startActivity(intent);
                                    } catch (Exception e) {
                                        GlobalVar.ShowDialog(MainPageActivity.this, "All Permission necessary", "Kindly please contact our Admin", true);
                                    }

                                    break;
                                } else {
                                    ActivityCompat.requestPermissions(
                                            MainPageActivity.this,
                                            new String[]{Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG, Manifest.permission.WRITE_CONTACTS
                                                    , Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE
                                                    , Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS,
                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_NUMBERS},
                                            7
                                    );
                                    break;
                                }
                            }
                        }
                        if (redirect) {
                            Intent delivery = new Intent(getApplicationContext(), DeliveryActivity.class);
                            startActivity(delivery);
                        }

                    } else {
                        //  GlobalVar.AskPermission_Location(MainPageActivity.this);
                        for (int i = 0; i < permissions.length; i++) {
                            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                                try {
                                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                    startActivity(intent);
                                } catch (Exception e) {
                                    GlobalVar.ShowDialog(MainPageActivity.this, "Permission necessary", "Kindly please contact our Admin", true);
                                }
                                break;
                            }
                        }
                    }
                }
                break;
            case 8:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!GlobalVar.isMyServiceRunning(LocationService.class, getApplicationContext())) {
                        startService(new Intent(getBaseContext(),
                                LocationService.class));
                    }
                    if (GlobalVar.AskPermission_Contcatcs(MainPageActivity.this, 9)) {
                        Intent mapList = new Intent(getApplicationContext(), DeliverySheet.class);
                        startActivity(mapList);
                    }
                } else {
                    try {
                        Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                        startActivity(i);
                    } catch (Exception e) {
                        GlobalVar.ShowDialog(MainPageActivity.this, "Permission necessary", "Kindly please contact our Admin", true);
                    }
                }
                break;
            case 9:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Intent mapList = new Intent(getApplicationContext(), DeliverySheet.class);
                    startActivity(mapList);
                } else {
                    //  GlobalVar.AskPermission_Location(MainPageActivity.this);
                    if (ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_DENIED) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                            try {
                                Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                startActivity(i);
                            } catch (Exception e) {
                                GlobalVar.ShowDialog(MainPageActivity.this, "Contacts Permission necessary", "Kindly please contact our Admin", true);
                            }
                        } else {
                            ActivityCompat.requestPermissions(
                                    MainPageActivity.this,
                                    new String[]{Manifest.permission.CAMERA}, 6);
                        }
                    }
                }
                break;

            case 10:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent delivery = new Intent(getApplicationContext(), MultiDeliveryActivity.class);
                    startActivity(delivery);
                } else {
                    if (ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_DENIED) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                            try {
                                Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                startActivity(i);
                            } catch (Exception e) {
                                GlobalVar.ShowDialog(MainPageActivity.this, "Contacts Permission necessary", "Kindly please contact our Admin", true);
                            }
                        } else {
                            ActivityCompat.requestPermissions(
                                    MainPageActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    10);
                        }
                    }
                }
                break;
            case 11:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent suggestds = new Intent(getApplicationContext(), com.naqelexpress.naqelpointer.Activity.SuggestDeliverysheet.MyRouteActivity.class);
                    startActivity(suggestds);

                } else {
                    if (ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_DENIED) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                            try {
                                Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                startActivity(i);
                            } catch (Exception e) {
                                GlobalVar.ShowDialog(MainPageActivity.this, "Location Permission necessary", "Kindly please contact our Admin", true);
                            }
                        } else {
                            ActivityCompat.requestPermissions(
                                    MainPageActivity.this,
                                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                                    11);
                        }
                    }
                }

                break;
            case 12:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent suggestds = new Intent(getApplicationContext(), com.naqelexpress.naqelpointer.Activity.SuggestDeliverysheet.MyRouteActivity.class);
                    startActivity(suggestds);

                } else {
                    if (ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_DENIED) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                            try {
                                Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                startActivity(i);
                            } catch (Exception e) {
                                GlobalVar.ShowDialog(MainPageActivity.this, "Contacts Permission necessary", "Kindly please contact our Admin", true);
                            }
                        } else {
                            ActivityCompat.requestPermissions(
                                    MainPageActivity.this,
                                    new String[]{Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS},
                                    12);
                        }
                    }
                }

                break;

            case 13:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent suggestds = new Intent(getApplicationContext(), NightStockActivity.class);
                    startActivity(suggestds);

                } else {
                    if (ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_DENIED) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                            try {
                                Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                startActivity(i);
                            } catch (Exception e) {
                                GlobalVar.ShowDialog(MainPageActivity.this, "Location Permission necessary", "Kindly please contact our Admin", true);
                            }
                        } else {
                            ActivityCompat.requestPermissions(
                                    MainPageActivity.this,
                                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                                    13);
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
                                GlobalVar.ShowDialog(MainPageActivity.this, "Permission necessary", "Kindly please contact our Admin", true);
                            }
                        } else {
                            ActivityCompat.requestPermissions(
                                    MainPageActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                    14);
                        }
                    }
                }
                break;
            case 15:
                boolean background = ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
                if (!background) {
                    try {
                        Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                        startActivityForResult(i, 15);
                    } catch (Exception e) {
                        GlobalVar.ShowDialog(MainPageActivity.this, "Contacts Permission necessary", "Kindly please contact our Admin", true);
                    }
                }

                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1: {
                if (resultCode == Activity.RESULT_OK) {
                    String returnValue = data.getStringExtra("result");
                    if (returnValue.equals("done"))
                        finish();
                }
                break;
            }
            case 15:
                boolean background = ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
                if (!background) {
                    try {
                        Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                        startActivityForResult(i, 15);
                    } catch (Exception e) {
                        GlobalVar.ShowDialog(MainPageActivity.this, "Contacts Permission necessary", "Kindly please contact our Admin", true);
                    }
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("test");
    }


    private void setSavedInstance(Bundle savedInstanceState) {

        GlobalVar.GV().EmployID = savedInstanceState.getInt("EmployID");
        GlobalVar.GV().UserID = savedInstanceState.getInt("UserID");
        GlobalVar.GV().StationID = savedInstanceState.getInt("StationID");
        GlobalVar.GV().EmployMobileNo = savedInstanceState.getString("EmployMobileNo");
        GlobalVar.GV().EmployName = savedInstanceState.getString("EmployName");
        GlobalVar.GV().EmployStation = savedInstanceState.getString("EmployStation");
        GlobalVar.GV().currentSettings = savedInstanceState.getParcelable("currentSettings");
        GlobalVar.GV().currentSettings.ID = savedInstanceState.getInt("currentSettingsID");
        devision = savedInstanceState.getString("devision");
        installaionfile = savedInstanceState.getString("installaionfile");

        itemposition = (HashMap<Integer, Integer>) savedInstanceState.getSerializable("itemposition");
        LoadMenu();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("EmployID", GlobalVar.GV().EmployID);
        outState.putInt("UserID", GlobalVar.GV().UserID);
        outState.putInt("StationID", GlobalVar.GV().StationID);
        outState.putString("EmployMobileNo", GlobalVar.GV().EmployMobileNo);
        outState.putString("EmployName", GlobalVar.GV().EmployName);
        outState.putString("EmployStation", GlobalVar.GV().EmployStation);
        outState.putParcelable("currentSettings", GlobalVar.GV().currentSettings);
        outState.putInt("currentSettingsID", GlobalVar.GV().currentSettings.ID);
        outState.putString("devision", devision);
        outState.putSerializable("itemposition", itemposition);
        outState.putString("installaionfile", installaionfile);

        super.onSaveInstanceState(outState);
    }

    ProgressDialog progressDialog;

    private class GetMasterData extends AsyncTask<String, Integer, String> {
        StringBuffer buffer;

        @Override
        protected void onPreExecute() {

            if (progressDialog == null)
                progressDialog = ProgressDialog.show(MainPageActivity.this, "Please wait.", "Bringing Master Details.", true);
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
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "GetMasterData");
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

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute("");
            if (result != null) {

                try {
                    Context context = getApplicationContext();
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray appversion = jsonObject.getJSONArray("AppVersion");
                    View view = getWindow().getDecorView().getRootView();
                    JSONObject jo = appversion.getJSONObject(0);
                    DBConnections dbConnections = new DBConnections(context, null);
                    dbConnections.InsertAppVersion(jo.getInt("VersionCode"), context);
                    int versioncode = GlobalVar.VersionCode(context);
                    if (GlobalVar.GV().EmployID == 19127)
                        versioncode = jo.getInt("VersionCode");
                    if (jo.getInt("VersionCode") == versioncode) {
                        fetchMasterData(jsonObject, view, getApplicationContext(), jo.getInt("ChangesMainMenu"));
                    } else {
                        deleteApk();
                        updateApp();
                    }
                    dbConnections.close();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                LoadDivisionError();
            }

            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }

            LoadMenu();

        }
    }

    private void updateApp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainPageActivity.this);
        builder.setTitle("Info")
                .setMessage("Kindly Please update our latest version")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        ActivityCompat.requestPermissions(
                                MainPageActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                14
                        );
                    }
                }).setCancelable(false);//.setNegativeButton("Cancel", null).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void fetchMasterData(JSONObject jsonObject, View view, Context context, int updatemenu) {
        try {
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

            JSONArray contacts = jsonObject.getJSONArray("Contacts");
            if (contacts.length() > 0)
                new Contacts(contacts.toString(), view, context);


            devision = jsonObject.getString("Division");
            DBConnections dbConnections = new DBConnections(context, null);
            dbConnections.UpdateUserDivision(devision, getWindow().getDecorView().getRootView(), updatemenu);
            JSONArray deliverysubstatus = jsonObject.getJSONArray("DeliveryStatusReason");
            if (deliverysubstatus.length() > 0)
                new DeliveryStatus(deliverysubstatus.toString(), view, context, 0);


            int vc = dbConnections.GetAppVersion(getApplicationContext());
            int versioncode = GlobalVar.VersionCode(getApplicationContext());
            if (vc != versioncode) {
                version.setText("Versioncode :-" + " OLD ");
            } else
                version.setText("Versioncode :-" + " NEW ");

            dbConnections.close();
            LoadMenu();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void LoadDivisionError() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainPageActivity.this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Info.");
        alertDialog.setMessage("Kindly Check your Internet Connection,please try again");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,"OK",
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


    Point p;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        int[] location = new int[2];
        GridView gridView = (GridView) findViewById(R.id.gridView);

        gridView.getLocationOnScreen(location);

        p = new Point();
        p.x = location[0];
        p.y = location[1];
    }

    PopupWindow popup;

    private void showPopup(final Activity context, Point p, final int redirect) {
        LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = layoutInflater.inflate(R.layout.tripplanpopup, viewGroup);

        final EditText tripplanbarcode = (EditText) layout.findViewById(R.id.palletbarcode);

        tripplanbarcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (tripplanbarcode != null && tripplanbarcode.getText().length() == 6) {
                    if (redirect == 0)
                        redirectClass(tripplanbarcode.getText().toString());
                    else if (redirect == 1)
                        redirectADClass(tripplanbarcode.getText().toString());
                    popup.dismiss();
                }
            }
        });

        popup = new PopupWindow(layout, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        popup.setFocusable(true);
        popup.update();
        popup.setOutsideTouchable(false);
        int OFFSET_X = 30;
        int OFFSET_Y = 30;
        // Clear the default translucent background
        popup.setBackgroundDrawable(new BitmapDrawable());
        popup.showAtLocation(layout, Gravity.CENTER, 0, 0);
    }

    private void redirectClass(String triplanID) {

        Intent ld = new Intent(getApplicationContext(), LoadtoDestination.class);
        ld.putExtra("triplanID", triplanID);
        startActivity(ld);

    }

    private void redirectADClass(String triplanID) {

        Intent ld = new Intent(getApplicationContext(), ArrivedatDestination.class);
        ld.putExtra("triplanID", triplanID);
        startActivity(ld);

    }

    ProgressDialog mProgressDialog;
    String installaionfile = "";

    private class DownloadApk extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            String DIRECTORY = "";
            if (Build.VERSION.SDK_INT >= 30)
                DIRECTORY = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/NaqelSignature/";
            else
                DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/NaqelSignature/";

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
            mProgressDialog = new ProgressDialog(MainPageActivity.this);
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
                        String DIRECTORY = "";
                        if (Build.VERSION.SDK_INT >= 30)
                            DIRECTORY = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()
                                    + "/NaqelSignature/" + installaionfile;
                        else
                            DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/NaqelSignature/" + installaionfile;

                        File file = new File(DIRECTORY);
                        if (file.exists()) {

                            String DIRECTORY1 = "";
                            if (Build.VERSION.SDK_INT >= 30)
                                DIRECTORY1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()
                                        + "/NaqelSignature";
                            else
                                DIRECTORY1 = Environment.getExternalStorageDirectory().getPath() + "/NaqelSignature";

                            File toInstall = new File(DIRECTORY1, installaionfile);


                            Uri contentUri = FileProvider.getUriForFile(
                                    getApplicationContext(),
                                    "com.naqelexpress.naqelpointer.fileprovider", toInstall);

                            Intent intent = new Intent(Intent.ACTION_VIEW, contentUri);
                            intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(intent);
                        }
                    } else {
                        try {
                            startActivity(new Intent(android.provider.Settings.ACTION_SECURITY_SETTINGS));
                        } catch (Exception e) {
                            GlobalVar.ShowDialog(MainPageActivity.this, "Need Unknown Source Permission", "Kindly please Enable Unknown Source", true);
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

    private void deleteApk() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        try {
            String DIRECTORY = "";
            if (Build.VERSION.SDK_INT >= 30)
                DIRECTORY = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()
                        + "/NaqelSignature/" + installaionfile;
            else
                DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/NaqelSignature/"
                        + installaionfile;

            File file = new File(DIRECTORY);
            if (file.exists()) {
                file.delete();
            }

        } catch (Exception e) {
            System.out.println(e);
        }


    }


    private boolean VersionMatct() {
        if (GlobalVar.GV().EmployID == 19127)
            return true;

        boolean valid = true;
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        int vc = dbConnections.GetAppVersion(getApplicationContext());
        int versioncode = GlobalVar.VersionCode(getApplicationContext());
        if (vc != versioncode)
            valid = false;

        dbConnections.close();
        return valid;
    }

    Handler isdeviceonlinehandler;

    private void isDeviceOnline() {
        try {
            isdeviceonlinehandler = new Handler();
            isdeviceonlinehandler.postDelayed(new Runnable() {
                public void run() {
                    try {
                        new InternetAvailability().execute();
                        isdeviceonlinehandler.postDelayed(this, 10000);
                    } catch (Exception e) {

                        isdeviceonlinehandler.postDelayed(this, 10000);
                        Log.e("Dashboard thread", e.toString());
                    }

                }
            }, 10000);
        } catch (Exception e) {

        }

    }

    private class InternetAvailability extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... sUrl) {
            String ds = "";
            try {
                HttpURLConnection urlc = (HttpURLConnection)
                        (new URL("http://clients3.google.com/generate_204")
                                .openConnection());
                urlc.setRequestProperty("User-Agent", "Android");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();

                if (urlc.getResponseCode() == 204 && urlc.getContentLength() == 0)
                    ds = "Device is Online!";
                else
                    ds = "No Internet!";

            } catch (IOException e) {
                ds = "No Internet!";
                Log.e("", "Error checking internet connection", e);
            }

            return ds;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(String result) {
            devicestatus.setText(result);
            if (result.contains("No Internet"))
                devicestatus.setTextColor(getResources().getColor(R.color.NaqelRed));
            else
                devicestatus.setTextColor(getResources().getColor(R.color.Green));

        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        isdeviceonlinehandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onResume() {
        setValidationText();
        deleteExistingUser();
        setProductivitytext();
        isDeviceOnline();
        super.onResume();
    }


    private void deleteExistingUser() {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        dbConnections.DeleteExsistingLogin(getApplicationContext());
        Cursor result = dbConnections.Fill("select * from UserME where StatusID <> 3 ", getApplicationContext());
        if (result != null && result.getCount() == 0) {
            result.close();
            dbConnections.close();
            android.os.Process.killProcess(android.os.Process.myPid());
            Intent intent = new Intent(getApplicationContext(), SplashScreenActivity.class);
            startActivity(intent);
        }
    }

    private void Logout() {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        int id = dbConnections.getMaxID(" UserMeLogin where LogoutDate is NULL ", getApplicationContext());
        UserMeLogin userMeLogin = new UserMeLogin(id);
        dbConnections.UpdateUserMeLogout(userMeLogin, getApplicationContext());
        dbConnections.close();
        Intent intent = new Intent(getApplicationContext(), SplashScreenActivity.class);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void VersionMismatch(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainPageActivity.this).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Logout",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Logout();
                    }
                });
        alertDialog.show();
    }

    private void registerFirebase() {
        try {
            FirebaseApp.initializeApp(getApplicationContext());
            final FirebaseAuth mAuth = FirebaseAuth.getInstance();
            if (mAuth.getUid() == null) {
                mAuth.signInWithEmailAndPassword("966593793637@naqel.com.sa", "M@d237467")
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    //  updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.

                                    System.out.println("");

                                }
                            }

                        });
            }
        } catch (Exception e) {
            System.out.println("");
        }
    }
}
