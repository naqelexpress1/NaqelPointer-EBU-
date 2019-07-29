package com.naqelexpress.naqelpointer.Activity.MyRoute;

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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.naqelexpress.naqelpointer.Activity.Booking.BookingPlanActivity;
import com.naqelexpress.naqelpointer.Activity.Waybill.WaybillPlanActivity;
import com.naqelexpress.naqelpointer.Activity.routeMap.MapMovingOnCurLatLng;
import com.naqelexpress.naqelpointer.Activity.routeMap.RouteMap;
import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.Classes.NewBarCodeScanner;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.MyRouteShipments;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.JSON.Request.BringMyRouteShipmentsRequest;
import com.naqelexpress.naqelpointer.R;

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

public class MyRouteActivity
        extends AppCompatActivity implements RouteListAdapterNew.RouteAdapterListener {
    private RecyclerView mapListview;
    private RouteListAdapterNew adapter;
    Button btnStartTrip, btnCloseTrip;
    TextView txtStartTrip, txtCloseTrip;
    public static ArrayList<Location> places = new ArrayList<>();//96346
    protected boolean flag_thread = false;
    static int progressflag = 0;

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.myroutenew);

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


        adapter = new RouteListAdapterNew(getApplicationContext(), GlobalVar.GV().myRouteShipmentList,
                "CourierKpi", this);
        mapListview.setAdapter(adapter);

        whiteNotificationBar(mapListview);

        btnStartTrip = (Button) findViewById(R.id.btnStartTrip);
        btnCloseTrip = (Button) findViewById(R.id.btnCloseTrip);

        txtStartTrip = (TextView) findViewById(R.id.txtStartTrip);
        txtCloseTrip = (TextView) findViewById(R.id.txtCloseTrip);

        //if (savedInstanceState == null)
//        checkCourierDailyRouteID(false, 1);

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

//        mapListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (GlobalVar.GV().CourierDailyRouteID > 0) {
//
//                    position = GlobalVar.GV().kpi.get(position).Position;
//                    if (GlobalVar.GV().kpi.get(position).TypeID == 1) {
//                        Intent intent = new Intent(getApplicationContext(), WaybillPlanActivity.class);
//                        Bundle bundle = new Bundle();
//                        bundle.putString("ID", String.valueOf(GlobalVar.GV().kpi.get(position).ID));
//                        bundle.putString("WaybillNo", GlobalVar.GV().kpi.get(position).ItemNo);
//                        bundle.putInt("position", position);
//                        intent.putExtras(bundle);
//                        startActivity(intent);
//                    } else {
//                        Intent intent = new Intent(getApplicationContext(), BookingPlanActivity.class);
//                        startActivity(intent);
//                    }
//                } else
//                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to start a new trip before", GlobalVar.AlertType.Warning);
//            }
//        });

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

        // set creator
//        mapListview.setMenuCreator(creator);
//        mapListview.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
//                switch (index) {
//                    case 0:
//                        if (GlobalVar.GV().kpi.get(position).TypeID == 1) {
//                            Intent intent = new Intent(getApplicationContext(), WaybillPlanActivity.class);
//                            Bundle bundle = new Bundle();
//                            bundle.putString("ID", String.valueOf(GlobalVar.GV().kpi.get(position).ID));
//                            bundle.putString("WaybillNo", GlobalVar.GV().kpi.get(position).ItemNo);
//                            intent.putExtras(bundle);
//                            startActivity(intent);
//                        } else {
//                            Intent intent = new Intent(getApplicationContext(), BookingPlanActivity.class);
//                            Bundle bundle = new Bundle();
//                            bundle.putString("ID", String.valueOf(GlobalVar.GV().kpi.get(position).ID));
//                            bundle.putString("WaybillNo", GlobalVar.GV().kpi.get(position).ItemNo);
//                            intent.putExtras(bundle);
//                            startActivity(intent);
//                        }
//                        break;
//                    case 1:
//                        MyRouteShipments item = GlobalVar.GV().kpi.get(position);
//                        GlobalVar.GV().kpi.remove(item);
//                        adapter = new RouteListAdapterNew(getApplicationContext(), GlobalVar.GV().kpi, "CourierKpi");
//                        mapListview.setAdapter(adapter);
//                        break;
//                }
//                // false : close the menu; true : not close the menu
//                return false;
//            }
//        });

        GlobalVar.GV().CourierDailyRouteID = 0;
        checkCourierDailyRouteID(false, 1);
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
        DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
        if (GlobalVar.GV().CourierDailyRouteID == 0) {
            GlobalVar.GV().CourierDailyRouteID = dbConnections.getMaxID("CourierDailyRoute Where EmployID = " + GlobalVar.GV().EmployID + " and EndTime is NULL ", getApplicationContext());
            GlobalVar.GV().LoadMyRouteShipments("OrderNo", true, getApplicationContext(),
                    getWindow().getDecorView().getRootView());
            DuplicateCustomer();

            if (GlobalVar.GV().myRouteShipmentList.size() > 0) {
                btnStartTrip.setVisibility(View.GONE);
                txtStartTrip.setVisibility(View.GONE);

                btnCloseTrip.setVisibility(View.GONE);
                txtCloseTrip.setVisibility(View.GONE);

                adapter = new RouteListAdapterNew(getApplicationContext(), GlobalVar.GV().myRouteShipmentList, "CourierKpi", this);
                mapListview.setAdapter(adapter);


                //ValidateDatas();

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

        if (GlobalVar.GV().CourierDailyRouteID == 0 && CreateNewRoute) {
            BringMyRouteShipmentsRequest bringMyRouteShipmentsRequest = new BringMyRouteShipmentsRequest();
            progressflag = 1;
            BringMyRouteShipments(bringMyRouteShipmentsRequest, buttonclick);
        }

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
//                if (ActivityCompat.checkSelfPermission(CourierKpi.this, Manifest.permission.ACCESS_FINE_LOCATION)
//                        == PackageManager.PERMISSION_GRANTED &&
//                        ActivityCompat.checkSelfPermission(CourierKpi.this, Manifest.permission.ACCESS_COARSE_LOCATION)
//                                == PackageManager.PERMISSION_GRANTED) {
//                    Location location = GlobalVar.getLastKnownLocation(getApplicationContext());
//
//                    Latitude = location.getLatitude();
//                    Longitude = location.getLongitude();
//
//                }
//                Optimization optimization = new Optimization();
//                optimization.CurrentLocation = String.valueOf(Latitude) + "," + String.valueOf(Longitude);
//                optimization.EmployID = String.valueOf(GlobalVar.GV().EmployID);
//                optimization.FleetNo = "test";
//                String MyShipments = "";
//                for (int i = 0; i < GlobalVar.GV().kpi.size(); i++) {
//                    MyShipments += GlobalVar.GV().kpi.get(i).ItemNo;
//                    if (i < GlobalVar.GV().kpi.size() - 1)
//                        MyShipments += ",";
//                }
//                optimization.Waybills = MyShipments;
//                String jsonData = JsonSerializerDeserializer.serialize(optimization, true);
//                ProjectAsyncTask task = new ProjectAsyncTask("Optimize", "Post", jsonData, "http://35.188.10.142/NaqelRouteApi/api/");
//                task.setUpdateListener(new OnUpdateListener() {
//                    public void onPostExecuteUpdate(String obj) {
//                        new MyRouteShipments(obj, MyRouteShipments.UpdateType.Optimization, getApplicationContext(),
//                                getWindow().getDecorView().getRootView());
//                        adapter = new RouteListAdapter(getApplicationContext(), GlobalVar.GV().kpi, "CourierKpi");
//                        mapListview.setAdapter(adapter);
//                        //GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootView, "Finish Optimizing Shipments", GlobalVar.AlertType.Info);
//                    }
//
//                    public void onPreExecuteUpdate() {
//                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "Start Optimizing Shipments", GlobalVar.AlertType.Info);
//                    }
//                });
//                task.execute();
                return true;
            case R.id.deleteall:
                deleteConfirmRoute();
                return true;
            case R.id.mnuShowDeliverySheetOrder:
                //OrderNo
                GlobalVar.GV().LoadMyRouteShipments("ItemNo", true, getApplicationContext()
                        , getWindow().getDecorView().getRootView());

                adapter = new RouteListAdapterNew(getApplicationContext(), GlobalVar.GV().myRouteShipmentList,
                        "CourierKpi", this);
                mapListview.setAdapter(adapter);
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
                try {
                    Intent intent = new Intent(MyRouteActivity.this, RouteMap.class);
                    startActivity(intent);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                return true;
            case R.id.movingmap:
                try {
                    Intent intent = new Intent(MyRouteActivity.this, MapMovingOnCurLatLng.class);
                    startActivity(intent);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                return true;
            case R.id.camera:

                if (!GlobalVar.GV().checkPermission(MyRouteActivity.this, GlobalVar.PermissionType.Camera)) {
                    GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.NeedCameraPermission), GlobalVar.AlertType.Error);
                    GlobalVar.GV().askPermission(MyRouteActivity.this, GlobalVar.PermissionType.Camera);
                } else {
                    Intent intent = new Intent(getApplicationContext().getApplicationContext(), NewBarCodeScanner.class);
                    startActivityForResult(intent, GlobalVar.GV().CAMERA_PERMISSION_REQUEST);
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
        }
    }


    //------------------------Bring My Route Shipments -------------------------------
    public void BringMyRouteShipments(BringMyRouteShipmentsRequest bringMyRouteShipmentsRequest, int buttonclick) {

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

    private ProgressDialog progressDialog;

    private class BringMyRouteShipmentsList extends AsyncTask<String, Void, String> {

        String result = "";
        StringBuffer buffer;
        int buttonclick;

        @Override
        protected void onPreExecute() {
            if (progressflag == 1)
                progressDialog = ProgressDialog.show(MyRouteActivity.this, "Please wait.", "Downloading Shipments Details.", true);
        }

        @Override
        protected String doInBackground(String... params) { //17748
            String jsonData = params[0];
            buttonclick = Integer.parseInt(params[1]);

            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                String function = "BringDeliverySheetbyOFDPiece";
//                String function = "BringMyRouteShipments";
                if (!GetDivision())
                    function = "BringMyRouteShipments";

//                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "BringMyRouteShipments"); //Geofence
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + function); //Geofence
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

//            if (CourierKpi.this.isDestroyed()) {
//                return;
//            }
            if (finalJson != null) {
                if (buttonclick == 0) {
                    super.onPostExecute(String.valueOf(finalJson));
                    setDatatoAdapter(finalJson);

                } else
                    CrossCheckandUpdateFields(finalJson);
            } else
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.wentwrong), GlobalVar.AlertType.Error);

            if (MyRouteActivity.this.isDestroyed()) { // or call isFinishing() if min sdk version < 17
                return;
            }
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();

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
            DuplicateCustomer();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter = new RouteListAdapterNew(getApplicationContext(), GlobalVar.GV().myRouteShipmentList,
                "CourierKpi", this);

        mapListview.setAdapter(adapter);

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

                adapter = new RouteListAdapterNew(getApplicationContext(), GlobalVar.GV().myRouteShipmentList, "CourierKpi", this);
                mapListview.setAdapter(adapter);

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

    }

    private void deleteConfirmRoute() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MyRouteActivity.this);
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
                        new DeleteContact().execute("");
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
                loop = GlobalVar.deleteContactRawID(dbConnections.ContactDetails(getApplicationContext()), getApplicationContext());
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
            progressDialog = ProgressDialog.show(MyRouteActivity.this,
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
            Latitude = savedInstanceState.getDouble("Latitude");
            Longitude = savedInstanceState.getDouble("Longitude");
            places = (ArrayList<Location>) savedInstanceState.getSerializable("places");

            adapter = new RouteListAdapterNew(getApplicationContext(), GlobalVar.GV().myRouteShipmentList, "CourierKpi", this);
            mapListview.setAdapter(adapter);

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

    @Override
    public void onItemSelected(MyRouteShipments item) {
        // Toast.makeText(getApplicationContext(), "Selected: " + item.Position, Toast.LENGTH_LONG).show();

        if (GlobalVar.GV().CourierDailyRouteID > 0) {

            int position = item.Position;
            if (GlobalVar.GV().myRouteShipmentList.get(position).TypeID == 1) {
                Intent intent = new Intent(getApplicationContext(), WaybillPlanActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("ID", String.valueOf(GlobalVar.GV().myRouteShipmentList.get(position).ID));
                bundle.putString("WaybillNo", GlobalVar.GV().myRouteShipmentList.get(position).ItemNo);
                bundle.putInt("position", position);
                intent.putExtras(bundle);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getApplicationContext(), BookingPlanActivity.class);
                startActivity(intent);
            }
        } else
            GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "You have to start a new trip before", GlobalVar.AlertType.Warning);

    }
}