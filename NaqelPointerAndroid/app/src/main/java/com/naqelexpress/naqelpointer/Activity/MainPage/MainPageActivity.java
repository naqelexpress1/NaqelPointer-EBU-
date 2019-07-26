package com.naqelexpress.naqelpointer.Activity.MainPage;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.TextView;

import com.naqelexpress.naqelpointer.Activity.ArrivedDest.ArrivedatDestination;
import com.naqelexpress.naqelpointer.Activity.AtOriginNew.AtOrigin;
import com.naqelexpress.naqelpointer.Activity.Booking.BookingList;
import com.naqelexpress.naqelpointer.Activity.CheckCOD.CODCheckingActivity;
import com.naqelexpress.naqelpointer.Activity.CheckPoints.CheckPointsActivity;
import com.naqelexpress.naqelpointer.Activity.Delivery.DeliveryActivity;
import com.naqelexpress.naqelpointer.Activity.DeliverySheet.DeliverySheetActivity;
import com.naqelexpress.naqelpointer.Activity.History.History;
import com.naqelexpress.naqelpointer.Activity.LoadtoDest.LoadtoDestination;
import com.naqelexpress.naqelpointer.Activity.Login.LoginActivity;
import com.naqelexpress.naqelpointer.Activity.MultiDelivery.MultiDeliveryActivity;
import com.naqelexpress.naqelpointer.Activity.MyAccount.MyAccountActivity;
import com.naqelexpress.naqelpointer.Activity.MyRoute.MyRouteActivity;
import com.naqelexpress.naqelpointer.Activity.NotDelivered.NotDeliveredActivity;
import com.naqelexpress.naqelpointer.Activity.PendingMoney.PendingMoneyActivity;
import com.naqelexpress.naqelpointer.Activity.PickUp.PickUpActivity;
import com.naqelexpress.naqelpointer.Activity.Settings.SettingActivity;
import com.naqelexpress.naqelpointer.Activity.ValidationDS.ValidationDS;
import com.naqelexpress.naqelpointer.Activity.WaybillMeasurments.WaybillMeasurementActivity;
import com.naqelexpress.naqelpointer.BuildConfig;
import com.naqelexpress.naqelpointer.ContactNo.Contact;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.UserMeLogin;
import com.naqelexpress.naqelpointer.DB.DBObjects.UserSettings;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.JSON.DataSync;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.service.LocationService;

import org.joda.time.DateTime;
import org.joda.time.DurationFieldType;

import java.util.ArrayList;
import java.util.List;

public class MainPageActivity
        extends AppCompatActivity {

    protected List<AsyncTask<String, Integer, String>> asyncTasks = new ArrayList<AsyncTask<String, Integer, String>>();

    String[] cellTitle;
    int cellIcon[] = {

            R.drawable.deliverysheet,
            R.drawable.maplist,
            R.drawable.delivery,
            R.drawable.delivery,
            R.drawable.notdelivered,
            R.drawable.pickup,
            R.drawable.waybillmeasurement,
            R.drawable.settings,
            // R.drawable.datasync,
            R.drawable.money,
            R.drawable.pendingcod,
            R.drawable.customclearence,
            R.drawable.checkpoint,
            R.drawable.checkpoint,
            R.drawable.pendingdata,
            R.drawable.ld,
            R.drawable.importtruck,
            R.drawable.contacts,
            R.drawable.closetrip

    };
    GridView gridView;

    FloatingActionButton btnSignOut;
    public DataSync dataSync;
    int progressBarStatus = 0;
    TextView ofd, attempted, delivered, exceptions, productivity, complaint, compDelvrd, ComRemain;

    //TextView out
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //  GlobalVar.GV().MainContext = this;
        GlobalVar.GV().SignedIn = true;

        try {
            setContentView(R.layout.mainpage);


            android.support.v7.app.ActionBar mActionBar = getSupportActionBar();
            mActionBar.setDisplayShowHomeEnabled(false);
            mActionBar.setDisplayShowTitleEnabled(false);
            LayoutInflater mInflater = LayoutInflater.from(this);
            android.support.v7.app.ActionBar.LayoutParams layoutParams = new android.support.v7.app.ActionBar.LayoutParams(100,
                    100, Gravity.RIGHT);

            View mCustomView = mInflater.inflate(R.layout.circle, null);
            mActionBar.setCustomView(mCustomView, layoutParams);

            compDelvrd = (TextView) mCustomView.findViewById(R.id.Compdeliverd);

            ComRemain = (TextView) mCustomView.findViewById(R.id.remaining);

            mActionBar.setCustomView(mCustomView);
            mActionBar.setDisplayShowCustomEnabled(true);

        } catch (Exception e) {
            System.out.println(e);
        }


        ofd = (TextView) findViewById(R.id.ofd);
        attempted = (TextView) findViewById(R.id.attemped);
        delivered = (TextView) findViewById(R.id.delivered);
        exceptions = (TextView) findViewById(R.id.excep);
        productivity = (TextView) findViewById(R.id.productivity);
        complaint = (TextView) findViewById(R.id.complaint);


        setProductivitytext();

        //GlobalVar.GV().rootViewMainPage = mainRootView = findViewById(android.R.id.content);

        SharedPreferences sharedpreferences = getSharedPreferences("naqelSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("mc", true);
        editor.commit();

        //GlobalVar.GV().sendSMS("+966580679791","text",getApplicationContext());

        cellTitle = new String[18];
        cellTitle[0] = getResources().getString(R.string.DeliverySheetActivity);
        cellTitle[1] = getResources().getString(R.string.MyRouteActivity);
        cellTitle[2] = getResources().getString(R.string.DeliveryActivity);
        cellTitle[3] = getResources().getString(R.string.MultiDeliveryActivity);
        cellTitle[4] = getResources().getString(R.string.NotDeliveredActivity);
        cellTitle[5] = getResources().getString(R.string.PickUpActivity);
        cellTitle[6] = getResources().getString(R.string.WaybillMeasurementActivity);
        cellTitle[7] = getResources().getString(R.string.SettingsActivity);
        //cellTitle[8] = getResources().getString(R.string.DataSync);
        cellTitle[8] = getResources().getString(R.string.CODChecking);
        cellTitle[9] = getResources().getString(R.string.PendingCOD);
        cellTitle[10] = getResources().getString(R.string.CustomsClearance);
        cellTitle[11] = getResources().getString(R.string.BookingList);
        cellTitle[12] = getResources().getString(R.string.AtOrigin);
        cellTitle[13] = "Hisory";
        cellTitle[14] = getResources().getString(R.string.ld);
        cellTitle[15] = "Arrived Dest";
        cellTitle[16] = "Contacts";
        cellTitle[17] = "Validate DS";

        MainPageNavigation();


        final DBConnections dbConnections = new DBConnections(getApplicationContext(), null);

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
                                int id = dbConnections.getMaxID(" UserMeLogin where LogoutDate is NULL ", getApplicationContext());
                                UserMeLogin userMeLogin = new UserMeLogin(id);
                                dbConnections.UpdateUserMeLogout(userMeLogin, getApplicationContext());
                                finish();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                            }
                        }).setNegativeButton("Cancel", null).setCancelable(false);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        dbConnections.close();

        LoadUserSettings();

        if (getIntent().getIntExtra("getMaster", 0) == 1)
            GetMasterData();

//        Thread loadMasterDataThread = new Thread() {
//            @Override
//            public void run() {
//
//
//                // progressDialog.dismiss();
//
//                // if (GlobalVar.GV().myBookingList.size() <= 0)
//                //     GlobalVar.GV().LoadMyBookingList("BookingDate", true);
//            }
//        };
////
//        loadMasterDataThread.start();


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


    @Override
    protected void onResume() {
        setProductivitytext();
        super.onResume();
    }

//    private class LoadDefaultData extends AsyncTask<Void, Void, Void> {
//
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            progressDialog = ProgressDialog.show(GlobalVar.GV().context, "Please wait.", "Downloading Shipments Details.", true);
//
//        }
//
//        protected Void doInBackground(Void... param) {
//
//
////            if (GlobalVar.GV().StationList.size() <= 0)
////                GlobalVar.GV().GetStationList(true);
////
////            if (GlobalVar.GV().DeliveryStatusList.size() <= 0)
////                GlobalVar.GV().GetDeliveryStatusList(true);
////
////            if (GlobalVar.GV().CheckPointTypeList.size() <= 0)
////                GlobalVar.GV().GetCheckPointTypeList(true);
////
////            if (GlobalVar.GV().CheckPointTypeDetailList.size() <= 0)
////                GlobalVar.GV().GetCheckPointTypeDetailList(true, 0);
////
////            if (GlobalVar.GV().CheckPointTypeDDetailList.size() <= 0)
////                GlobalVar.GV().GetCheckPointTypeDDetailList(true, 0);
////
////            if (GlobalVar.GV().NoNeedVolumeReasonList.size() <= 0)
////                GlobalVar.GV().GetNoNeedVolumeReasonList(true);
//
//
//            while (!GlobalVar.gs || GlobalVar.dsl || GlobalVar.cptl || GlobalVar.cptdl || GlobalVar.cptddl || GlobalVar.nnvdl) {
//                try {
//                    Thread.sleep(10000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                if (GlobalVar.gs && GlobalVar.dsl && GlobalVar.cptl && GlobalVar.cptdl && GlobalVar.cptddl && GlobalVar.nnvdl) {
//                    // progressDialog.dismiss();
//
//                    break;
//                }
//
//            }
//            return null;
//        }
//
//        protected void onPostExecute(Void param) {
//            //Print Toast or open dialog
//            progressDialog.dismiss();
//
//        }
//    }


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


//    public void LoadNotification() {
//
//        try {
//            noti = new NotificationHelper(this);
//            Notification.Builder nb = noti.getNotification1("dfsdfsdf", "Test Notification");
//            Intent intent = new Intent(this, BookingDetailActivity.class);
//
//            Bundle bundle = new Bundle();
//            bundle.putString("ID", String.valueOf(25455));
//            bundle.putString("WaybillNo", "25464");
//            intent.putExtras(bundle);
//
//
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
//            nb.setContentIntent(pendingIntent);
//            noti.notify(NOTI_PRIMARY1, nb);
//        } catch (Exception ex) {
//            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT)
//                    .show();
//        }
//    }


//private class GetMasterData extends AsyncTask<String, Void, String> {
//    String result = "";
//    StringBuffer buffer;
//    ProgressDialog progressDialog;
//
//    //  ProgressDialog progressDialog;
//    @Override
//    protected void onPreExecute() {
//        super.onPreExecute();
//        progressDialog = ProgressDialog.show(MainPageActivity.this, "Please wait.", "Downloading Master Data."
//                , true);
//    }
//
//    @Override
//    protected String doInBackground(String... params) {
//
//        return null;
//    }
//
//    @Override
//    protected void onPostExecute(String finalJson) {
//
//        progressDialog.dismiss();
//        super.onPostExecute(String.valueOf(finalJson));
//    }
//
//}

    public void MainPageNavigation() {
        gridView = (GridView) findViewById(R.id.gridView);
        MainPageCellAdapter adapter = new MainPageCellAdapter(MainPageActivity.this, cellIcon, cellTitle);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        if (GlobalVar.AskPermission_Camera(MainPageActivity.this, 6)) {
                            Intent deliverySheet = new Intent(getApplicationContext(), DeliverySheetActivity.class);
                            startActivity(deliverySheet);
                        }
                        break;
                    case 1:

                        if (GlobalVar.AskPermission_Location(MainPageActivity.this)) {
                            if (!GlobalVar.isMyServiceRunning(LocationService.class, getApplicationContext())) {
                                startService(new Intent(getBaseContext(),
                                        LocationService.class));
                            }
                            if (GlobalVar.AskPermission_Contcatcs(MainPageActivity.this)) {
                                Intent mapList = new Intent(getApplicationContext(), MyRouteActivity.class);
                                startActivity(mapList);
                            }
                        }
                        break;
                    case 2:
                        if (GlobalVar.AskPermission_Camera(MainPageActivity.this, 2)) {

                            Intent delivery = new Intent(getApplicationContext(), DeliveryActivity.class);
                            startActivity(delivery);

//                            ActivityCompat.requestPermissions(
//                                    MainPageActivity.this,
//                                    new String[]{Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG, Manifest.permission.WRITE_CONTACTS
//                                            , Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE
//                                            , Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS,
//                                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_NUMBERS},
//                                    7
//                            );

                        }
                        break;
                    case 3:
                        Intent multiDelivery = new Intent(getApplicationContext(), MultiDeliveryActivity.class);
                        startActivity(multiDelivery);
                        break;
                    case 4:
                        if (GlobalVar.AskPermission_Camera(MainPageActivity.this, 3)) {
                            Intent notDelivered = new Intent(getApplicationContext(), NotDeliveredActivity.class);
                            startActivity(notDelivered);
                        }
                        break;
                    case 5:
                        if (GlobalVar.AskPermission_Camera(MainPageActivity.this, 4)) {
                            Intent pickup = new Intent(getApplicationContext(), PickUpActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("class", "MainPage");
                            pickup.putExtras(bundle);
                            startActivity(pickup);
                        }
                        break;
                    case 6:
                        Intent waybillMeasurement = new Intent(getApplicationContext(), WaybillMeasurementActivity.class);
                        startActivity(waybillMeasurement);
                        //GlobalVar.GV().ShowSnackbar(mainRootView,"Under Constraction",GlobalVar.AlertType.Info);
                        break;
                    case 7:
                        Intent settings = new Intent(getApplicationContext(), SettingActivity.class);
                        startActivity(settings);
                        break;
//                    case 8:
//                        //   GlobalVar.GV().SyncData(getApplicationContext(), getWindow().getDecorView().getRootView());
//                        break;
                    case 8:

                        Intent checkCOD = new Intent(getApplicationContext(), CODCheckingActivity.class);
                        startActivity(checkCOD);
                        break;
                    case 9:
                        Intent pendingMoney = new Intent(getApplicationContext(), PendingMoneyActivity.class);
                        startActivity(pendingMoney);
                        break;
                    case 10:
                        Intent checkPoint = new Intent(getApplicationContext(), CheckPointsActivity.class);
                        startActivity(checkPoint);
                        break;
                    case 11:
                        Intent bookingList = new Intent(getApplicationContext(), BookingList.class);
                        startActivity(bookingList);
                        break;
                    case 12:
                        if (GlobalVar.AskPermission_Camera(MainPageActivity.this, 5)) {
//                            Intent atorigin = new Intent(getApplicationContext(), ArrivedatDestination.class);
                            Intent atorigin = new Intent(getApplicationContext(), AtOrigin.class);
                            startActivity(atorigin);
                        }
                        break;
                    case 13:
                        Intent history = new Intent(getApplicationContext(), History.class);
                        startActivity(history);
                        break;
                    case 14:
                        showPopup(MainPageActivity.this, p, 0);

                        break;
                    case 15:
                        showPopup(MainPageActivity.this, p, 1);
                        break;
                    case 16:
                        Intent contact = new Intent(getApplicationContext(), Contact.class);
                        startActivity(contact);
                        break;
                    case 17:
                        Intent cvds = new Intent(getApplicationContext(), ValidationDS.class);
                        startActivity(cvds);
                        break;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
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
                    //  GlobalVar.AskPermission_Location(MainPageActivity.this);
                    Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                    startActivity(i);
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Our app need the Contacts Permission,please kindly allow me", GlobalVar.AlertType.Error);
                }
                break;

            case 1:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (!GlobalVar.isMyServiceRunning(LocationService.class, getApplicationContext())) {
                        startService(new Intent(getBaseContext(),
                                LocationService.class));
                    }
                    if (GlobalVar.AskPermission_Contcatcs(MainPageActivity.this)) {
                        Intent mapList = new Intent(getApplicationContext(), MyRouteActivity.class);
                        startActivity(mapList);
                    }

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

                    Intent delivery = new Intent(getApplicationContext(), DeliveryActivity.class);
                    startActivity(delivery);

//                    ActivityCompat.requestPermissions(
//                            MainPageActivity.this,
//                            new String[]{Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG, Manifest.permission.WRITE_CONTACTS
//                                    , Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE
//                                    , Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS,
//                                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_NUMBERS},
//                            7
//                    );

                } else {
                    //  GlobalVar.AskPermission_Location(MainPageActivity.this);
                    Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                    startActivity(i);
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Our app need the Contacts Permission,please kindly allow me", GlobalVar.AlertType.Error);
                }
                break;
            case 3:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent notDelivered = new Intent(getApplicationContext(), NotDeliveredActivity.class);
                    startActivity(notDelivered);

                } else {
                    Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                    startActivity(i);
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Our app need the Contacts Permission,please kindly allow me", GlobalVar.AlertType.Error);
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
                    Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                    startActivity(i);
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Our app need the Contacts Permission,please kindly allow me", GlobalVar.AlertType.Error);
                }
                break;
            case 5:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent atorigin = new Intent(getApplicationContext(), AtOrigin.class);
                    startActivity(atorigin);

                } else {
                    Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                    startActivity(i);
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Our app need the Contacts Permission,please kindly allow me", GlobalVar.AlertType.Error);
                }
                break;
            case 6:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent deliverySheet = new Intent(getApplicationContext(), DeliverySheetActivity.class);
                    startActivity(deliverySheet);

                } else {
                    Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                    startActivity(i);
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Our app need the Contacts Permission,please kindly allow me", GlobalVar.AlertType.Error);
                }

                break;

            case 7:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    boolean redirect = true;
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        for (int i = 0; i < permissions.length; i++) {
                            int result = ContextCompat.checkSelfPermission(this, permissions[i]);
                            if (result == PackageManager.PERMISSION_DENIED) {
                                redirect = false;
                                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                    startActivity(intent);
                                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Our app need the those Permission,please kindly allow me", GlobalVar.AlertType.Error);
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
                                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                startActivity(intent);
                                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Our app need the those Permission,please kindly allow me", GlobalVar.AlertType.Error);
                                break;
                            }
                        }
                    }
                }
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            GlobalVar.GV().EmployID = savedInstanceState.getInt("EmployID");
            GlobalVar.GV().UserID = savedInstanceState.getInt("UserID");
            GlobalVar.GV().StationID = savedInstanceState.getInt("StationID");
            GlobalVar.GV().EmployMobileNo = savedInstanceState.getString("EmployMobileNo");
            GlobalVar.GV().EmployName = savedInstanceState.getString("EmployName");
            GlobalVar.GV().EmployStation = savedInstanceState.getString("EmployStation");
            GlobalVar.GV().currentSettings = savedInstanceState.getParcelable("currentSettings");
            GlobalVar.GV().currentSettings.ID = savedInstanceState.getInt("currentSettingsID");
        }
        super.onRestoreInstanceState(savedInstanceState);
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
        super.onSaveInstanceState(outState);
    }

    ProgressDialog progressDialog;

    private class GetMasterData extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            if (progressDialog == null)
                progressDialog = ProgressDialog.show(MainPageActivity.this, "Please wait.", "Bringing Master Details.", true);
            super.onPreExecute();

        }

        @SuppressWarnings("deprecation")
        protected String doInBackground(String... params) {


            //uploadfilescount = uploadfilescount + 1;

            GlobalVar.GV().GetMasterData(MainPageActivity.this, getWindow().getDecorView().getRootView(), progressDialog);

//            if (GlobalVar.GV().StationList.size() <= 0)
//                GlobalVar.GV().GetStationList(true, getApplicationContext(), getWindow().getDecorView().getRootView());
//
//            if (GlobalVar.GV().DeliveryStatusList.size() <= 0)
//                GlobalVar.GV().GetDeliveryStatusList(true, getApplicationContext(), getWindow().getDecorView().getRootView());
//
//            if (GlobalVar.GV().CheckPointTypeList.size() <= 0)
//                GlobalVar.GV().GetCheckPointTypeList(true, getApplicationContext(), getWindow().getDecorView().getRootView());
//
//            if (GlobalVar.GV().CheckPointTypeDetailList.size() <= 0)
//                GlobalVar.GV().GetCheckPointTypeDetailList(true, 0, getApplicationContext(), getWindow().getDecorView().getRootView());
//
//            if (GlobalVar.GV().CheckPointTypeDDetailList.size() <= 0)
//                GlobalVar.GV().GetCheckPointTypeDDetailList(true, 0, getApplicationContext(), getWindow().getDecorView().getRootView());
//
//            if (GlobalVar.GV().NoNeedVolumeReasonList.size() <= 0)
            //              GlobalVar.GV().GetNoNeedVolumeReasonList(true, getApplicationContext(), getWindow().getDecorView().getRootView());
//
//
//
//            boolean loop = false;
//            while (!loop) {
//                if (GlobalVar.gs && GlobalVar.dsl && GlobalVar.cptl && GlobalVar.cptdl && GlobalVar.cptddl && GlobalVar.nnvdl) {
//                    if (progressDialog != null)
//                        progressDialog.dismiss();
//
//                    break;
//                }
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//

            return null;

        }

        @Override
        protected void onPostExecute(String result) {

//            checkthreadalive();

            super.onPostExecute("");

        }
    }

    public void checkthreadalive() {
        int checkthread = 1;
        for (int i = 0; i < asyncTasks.size(); i++) {
            AsyncTask<String, Integer, String> asyncTaskItem = (AsyncTask<String, Integer, String>) asyncTasks
                    .get(i);

            if (asyncTaskItem.getStatus() == AsyncTask.Status.FINISHED)
                checkthread++;

        }
//        if (checkthread == asyncTasks.size()) {
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
//        }


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

        popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setFocusable(true);
        popup.update();
        popup.setOutsideTouchable(false);
        int OFFSET_X = 30;
        int OFFSET_Y = 30;
        // Clear the default translucent background
        popup.setBackgroundDrawable(new BitmapDrawable());
        popup.showAtLocation(layout, Gravity.CENTER, p.x + OFFSET_X, p.y + OFFSET_Y);
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


}
