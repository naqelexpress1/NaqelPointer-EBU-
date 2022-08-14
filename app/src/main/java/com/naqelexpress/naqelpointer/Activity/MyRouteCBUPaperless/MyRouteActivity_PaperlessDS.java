package com.naqelexpress.naqelpointer.Activity.MyRouteCBUPaperless;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.google.gson.Gson;
import com.naqelexpress.naqelpointer.Activity.Booking.BookingPlanActivity;
import com.naqelexpress.naqelpointer.Activity.Waybill.WaybillPlanActivity;
import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.MyRouteShipments;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.JSON.Request.BringMyRouteShipmentsRequest;
import com.naqelexpress.naqelpointer.Models.Enum.Enum;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.Receiver.LocationupdateInterval;
import com.naqelexpress.naqelpointer.callback.AlertCallback;
import com.naqelexpress.naqelpointer.service.LocationService;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import Error.ErrorReporter;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class MyRouteActivity_PaperlessDS
        extends AppCompatActivity implements RouteListAdapterPaperlessDS.RouteAdapterListener, AlertCallback {
    private RecyclerView mapListview;
    private RouteListAdapterPaperlessDS adapter;
    Button btnStartTrip, btnCloseTrip;
    TextView txtStartTrip, txtCloseTrip, txtplannedlocationcount, txttoltallocationcount;
    public static ArrayList<Location> places = new ArrayList<>();//96346
    public static ArrayList<Location> Optmizeplaces = new ArrayList<>();//96346
    protected boolean flag_thread = false;
    static int progressflag = 0;

    private SearchView searchView;
    TextView lastseqstoptime;

    ArrayList<MyRouteShipments> myRouteShipmentstemp = new ArrayList<>();
    public static String AreaData = "";
    TableLayout tl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ErrorReporter());

        setContentView(R.layout.myroutenew);

//        String asd = "asd";
//        Integer.parseInt(asd);
        AreaData = "";
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Search");


        progressflag = 0;
        GlobalVar.GV().haslocation.clear();
        mapListview = (RecyclerView) findViewById(R.id.myRouteListView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mapListview.setLayoutManager(mLayoutManager);
        lastseqstoptime = (TextView) findViewById(R.id.laststopseqtime);

        setAdapter();


        whiteNotificationBar(mapListview);

        btnStartTrip = (Button) findViewById(R.id.btnStartTrip);
        btnCloseTrip = (Button) findViewById(R.id.btnCloseTrip);

        txtStartTrip = (TextView) findViewById(R.id.txtStartTrip);
        txtCloseTrip = (TextView) findViewById(R.id.txtCloseTrip);

        txtplannedlocationcount = (TextView) findViewById(R.id.plannedlocation);
        txttoltallocationcount = (TextView) findViewById(R.id.totallocation);

        tl = (TableLayout) findViewById(R.id.tl);
        tl.setVisibility(View.VISIBLE);

        if (GlobalVar.GV().CourierDailyRouteID == 0) {
            btnStartTrip.setVisibility(View.VISIBLE);
            txtStartTrip.setVisibility(View.VISIBLE);
            btnCloseTrip.setVisibility(View.GONE);
            txtCloseTrip.setVisibility(View.GONE);
        } else {
            btnStartTrip.setVisibility(View.GONE);
            txtStartTrip.setVisibility(View.GONE);
            if (GlobalVar.GV().myRouteShipmentList.size() > 0) {
                btnCloseTrip.setVisibility(View.GONE);
                txtCloseTrip.setVisibility(View.GONE);
            } else {
                btnCloseTrip.setVisibility(View.VISIBLE);
                txtCloseTrip.setVisibility(View.VISIBLE);
            }
        }

        txtStartTrip.setVisibility(View.GONE);

        txtCloseTrip.setVisibility(View.GONE);

        GlobalVar.GV().myRouteShipmentList.clear();
        btnStartTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCourierDailyRouteID(true, 0);
                flag_thread = true;

            }
        });

        btnCloseTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                GlobalVar.GV().CourierDailyRouteID = dbConnections.getMaxID("CourierDailyRoute Where EmployID = " + GlobalVar.GV().EmployID + " and EndTime is NULL ", getApplicationContext());
                if (GlobalVar.GV().CourierDailyRouteID > 0) {
                    dbConnections.CloseCurrentCourierDailyRoute(getWindow().getDecorView().getRootView(), getApplicationContext());
                    dbConnections.close();
                    finish();
                }
            }
        });

        Button critical = (Button) findViewById(R.id.critical);
        critical.setVisibility(View.VISIBLE);
        critical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("EmployID", GlobalVar.GV().EmployID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new CriticalRouteLine().execute(jsonObject.toString());
            }
        });

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                int menuItemWidth = 120;
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                openItem.setBackground(R.color.NaqelBlue);
                //(new ColorDrawable(Color.rgb(0xC9, 0xC9,0xCE)));
                // set item width
                openItem.setWidth(menuItemWidth);
                // set item title
                openItem.setTitle("Open");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

                // create "Deliver Later" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(R.color.NaqelRed);
                //(new ColorDrawable(Color.rgb(0xF9,0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(menuItemWidth);
                // set a icon
                //deleteItem.setIcon(R.drawable.settings);
                deleteItem.setTitle("Delete");
                // set item title font color
                deleteItem.setTitleColor(Color.WHITE);
                // set item title fontsize
                deleteItem.setTitleSize(18);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };


        GlobalVar.GV().CourierDailyRouteID = 0;
        checkCourierDailyRouteID(false, 1);
        GetPlannedLocation(); //please uncomment in live
        IstotalLocationcount();

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        Cursor result = dbConnections.Fill("select * from SkipRouteSequencer ", getApplicationContext());

        if (result.getCount() > 0) {
            deleteConfirmRoute_SkipRoute();

        }
        result.close();
        dbConnections.close();
        //MyRouteCompliance1();
        //deleteConfirmRoute();

    }

    private void ValidateDatas() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // new DownloadJSON().execute();
                try {
                    if (!flag_thread) {
                        flag_thread = true;

                        progressflag = 0;
                        BringMyRouteShipmentsRequest bringMyRouteShipmentsRequest = new BringMyRouteShipmentsRequest();
                        BringMyRouteShipments(bringMyRouteShipmentsRequest, 1);


                    }
                    handler.postDelayed(this, 20000);
                } catch (Exception e) {
                    flag_thread = false;
                    handler.postDelayed(this, 20000);
                    Log.e("Dashboard thread", e.toString());
                }

            }
        }, 20000);
    }

    private void checkCourierDailyRouteID(boolean CreateNewRoute, int buttonclick) {
        final DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        if (GlobalVar.GV().CourierDailyRouteID == 0) {
            GlobalVar.GV().CourierDailyRouteID = dbConnections.getMaxID("CourierDailyRoute Where EmployID = " + GlobalVar.GV().EmployID + " and EndTime is NULL ", getApplicationContext());
            if (GlobalVar.GV().CourierDailyRouteID != 0) {
                GlobalVar.GV().LoadMyRouteShipments("OrderNo", true, getApplicationContext(),
                        getWindow().getDecorView().getRootView());
                myRouteShipmentstemp.clear();
                myRouteShipmentstemp.addAll(GlobalVar.GV().myRouteShipmentList);

                RouteMap.places.clear();
                RouteMap_byDistrict.places.clear();

//                //UnComment
//                Intent intent = new Intent(MyRouteActivity_Complaince_GroupbyPhn.this, RouteMap.class);
//                intent.putParcelableArrayListExtra("myroute", GlobalVar.GV().myRouteShipmentList);
//                startActivityForResult(intent, 1);
//                //*******

                DuplicateCustomer();


                if (GlobalVar.GV().myRouteShipmentList.size() > 0) {
                    btnStartTrip.setVisibility(View.GONE);
                    txtStartTrip.setVisibility(View.GONE);

                    btnCloseTrip.setVisibility(View.GONE);
                    txtCloseTrip.setVisibility(View.GONE);

                    if (!isRestarted())
                        return;

                    MyRouteCompliance();


                    if (IsHasLocation()) {
                        GlobalVar.GV().LoadMyRouteShipments_RouteOpt("ItemNo", true, getApplicationContext()
                                , getWindow().getDecorView().getRootView());

//                        adapter = new RouteListAdapterGroupbyPNo(getApplicationContext(), GlobalVar.GV().myRouteShipmentList,
//                                "CourierKpi", this);
//                        mapListview.setAdapter(adapter);
                        setAdapter();
                        hideenableListview();

                    } else {
                        setAdapter();
//                        adapter = new RouteListAdapterGroupbyPNo(getApplicationContext(), GlobalVar.GV().myRouteShipmentList,
//                                "CourierKpi", this);
//                        mapListview.setAdapter(adapter);
                    }

                    //ValidateDatas();

                }

            }
            dbConnections.close();
        } else if (GlobalVar.GV().myRouteShipmentList.size() == 0) {
            btnStartTrip.setVisibility(View.GONE);
            txtStartTrip.setVisibility(View.GONE);

            btnCloseTrip.setVisibility(View.VISIBLE);
            txtCloseTrip.setVisibility(View.VISIBLE);
        } else {
            btnStartTrip.setVisibility(View.GONE);
            txtStartTrip.setVisibility(View.GONE);

            btnCloseTrip.setVisibility(View.GONE);
            txtCloseTrip.setVisibility(View.GONE);
        }
        // && CreateNewRoute
        if (GlobalVar.GV().CourierDailyRouteID == 0 && CreateNewRoute) {
            BringMyRouteShipmentsRequest bringMyRouteShipmentsRequest = new BringMyRouteShipmentsRequest();
            progressflag = 1;
            BringMyRouteShipments(bringMyRouteShipmentsRequest, buttonclick);
        }


    }

    private void IsplannedLocationLoaded() {


        final DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        if (dbConnections.isMyRouteComplainceselect(getApplicationContext())) {

//            Cursor result = dbConnections.Fill("select * from plannedLocation where Date = '" + GlobalVar.getDate() + "'" +
//                    " and EmpID = " + GlobalVar.GV().EmployID, getApplicationContext());

            Cursor result = dbConnections.Fill("select * from plannedLocation where EmpID = " + GlobalVar.GV().EmployID, getApplicationContext());

            int Legscount = plannedCount();
//            GetSeqWaybillNo();

            if (result.getCount() == 1) {
                if (places.size() == 0) {
                    alertPlannedLocation();
                }
                return;
            }

            //if (result.getCount() != places.size()) {
            if (result.getCount() != Legscount) {
                alertPlannedLocation();


            }
            result.close();
        }
    }

//    private int plannedCount() {
//        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
//        Cursor plannedcount = dbConnections.Fill("select * from OptimizeLastSeqStopTime ", getApplicationContext());
//        int Legscount = 0;
//        if (plannedcount.getCount() > 0) {
//            plannedcount.moveToFirst();
//            Legscount = plannedcount.getInt(plannedcount.getColumnIndex("GooglePlannedLocationCount"));
//        }
//        plannedcount.close();
//        return Legscount;
//    }

    private int plannedCount() {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        Cursor plannedcount = dbConnections.Fill("select * from OptimizeLastSeqStopTime ", getApplicationContext());
        int Legscount = 0;
        if (plannedcount.getCount() > 0) {
            plannedcount.moveToFirst();
            do {
                Legscount = Legscount + plannedcount.getInt(plannedcount.getColumnIndex("GooglePlannedLocationCount"));
            } while (plannedcount.moveToNext());

        }
        plannedcount.close();
        return Legscount;
    }

    private void alertPlannedLocation() {
        SweetAlertDialog eDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
        eDialog.setTitleText("Kindly Load Planned Location");
        eDialog.setContentText("Be patient until Load Fully");
        eDialog.setConfirmText("OK");
        eDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismissWithAnimation();
                try {
                    GlobalVar.GV().myRouteShipmentList.clear();
                    GlobalVar.GV().LoadMyRouteShipments("OrderNo", true, getApplicationContext(),
                            getWindow().getDecorView().getRootView());

                    if (getAreaWaybills().size() <= GlobalVar.GV().isRouteLineSeqLimit) {
                        Intent intent = new Intent(MyRouteActivity_PaperlessDS.this, RouteMap.class);
                        intent.putParcelableArrayListExtra("myroute", GlobalVar.GV().myRouteShipmentList);
                        intent.putParcelableArrayListExtra("places", Optmizeplaces);
                        intent.putExtra("AreaData", AreaData);
                        startActivityForResult(intent, 1);
                    } else {
                        Intent intent = new Intent(MyRouteActivity_PaperlessDS.this, RouteMap_byDistrict.class);
                        intent.putParcelableArrayListExtra("myroute", GlobalVar.GV().myRouteShipmentList);
                        intent.putParcelableArrayListExtra("places", Optmizeplaces);
                        intent.putExtra("AreaData", AreaData);
                        startActivityForResult(intent, 1);
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }


            }
        });
        eDialog.setCancelable(false);
        eDialog.show();
    }

    private boolean IsHasLocation() {

        boolean ishasLocation = false;
        final DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        Cursor ds = dbConnections.Fill("select count(ID) totalcount from MyRouteShipments " +
                "where Latitude <>'0' and Longitude <> '0' and Latitude <>'' and Longitude <> '' and IsPlan = 1  and CourierDailyRouteID = " +
                GlobalVar.GV().CourierDailyRouteID, getApplicationContext());
        if (ds.getCount() > 0) {
            ds.moveToFirst();
            int count = ds.getInt(ds.getColumnIndex("totalcount"));
            if (count >= 1)
                ishasLocation = true;
        }
        return ishasLocation;
    }

    private boolean IstotalLocationcount() {

        boolean ishasLocation = false;
        final DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        Cursor ds = dbConnections.Fill("select ID  from MyRouteShipments " +
                "where Latitude <> '0' and Longitude <> '0' and Latitude <>'' and Longitude <> ''  and CourierDailyRouteID = " +
                GlobalVar.GV().CourierDailyRouteID, getApplicationContext());
        if (ds != null)
            txttoltallocationcount.setText("Total Location " + String.valueOf(ds.getCount()));
//        if (ds.getCount() > 0) {
//            ds.moveToFirst();
//            int count = ds.getInt(ds.getColumnIndex("totalcount"));
//            if (count >= 1)
//                ishasLocation = true;


//        }
        ds.close();
        dbConnections.close();
        return ishasLocation;
    }


    public void GetSeqWaybillNo() {


        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);

        Cursor result = dbConnections.Fill("select * from SuggestLocations where Date = '" + GlobalVar.getDate() + "'" +
                " and EmpID = " + GlobalVar.GV().EmployID, getApplicationContext());
        if (result != null && result.getCount() > 0) {
            places.clear();
            result.moveToFirst();
            do {

                String data = result.getString(result.getColumnIndex("StringData"));
                String split[] = data.split("@");
                for (int i = 0; i < split.length; i++) {
                    String temp[] = split[i].split("_");
                    Location sp = new Location("");
                    sp.setLatitude(Double.parseDouble(temp[1]));
                    sp.setLongitude(Double.parseDouble(temp[2]));
                    sp.setAltitude(Double.parseDouble(temp[4]));
                    sp.setSpeed(Float.parseFloat(temp[temp.length - 1]));
                    if (i == 0)
                        sp.setAltitude(0);
                    else
                        sp.setAltitude(Double.parseDouble(temp[0]));
                    places.add(sp);
                }


            }
            while (result.moveToNext());
        }

        result.close();
        dbConnections.close();

    }

    private boolean isRestarted() {

        final DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        if (!dbConnections.GetMyRouteShipmentsIsRestarted(getApplicationContext())) {

            SweetAlertDialog eDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);

            eDialog.setCancelable(false);
            eDialog.setTitleText("Info");
            eDialog.setContentText("Kindly Restart the Device for better performance before start Route");
            eDialog.setConfirmText("Ok");

            eDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {

                    sDialog.dismissWithAnimation();
                    finish();
                }
            });
            eDialog.show();
            dbConnections.close();
            return false;
        }
        return true;
    }

    private void MyRouteCompliance() {

        final DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        if (GlobalVar.GV().CourierDailyRouteID == 0)
            return;

        if (IsHasLocation()) {
            //Comment this line Google Given Less Location compare with Actual One
            IsplannedLocationLoaded();

            if (!dbConnections.isMyRouteComplaince(getApplicationContext())) {
                SweetAlertDialog eDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);

                eDialog.setCancelable(false);
                eDialog.setTitleText("Are you sure?");
                eDialog.setContentText("Are you follow to Deliver by Google Map");
                eDialog.setConfirmText("Yes");
//                eDialog.setCancelText("Yes");

                eDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        dbConnections.InsertMyRouteComplaince(getApplicationContext(), 1);
                        sDialog.dismissWithAnimation();

                        try {
                            if (getAreaWaybills().size() <= GlobalVar.GV().isRouteLineSeqLimit) {
                                Intent intent = new Intent(MyRouteActivity_PaperlessDS.this, RouteMap.class);
                                intent.putParcelableArrayListExtra("myroute", GlobalVar.GV().myRouteShipmentList);
                                intent.putParcelableArrayListExtra("places", Optmizeplaces);
                                intent.putExtra("AreaData", AreaData);
                                startActivityForResult(intent, 1);
                            } else {
                                Intent intent = new Intent(MyRouteActivity_PaperlessDS.this, RouteMap_byDistrict.class);
                                intent.putParcelableArrayListExtra("myroute", GlobalVar.GV().myRouteShipmentList);
                                intent.putParcelableArrayListExtra("places", Optmizeplaces);
                                intent.putExtra("AreaData", AreaData);
                                startActivityForResult(intent, 1);
                            }
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }


                    }
                });
                eDialog.show();
//                eDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                    @Override
//                    public void onClick(SweetAlertDialog sDialog) {
////                        dbConnections.InsertMyRouteComplaince(getApplicationContext(), 1);
//                        sDialog.dismissWithAnimation();
////
////                        try {
////                            Intent intent = new Intent(MyRouteActivity_Complaince.this, RouteMap.class);
////                            intent.putParcelableArrayListExtra("myroute", GlobalVar.GV().myRouteShipmentList);
////                            startActivity(intent);
////                        } catch (Exception e) {
////                            System.out.println(e.getMessage());
////                        }
//
//
//                    }
//                });

//                eDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                    @Override
//                    public void onClick(SweetAlertDialog sDialog) {
////                        dbConnections.InsertMyRouteComplaince(getApplicationContext(), 2);
//                        sDialog.dismissWithAnimation();
//                        //sDialog.dismissWithAnimation();
//                        dbConnections.InsertMyRouteComplaince(getApplicationContext(), 1);
//                        sDialog.dismissWithAnimation();
//
//                        try {
//                            Intent intent = new Intent(MyRouteActivity_Complaince_GroupbyPhn.this, RouteMap.class);
//                            intent.putParcelableArrayListExtra("myroute", GlobalVar.GV().myRouteShipmentList);
//                            startActivityForResult(intent, 1);
//                        } catch (Exception e) {
//                            System.out.println(e.getMessage());
//                        }
//
//                    }
//                });
                eDialog.show();
            }
        } else {
            dbConnections.InsertMyRouteComplaince(getApplicationContext(), 2);
        }

        startActualRoute();
        RestartServiceLocationMorethan30();


        dbConnections.close();
    }


    private void MyRouteCompliance1() {

        final DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        dbConnections.clearMyRouteComplaince(getApplicationContext());

        if (GlobalVar.GV().CourierDailyRouteID == 0)
            return;


        SweetAlertDialog eDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);

        eDialog.setCancelable(false);
        eDialog.setTitleText("Are you sure?");
        eDialog.setContentText("Are you follow to Deliver by Google Map");
        eDialog.setConfirmText("Yes");
//                eDialog.setCancelText("Yes");

        eDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {

                sDialog.dismissWithAnimation();

                try {
                    Intent intent = new Intent(MyRouteActivity_PaperlessDS.this, RouteMap_byDistrict.class);
                    intent.putParcelableArrayListExtra("myroute", GlobalVar.GV().myRouteShipmentList);
                    intent.putParcelableArrayListExtra("places", Optmizeplaces);
                    intent.putExtra("AreaData", AreaData);
                    startActivityForResult(intent, 1);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }


            }
        });
        eDialog.show();

        dbConnections.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.myroutemenu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                adapter.getFilter().filter(query);
                return false;
            }
        });
//        return true;

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void returnOk(int ok, Activity activity) {
        exitdialog();
    }

    @Override
    public void returnCancel(int cancel, SweetAlertDialog alertDialog) {
        this.alertDialog = alertDialog;
    }


    class Optimization {
        public String CurrentLocation;
        public String EmployID;
        public String FleetNo;
        public String Waybills;
    }

    double Latitude = 0, Longitude = 0;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.OptimizeShipments:

                return true;
            case R.id.deleteall:
                deleteConfirmRoute();

                return true;
            case R.id.mnuShowDeliverySheetOrder:
                //OrderNo
//                if (GlobalVar.GV().GetDivision(getApplicationContext())) {
//                    DBConnections dbConnections1 = new DBConnections(getApplicationContext(), null);
//                    Cursor result = dbConnections1.Fill("select * from SuggestLocations where Date = '" + GlobalVar.getDate() + "'" +
//                            " and EmpID = " + GlobalVar.GV().EmployID, getApplicationContext());
//                    if (result != null && result.getCount() > 0) {
//                        GlobalVar.GV().LoadMyRouteShipments_RouteOpt("ItemNo", true, getApplicationContext()
//                                , getWindow().getDecorView().getRootView());
//                    } else {
//                        try {
//                            Intent intent = new Intent(MyRouteActivity_Complaince.this, RouteMap.class);
//                            intent.putParcelableArrayListExtra("myroute", GlobalVar.GV().myRouteShipmentList);
//                            startActivity(intent);
//                        } catch (Exception e) {
//                            System.out.println(e.getMessage());
//                        }
//                    }
//                } else

                //delete below


                //uncomment for Live
//                GlobalVar.GV().LoadMyRouteShipments_RouteOpt("ItemNo", true, getApplicationContext()
//                        , getWindow().getDecorView().getRootView());
//
//                adapter = new RouteListAdapterGroupbyPNo(getApplicationContext(), GlobalVar.GV().myRouteShipmentList,
//                        "CourierKpi", this);
//                mapListview.setAdapter(adapter);
//
//                hideenableListview();


//                ArrayList<MyRouteShipments> myRouteShipmentList1 = GlobalVar.GV().LoadMyRouteShipmentsOptimizeMap("OrderNo", true, getApplicationContext(),
//                        getWindow().getDecorView().getRootView());
//
//                try {
//                    Intent intent = new Intent(MyRouteActivity_Complaince_GroupbyPhn.this, RouteMap.class);
//                    intent.putParcelableArrayListExtra("myroute", myRouteShipmentList1);
//                    startActivityForResult(intent, 2);
//                } catch (Exception e) {
//                    System.out.println(e.getMessage());
//                }

                return true;
//            case R.id.DeleteAll:
////                //TODO remove delete all from the menu itself.
////                GlobalVar.GV().kpi = new ArrayList<>();
////                adapter = new RouteListAdapter(getApplicationContext(), GlobalVar.GV().kpi);
////                mapListview.setAdapter(adapter);
////                btnCloseTrip.setVisibility(View.VISIBLE);
////                txtCloseTrip.setVisibility(View.VISIBLE);
//                GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootView,"You don't have a privillages.", GlobalVar.AlertType.Warning);
//                return true;
//            case R.id.CheckNewBooking:
//                //com.naqelexpress.naqelpointer.JSON.DataSync dataSync = new DataSync();
//                //dataSync.CheckOnlineBooking();
//                GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootView,"You don't have a privillages.", GlobalVar.AlertType.Warning);
//                return true;
            case R.id.mnuSyncData:
                //   GlobalVar.GV().SyncData(getApplicationContext(), getWindow().getDecorView().getRootView());
                return true;
            case R.id.groupmap:
//                if (IsHasLocation() && GlobalVar.GV().myRouteShipmentList.size() == 1) {
//                    try {
//                        Intent intent = new Intent(MyRouteActivity_Complaince_GroupbyPhn.this, RouteMap_SingleWaybill.class);
//                        intent.putParcelableArrayListExtra("myroute", GlobalVar.GV().myRouteShipmentList);
//                        startActivity(intent);
//                    } catch (Exception e) {
//                        System.out.println(e.getMessage());
//                    }
//
//                } else if (IsHasLocation()) {
//                    try {
//                        Intent intent = new Intent(MyRouteActivity_Complaince_GroupbyPhn.this, RouteMap.class);
//                        intent.putParcelableArrayListExtra("myroute", GlobalVar.GV().myRouteShipmentList);
//                        startActivity(intent);
//                    } catch (Exception e) {
//                        System.out.println(e.getMessage());
//                    }
//                } else
//                    Toast.makeText(getApplicationContext(), "No Location ", Toast.LENGTH_LONG).show();

                if (!GlobalVar.isMyroutesync(getApplicationContext())) {
                    GlobalVar.ShowDialog(MyRouteActivity_PaperlessDS.this,
                            "Info", "Please wait, try after sometime ", true);
                    return false;
                }
                ArrayList<MyRouteShipments> myRouteShipmentList = GlobalVar.GV().LoadMyRouteShipmentsOptimizeMap("OrderNo", true, getApplicationContext(),
                        getWindow().getDecorView().getRootView());

                try {
                    int areaCount = getAreaWaybills().size();
                    if (areaCount == 0) {
                        GlobalVar.GV().alertMsgAll("Info", "No Planned Location", MyRouteActivity_PaperlessDS.this, Enum.ERROR_TYPE,
                                "MyRouteActivity_Complaince_GroupbyPhn");
                        return true;
                    }
                    if (getAreaWaybills().size() <= GlobalVar.GV().isRouteLineSeqLimit) {
                        Intent intent = new Intent(MyRouteActivity_PaperlessDS.this, OptimiseMap.class);
                        intent.putParcelableArrayListExtra("myroute", myRouteShipmentList);
                        startActivityForResult(intent, 2);
                    } else {
                        Intent intent = new Intent(MyRouteActivity_PaperlessDS.this, OptimiseMapbyDistrict.class);
                        intent.putParcelableArrayListExtra("myroute", myRouteShipmentList);
                        startActivityForResult(intent, 2);
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                return true;
            case R.id.movingmap:
                try {
                    Intent intent = new Intent(MyRouteActivity_PaperlessDS.this, MapMovingOnCurLatLng.class);
                    startActivity(intent);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                return true;

//            case R.id.camera:
//
//                if (!GlobalVar.GV().checkPermission(MyRouteActivity_Complaince_GroupbyPhn.this, GlobalVar.PermissionType.Camera)) {
//                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.NeedCameraPermission), GlobalVar.AlertType.Error);
//                    GlobalVar.GV().askPermission(MyRouteActivity_Complaince_GroupbyPhn.this, GlobalVar.PermissionType.Camera);
//                } else {
//                    Intent intent = new Intent(getApplicationContext().getApplicationContext(), NewBarCodeScanner.class);
//                    startActivityForResult(intent, GlobalVar.GV().CAMERA_PERMISSION_REQUEST);
//                }
//                return true;

            case R.id.refresh:
                if (!GlobalVar.GV().isSeqComplete(getApplicationContext())) {
                    DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                    dbConnections.RefreshMyRouteActionActivitySeqNo(getApplicationContext(), GlobalVar.GV().myRouteShipmentList);
                    dbConnections.close();
                    finish();
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GlobalVar.GV().CAMERA_PERMISSION_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    if (extras.containsKey("barcode")) {
                        String query = extras.getString("barcode");
                        if (query.length() > 8)
                            query = query.substring(0, 8);
                        searchView.setIconified(false);
                        searchView.setQuery(query, false);
                        //searchView.clearFocus();
//                        getSupportActionBar().setTitle(query); //.setText(barcode);
//                        adapter.getFilter().filter(query);

//                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//                            @Override
//                            public boolean onQueryTextSubmit(String query) {
//                                // filter recycler view when query submitted
//                                adapter.getFilter().filter(query);
//                                return false;
//                            }
//
//                            @Override
//                            public boolean onQueryTextChange(String query) {
//                                // filter recycler view when text is changed
//                                adapter.getFilter().filter(query);
//                                return false;
//                            }
//                        });


                    }
                }

//                final Barcode barcode = data.getParcelableExtra("barcode");
//                txtBarCode.post(new Runnable()
//                {
//                    @Override
//                    public void run()
//                    {
//                        txtBarCode.setText(barcode.displayValue);
//
//                        if (txtBarCode.getText().toString().length() > 8)
//                            AddNewPiece();
//                    }
//                });
            }
        } else if (requestCode == 1 && resultCode == RESULT_OK) { //Refresh Data
            Bundle extras = data.getExtras();

            if (extras != null) {
                if (extras.containsKey("result")) {
                    String result = extras.getString("result");
                    if (result.equals("finish"))
                        finish();
                    if (result.equals("refreshdata") && !GlobalVar.GV().isSeqComplete(getApplicationContext())) {
                        GlobalVar.GV().LoadMyRouteShipments_RouteOpt("ItemNo", true, getApplicationContext()
                                , getWindow().getDecorView().getRootView());


//                        adapter = new RouteListAdapterGroupbyPNo(getApplicationContext(), GlobalVar.GV().myRouteShipmentList,
//                                "CourierKpi", this);
//                        mapListview.setAdapter(adapter);
                        setAdapter();
                        adapter.notifyDataSetChanged();
                        hideenableListview();
                        GetPlannedLocation();
                    } else {
                        if (GlobalVar.GV().GetMyRouteShipmentsCount(getApplicationContext()) != GlobalVar.GV().myRouteShipmentList.size())
                            finish();
                    }

                }
            }
        }
    }


    //------------------------Bring My Route Shipments -------------------------------
    public void BringMyRouteShipments(BringMyRouteShipmentsRequest bringMyRouteShipmentsRequest,
                                      int buttonclick) {

        String jsonData = JsonSerializerDeserializer.serialize(bringMyRouteShipmentsRequest, true);
        new BringMyRouteShipmentsList().execute(jsonData, String.valueOf(buttonclick));
    }

    private boolean GetDivision() {
        String division = GlobalVar.GV().getDivisionID(getApplicationContext(), GlobalVar.GV().EmployID);

        if (division.equals("Express") || division.equals("IRS"))
            return false;
        else
            return true;

    }

    private void setAdapter() {
        adapter = new RouteListAdapterPaperlessDS(getApplicationContext(), GlobalVar.GV().myRouteShipmentList,
                "CourierKpi", this);
        mapListview.setAdapter(adapter);
    }

    private ProgressDialog progressDialog;
    private ProgressDialog progressDialog_NotifyClient;
    //private ProgressDialog progressDialog_critical;

    private class BringMyRouteShipmentsList extends AsyncTask<String, Void, String> {

        String result = "";
        StringBuffer buffer;
        int buttonclick;
        String DomainURL = "";
        String isInternetAvailable = "";

        @Override
        protected void onPreExecute() {
            if (progressflag == 1)
                progressDialog = ProgressDialog.show(MyRouteActivity_PaperlessDS.this, "Please wait.", "Downloading Shipments Details.", true);
            DomainURL = GlobalVar.GV().GetDomainURL(getApplicationContext());
        }

        @Override
        protected String doInBackground(String... params) { //17748
            String jsonData = params[0];
            buttonclick = Integer.parseInt(params[1]);

            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                String function = "BringDeliverySheetbyOFDPiece_ExcludeRoute_GroupbyDistrict";//""BringDeliverySheetbyOFDPiece_ExcludeRoute"; //CBU division BringDeliverySheetbyOFDPiece
//                function = "BringDeliverySheetbyOFDPiece_PlanAll"; //BringDeliverySheetbyOFDPiece_PlanAll
//                String function = "BringMyRouteShipments";
                if (!GetDivision())
                    function = "BringMyRouteShipments"; //EBU Divison
                if (GlobalVar.GV().isFortesting) {
                    // function = "BringDeliverySheetbyOFDPiece_ExcludeRoute"; //EBU Divison //BringDeliverySheetFortest for test one
                    function = "BringDeliverySheetbyOFDPiece_PlanAll";
                }

//                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "BringMyRouteShipments"); //Geofence
                URL url = new URL(DomainURL + function); //Geofence
                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setReadTimeout(130000);
                httpURLConnection.setConnectTimeout(130000);
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
                isInternetAvailable = e.toString();
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

//            if (CourierKpi.this.isDestroyed()) {
//                return;
//            }
            DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
            dbConnections.clearAllCourierDailyRoute(getApplicationContext());
            if (finalJson != null) {
                if (buttonclick == 0) {
                    super.onPostExecute(String.valueOf(finalJson));

                    setDatatoAdapter(finalJson);

                } else
                    CrossCheckandUpdateFields(finalJson);
            } else {
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

            if (MyRouteActivity_PaperlessDS.this.isDestroyed()) { // or call isFinishing() if min sdk version < 17
                return;
            }
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }
            progressflag = 0;
            flag_thread = false;
        }
    }


    private void setDatatoAdapter(String finalJson) {
        try {
            JSONObject jsonObject = new JSONObject(finalJson);
            //jsonObject.getJSONObject("");

            new MyRouteShipments(finalJson, String.valueOf(Latitude), String.valueOf(Longitude), getApplicationContext(),
                    getWindow().getDecorView().getRootView());
            myRouteShipmentstemp.clear();
            myRouteShipmentstemp.addAll(GlobalVar.GV().myRouteShipmentList);
            DuplicateCustomer();
            MyRouteCompliance();

            if (!isRestarted())
                return;

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        adapter = new RouteListAdapterGroupbyPNo(getApplicationContext(), GlobalVar.GV().myRouteShipmentList,
//                "CourierKpi", this);
//
//        mapListview.setAdapter(adapter);
        setAdapter();
        //ValidateDatas();
        btnStartTrip.setVisibility(View.GONE);
        btnCloseTrip.setVisibility(View.GONE);

    }

    private void DuplicateCustomer() {
        try {
            DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
            int i = 0;
            for (MyRouteShipments temp : GlobalVar.GV().myRouteShipmentList) {
                String cmno =
                        (temp.ConsigneeMobile.length() >= 9) ? temp.ConsigneeMobile.substring(temp.ConsigneeMobile.length() - 9) : "Not";
                String cphno = (temp.ConsigneePhoneNumber.length() >= 9) ? temp.ConsigneePhoneNumber.substring(temp.ConsigneePhoneNumber.length() - 9) : "Not";
                String seqnos = "";
                if (!cmno.equals("Not") || !cphno.equals("Not")) {
                    HashSet<Integer> seqNo = dbConnections.getSeqNoByWaybillByMobileNo(cmno, cphno, getApplicationContext());

                    if (seqNo.size() > 0) {
                        Iterator<Integer> iterator = seqNo.iterator();
                        int j = 0;
                        while (iterator.hasNext()) {
                            if (j == 0)
                                seqnos = String.valueOf(iterator.next());
                            else
                                seqnos = seqnos + "," + String.valueOf(iterator.next());
                            j++;
                        }
                    }
                }
                GlobalVar.GV().myRouteShipmentList.get(i).ExistUser = seqnos;
                i++;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    int manualtest = 0;

    private void CrossCheckandUpdateFields(String json) {

        try {
            JSONArray jsonArray = new JSONArray(json);

            if (manualtest == 2) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject newdata = jsonArray.getJSONObject(i);
                    if (i == 10 || i == 0) {
                        newdata.getString("ConsigneeMobile").replace(newdata.getString("ConsigneeMobile"), "0593793637");
                        newdata.put("ConsigneeMobile", "0593793637");
                        jsonArray.put(i, newdata);
                    }
                }
            }
            boolean change = false;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject newdata = jsonArray.getJSONObject(i);
                for (int j = 0; j < GlobalVar.GV().myRouteShipmentList.size(); j++) {
                    if (newdata.getString("ItemNo").equals(GlobalVar.GV().myRouteShipmentList.get(j).ItemNo)) {
                        if (!newdata.getString("ConsigneeName").equals(GlobalVar.GV().myRouteShipmentList.get(j).ConsigneeName)
                                || !newdata.getString("ConsigneeMobile").equals(GlobalVar.GV().myRouteShipmentList.get(j).ConsigneeMobile)
                                || !newdata.getString("BillingType").equals(GlobalVar.GV().myRouteShipmentList.get(j).BillingType)
                                || Double.parseDouble(newdata.getString("CODAmount")) != GlobalVar.GV().myRouteShipmentList.get(j).CODAmount
                                || !newdata.getString("ConsigneePhoneNumber").equals(GlobalVar.GV().myRouteShipmentList.get(j).ConsigneePhoneNumber)
                                || !newdata.getString("ConsigneeLatitude").equals(GlobalVar.GV().myRouteShipmentList.get(j).Latitude)
                                || !newdata.getString("ConsigneeLongitude").equals(GlobalVar.GV().myRouteShipmentList.get(j).Longitude)) {

                            String header = "";
                            if (!newdata.getString("ConsigneeName").equals(GlobalVar.GV().myRouteShipmentList.get(j).ConsigneeName)) {
                                header = "Name";
                            }
                            if (!newdata.getString("ConsigneeMobile").equals(GlobalVar.GV().myRouteShipmentList.get(j).ConsigneeMobile)) {
                                if (header.length() > 0)
                                    header = header + "," + "Mobile";
                                else
                                    header = "Mobile";

                            }
                            if (!newdata.getString("BillingType").equals(GlobalVar.GV().myRouteShipmentList.get(j).BillingType)) {
                                if (header.length() > 0)
                                    header = header + "," + "BillingType";
                                else
                                    header = "BillingType";
                            }

                            if (Double.parseDouble(newdata.getString("CODAmount")) != GlobalVar.GV().myRouteShipmentList.get(j).CODAmount) {
                                if (header.length() > 0)
                                    header = header + "," + "CODAmount";
                                else
                                    header = "CODAmount";

                            }

                            if (!newdata.getString("ConsigneePhoneNumber").equals(GlobalVar.GV().myRouteShipmentList.get(j).ConsigneePhoneNumber)) {
                                if (header.length() > 0)
                                    header = header + "," + "Ph No";
                                else
                                    header = "Ph No";

                            }
                            if (!newdata.getString("ConsigneeLatitude").equals(GlobalVar.GV().myRouteShipmentList.get(j).Latitude)) {
                                if (header.length() > 0)
                                    header = header + "," + "Location";
                                else
                                    header = "Location";

                            }
                            if (!newdata.getString("ConsigneeLongitude").equals(GlobalVar.GV().myRouteShipmentList.get(j).Longitude)) {

                                if (!header.contains("Location")) {
                                    header = header + "," + "Location";
                                }
                            }


                            MyRouteShipments update = GlobalVar.GV().myRouteShipmentList.get(j);

                            update.ConsigneeName = newdata.getString("ConsigneeName");
                            update.ConsigneeMobile = newdata.getString("ConsigneeMobile");
                            update.BillingType = newdata.getString("BillingType");
                            update.CODAmount = Double.parseDouble(newdata.getString("CODAmount"));
                            update.ConsigneePhoneNumber = newdata.getString("ConsigneePhoneNumber");

                            update.Latitude = newdata.getString("Latitude");
                            update.Longitude = newdata.getString("Longitude");

                            if (update.Latitude.length() > 0 && update.Latitude.equals("null") && update.Latitude != null) {

                                Location sp = new Location("");
                                update.Latitude = "0";
                                update.Longitude = "0";

                            }


                            GlobalVar.GV().myRouteShipmentList.set(j, update);


                            DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                            Cursor resultDetail = dbConnections.Fill("select * from MyRouteShipments where ItemNo = " +
                                    newdata.getString("ItemNo"), getApplicationContext());

                            if (resultDetail.getCount() > 0) {
                                //resultDetail.moveToLast();
                                dbConnections.UpdateMyRouteShipmentsWithHeader
                                        (newdata.getString("ItemNo"), true, header, getApplicationContext(),
                                                getWindow().getDecorView().getRootView());
                            }

                            // adapter = new RouteListAdapter(getApplicationContext(), GlobalVar.GV().kpi, "CourierKpi");
                            // mapListview.setAdapter(adapter);

                            GlobalVar.GV().MakeSound(getApplicationContext(), R.raw.barcodescanned);

//                            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_in_left);
//                            viewToAnimate.startAnimation(animation);
//                            lastPosition = position;

                        }
                        change = true;
                        break;
                    }
                }
            }

            if (change) {
                ArrayList<MyRouteShipments> temp = new ArrayList<MyRouteShipments>();
                temp.addAll(GlobalVar.GV().myRouteShipmentList);
                GlobalVar.GV().myRouteShipmentList.clear();
                GlobalVar.GV().myRouteShipmentList = new ArrayList<>();
                GlobalVar.GV().myRouteShipmentList.addAll(temp);
                temp.clear();

//                adapter = new RouteListAdapterGroupbyPNo(getApplicationContext(), GlobalVar.GV().myRouteShipmentList, "CourierKpi", this);
//                mapListview.setAdapter(adapter);
                setAdapter();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        manualtest++;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        }

        if (progressDialog_NotifyClient != null) {
            if (progressDialog_NotifyClient.isShowing())
                progressDialog_NotifyClient.dismiss();
        }

    }

    private void deleteConfirmRoute() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MyRouteActivity_PaperlessDS.this);
        builder1.setTitle("Info");
        builder1.setMessage("Do you want to delete all? ");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        GlobalVar.GV().myRouteShipmentList.clear();

                        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                        dbConnections.clearAllCourierDailyRoute(getApplicationContext());
                        //  dbConnections.DeleteAllSuggestLocation(getApplicationContext());
                        new DeleteContact().execute("");

                        // dbConnections.DeleteAllPlannedLocation(getApplicationContext());
                        dbConnections.close();

                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private class DeleteContact extends AsyncTask<String, String, String> {


        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {

            try {
                DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                boolean loop = false;
                loop = GlobalVar.deleteContactRawID(dbConnections.ContactDetails(getApplicationContext()), getApplicationContext(), 0);
                int time = 1000;
                while (!loop)
                    Thread.sleep(time);


            } catch (InterruptedException e) {
                e.printStackTrace();

            } catch (Exception e) {
                e.printStackTrace();

            }
            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            finish();
        }


        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MyRouteActivity_PaperlessDS.this,
                    "Info",
                    "Your Request is being process,kindly please wait");
        }


        @Override
        protected void onProgressUpdate(String... text) {


        }
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
            GlobalVar.GV().CourierDailyRouteID = savedInstanceState.getInt("CourierDailyRouteID");
            GlobalVar.GV().myRouteShipmentList = savedInstanceState.getParcelableArrayList("kpi");
            myRouteShipmentstemp = savedInstanceState.getParcelableArrayList("myRouteShipmentstemp");
            Latitude = savedInstanceState.getDouble("Latitude");
            Longitude = savedInstanceState.getDouble("Longitude");
            places = (ArrayList<Location>) savedInstanceState.getSerializable("places");
            Optmizeplaces = (ArrayList<Location>) savedInstanceState.getSerializable("Optmizeplaces");
            AreaData = savedInstanceState.getString("AreaData");

//            adapter = new RouteListAdapterGroupbyPNo(getApplicationContext(), GlobalVar.GV().myRouteShipmentList, "CourierKpi", this);
//            mapListview.setAdapter(adapter);
            setAdapter();

            GlobalVar.GV().haslocation = (ArrayList<Integer>) savedInstanceState.getSerializable("haslocation");

            if (GlobalVar.GV().CourierDailyRouteID == 0) {
                btnStartTrip.setVisibility(View.VISIBLE);
                txtStartTrip.setVisibility(View.VISIBLE);
                btnCloseTrip.setVisibility(View.GONE);
                txtCloseTrip.setVisibility(View.GONE);
            } else {
                btnStartTrip.setVisibility(View.GONE);
                txtStartTrip.setVisibility(View.GONE);
                if (GlobalVar.GV().myRouteShipmentList.size() > 0) {
                    btnCloseTrip.setVisibility(View.GONE);
                    txtCloseTrip.setVisibility(View.GONE);
                } else {
                    btnCloseTrip.setVisibility(View.VISIBLE);
                    txtCloseTrip.setVisibility(View.VISIBLE);
                }
            }
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("EmployID", GlobalVar.GV().EmployID);
        outState.putInt("UserID", GlobalVar.GV().UserID);
        outState.putInt("StationID", GlobalVar.GV().StationID);
        outState.putString("EmployMobileNo", GlobalVar.GV().EmployMobileNo);
        outState.putString("EmployName", GlobalVar.GV().EmployName);
        outState.putString("EmployStation", GlobalVar.GV().EmployStation);
        outState.putInt("CourierDailyRouteID", GlobalVar.GV().CourierDailyRouteID);
        outState.putParcelableArrayList("kpi", GlobalVar.GV().myRouteShipmentList);
        outState.putDouble("Latitude", Latitude);
        outState.putDouble("Longitude", Longitude);
        outState.putSerializable("places", places);
        outState.putSerializable("haslocation", GlobalVar.GV().haslocation);
        outState.putSerializable("myRouteShipmentstemp", myRouteShipmentstemp);
        outState.putSerializable("Optmizeplaces", Optmizeplaces);
        outState.putString("AreaData", AreaData);
        super.onSaveInstanceState(outState);
    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    private boolean isnotificationsend(int Waybillno, String consLocation, int parentwaybillno) {
        // SweetAlertLoading();
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        boolean issend = false;//dbConnections.IsNotificationSend(getApplicationContext());
        double Latitude = 0.0, Longitude = 0.0;
        if (!issend) {
            Location location = GlobalVar.getLastKnownLocation(getApplicationContext());
            if (location != null) {
                Latitude = location.getLatitude();
                Longitude = location.getLongitude();
            }


            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("WayBillNo", Waybillno);

                jsonObject.put("EmployID", GlobalVar.GV().EmployID);
                jsonObject.put("Type", "LiveTracking");
                jsonObject.put("Lat", Latitude);
                jsonObject.put("Long", Longitude);
                jsonObject.put("ConsLocation", consLocation);
                jsonObject.put("StartTime", DateTime.now().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new SendNotificationtoConsignee().execute(jsonObject.toString(), String.valueOf(Waybillno), String.valueOf(parentwaybillno));

            //String result = new SendNotificationtoConsignee().execute(jsonObject.toString()).get();

//                if (result != null) {
//                    JSONObject jsonResult = null;
//                    try {
//                        jsonResult = new JSONObject(result);
//                        if (!jsonResult.getBoolean("HasError")) {
//                            AlertCommon("Info", "Notification sent Sucessfully to the Consignee, kindly start deliver process ", 2,
//                                    2, Waybillno);
//                            return true;
//                        } else {
//                            AlertCommon("Error", "Kindly try again to Notify the Consignee", 1,
//                                    1, Waybillno);
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                }


        }

        dbConnections.close();
        return issend;
    }

    @Override
    public void onItemSelected(MyRouteShipments item, int pos) {
        // Toast.makeText(getApplicationContext(), "Selected: " + item.Position, Toast.LENGTH_LONG).show();

        if (GlobalVar.GV().CourierDailyRouteID > 0) {

            DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
            int wno = Integer.parseInt(item.ItemNo);
            String Latitude = item.Latitude;
            String Longitude = item.Longitude;

            if (!item.IsNotifyCust) {
                wno = item.IsNotifyWaybillNo;
                Latitude = item.ParentLatitude;
                Longitude = item.ParentLongitude;
            }


            if (!dbConnections.IsNotificationSend(getApplicationContext()) && !GlobalVar.GV().isFortesting) {
                isnotificationsend(wno, (Latitude + "," + Longitude), Integer.parseInt(item.ItemNo));
                return;
            } else
                RedirectwaybillDetailActivity(item.ItemNo);

        } else
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to start a new trip before", GlobalVar.AlertType.Warning);

    }

    private void RedirectwaybillDetailActivity(String WaybillNo) {
        ValidatePlannedSuggestRouteSync();
        RestartServiceLocationMorethan30();
        RestartDeviceActivity();
        // OLD Method commented date - 04-09-2020
        // int position = item.Position;

        int position = getItemPosition(WaybillNo);


        if (GlobalVar.GV().myRouteShipmentList.get(position).TypeID == 1) {
            Intent intent = null;
//            if (GlobalVar.GV().myRouteShipmentList.get(position).IsMap == 1)
//                intent = new Intent(getApplicationContext(), WaybillPlanActivity.class);
//            else
//                intent = new Intent(getApplicationContext(), WaybillPlanActivityNoMap.class);
            intent = new Intent(getApplicationContext(), WaybillPlanActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("ID", String.valueOf(GlobalVar.GV().myRouteShipmentList.get(position).ID));
            bundle.putString("WaybillNo", GlobalVar.GV().myRouteShipmentList.get(position).ItemNo);
            bundle.putDouble("COD", GlobalVar.GV().myRouteShipmentList.get(position).CODAmount);
            bundle.putString("BT", GlobalVar.GV().myRouteShipmentList.get(position).BillingType);
            bundle.putInt("SeqNo", GlobalVar.GV().myRouteShipmentList.get(position).DsOrderNo);
            bundle.putInt("isOtp", GlobalVar.GV().myRouteShipmentList.get(position).isOtp);
            bundle.putInt("position", position);
            bundle.putBoolean("isupdate", GlobalVar.GV().myRouteShipmentList.get(position).isupdate);
            intent.putExtras(bundle);
            startActivityForResult(intent, 1);


        } else {
            Intent intent = new Intent(getApplicationContext(), BookingPlanActivity.class);
            startActivity(intent);
        }
    }

    private int getItemPosition(String ItemNo) {
        for (int position = 0; position < GlobalVar.GV().myRouteShipmentList.size(); position++)
            if (GlobalVar.GV().myRouteShipmentList.get(position).ItemNo.equals(ItemNo))
                return position;
        return 0;
    }

    private void ValidatePlannedSuggestRouteSync() {
        DBConnections db = new DBConnections(getApplicationContext(), null);
        //db.DeleteAllSuggestLocation(getApplicationContext());
        Cursor result = db.Fill("select * from MyRouteCompliance where IsSync = 0 Limit 1 ", getApplicationContext());
        if (result.getCount() > 0) {
            if (!isMyServiceRunning(com.naqelexpress.naqelpointer.service.PlannedRoute_MyRouteComp.class)) {
                startService(
                        new Intent(this,
                                com.naqelexpress.naqelpointer.service.PlannedRoute_MyRouteComp.class));
            }
        }
        result.close();
        db.close();


    }

    private void RestartService() {
        DBConnections db = new DBConnections(getApplicationContext(), null);
        //db.DeleteAllSuggestLocation(getApplicationContext());
        Cursor result = db.Fill("select * from LocationintoMongo  Limit 1", getApplicationContext());

        if (result.getCount() > 0) {

            stopService(
                    new Intent(this,
                            com.naqelexpress.naqelpointer.service.LocationIntoMongo.class));
            startService(
                    new Intent(this,
                            com.naqelexpress.naqelpointer.service.LocationIntoMongo.class));

        }
        result.close();
        db.close();


    }

    private void RestartServiceLocationMorethan30() {
        DBConnections db = new DBConnections(getApplicationContext(), null);
        //db.DeleteAllSuggestLocation(getApplicationContext());
        Cursor result = db.Fill("select count(*) totalcount from LocationintoMongo", getApplicationContext());
        if (result.getCount() > 0) {

            result.moveToFirst();
            int count = result.getInt(result.getColumnIndex("totalcount"));
            if (count > 30) {
                stopService(
                        new Intent(this,
                                com.naqelexpress.naqelpointer.service.LocationIntoMongo.class));
                startService(
                        new Intent(this,
                                com.naqelexpress.naqelpointer.service.LocationIntoMongo.class));


            }

        }
        result.close();
        db.close();


    }

    public void RestartDeviceActivity() {

        stopService(
                new Intent(getApplicationContext(),
                        com.naqelexpress.naqelpointer.service.DeviceActivity.class));

        startService(
                new Intent(getApplicationContext(),
                        com.naqelexpress.naqelpointer.service.DeviceActivity.class));

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

    private void startActualRoute() {

        if (GlobalVar.locationEnabled(getApplicationContext())) {
            if (!GlobalVar.isMyServiceRunning(LocationService.class, getApplicationContext())) {
                startService(new Intent(getBaseContext(),
                        LocationService.class));
            }


            LocationupdateInterval.cancelAlarm(getApplicationContext());
            LocationupdateInterval.setAlarm(true, getApplicationContext());
        }

        if (GlobalVar.locationEnabled(getApplicationContext())) {
            if (!isMyServiceRunning(com.naqelexpress.naqelpointer.Activity.GoogleApiFusedLocation.LocationService.class)) {
                startService(new Intent(getBaseContext(),
                        com.naqelexpress.naqelpointer.Activity.GoogleApiFusedLocation.LocationService.class));
            }
        }
    }

    private void hideenableListview() {
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        int count = dbConnections.isCompleteDuplicateCustomerScans(getApplicationContext(), true);
        if (count > 0) {
            duplicatecustmoerList(dbConnections);
            return;
        }

        DuplicateCustomer();


        boolean isLoadFull = true;
        Cursor result = dbConnections.Fill("select * from MyRouteActionActivity", getApplicationContext());
        if (result != null && result.getCount() > 0) {

            result.moveToFirst();
            //do {
            int NextActivitySeqNo = result.getInt(result.getColumnIndex("NextActivitySeqNo"));
            int NextActivityWaybillNo = result.getInt(result.getColumnIndex("NextActivityWaybillNo"));
            int LastActivitySeqno = result.getInt(result.getColumnIndex("LastActivitySeqno"));
            int TotalLocationCount = result.getInt(result.getColumnIndex("TotalLocationCount"));
            int isComplete = result.getInt(result.getColumnIndex("isComplete"));
            if (isComplete == 0) {
                ArrayList<MyRouteShipments> tl = new ArrayList<>();
                for (int i = 0; i < GlobalVar.GV().myRouteShipmentList.size(); i++) {
                    MyRouteShipments t = GlobalVar.GV().myRouteShipmentList.get(i);

                    if (Integer.parseInt(t.ItemNo) == NextActivityWaybillNo) {
                        //ArrayList<MyRouteShipments> temp = new ArrayList<>();
                        //temp.addAll(GlobalVar.GV().myRouteShipmentList);

                        String[] spltdupl = t.ExistUser.split(",");
                        //GlobalVar.GV().myRouteShipmentList.clear();

                        if (spltdupl.length > 0 && spltdupl[0].length() > 0) {

                            for (int j = 0; j < spltdupl.length; j++) {
                                MyRouteShipments t1 = myRouteShipmentstemp.get(Integer.parseInt(spltdupl[j]) - 1);
                                t1.IsNotifyWaybillNo = Integer.parseInt(t.ItemNo);
                                t1.IsDuplicateCustomer = true;
                                t1.ParentLatitude = t.Latitude;
                                t1.ParentLongitude = t.Longitude;
                                t1.BGColor = "CurrentOne";
//                                if (j == 0) {
//                                    //t1.DsOrderNo = t.DsOrderNo;
//                                    t1.IsDuplicateCustomer = true;
//
//                                } else
                                if (j != 0) {
                                    // t1.DsOrderNo = Integer.parseInt(spltdupl[j]);

                                    t1.IsNotifyCust = false;

                                    //t1.ParentWaybillno = t.ItemNo;

                                }
                                Gson gson = new Gson();
                                String jsonString = gson.toJson(t1);

                                dbConnections.InsertDuplicateCustomer(getApplicationContext(), jsonString, t1.DsOrderNo, Integer.parseInt(t.ItemNo));
                                tl.add(t1);
                                // GlobalVar.GV().myRouteShipmentList.add(t1);

                            }
                            GlobalVar.GV().myRouteShipmentList.clear();
                            GlobalVar.GV().myRouteShipmentList.addAll(tl);
                            addPrevNotDeliveredintoExsistingList(1);

                        } else {
                            GlobalVar.GV().myRouteShipmentList.clear();
                            GlobalVar.GV().myRouteShipmentList.add(t);
                            addPrevNotDeliveredintoExsistingList(0);

                        }


                        break;
                    }

                }
            }
            if (isComplete == 1) {
                // order by Location / No Location
//                GlobalVar.GV().LoadMyRouteShipments_RouteOpt("ItemNo", true, getApplicationContext()
//                        , getWindow().getDecorView().getRootView());
                GlobalVar.GV().LoadMyRouteShipments("OrderNo", true, getApplicationContext(),
                        getWindow().getDecorView().getRootView());
                adapter = new RouteListAdapterPaperlessDS(getApplicationContext(), GlobalVar.GV().myRouteShipmentList,
                        "CourierKpi", this);

                DuplicateCustomer();
                mapListview.setAdapter(adapter);

            }
            adapter.notifyDataSetChanged();
            // } while (result.moveToFirst());
        }
        result.close();
        dbConnections.close();
    }


    private void addPrevNotDeliveredintoExsistingList(int isduplicatecustomer) {
        int waybillno = 0;
        String latitude = "";
        String longitude = "";
        boolean isupdate = true;
        if (isduplicatecustomer == 1) {
            waybillno = GlobalVar.GV().myRouteShipmentList.get(0).IsNotifyWaybillNo;
            latitude = GlobalVar.GV().myRouteShipmentList.get(0).ParentLatitude;
            longitude = GlobalVar.GV().myRouteShipmentList.get(0).ParentLongitude;
        } else {
            waybillno = Integer.parseInt(GlobalVar.GV().myRouteShipmentList.get(0).ItemNo);
            latitude = GlobalVar.GV().myRouteShipmentList.get(0).Latitude;
            longitude = GlobalVar.GV().myRouteShipmentList.get(0).Longitude;
            isupdate = false;

        }

        MyRouteShipments temp = (GlobalVar.GV().FetchMyRouteShipments(getApplicationContext()
                , waybillno, "#b0afb3", latitude, longitude, isupdate));
        if (temp != null && temp.ItemNo.length() > 2)
            GlobalVar.GV().myRouteShipmentList.add(0, temp);
    }

    private void duplicatecustmoerList(DBConnections dbConnections) {

        GlobalVar.GV().myRouteShipmentList.clear();


        GlobalVar.GV().myRouteShipmentList.addAll(dbConnections.ListDuplicateCustomerScans(getApplicationContext()));
        addPrevNotDeliveredintoExsistingList(1);

//        MyRouteShipments temp = (GlobalVar.GV().FetchMyRouteShipments(getApplicationContext()
//                , GlobalVar.GV().myRouteShipmentList.get(0).IsNotifyWaybillNo, "#b0afb3"));
//        if (temp != null && temp.ItemNo.length() > 2)
//            GlobalVar.GV().myRouteShipmentList.add(0, temp);
        adapter.notifyDataSetChanged();
        dbConnections.close();
//
//
//        result.moveToFirst();

//
//        do {
//            String str = result.getString(result.getColumnIndex("data"));
//            MyRouteShipments list = (MyRouteShipments) new Gson().fromJson(str, List.class);
//            GlobalVar.GV().myRouteShipmentList.add(list);
//        } while (result.moveToNext());
//

//        dbConnections.close();
//        result.close();

    }

    private class SendNotificationtoConsignee extends AsyncTask<String, Integer, String> {
        StringBuffer buffer;
        String DomainURL = "";
        String isInternetAvailable = "";
        int Waybillno = 0, itemselectedWaybillno = 0;

        @Override
        protected void onPreExecute() {

            if (progressDialog_NotifyClient == null)
                progressDialog_NotifyClient = ProgressDialog.show(MyRouteActivity_PaperlessDS.this,
                        "Please wait.", "Notify the Consignee for Delivery", true);

            DomainURL = GlobalVar.GV().GetDomainURL(getApplicationContext());
            super.onPreExecute();

        }

        protected String doInBackground(String... params) {

            //GetFacilityStatusRequest getDeliveryStatusRequest = new GetFacilityStatusRequest();
            String jsonData = params[0];
            Waybillno = Integer.parseInt(params[1]);
            itemselectedWaybillno = Integer.parseInt(params[2]);
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                //URL url = new URL(DomainURL + "SendNotificationtoConsigneeForCustApp");
                URL url = new URL(DomainURL + "SendNotificationtoConsigneeForCustApp"); //SpecificCons
                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setReadTimeout(GlobalVar.GV().loadbalance_ConRedtimeout);
                httpURLConnection.setConnectTimeout(GlobalVar.GV().loadbalance_ConRedtimeout);
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
            }


            return null;
//


        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute("");
            if (result != null) {
                JSONObject jsonResult = null;
                try {
                    jsonResult = new JSONObject(result);
                    if (!jsonResult.getBoolean("HasError")) {
//                        AlertCommon("Info", "Notification sent Sucessfully to the Consignee, kindly start deliver process ", 2,
//                                2, Waybillno);
                        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                        dbConnections.UpdateMyRouteActionActivityNotification(getApplicationContext(), Waybillno);
                        dbConnections.close();
                        //  return true;
                        RedirectwaybillDetailActivity(String.valueOf(itemselectedWaybillno));
                    } else {
                        AlertCommon("Error", "Kindly try again to Notify the Consignee", 1,
                                1, Waybillno);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(getApplicationContext(), "Something goes wrong , kindly try again", Toast.LENGTH_LONG).show();
            }
            if (progressDialog_NotifyClient != null && progressDialog_NotifyClient.isShowing()) {
                progressDialog_NotifyClient.dismiss();
                progressDialog_NotifyClient = null;
            }

        }
    }

    private void AlertCommon(String title, String message, final int type, final int updateDB,
                             final int refNo) {
        SweetAlertDialog eDialog = new SweetAlertDialog(this, type);
        eDialog.setTitleText(title);
        eDialog.setContentText(message);
        eDialog.setConfirmText("OK");
        eDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismissWithAnimation();
                DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                if (type == 2 && updateDB == 2) // updateDB = 2 - Notification in myrouteActivity
                {
                    dbConnections.UpdateMyRouteActionActivityNotification(getApplicationContext(), refNo);
                }

            }
        });
        eDialog.setCancelable(false);
        eDialog.show();
    }


    public void GetPlannedLocation() {

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);

        String sd = dbConnections.isMyRouteComplainceDate(getApplicationContext());
        String addtime = sd;
//        Cursor result = dbConnections.Fill("select * from plannedLocation where   Date ='" + GlobalVar.getDate()
//                + "' and  EmpID = " + GlobalVar.GV().EmployID + "  order by position asc ", getApplicationContext());

        Cursor result = dbConnections.Fill("select * from plannedLocation where EmpID = " +
                GlobalVar.GV().EmployID + "  order by position asc ", getApplicationContext());


        int seconds = 0;
        int count = result.getCount();
        int exit = 1;
        if (result != null && result.getCount() > 0) {

            result.moveToFirst();

            do {

                int value = result.getInt(result.getColumnIndex("PETA_Value"));
                int WaybillNo = result.getInt(result.getColumnIndex("WaybillNo"));
                addtime = GlobalVar.getCurrentDatewithCustomSeconds(value + seconds, addtime);
                if (exit == count - 1)
                    break;
                seconds = 420;

                exit = exit + 1;

//                String data = result.getString(result.getColumnIndex("StringData"));
//
//                if (data.contains("ZERO_RESULTS")) {
//                    System.out.println("true");
//                }
//
//                try {
//                    JSONObject jsonObject = new JSONObject(data);
//                    JSONArray jRoutes = jsonObject.getJSONArray("routes");
//                    for (int i = 0; i < jRoutes.length(); i++) {
//                        JSONArray jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
//                        lastseqstoptime.setText((String) ((JSONObject) ((JSONObject) jLegs.get(i)).get("duration")).get("text"));
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

            } while (result.moveToNext());
        }
        if (result != null && result.getCount() == 0) {
            addtime = "";
            lastseqstoptime.setText("No Plan");
        } else
            lastseqstoptime.setText("Last Seq Stop time : " + addtime);

        if (lastseqstoptime.getText().toString().equals("No Plan"))
            txtplannedlocationcount.setText("");
        else
            txtplannedlocationcount.setText("Planned Count : " + String.valueOf(result.getCount()));

        result.close();
        dbConnections.close();

    }

    private class CriticalRouteLine extends AsyncTask<String, Integer, String> {
        StringBuffer buffer;
        String DomainURL = "";
        String isInternetAvailable = "";

        @Override
        protected void onPreExecute() {

            if (progressDialog_NotifyClient == null)
                progressDialog_NotifyClient = ProgressDialog.show(MyRouteActivity_PaperlessDS.this,
                        "Please wait.", "Cross check with data .", true);

            DomainURL = GlobalVar.GV().GetDomainURL(getApplicationContext());
            super.onPreExecute();

        }

        protected String doInBackground(String... params) {

            //GetFacilityStatusRequest getDeliveryStatusRequest = new GetFacilityStatusRequest();
            String jsonData = params[0];

            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(DomainURL + "CriticalRouteLineSeq");
                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setReadTimeout(GlobalVar.GV().loadbalance_ConRedtimeout);
                httpURLConnection.setConnectTimeout(GlobalVar.GV().loadbalance_ConRedtimeout);
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
                        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                        dbConnections.UpdateMyRouteActionActivitySeqNo_Critical(getApplicationContext());
                        dbConnections.close();
                        finish();
                    } else
                        GlobalVar.ShowDialog(MyRouteActivity_PaperlessDS.this, "Error", jsonObject.getString("ErrorMessage"), true);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                System.out.println(result);
            } else {
                if (isInternetAvailable.contains("No address associated with hostname")) {
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Kindly check your internet", GlobalVar.AlertType.Error);
                } else {
                    GlobalVar.GV().triedTimes = GlobalVar.GV().triedTimes + 1;
                    if (GlobalVar.GV().triedTimes == GlobalVar.GV().triedTimesCondition) {
                        //dbConnections.UpdateDomaintriedTimes(GlobalVar.GV().triedTimes, DomainURL, getApplicationContext());
                        GlobalVar.GV().SwitchoverDomain(getApplicationContext(), DomainURL);
                    }

                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.servererror), GlobalVar.AlertType.Error);
                }

            }
            if (progressDialog_NotifyClient != null && progressDialog_NotifyClient.isShowing()) {
                progressDialog_NotifyClient.dismiss();
                progressDialog_NotifyClient = null;
            }

        }
    }

    private ArrayList<Location> getAreaWaybills() {
        GlobalVar.GV().alertMsgAll("please wait ", "", MyRouteActivity_PaperlessDS.this, Enum.PROGRESS_TYPE,
                "MyRouteActivity_Complaince_GroupbyPhn");

//        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
//        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
//        pDialog.setTitleText("Optimizing routes kindly please wait");
//        pDialog.setCancelable(false);
//        pDialog.show();

        ArrayList<Location> tlocation = new ArrayList<>();

        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);

        GlobalVar.GV().CourierDailyRouteID = dbConnections.getMaxID("CourierDailyRoute Where EmployID = " + GlobalVar.GV().EmployID
                + " and EndTime is NULL ", getApplicationContext());
        String AreaWaypoints = "";
        if (GlobalVar.GV().CourierDailyRouteID != 0) {
            Cursor result = dbConnections.Fill("select * from MyRouteShipments Where CourierDailyRouteID = " +
                    GlobalVar.GV().CourierDailyRouteID + " and AreaWaypoints <> '0' ", getApplicationContext());
            if (result.getCount() > 0) {
                result.moveToFirst();
                AreaWaypoints = result.getString(result.getColumnIndex("AreaWaypoints"));
                System.out.println(AreaWaypoints);
            }
        }

        if (AreaWaypoints.length() > 5) {
            String spllitbyArea[] = AreaWaypoints.split(";");

            for (int i = 1; i < spllitbyArea.length; i++) {
                //  tlocation.clear();
                String splitAreaLength[] = spllitbyArea[i].split("#");
                String splitWaybillinfo1[] = splitAreaLength[0].split(":");
                for (int j = 0; j < splitWaybillinfo1.length; j++) {
                    String splitWaybillinfo[] = splitWaybillinfo1[j].split("_");
                    String splitGeoinfo[] = splitWaybillinfo[3].split(",");
                    Location sp = new Location("");
                    try {

                        sp.setLatitude(Double.parseDouble(splitGeoinfo[0]));
                        sp.setLongitude(Double.parseDouble(splitGeoinfo[1]));
                        if (Double.parseDouble(splitGeoinfo[1]) != 0.0) {

                            sp.setSpeed(Integer.parseInt(splitWaybillinfo[0]) + 1);
                        }
                    } catch (Exception e) {
                        sp.setLatitude(0);
                        sp.setLongitude(0);
                    }
                    tlocation.add(sp);
                }


            }
        }
        exitdialog();
        return tlocation;
    }

    static SweetAlertDialog alertDialog;

    private void exitdialog() {
        if (alertDialog != null) {
            alertDialog.dismissWithAnimation();
            alertDialog = null;
        }
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (progressDialog != null && progressDialog.isShowing()) {
            Toast.makeText(getApplicationContext(), "Please Dont Press Home/Recent App button , Data will be loss", Toast.LENGTH_SHORT).show();
            DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
            dbConnections.InsertSkipRouteSequncer(getApplicationContext());
            dbConnections.close();

        }
    }

    private void deleteConfirmRoute_SkipRoute() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MyRouteActivity_PaperlessDS.this);
        builder1.setTitle("Warning");
        builder1.setMessage("You killed app , while loading fully , please delete and reolaod again ");
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        GlobalVar.GV().myRouteShipmentList.clear();

                        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                        dbConnections.clearAllCourierDailyRoute(getApplicationContext());
                        //  dbConnections.DeleteAllSuggestLocation(getApplicationContext());
                        new DeleteContact().execute("");
                        dbConnections.DeleteSkipRouteData(getApplicationContext());
                        // dbConnections.DeleteAllPlannedLocation(getApplicationContext());
                        dbConnections.close();

                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}