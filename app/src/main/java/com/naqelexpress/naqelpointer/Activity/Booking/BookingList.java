package com.naqelexpress.naqelpointer.Activity.Booking;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

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

public class BookingList extends AppCompatActivity {


    private SwipeMenuListView mapListview;
    private BookingListAdapter adapter;
    public ArrayList<Booking> myBookingList;
    private TextView nodata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {


            super.onCreate(savedInstanceState);
            setContentView(R.layout.content_booking_list);
            mapListview = (SwipeMenuListView) findViewById(R.id.myBookingListView);

            myBookingList = new ArrayList<>();

            setAdapter();


            nodata = (TextView) findViewById(R.id.nodata);

            if (savedInstanceState == null)
                GetBookingList();

            // GlobalVar.GV().rootViewMainPage = mainRootView = findViewById(android.R.id.content);

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
                    // menu.addMenuItem(openItem);

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
                    // menu.addMenuItem(deleteItem);
                }
            };
            mapListview.setMenuCreator(creator);

            mapListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    DBConnections dbConnections = new DBConnections(getApplicationContext(), null);
                    Cursor result = dbConnections.Fill("select * from PickUpAuto where IsSync = 0 and RefNo=" + myBookingList.get(position).RefNo,getApplicationContext());
                    if (result.getCount() == 0) {
                        Intent intent = new Intent(getApplicationContext(), BookingDetailActivity.class);
                        Bundle bundle = new Bundle();
                        intent.putParcelableArrayListExtra("value", myBookingList);
                        bundle.putInt("position", position);
                        bundle.putString("ID", String.valueOf(myBookingList.get(position).ID));
                        intent.putExtras(bundle);
                        startActivityForResult(intent, 0);
                    } else
                        GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), "you picked up this item, please sync data", GlobalVar.AlertType.Error);
                    dbConnections.close();

                }
            });
        } catch
                (Exception ex) {
            System.out.println(ex.getMessage());
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.refresh, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                GetBookingList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setAdapter() {
        adapter = new BookingListAdapter(BookingList.this, myBookingList, "BookingList");

        mapListview.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                int result = data.getIntExtra("result", -1);
                if (result == 0) {
                    //myBookingList
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    private void GetBookingList() {

        // GlobalVar.GV().LoadMyBooking();
        //GlobalVar.GV().LoadMyBookingList("BringBookingList",true);
        JSONObject jsonObject = new JSONObject();
        try {


            jsonObject.put("EmployID", GlobalVar.GV().EmployID);


            jsonObject.put("AppTypeID", "");
            jsonObject.put("AppVersion", "");
            jsonObject.put("LanguageID", "");
            String jsonData = jsonObject.toString();

            new BringBookingData().execute(jsonData);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //adapter.notifyDataSetChanged();


    }

    private class BringBookingData extends AsyncTask<String, Void, String> {
        String result = "";
        StringBuffer buffer;
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(BookingList.this);
            pd.setTitle("Loading");
            pd.setMessage("Downloading your Booking Request ");
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "BringBookingList");
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
                myBookingList.clear();
                Booking(finalJson);
                pd.dismiss();

            } else {
                //
                GlobalVar.GV().ShowSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.wentwrong), GlobalVar.AlertType.Error);
            }
            if (pd != null)
                pd.dismiss();
            super.onPostExecute(String.valueOf(finalJson));
        }
    }

    public void Booking(String finalJson) {
        try {

            //  JSONObject dataObject = new JSONObject(finalJson);
            JSONArray jsonArray = new JSONArray(finalJson);
            if (jsonArray.length() > 0) {
                nodata.setVisibility(View.GONE);
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Booking instance = new Booking();
                    try {
                        //instance.ID = Integer.parseInt(jsonObject.getString("id"));
                        //hot code
                        //instance.ID = 923122 + i;
                        // Delete Old ID If found
                        //        dbConnections.DeleteBooking( instance.ID);

                        instance.RefNo = jsonObject.getString("RefNo");
                        instance.ClientID = Integer.parseInt(jsonObject.getString("ClientID"));
                        instance.ClientName = jsonObject.getString("ClientName");
                        instance.PickUpReqDT = jsonObject.getString("PickUpReqDT");
                        instance.PicesCount = jsonObject.getDouble("PicesCount");
                        instance.Weight = Double.parseDouble(jsonObject.getString("Weight"));
                        instance.SpecialInstruction = jsonObject.getString("SpecialInstruction");
                        instance.OfficeUpTo = DateTime.parse(jsonObject.getString("OfficeUpTo"));
                        //instance.PickUpReqDT = DateTime.parse(jsonObject.getString("PickUpReqDT"));
                        instance.ContactPerson = jsonObject.getString("ContactPerson");
                        instance.ContactNumber = jsonObject.getString("ContactNumber");
                        instance.Address = jsonObject.getString("FirstAddress");
                        //instance.Latitude = jsonObject.getString("Latitude");
                        //instance.Longitude = jsonObject.getString("Longitude");
                        instance.GPSLocation = jsonObject.getString("GPSLocation");
                        instance.Status = jsonObject.getInt("CurrentStatusID");
                        instance.Orgin = jsonObject.getString("Origin");
                        instance.Destination = jsonObject.getString("Destination");
                        //instance.LoadType = jsonObject.getString("LoadType");
                        //instance.BillType = jsonObject.getString("BillType");
                        instance.BillType = jsonObject.getString("BillCode");
                        instance.EmployeeId = Integer.parseInt(jsonObject.getString("AssignedCourierEmployeeID"));
                        instance.OriginId = jsonObject.getInt("OriginStationID");
                        instance.DestinationId = jsonObject.getInt("DestinationStationID");


                        //boolean v = dbConnections.InsertBooking(instance);
//                    System.out.println(v);


                        myBookingList.add(new Booking(0, instance.RefNo, instance.ClientID, instance.ClientName, "",
                                instance.BookingDate, instance.PicesCount, instance.Weight, instance.SpecialInstruction, instance.OfficeUpTo,
                                instance.PickUpReqDT, instance.ContactPerson, instance.ContactNumber, instance.Address, instance.Latitude,
                                instance.Longitude, instance.Status, instance.Orgin, instance.Destination, instance.LoadType, instance.BillType
                                , instance.EmployeeId, instance.OriginId, instance.DestinationId, instance.GPSLocation));

                        // myBookingList.add(instance);

                    } catch (JSONException ignored) {
                        System.out.println(ignored);
                    }
                }
                adapter.notifyDataSetChanged();
            } else
                nodata.setVisibility(View.VISIBLE);

        } catch (JSONException ignored) {
            System.out.println(ignored);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("myBookingList", myBookingList);
        outState.putInt("EmployID", GlobalVar.GV().EmployID);
        outState.putInt("UserID", GlobalVar.GV().UserID);
        outState.putInt("StationID", GlobalVar.GV().StationID);
        outState.putString("EmployMobileNo", GlobalVar.GV().EmployMobileNo);
        outState.putString("EmployName", GlobalVar.GV().EmployName);
        outState.putString("EmployStation", GlobalVar.GV().EmployStation);
        outState.putParcelable("currentSettings", GlobalVar.GV().currentSettings);
        outState.putInt("currentSettingsID", GlobalVar.GV().currentSettings.ID);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            myBookingList = savedInstanceState.getParcelableArrayList("myBookingList");
            setAdapter();
            GlobalVar.GV().EmployID = savedInstanceState.getInt("EmployID");
            GlobalVar.GV().UserID = savedInstanceState.getInt("UserID");
            GlobalVar.GV().StationID = savedInstanceState.getInt("StationID");
            GlobalVar.GV().EmployMobileNo = savedInstanceState.getString("EmployMobileNo");
            GlobalVar.GV().EmployName = savedInstanceState.getString("EmployName");
            GlobalVar.GV().EmployStation = savedInstanceState.getString("EmployStation");
            GlobalVar.GV().currentSettings = savedInstanceState.getParcelable("currentSettings");
            GlobalVar.GV().currentSettings.ID = savedInstanceState.getInt("currentSettingsID");
        }
    }
}


